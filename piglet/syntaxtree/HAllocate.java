//
// Generated by JTB 1.3.2
//

package piglet.syntaxtree;

/**
 * Grammar production:
 * f0 -> "HALLOCATE"
 * f1 -> Exp()
 */
public class HAllocate implements Node {
   public NodeToken f0;
   public Exp f1;

   public HAllocate(NodeToken n0, Exp n1) {
      f0 = n0;
      f1 = n1;
   }

   public HAllocate(Exp n0) {
      f0 = new NodeToken("HALLOCATE");
      f1 = n0;
   }

   public void accept(piglet.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(piglet.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(piglet.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(piglet.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

