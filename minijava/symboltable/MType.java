package minijava.symboltable;

public class MType {
    protected String name;
    protected int line = 0;
    protected int column = 0;

    // ---for typecheck, bonus part begins---
    protected int hasInitLength = 0;
    // 0: has initialized, but not sure of value
    // -1: has not initialized
    // 1: has initialized, and sure of value
    protected int length;
    public int getHasInitLength() {
        return hasInitLength;
    }
    public void setHasInitLength(int hasInitLength) {
        this.hasInitLength = hasInitLength;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }

    protected int hasInit = 0;
    // 0: has initialized, but not sure of value
    // -1: has not initialized
    // 1: has initialized, and sure of value
    protected int intValue;
    protected boolean booleanValue;
    public int getHasInit() {
        return hasInit;
    }
    public void setHasInit(int hasInit) {
        this.hasInit = hasInit;
    }
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }
    public int getIntValue() {
        return intValue;
    }
    public boolean getBooleanValue() {
        return booleanValue;
    }
    // ---bonus part ends---

    // ---Piglet part
    protected String pigletName = null; // null: hasn't been initialized
    public void setPigletName(String pigletName) {
        this.pigletName = pigletName;
    }
    public String getPigletName() {
        return pigletName;
    }
    protected int pigletStatus = -1;
    // -1: not completeClass()
    // 0: already completeClass() but not allocTemp()
    // 1: already allocTemp()
    public void setPigletStatus(int pigletStatus) {
        this.pigletStatus = pigletStatus;
    }
    public int getPigletStatus() {
        return pigletStatus;
    }

    protected int tempNum = 0;
    protected int offset = -1; // -1: hasn't been initialized
    public void setOffset(int offset) {
        this.offset = offset;
    }
    public int getOffset() {
        return offset;
    }

    public void setTempNum(int tempNum) {
        this.tempNum = tempNum;
    }

    public int getTempNum() {
        return tempNum;
    }

    public boolean judgeTemp() {
        return tempNum > 0;
    }


    public MType() {
        
    }

    @Override
    public boolean equals(Object obj) {  
        if (!(obj instanceof MType)) {
            return false;
        } else if (!name.equals(((MType)obj).name)) {
            return false;
        } else {
            if (pigletName==null && ((MType)obj).pigletName==null) {
                return true;
            } else if (pigletName!=null && ((MType)obj).pigletName!=null) {
                return pigletName.equals(((MType)obj).name);
            } else {
                return false;
            }
        }
    }  
    @Override
    public int hashCode() {  
        return name.hashCode();  
        // if conflict, check name & pigletName.
    }  

    public MType(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
