package kanga.visitor;

import java.util.Enumeration;

import kanga.syntaxtree.*;
import kanga.symboltable.Method;
import kanga.symboltable.Context;

public class Kanga2MIPSVisitor extends GJDepthFirst<String, Method> {
	//
	// Auto class visitors--probably don't need to be overridden.
	//
	public String visit(NodeList n, Method argu) {
		String _ret=null;
		int _count=0;
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
			e.nextElement().accept(this, argu);
			_count++;
		}
		return _ret;
	}

	public String visit(NodeListOptional n, Method argu) {
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

	public String visit(NodeOptional n, Method argu) {
		/*if ( n.present() )
			return n.node.accept(this, argu);
		else
			return null;*/
		if ( n.present() ) // print label
			Context.out.append(n.node.accept(this, argu) + ":");
		return null;
	}

	public String visit(NodeSequence n, Method argu) {
		String _ret=null;
		int _count=0;
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
			e.nextElement().accept(this, argu);
			_count++;
		}
		return _ret;
	}

	public String visit(NodeToken n, Method argu) { return null; }

	//
	// User-generated visitor methods below
	//

	/**
	 * f0 -> "MAIN"
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> "["
	 * f5 -> IntegerLiteral()
	 * f6 -> "]"
	 * f7 -> "["
	 * f8 -> IntegerLiteral()
	 * f9 -> "]"
	 * f10 -> StmtList()
	 * f11 -> "END"
	 * f12 -> ( Procedure() )*
	 * f13 -> <EOF>
	 */
	public String visit(Goal n, Method argu) {
		String _ret=null;

		n.f0.accept(this, argu);
		String methodName = "main";
		n.f1.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f6.accept(this, argu);
		n.f7.accept(this, argu);
		n.f9.accept(this, argu);

		int paramNum = Integer.parseInt(n.f2.accept(this, argu));
		paramNum = paramNum > 4 ? paramNum - 4 : 0;
		// 4 params using registers
		int callParamNum = Integer.parseInt(n.f8.accept(this, argu));
		callParamNum = callParamNum > 4 ? callParamNum - 4 : 0;
		int stackNum = Integer.parseInt(n.f5.accept(this, argu));
		stackNum = stackNum - paramNum + callParamNum + 2;

		Method currMethod = new Method(methodName, paramNum);
		currMethod.callParamNum = callParamNum;
		currMethod.stackNum = stackNum;
		// begin
		Context.out.append("\t.text\n");
		Context.out.append("\t.globl " + currMethod.methodName + "\n");
		Context.out.append(currMethod.methodName + ":\n");
		// new frame
		Context.out.append("\tsw $fp, -8($sp)\n");
		Context.out.append("\tsw $ra, -4($sp)\n");
		Context.out.append("\tmove $fp, $sp\n");
		Context.out.append("\tsubu $sp, $sp, " + (4 * currMethod.stackNum) + "\n");

		n.f10.accept(this, currMethod);//inside main
		n.f11.accept(this, currMethod);//inside main

		// recover frame
		Context.out.append("\tlw $ra, -4($fp)\n");
		Context.out.append("\tlw $fp, -8($fp)\n");
		Context.out.append("\taddu $sp, $sp, " + (4 * currMethod.stackNum) + "\n");
		Context.out.append("\tj $ra\n");
		Context.out.append("\n");

		n.f12.accept(this, argu);

		// _halloc:
		Context.out.append("\t.text\n");
		Context.out.append("\t.globl _halloc\n");
		Context.out.append("_halloc:\n");
		Context.out.append("\tli $v0, 9\n");
		Context.out.append("\tsyscall\n");
		Context.out.append("\tj $ra\n");
		Context.out.append("\n");
		// _print:
		Context.out.append("\t.text\n");
		Context.out.append("\t.globl _print\n");
		Context.out.append("_print:\n");// _print:
		Context.out.append("\tli $v0, 1\n");
		Context.out.append("\tsyscall\n");
		Context.out.append("\tla $a0, newl\n");
		Context.out.append("\tli $v0, 4\n");
		Context.out.append("\tsyscall\n");
		Context.out.append("\tj $ra\n");
		Context.out.append("\n");
		// newl:
		Context.out.append("\t.data\n");
		Context.out.append("\t.align 0\n");
		Context.out.append("newl:\n");// newl:
		Context.out.append("\t.asciiz \"\\n\"\n");
		// str_er:
		Context.out.append("\t.data\n");
		Context.out.append("\t.align 0\n");
		Context.out.append("str_er:\n");// str_er:
		Context.out.append("\t.asciiz \" ERROR: abnormal termination\\n\"\n");

		n.f13.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> "["
	 * f5 -> IntegerLiteral()
	 * f6 -> "]"
	 * f7 -> "["
	 * f8 -> IntegerLiteral()
	 * f9 -> "]"
	 * f10 -> StmtList()
	 * f11 -> "END"
	 */
	public String visit(Procedure n, Method argu) {
		String _ret=null;

		String methodName = n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f6.accept(this, argu);
		n.f7.accept(this, argu);
		n.f9.accept(this, argu);
		
		int paramNum = Integer.parseInt(n.f2.accept(this, argu));
		paramNum = paramNum > 4 ? paramNum - 4 : 0;
		// 4 params using registers
		int callParamNum = Integer.parseInt(n.f8.accept(this, argu));
		callParamNum = callParamNum > 4 ? callParamNum - 4 : 0;
		int stackNum = Integer.parseInt(n.f5.accept(this, argu));
		stackNum = stackNum - paramNum + callParamNum + 2;

		Method currMethod = new Method(methodName, paramNum);
		currMethod.callParamNum = callParamNum;
		currMethod.stackNum = stackNum;
		// begin
		Context.out.append("\t.text\n");
		Context.out.append("\t.globl " + currMethod.methodName + "\n");
		Context.out.append(currMethod.methodName + ":\n");
		// new frame
		Context.out.append("\tsw $fp, -8($sp)\n");
		Context.out.append("\tsw $ra, -4($sp)\n");
		Context.out.append("\tmove $fp, $sp\n");
		Context.out.append("\tsubu $sp, $sp, " + (4 * currMethod.stackNum) + "\n");

		n.f10.accept(this, currMethod);//inside method

		// recover frame
		Context.out.append("\tlw $ra, -4($fp)\n");
		Context.out.append("\tlw $fp, -8($fp)\n");
		Context.out.append("\taddu $sp, $sp, " + (4 * currMethod.stackNum) + "\n");
		Context.out.append("\tj $ra\n");
		Context.out.append("\n");

		n.f11.accept(this, currMethod);//inside method
		return _ret;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public String visit(NoOpStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tnop\n");
		return _ret;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Reg()
	 * f2 -> Label()
	 */
	public String visit(CJumpStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tbeqz $" + n.f1.accept(this, argu) + ", "
			+ n.f2.accept(this, argu) + "\n");
		return _ret;
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public String visit(JumpStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tb " + n.f1.accept(this, argu) + "\n");
		return _ret;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Reg()
	 * f2 -> IntegerLiteral()
	 * f3 -> Reg()
	 */
	public String visit(HStoreStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tsw $" + n.f3.accept(this, argu) + ", " //regFrom
			+ n.f2.accept(this, argu) //offset
			+ "($" + n.f1.accept(this, argu) + ")" + "\n");//regTo
		return _ret;
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Reg()
	 * f2 -> Reg()
	 * f3 -> IntegerLiteral()
	 */
	public String visit(HLoadStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tlw $" + n.f1.accept(this, argu) + ", "//regTo
			+ n.f3.accept(this, argu) //offset
			+ "($" + n.f2.accept(this, argu) + ")" + "\n"); //regFrom
		return _ret;
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Reg()
	 * f2 -> Exp()
	 */
	public String visit(MoveStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tmove $" + n.f1.accept(this, argu)//regTo
			+ ", $" + n.f2.accept(this, argu) + "\n");//regFrom
		return _ret;
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> SimpleExp()
	 */
	public String visit(PrintStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tmove $a0, $" + n.f1.accept(this, argu) + "\n");
		Context.out.append("\tjal _print\n");
		return _ret;
	}

	/**
	 * f0 -> "ALOAD"
	 * f1 -> Reg()
	 * f2 -> SpilledArg()
	 */
	public String visit(ALoadStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tlw $" + n.f1.accept(this, argu) + ", " //regTo
			+ n.f2.accept(this, argu) + "\n");//spilled
		return _ret;
	}

	/**
	 * f0 -> "ASTORE"
	 * f1 -> SpilledArg()
	 * f2 -> Reg()
	 */
	public String visit(AStoreStmt n, Method argu) {
		String _ret = null;
		n.f0.accept(this, argu);

		Context.out.append("\tsw $" + n.f2.accept(this, argu) + ", "//regFrom
			+ n.f1.accept(this, argu) + "\n");//spilled
		return _ret;
	}

	/**
	 * f0 -> "PASSARG"
	 * f1 -> IntegerLiteral()
	 * f2 -> Reg()
	 */
	public String visit(PassArgStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		int offset = Integer.parseInt(n.f1.accept(this, argu)) - 1;//ATTENTION: -=1
		Context.out.append("\tsw $" + n.f2.accept(this, argu) + ", "//regFrom
			+ 4 * offset
			+ "($sp)" + "\n");
		return _ret;
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 */
	public String visit(CallStmt n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tjalr $" + n.f1.accept(this, argu) + "\n");
		return _ret;
	}

	/**
	 * f0 -> HAllocate()
	 * | BinOp()
	 * | SimpleExp()
	 */
	public String visit(Exp n, Method argu) {
		String _ret = n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> SimpleExp()
	 */
	public String visit(HAllocate n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\tmove $a0, $" + n.f1.accept(this, argu) + "\n");//it's a register
		Context.out.append("\tjal _halloc\n");
		return "v0";
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Reg()
	 * f2 -> SimpleExp()
	 */
	public String visit(BinOp n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		Context.out.append("\t" + n.f0.accept(this, argu)//operator
			+ " $v1, $" + n.f1.accept(this, argu)
			+ ", $" + n.f2.accept(this, argu) + "\n");
		return "v1";
	}

	/**
	 * f0 -> "LT"
	 * | "PLUS"
	 * | "MINUS"
	 * | "TIMES"
	 */
	public String visit(Operator n, Method argu) {
		String _ret=null;
		n.f0.accept(this, argu);

		switch (n.f0.which) {
			case 0: _ret = "slt"; break;
			case 1: _ret = "add"; break;
			case 2: _ret = "sub"; break;
			case 3: _ret = "mul"; break;
			// default: System.out.println("Operator ERROR");
		}
		return _ret;
	}

	/**
	 * f0 -> "SPILLEDARG"
	 * f1 -> IntegerLiteral()
	 */
	public String visit(SpilledArg n, Method argu) {
		String _ret=null;
		int idx = Integer.parseInt(n.f1.accept(this, argu));
		// SpilledArg starts from 0

		if (idx >= argu.paramNum) {//only use argu here.
			// is not parameter
			// is spilled register/saved register
			idx = argu.paramNum - idx - 3;// below $fp [$ra] [$fp]
		}

		_ret = 4 * idx + "($fp)";
		return _ret;
	}

	/**
	 * f0 -> Reg()
	 * | IntegerLiteral()
	 * | Label()
	 */
	public String visit(SimpleExp n, Method argu) {
		String _ret=null;

		String simpleExp = n.f0.accept(this, argu);
		if (n.f0.which == 0) {
			_ret = simpleExp;
		} else {
			_ret = "v1";
			if (n.f0.which == 1) {
				Context.out.append("\tli $v1, " + simpleExp + "\n");
			} else {
				Context.out.append("\tla $v1, " + simpleExp + "\n");
			}
		}
		return _ret;
	}

	/**
	 * f0 -> "a0"
	 * | "a1"
	 * | "a2"
	 * | "a3"
	 * | "t0"
	 * | "t1"
	 * | "t2"
	 * | "t3"
	 * | "t4"
	 * | "t5"
	 * | "t6"
	 * | "t7"
	 * | "s0"
	 * | "s1"
	 * | "s2"
	 * | "s3"
	 * | "s4"
	 * | "s5"
	 * | "s6"
	 * | "s7"
	 * | "t8"
	 * | "t9"
	 * | "v0"
	 * | "v1"
	 */
	public String visit(Reg n, Method argu) {
		String _ret = ((NodeToken)n.f0.choice).tokenImage;
		return _ret;
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public String visit(IntegerLiteral n, Method argu) {
		String _ret = n.f0.tokenImage;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public String visit(Label n, Method argu) {
		String _ret = n.f0.tokenImage;
		n.f0.accept(this, argu);
		return _ret;
	}

}
