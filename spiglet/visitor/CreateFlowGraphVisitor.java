package spiglet.visitor;

import java.util.Enumeration;

import spiglet.syntaxtree.*;
import spiglet.symboltable.Context;
import spiglet.symboltable.FlowGraphPointer;

public class CreateFlowGraphVisitor extends GJDepthFirst<String, FlowGraphPointer> {
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
		if ( n.present() )
			return n.node.accept(this, argu);
		else
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
		argu.setMethod(Context.mMethod.get("MAIN"));
		
		argu.resetVid();
		argu.addEdgeTo(argu.getVid() + 1);
		argu.incVid();

		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return _ret;//null
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

		argu.resetVid();
		String methodName = n.f0.accept(this, argu);
		argu.setMethod(Context.mMethod.get(methodName));

		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return _ret;//null
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

		argu.updateVertex(); //start of new stmt
		n.f0.accept(this, argu);
		argu.incVid();
		return _ret;//null
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
		argu.addEdgeTo(argu.getVid() + 1);
		argu.incVid();

		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return _ret;//null
	}

	/**
	 * f0 -> "NOOP"
	 */
	public String visit(NoOpStmt n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		argu.addEdgeTo(argu.getVid() + 1);
		return _ret;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public String visit(ErrorStmt n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		argu.addEdgeTo(argu.getVid() + 1);
		return _ret;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Temp()
	 * f2 -> Label()
	 */
	public String visit(CJumpStmt n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		// Temp Use
		int tempNo = Integer.parseInt(n.f1.accept(this, argu));
		argu.getUse().add(tempNo);
		int jumpVid = Context.mLabel.get(n.f2.accept(this, argu));
		argu.addEdgeTo(argu.getVid() + 1);
		argu.addEdgeTo(jumpVid);
		return _ret;//null
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public String visit(JumpStmt n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		int jumpVid = Context.mLabel.get(n.f1.accept(this, argu));
		argu.addEdgeTo(jumpVid);
		return _ret;//null
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Temp()
	 * f2 -> IntegerLiteral()
	 * f3 -> Temp()
	 */
	public String visit(HStoreStmt n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		
		argu.getUse().add(Integer.parseInt(n.f1.accept(this, argu)));
		n.f2.accept(this, argu);//don't need its value
		argu.getUse().add(Integer.parseInt(n.f3.accept(this, argu)));
		argu.addEdgeTo(argu.getVid() + 1);
		return _ret;//null
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Temp()
	 * f2 -> Temp()
	 * f3 -> IntegerLiteral()
	 */
	public String visit(HLoadStmt n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);

		argu.getDef().add(Integer.parseInt(n.f1.accept(this, argu)));
		argu.getUse().add(Integer.parseInt(n.f2.accept(this, argu)));
		argu.addEdgeTo(argu.getVid() + 1);

		n.f3.accept(this, argu);
		return _ret;//null
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 */
	public String visit(MoveStmt n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		
		argu.getDef().add(Integer.parseInt(n.f1.accept(this, argu)));
		argu.addEdgeTo(argu.getVid() + 1);
		n.f2.accept(this, argu);
		return _ret;//null
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> SimpleExp()
	 */
	public String visit(PrintStmt n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		argu.addEdgeTo(argu.getVid() + 1);
		return _ret;//null
	}

	/**
	 * f0 -> Call()
	 * | HAllocate()
	 * | BinOp()
	 * | SimpleExp()
	 */
	public String visit(Exp n, FlowGraphPointer argu) {
		String _ret = n.f0.accept(this, argu);
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
		argu.insideMethod = true;
		n.f3.accept(this, argu);
		argu.insideMethod = false;
		n.f4.accept(this, argu);
		return _ret;//null
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> SimpleExp()
	 */
	public String visit(HAllocate n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		_ret = n.f1.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Temp()
	 * f2 -> SimpleExp()
	 */
	public String visit(BinOp n, FlowGraphPointer argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		
		argu.getUse().add(Integer.parseInt(n.f1.accept(this, argu)));
		n.f2.accept(this, argu);
		return _ret;//null
	}

	/**
	 * f0 -> Temp()
	 * | IntegerLiteral()
	 * | Label()
	 */
	public String visit(SimpleExp n, FlowGraphPointer argu) {
		String _ret=null;

		if (n.f0.which == 0) {// Temp Use
			argu.getUse().add(Integer.parseInt(n.f0.accept(this, argu)));
		}
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
		if (argu.insideMethod) {// Temp Use
			argu.getUse().add(tempNo);
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
