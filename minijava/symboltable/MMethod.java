package minijava.symboltable;

import minijava.typecheck.ErrorPrinter;

import java.util.ArrayList;
import java.util.HashSet;

public class MMethod extends MType {
    protected String returnType;
    protected String className;
    protected ArrayList<MVar> mParamList = new ArrayList<MVar>();
    protected HashSet<MVar> mVarSet = new HashSet<MVar>();

    public String getClassName() {
        return className;
    }

    public ArrayList<MVar> getmParamList() {
        return mParamList;
    }

    public String getReturnType() {
        return returnType;
    }

    public MMethod(String name,String returnType,String className,int line,int column) {
        super(name,line,column);
        this.returnType = returnType;
        this.className = className;
    }

    public boolean judgeEqualParamList(ArrayList<MVar> paramList) {
        if(mParamList.size() != paramList.size()) return false;
        int s = paramList.size();
        for (int i=0; i < s; i++) {
            if(!mParamList.get(i).type.equals(paramList.get(i).type)) {
                return false;
            }
        }
        return true;
    }

    public boolean judgeParamList(ArrayList<MVar> paramList) {
        if (mParamList.size() != paramList.size()) {
            return false;
        }
        // judge paramlist one by one
        int s = paramList.size();
        for (int i=0; i < s; i++) {
            if (!MClassList.instance.judgeParentClass(paramList.get(i).type, mParamList.get(i).type)) {
                return false;
            }
        }
        return true;
    }

    public boolean repeatedParam(String paramName) {
        for (MVar knownParam:mParamList) {
            if (paramName.equals(knownParam.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean addParam(MVar nParam) {
        if (repeatedParam(nParam.getName())) {
            ErrorPrinter.instance.printError(nParam.getLine(),nParam.getColumn(),"Param " + nParam.getName() + " repeated declared");
            return false;
        } else {
            mParamList.add(nParam);
            return true;
        }
    }

    public boolean addVar(MVar nVar) {
        if (!mVarSet.add(nVar)) {
            ErrorPrinter.instance.printError(nVar.getLine(),nVar.getColumn(),"Var " + nVar.getName() + " repeated declared");
            return false;
        } else {
            return true;
        }
    }

    // no need to getParam by now

    public MVar getVar(String name) {
        for (MVar knownVar:mVarSet) {
            if (name.equals(knownVar.getName())) {
                return knownVar;
            }
        }
        for (MVar knownParam:mParamList) {
            if (name.equals(knownParam.getName())) {
                return knownParam;
            }
        }
        MClass nClass = MClassList.instance.findClass(className);
        if (nClass != null) {
            return nClass.getVar(name);
        } else { //that will not happen
            return null;
        }
    }

    public String getPigletDefineName() {
        // have got pigletName in completeClass()
        String pigletName = getPigletName() + " [ " + (mParamList.size()+1) + " ] ";
        return pigletName;
    }

    public int allocTemp(int currentTemp) {
        int num = 0;
        for(MVar mVar : mParamList) {
            mVar.setTempNum(++num);
        }
        for(MVar mVar : mVarSet) {
            mVar.setTempNum(currentTemp++);
        }
        return currentTemp;
    }
}
