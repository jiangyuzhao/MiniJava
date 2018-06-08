package minijava.symboltable;

import minijava.typecheck.ErrorPrinter;

import java.util.HashSet;
import java.util.ArrayList;

public class MClass extends MType {

    protected HashSet<MMethod> mMethodSet = new HashSet<MMethod>();
    protected HashSet<MVar> mVarSet = new HashSet<MVar>();
    protected String parentClass;
    // piglet
    protected String methodTempName;
    public void setMethodTempName(String methodTempName) {
        this.methodTempName = methodTempName;
    }
    public String getMethodTempName() {
        return methodTempName;
    }

    public MClass(String name,int line,int column) {
        super(name,line,column);
    }

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    public HashSet<MMethod> getMethodSet() {
        return mMethodSet;
    }

    public HashSet<MVar> getVarSet() {
        return mVarSet;
    }

    public boolean repeatedMethod(String methodName,String returnType,ArrayList<MVar> paramList) {
        MClass nClass = this;
        MMethod nMethod = this.getMethod(methodName);
        // there will be no circle extension here
        while (true) {
            for (MMethod knownMethod:nClass.getMethodSet()) {
                if (methodName.equals(knownMethod.getName())) {
                    if (!knownMethod.judgeEqualParamList(paramList) || !returnType.equals(knownMethod.getReturnType())) {
                        //method name equal but param not equal
                        ErrorPrinter.instance.printError(nMethod.getLine(), nMethod.getColumn(), "Method " + nMethod.getName() + " repeated declared");
                        return true;
                    }
                }
            }
            if(nClass.getParentClass() != null) {
                nClass = MClassList.instance.findClass(nClass.getParentClass());
            }
            else {
                break;
            }
        }
        return false;
    }

    public boolean addMethod(MMethod nMethod) {
        if (!mMethodSet.add(nMethod)) {
            ErrorPrinter.instance.printError(nMethod.getLine(),nMethod.getColumn(),"Method " + nMethod.getName() + " repeated declared");
            return false;
        } else {
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

    // get a method object according to its name and paramList
    public MMethod getMethod(String name) {
        MClass nClass = this;
        // assume that there will be no circle extension here
        while (true) {
            for (MMethod knownMethod:nClass.getMethodSet()) {
                if (name.equals(knownMethod.getName())) {
                    return knownMethod;
                }
            }
            if(nClass.getParentClass() != null) {
                nClass = MClassList.instance.findClass(nClass.getParentClass());
            }
            else {
                break;
            }
        }
        return null;
    }

    public MVar getVar(String name) {
        MClass nClass = this;
        // assume that there will be no circle extension here
        while (true) {
            for (MVar knownVar:nClass.getVarSet()) {
                if (name.equals(knownVar.getName())) return knownVar;
            }
            if (nClass.getParentClass() != null) {
                nClass = MClassList.instance.findClass(nClass.getParentClass());
            }
            else {
                break;
            }
        }
        return null;
    }

    public boolean findMethodName(String name) {
        for (MMethod knownMethod:mMethodSet) {
            if (name.equals(knownMethod.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean findVarName(String name) {
        for (MVar knownVar:mVarSet) {
            if (name.equals(knownVar.getName())) {
                return true;
            }
        }
        return false;
    }

    public void completeClass() {
        // completeClass() only once.
        if (getPigletStatus() == 0) {
            return;
        }
        // make sure parent has done completeClass()
        MClass parentClass;
        if (getParentClass() != null) {
            parentClass = MClassList.instance.findClass(getParentClass());
            parentClass.completeClass();
            // get parent's methods and vars
            for (MMethod mMethod : parentClass.mMethodSet) {
                if(findMethodName(mMethod.getName())) continue;
                mMethodSet.add(mMethod);
            }
            for (MVar mVar : parentClass.mVarSet) {
                // if(findVarName(mVar.getName())) continue;
                mVarSet.add(mVar); // we need to add all vars, for sake of Polymorphism.
                // there will be no conflict since we have done setPigletName().
            }
        }
        // set pigletName
        for (MMethod mMethod : mMethodSet) {
            if (mMethod.getPigletName() == null) {
                // only initialize own methods' and vars' pigletName
                mMethod.setPigletName(getName() + "_" + mMethod.getName());
            }
        }
        for (MVar mVar : mVarSet) {
            if (mVar.getPigletName() == null) {
                mVar.setPigletName(getName() + "_" + mVar.getName());
            }
        }
        // completeClass() done.
        setPigletStatus(0);
    }

    public int allocTemp(int currentTemp) {
        // allocTemp() only once.
        if (getPigletStatus() == 1) {
            return currentTemp;
        }
        // make sure parent has done allocTemp()
        MClass parentClass;
        if (getParentClass() != null) {
            parentClass = MClassList.instance.findClass(getParentClass());
            currentTemp = parentClass.allocTemp(currentTemp);
            // has got parent's methods and vars in completeClass()
        }
        // set offset. there's no gap in vars' usedOffset, so using maxUsedOffset is OK.
        int maxUsedOffset = -1;
        int offset;
        for (MVar mVar : mVarSet) {
            // get the max offset by now
            if (mVar.getOffset() != -1) {
                if (mVar.getOffset() > maxUsedOffset) {
                    maxUsedOffset = mVar.getOffset();
                }
            }
        }
        if (maxUsedOffset == -1) {
            // all offsets are -1
            offset = 4;
        } else {
            offset = maxUsedOffset + 4;
        }
        for (MVar mVar : mVarSet) {
            if (mVar.getOffset() == -1) {
                // only initialize own methods' and vars' offset
                mVar.setOffset(offset);
                offset += 4;
            }
        }
        // there's some gap in methods' usedOffset, so cannot just use maxUsedOffset.
        HashSet<Integer> usedOffset = new HashSet<>();
        for (MMethod mMethod : mMethodSet) {
            // get the used offset by now
            if (mMethod.getOffset() != -1) {
                usedOffset.add(mMethod.getOffset());
            }
        }
        offset = 0;
        for (MMethod mMethod : mMethodSet) {
            if (mMethod.getOffset() == -1) {
                // only initialize own methods' and vars' offset
                while (!usedOffset.add(offset)) {
                    offset += 4;
                }
                mMethod.setOffset(offset);
                offset += 4;
                currentTemp = mMethod.allocTemp(currentTemp);
            }
        }
        // allocTemp() done.
        setPigletStatus(1);
        return currentTemp;
    }
}
