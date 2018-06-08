package spiglet.visitor;

import java.util.Enumeration;

import spiglet.syntaxtree.*;
import spiglet.symboltable.Context;
import spiglet.symboltable.Method;
import spiglet.symboltable.LiveInterval;
import spiglet.symboltable.FlowGraphPointer;

public class CreateFlowGraphVertexVisitor extends GJDepthFirst<String, FlowGraphPointer> {
	//
	// Auto class visitors--probably don't need to be overridden.
	//
	public String visit(NodeList n, FlowGraphPointer argu) {
		String _ret=null;
		int _count=0;
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
			e.nextElement().accept(this, argu);
			_count++;
		}
		return _ret;
	}

	public String visit(NodeListOptional n, FlowGraphPointer argu) {
		if ( n.present() ) {
			String _ret=null;
			int _count=0;
			for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
				e.nextElement().accept(this, argu);
				_count++;
			}
			return _ret;
		}
		else
			return null;
	}
	public String visit(NodeOptional n, FlowGraphPointer argu) {
		/*if ( n.present() )
			return n.node.accept(this, argu);
		else
			return null;*/
		if ( n.present() ) {// get labels
			Context.mLabel.put(n.node.accept(this, argu), argu.getVid());
		}
		return null;
	}

	public String visit(NodeSequence n, FlowGraphPointer argu) {
		String _ret=null;
		int _count=0;
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
			e.nextElement().accept(this, argu);
			_count++;
		}
		return _ret;
	}

	public String visit(NodeToken n, FlowGraphPointer argu) { return null; }

	//
    // User-generated visitor methods below
    //

	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public String visit(Goal n, FlowGraphPointer argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		argu.setMethod(new Method("MAIN", 0));
		Context.mMethod.put("MAIN", argu.getMethod());
		argu.resetVid();
		
		argu.addVertex();
		argu.incVid();
		n.f1.accept(this, argu);
		
		n.f2.accept(this, argu);

		argu.addVertex();
		n.f3.accept(this, argu);

		n.f4.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> ( ( Label() )? Stmt() )*
	 */
	public String visit(StmtList n, FlowGraphPointer argu) {
	    String _ret=null;
	    n.f0.accept(this, argu);
	    return _ret;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public String visit(Procedure n, FlowGraphPointer argu) {
		String _ret=null;
		String methodName = n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		argu.resetVid();
		
		int paramNum = Integer.parseInt(n.f2.accept(this, argu));
		argu.setMethod(new Method(methodName, paramNum));
		Context.mMethod.put(methodName, argu.getMethod());

		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> NoOpStmt()
	 * | ErrorStmt()
	 * | CJumpStmt()
	 * | JumpStmt()
	 * | HStoreStmt()
	 * | HLoadStmt()
	 * | MoveStmt()
	 * | PrintStmt()
	 */
	public String visit(Stmt n, FlowGraphPointer argu) {
		String _ret=null;
		// Every Statement -> Vertex
		argu.addVertex();
		n.f0.accept(this, argu);
		argu.incVid();
		return _ret;
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 * f2 -> "("
	 * f3 -> ( Temp() )*
	 * f4 -> ")"
	 */
	public String visit(Call n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		argu.getMethod().flowGraph.callPos.add(argu.getVid());
		// callParamNum uses the MAX
		if (argu.getMethod().callParamNum < n.f3.size())
			argu.getMethod().callParamNum = n.f3.size();
		n.f4.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> SimpleExp()
	 * f4 -> "END"
	 */
	public String visit(StmtExp n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		argu.addVertex();
		argu.incVid();

		n.f1.accept(this, argu);
		
		n.f2.accept(this, argu);
		argu.addVertex();
		argu.incVid();

		n.f3.accept(this, argu);

		n.f4.accept(this, argu);
		argu.addVertex();
		return _ret;//null
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public String visit(Temp n, FlowGraphPointer argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Integer tempNo = Integer.parseInt(n.f1.accept(this, argu));
		if (!argu.getMethod().mTemp.containsKey(tempNo)) {
			if (tempNo < argu.getMethod().paramNum) {// parameter
				argu.addTempLiveInterval(tempNo, 0, argu.getVid());
			} else {// local Temp (first shows up at vid)
				argu.addTempLiveInterval(tempNo, argu.getVid(), argu.getVid());
			}
		}
		_ret = tempNo.toString();
		return _ret;
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public String visit(IntegerLiteral n, FlowGraphPointer argu) {
		String _ret = n.f0.tokenImage;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public String visit(Label n, FlowGraphPointer argu) {
		String _ret = n.f0.tokenImage;
		n.f0.accept(this, argu);
		return _ret;
	}

}
