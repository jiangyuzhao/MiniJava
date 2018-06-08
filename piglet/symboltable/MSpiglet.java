package piglet.symboltable;

import java.util.ArrayList;

public class MSpiglet {
    private StringBuilder code;
    private String temp;
    private int opType;
    private ArrayList<String> tempList = new ArrayList<String>();
    private String simpleExp = null;
    private String expStmt = null;
    private String exp = null;

    public StringBuilder getCode() {
        return code;
    }

    public MSpiglet(String code) {
        this.code = new StringBuilder(code);
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp() {
        return temp;
    }

    public ArrayList<String> getTempList() {
        return tempList;
    }

    public void addTemp(String temp) {
        tempList.add(temp);
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public String getOp() {
        if(opType == 0) return "LT";
        else if(opType == 1) return "PLUS";
        else if(opType == 2) return "MINUS";
        else return "TIMES";
    }

    public void setSimpleExp(String simpleExp) {
        this.simpleExp = simpleExp;
    }
    public String getSimpleExp() {
        return simpleExp;
    }
    public boolean isSimpleExp() {
        return simpleExp != null;
    }

    public void setExpStmt(String expStmt) {
        this.expStmt = expStmt;
    }
    public void setExp(String exp) {
        this.exp = exp;
    }
    public String getExpStmt() {
        return expStmt;
    }
    public String getExp() {
        return exp;
    }
    public boolean isExp() {
        return exp != null;
    }
    public boolean isTemp() {
        return isSimpleExp() && simpleExp.equals(temp);
    }

    public void appendCode(MSpiglet mSpiglet) {
        if(mSpiglet == null) return;
        else if(mSpiglet.getCode().length() == 0) return; //empty exp
        // cannot use equals("") here.
        if (code.length() != 0) {//If not first line
            code.append("\n");
        }
        code.append(mSpiglet.getCode());
    }
}
