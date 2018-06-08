package minijava.symboltable;

import minijava.typecheck.ErrorPrinter;

import java.util.HashSet;

public class MClassList extends MType {
    public static MClassList instance = new MClassList();

    private HashSet<MClass> mClassSet = new HashSet<MClass>();
    private static int tempNumber = 20;
    private static int labelNumber = 2;

    private MClassList(){
        mClassSet.add(new MClass("int",0,0));
        mClassSet.add(new MClass("int[]",0,0));
        mClassSet.add(new MClass("boolean",0,0));
    }

    public void setLabelNumber(int labelNumber) {
        MClassList.labelNumber = labelNumber;
    }

    public int getLabelNumber() {
        return labelNumber;
    }

    public int getTempNum() {
        return this.tempNumber;
    }

    public void setTempNum(int tempNum) {
        this.tempNumber = tempNum;
    }


    //insert a new class in the list
    public boolean addClass(MClass nClass) {
        if (!mClassSet.add(nClass)) {
            ErrorPrinter.instance.printError(nClass.getLine(),nClass.getColumn(),"Class " + nClass.getName() + " repeated declared");
            return false;
        } else {
            return true;
        }
    }

    //search a class according to name
    public MClass findClass(String name) {
        for (MClass knownClass:mClassSet) {
            if (name.equals(knownClass.getName())) {
                return knownClass;
            }
        }
        return null;
    }

    //judge whether class b is the father class of class a
    public boolean judgeParentClass(String a,String b) {
        MClass aa = MClassList.instance.findClass(a);
        while (aa != null) {
            if (aa.getName().equals(b)) {
                return true;
            }
            String parent = aa.getParentClass();
            aa = MClassList.instance.findClass(parent);
        }
        return false;
    }

    public void completeClass() {
        for (MClass mClass : mClassSet) {
            mClass.completeClass();
        }
    }

    public void allocTemp(int currentTemp) {
        for (MClass mClass : mClassSet) {
            currentTemp =  mClass.allocTemp(currentTemp);
        }
    }
}
