package minijava.symboltable;

public class MPiglet {
    protected StringBuilder code;
    protected MClass mClass;
    protected MVar mVar;

    public StringBuilder getCode() {
        return code;
    }

    public MClass getmClass() {
        return mClass;
    }

    public void setmClass(MClass mClass) {
        this.mClass = mClass;
    }

    public MVar getmVar() {
        return mVar;
    }

    public void setmVar(MVar mVar) {
        this.mVar = mVar;
    }
    
    public MPiglet(String s) {
        code = new StringBuilder(s);
    }

    public void appendString(String s) {
        code.append(s);
    }

    public void appendCode(MPiglet mPiglet) {
        if(mPiglet == null) return;
        code.append("\n");
        code.append(mPiglet.getCode());
    }

    public void appendCode(MPiglet mPiglet, boolean noSpace) {
        if(mPiglet == null) return;
        if (!noSpace) {
            code.append(" ");
        }
        code.append(mPiglet.getCode());
    }
}
