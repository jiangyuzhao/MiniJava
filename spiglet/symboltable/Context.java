package spiglet.symboltable;

import java.util.HashMap;
import java.io.PrintStream;

public class Context {
	public static HashMap<String, Method> mMethod;
    public static HashMap<String, Integer> mLabel;

    public static HashMap<String, Integer> mGlobalLabel; //methodName_label -> globalLabelNo
    public static int globalLabelNo;
    
    public static StringBuilder out;

    public Context(StringBuilder out) {
    	mMethod = new HashMap<String, Method>();
    	mLabel = new HashMap<String, Integer>();
    	mGlobalLabel = new HashMap<String, Integer>();
        globalLabelNo = 2;
    	this.out = out;
    }
}