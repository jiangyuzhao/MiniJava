package minijava.symboltable;

import java.util.ArrayList;

public class MCallList extends MType {
    protected MMethod callerMethod;
    protected MMethod contextMethod;
    protected ArrayList<MVar> mVarList;

    public MCallList(MMethod callerMethod,MMethod contextMethod) {
        this.callerMethod = callerMethod;
        this.contextMethod = contextMethod;
        mVarList = new ArrayList<MVar>();
    }

    public ArrayList<MVar> getmVarList() {
        return mVarList;
    }

    public MMethod getContextMethod() {
        return contextMethod;
    }
}
