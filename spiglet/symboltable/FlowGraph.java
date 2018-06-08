package spiglet.symboltable;

import java.util.Vector;
import java.util.HashSet;
import java.util.HashMap;

public class FlowGraph {
	public HashMap<Integer, FlowGraphVertex> mVertex = new HashMap<Integer, FlowGraphVertex>();;
	public HashSet<Integer> callPos = new HashSet<Integer>();

	public FlowGraphVertex getVertex(int vid) {
		return mVertex.get(vid);
	}

	public void addVertex(int vid) {
		FlowGraphVertex v = new FlowGraphVertex(vid);
		mVertex.put(vid, v);
	}

	public void addEdge(int src_id, int dst_id) {
		FlowGraphVertex src = mVertex.get(src_id);
		FlowGraphVertex dst = mVertex.get(dst_id);
		src.Succ.add(dst);
		dst.Pred.add(src);
	}

}
