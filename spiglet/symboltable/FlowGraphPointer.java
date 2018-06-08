package spiglet.symboltable;

import java.util.HashSet;

// import spiglet.symboltable.Context;

public class FlowGraphPointer {
	private Method currMethod;
	private FlowGraphVertex currVertex;
	private int vid;
	public boolean insideMethod;

	private static FlowGraphPointer instance = null;
	public static FlowGraphPointer getInstance() {
		if (instance == null) {    
            instance = new FlowGraphPointer();  
        }
        return instance;
	}

	private FlowGraphPointer() {//singleton
		vid = 0;
		insideMethod = false;
	}
	public void setMethod(Method method) {
		currMethod = method;
	}
	public Method getMethod() {
		return currMethod;
	}
	public void addVertex() {
		currMethod.flowGraph.addVertex(vid);
	}
	public void updateVertex() {
		currVertex = currMethod.flowGraph.getVertex(vid);
	}
	public HashSet<Integer> getUse() {
		return currVertex.Use;
	}
	public HashSet<Integer> getDef() {
		return currVertex.Def;
	}
	public void resetVid() {
		vid = 0;
	}
	public int getVid() {
		return vid;
	}
	public void incVid() {
		vid ++;
	}
	public void addEdgeTo(int dstId){ // from vid to dstId
		currMethod.flowGraph.addEdge(vid, dstId);
	}
	public void addTempLiveInterval(int tempNo, int begin, int end) {
		currMethod.mTemp.put(tempNo, new LiveInterval(tempNo, begin, end));
	}
}