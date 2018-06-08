package piglet.visitor;

import piglet.symboltable.MSpiglet;
import piglet.syntaxtree.*;

import java.util.Enumeration;
import java.util.Queue;
import java.util.LinkedList;

public class Piglet2SpigletVisitor extends GJNoArguDepthFirst<MSpiglet> {
    private int tempNum;

    private Queue<String> recycledTempNum = new LinkedList<>();

    public Piglet2SpigletVisitor(int num) {
        tempNum = num;
    }

    public String getNextTemp() {
        if (!recycledTempNum.isEmpty()) {
            return recycledTempNum.remove();
        }
        return "TEMP " + (++tempNum);
    }
    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public MSpiglet visit(NodeList n) {
        MSpiglet _ret = new MSpiglet("");
        int _count=0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            //Attention, e.nextElement().accept(this) can be null!
            MSpiglet r1 = e.nextElement().accept(this);
            if (r1 != null)
                _ret.appendCode(r1);
            _count++;
        }
        return _ret;
    }

    public MSpiglet visit(NodeListOptional n) {
        if (n.present()) {
            MSpiglet _ret = new MSpiglet("");
            int _count=0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                //Attention, e.nextElement().accept(this) can be null!
                MSpiglet r1 = e.nextElement().accept(this);
                if (r1 != null)
                    _ret.appendCode(r1);
                _count++;

                if (r1 != null && r1.getTemp() != null)
                    _ret.addTemp(r1.getTemp());
            }
            return _ret;
        }
        else
            return null;
    }

    public MSpiglet visit(NodeOptional n) {
        if (n.present()) {
            if (n.node instanceof Label) {
                return new MSpiglet(((Label) n.node).f0.tokenImage);
                //TODO: for label like L2, you may not put a new line here.
            }
            return n.node.accept(this);
        }
        else
            return null;
    }

    public MSpiglet visit(NodeSequence n) {
        MSpiglet _ret = new MSpiglet("");
        int _count=0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            //Attention, e.nextElement().accept(this) can be null!
            MSpiglet r1 = e.nextElement().accept(this);
            if (r1 != null)
                _ret.appendCode(r1);
            _count++;
        }
        return _ret;
    }

    public MSpiglet visit(NodeToken n) { return null; }

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
    public MSpiglet visit(Goal n) {
        MSpiglet _ret = new MSpiglet("MAIN");
        n.f0.accept(this);
        _ret.appendCode(n.f1.accept(this));
        n.f2.accept(this);
        _ret.appendCode(new MSpiglet("END"));
        _ret.appendCode(n.f3.accept(this));
        n.f4.accept(this);
        return _ret;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
     */
    public MSpiglet visit(StmtList n) {
        MSpiglet _ret = n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> Label()
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> StmtExp()
     */
    public MSpiglet visit(Procedure n) {
        MSpiglet _ret = new MSpiglet("");
        _ret.appendCode(new MSpiglet(n.f0.f0.tokenImage + " [" + n.f2.f0.tokenImage + "]"));
        _ret.appendCode(new MSpiglet("BEGIN"));
        n.f0.accept(this);
        n.f1.accept(this);
        MSpiglet integerLiteral = n.f2.accept(this);
        recycledTempNum.add(integerLiteral.getTemp()); //recycle temp

        n.f3.accept(this);
        MSpiglet exp = n.f4.accept(this);
        if (!exp.isSimpleExp()) {
            _ret.appendCode(exp);
            _ret.appendCode(new MSpiglet("RETURN " + exp.getTemp()));
        } else {
            _ret.appendCode(new MSpiglet("RETURN " + exp.getSimpleExp()));
            if (!exp.isTemp()) {
                recycledTempNum.add(exp.getTemp()); //recycle temp
            }
        }
        _ret.appendCode(new MSpiglet("END"));
        return _ret;
    }

    /**
     * f0 -> NoOpStmt()
     *       | ErrorStmt()
     *       | CJumpStmt()
     *       | JumpStmt()
     *       | HStoreStmt()
     *       | HLoadStmt()
     *       | MoveStmt()
     *       | PrintStmt()
     */
    public MSpiglet visit(Stmt n) {
        MSpiglet _ret = n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "NOOP"
     */
    public MSpiglet visit(NoOpStmt n) {
        MSpiglet _ret = new MSpiglet("NOOP");
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "ERROR"
     */
    public MSpiglet visit(ErrorStmt n) {
        MSpiglet _ret = new MSpiglet("ERROR");
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Exp()
     * f2 -> Label()
     */
    public MSpiglet visit(CJumpStmt n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp = n.f1.accept(this);
        MSpiglet label = n.f2.accept(this);
        recycledTempNum.add(label.getTemp()); //recycle temp
        
        _ret.appendCode(exp);
        _ret.appendCode(new MSpiglet("CJUMP " + exp.getTemp() + " " + n.f2.f0.tokenImage));
        return _ret;
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    public MSpiglet visit(JumpStmt n) {
        MSpiglet _ret = new MSpiglet("JUMP " + n.f1.f0.tokenImage);
        n.f0.accept(this);
        MSpiglet label = n.f1.accept(this);
        recycledTempNum.add(label.getTemp()); //recycle temp
        return _ret;
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Exp()
     * f2 -> IntegerLiteral()
     * f3 -> Exp()
     */
    public MSpiglet visit(HStoreStmt n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp1 = n.f1.accept(this);
        MSpiglet integerLiteral = n.f2.accept(this);
        recycledTempNum.add(integerLiteral.getTemp()); //recycle temp

        MSpiglet exp2 = n.f3.accept(this);
        _ret.appendCode(exp1);
        _ret.appendCode(exp2);
        _ret.appendCode(new MSpiglet("HSTORE " + exp1.getTemp() + " " + n.f2.f0.tokenImage + " " + exp2.getTemp()));
        return _ret;
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Temp()
     * f2 -> Exp()
     * f3 -> IntegerLiteral()
     */
    public MSpiglet visit(HLoadStmt n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp1 = n.f1.accept(this);
        MSpiglet exp2 = n.f2.accept(this);
        MSpiglet integerLiteral = n.f3.accept(this);
        recycledTempNum.add(integerLiteral.getTemp()); //recycle temp

        _ret.appendCode(exp1);
        _ret.appendCode(exp2);
        _ret.appendCode(new MSpiglet("HLOAD " + exp1.getTemp() + " " + exp2.getTemp() + " " + n.f3.f0.tokenImage));
        return _ret;
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Temp()
     * f2 -> Exp()
     */
    public MSpiglet visit(MoveStmt n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp1 = n.f1.accept(this);
        MSpiglet exp2 = n.f2.accept(this);
        _ret.appendCode(exp1);
        if (!exp2.isExp()) {
            _ret.appendCode(exp2);
            _ret.appendCode(new MSpiglet("MOVE " + exp1.getTemp() + " " + exp2.getTemp()));
        } else {
            _ret.appendCode(new MSpiglet(exp2.getExpStmt()
                + "MOVE " + exp1.getTemp() + " " + exp2.getExp()));
            /*
            exp2.getCode == "MOVE TEMPNUM2 Exp1 \n MOVE TEMPNUM3 Exp2 \n OP TEMPNUM2 TEMPNUM3"
            exp2.getExp() == "MOVE TEMPNUM2 Exp1 \n OP TEMPNUM2 Exp2";
            now if we appendCode, it = "MOVE TEMPNUM1 MOVE TEMPNUM2 Exp1 \n OP TEMPNUM2 Exp2"
            it should be: "MOVE TEMPNUM2 Exp1 \n MOVE TEMPNUM1 OP TEMPNUM2 Exp2"
            */
            if (!exp2.isTemp()) {
                recycledTempNum.add(exp2.getTemp()); //recycle temp
            }
        }
        return _ret;
    }

    /**
     * f0 -> "PRINT"
     * f1 -> Exp()
     */
    public MSpiglet visit(PrintStmt n) {
        n.f0.accept(this);
        MSpiglet _ret = new MSpiglet("");
        MSpiglet exp = n.f1.accept(this);
        if (!exp.isSimpleExp()) {
            _ret.appendCode(exp);
            _ret.appendCode(new MSpiglet("PRINT " + exp.getTemp()));
        } else {
            _ret.appendCode(new MSpiglet("PRINT " + exp.getSimpleExp()));
            if (!exp.isTemp()) {
                recycledTempNum.add(exp.getTemp()); //recycle temp
            }
        }
        return _ret;
    }

    /**
     * f0 -> StmtExp()
     *       | Call()
     *       | HAllocate()
     *       | BinOp()
     *       | Temp()
     *       | IntegerLiteral()
     *       | Label()
     */
    public MSpiglet visit(Exp n) {
        MSpiglet _ret = n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "BEGIN"
     * f1 -> StmtList()
     * f2 -> "RETURN"
     * f3 -> Exp()
     * f4 -> "END"
     */
    public MSpiglet visit(StmtExp n) {
        n.f0.accept(this);
        MSpiglet _ret = new MSpiglet("");
        _ret.appendCode(n.f1.accept(this));        
        n.f2.accept(this);
        MSpiglet exp = n.f3.accept(this);
        n.f4.accept(this);
        _ret.appendCode(exp); //TODO: has been ensured to be tempNum?
        _ret.setTemp(exp.getTemp());
        // use Temp, not exp.
        // or you should let getExp() += n.f1.accept(this) first.
        return _ret;
    }

    /**
     * f0 -> "CALL"
     * f1 -> Exp()
     * f2 -> "("
     * f3 -> ( Exp() )*
     * f4 -> ")"
     */
    public MSpiglet visit(Call n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp1 = n.f1.accept(this);
        n.f2.accept(this);
        MSpiglet exp2 = n.f3.accept(this);
        n.f4.accept(this);

        String name = getNextTemp();
        String callCode;
        if (!exp1.isSimpleExp()) {
            _ret.appendCode(exp1);
            callCode = "CALL " + exp1.getTemp() + "( ";
        } else {
            callCode = "CALL " + exp1.getSimpleExp() + "( ";
            if (!exp1.isTemp()) {
                recycledTempNum.add(exp1.getTemp()); //recycle temp
            }
        }
        _ret.appendCode(exp2);
        for(String temp : exp2.getTempList()) {
            callCode += temp + " ";
        }
        callCode += ')';
        
        _ret.appendCode(new MSpiglet("MOVE " + name + " " + callCode));
        _ret.setTemp(name);
        if (!exp1.isSimpleExp()) {
            // use Temp, not exp
        } else {
            _ret.setExpStmt(exp2.getCode().toString() + "\n");
            _ret.setExp(callCode);
        }
        return _ret;
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> Exp()
     */
    public MSpiglet visit(HAllocate n) {
        MSpiglet exp = n.f1.accept(this);
        MSpiglet _ret = new MSpiglet("");
        if (!exp.isSimpleExp()) {
            _ret.appendCode(exp);
            String name = getNextTemp();
            _ret.appendCode(new MSpiglet("MOVE " + name + " HALLOCATE " + exp.getTemp()));
            _ret.setTemp(name);
            // use Temp, not exp
        } else {
            String name = getNextTemp();
            _ret.appendCode(new MSpiglet("MOVE " + name + " HALLOCATE " + exp.getSimpleExp()));
            _ret.setTemp(name);
            _ret.setExpStmt("");
            _ret.setExp("HALLOCATE " + exp.getSimpleExp());
            if (!exp.isTemp()) {
                recycledTempNum.add(exp.getTemp()); //recycle temp
            }
        }
        return _ret;
    }

    /**
     * f0 -> Operator()
     * f1 -> Exp()
     * f2 -> Exp()
     */
    public MSpiglet visit(BinOp n) {
        MSpiglet _ret = new MSpiglet("");
        MSpiglet op = n.f0.accept(this);
        MSpiglet exp1 = n.f1.accept(this);
        MSpiglet exp2 = n.f2.accept(this);
        _ret.appendCode(exp1);
        if (!exp2.isSimpleExp()) {
            _ret.appendCode(exp2);
            String name = getNextTemp();
            _ret.appendCode(new MSpiglet("MOVE " + name + " " + op.getOp() + " " + exp1.getTemp() + " " + exp2.getTemp()));
            _ret.setTemp(name);
        } else {
            String name = getNextTemp();
            _ret.appendCode(new MSpiglet("MOVE " + name + " " + op.getOp() + " " + exp1.getTemp() + " " + exp2.getSimpleExp()));
            _ret.setTemp(name);
            _ret.setExpStmt(exp1.getCode() + "\n");
            _ret.setExp(op.getOp() + " " + exp1.getTemp() + " " + exp2.getSimpleExp());
            if (!exp2.isTemp()) {
                recycledTempNum.add(exp2.getTemp()); //recycle temp
            }
        }
        return _ret;
    }

    /**
     * f0 -> "LT"
     *       | "PLUS"
     *       | "MINUS"
     *       | "TIMES"
     */
    public MSpiglet visit(Operator n) {
        MSpiglet _ret = new MSpiglet("");
        _ret.setOpType(n.f0.which);
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    public MSpiglet visit(Temp n) {
        MSpiglet _ret = new MSpiglet("");
        String name = "TEMP " + n.f1.f0.tokenImage;
        _ret.setTemp(name);
        _ret.setSimpleExp(name);
        _ret.setExpStmt("");
        _ret.setExp(name);
        n.f0.accept(this);
        MSpiglet integerLiteral = n.f1.accept(this);
        recycledTempNum.add(integerLiteral.getTemp()); //recycle temp
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public MSpiglet visit(IntegerLiteral n) {
        String name = getNextTemp();
        MSpiglet _ret = new MSpiglet("MOVE " + name + " " + n.f0.tokenImage);
        _ret.setTemp(name);
        _ret.setSimpleExp(n.f0.tokenImage);
        _ret.setExpStmt("");
        _ret.setExp(n.f0.tokenImage);
        // most of the time, its TEMPNUM can be recycled later.
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public MSpiglet visit(Label n) {
        String name = getNextTemp();
        MSpiglet _ret = new MSpiglet("MOVE " + name + " " + n.f0.tokenImage);
        _ret.setTemp(name);
        // neither a label nor a method needs a TEMPNUM
        _ret.setSimpleExp(n.f0.tokenImage);
        _ret.setExpStmt("");
        _ret.setExp(n.f0.tokenImage);
        n.f0.accept(this);
        return _ret;
    }
}
