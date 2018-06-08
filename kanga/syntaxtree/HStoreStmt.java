//
// Generated by JTB 1.3.2
//

package kanga.syntaxtree;

/**
 * Grammar production:
 * f0 -> "HSTORE"
 * f1 -> Reg()
 * f2 -> IntegerLiteral()
 * f3 -> Reg()
 */
public class HStoreStmt implements Node {
   public NodeToken f0;
   public Reg f1;
   public IntegerLiteral f2;
   public Reg f3;

   public HStoreStmt(NodeToken n0, Reg n1, IntegerLiteral n2, Reg n3) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
   }

   public HStoreStmt(Reg n0, IntegerLiteral n1, Reg n2) {
      f0 = new NodeToken("HSTORE");
      f1 = n0;
      f2 = n1;
      f3 = n2;
   }

   public void accept(kanga.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(kanga.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(kanga.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(kanga.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

