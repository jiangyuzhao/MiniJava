package kanga.symboltable;

// import kanga.symboltable.Context;

public class Method {
	public String methodName;
	public int paramNum, stackNum = 0, callParamNum = 0;

	public Method(String methodName, int paramNum) {
		this.methodName = methodName;
		this.paramNum = paramNum;
	}
}
