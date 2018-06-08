import minijava.typecheck.ErrorPrinter;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError; 

import minijava.syntaxtree.Node; 
import minijava.visitor.SymbolTableVisitor; 
import minijava.visitor.TypeCheckVisitor;

import minijava.symboltable.MClass; 
import minijava.symboltable.MMethod; 
import minijava.symboltable.MVar; 
import minijava.symboltable.MType; 
import minijava.symboltable.MClassList; 
import minijava.symboltable.MCallList; 

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Main { 
 
    public static void main(String[] args) {
    	try {
			// InputStream is = new FileInputStream("test/BinaryTree-error.java");
            // InputStream is = new FileInputStream("test/BinaryTree.java");
            // InputStream is = new FileInputStream("test/BubbleSort-error.java");
            InputStream is = new FileInputStream("test/BubbleSort.java");
            // InputStream is = new FileInputStream("test/Factorial-error.java");
            // InputStream is = new FileInputStream("test/Factorial.java");
            // InputStream is = new FileInputStream("test/LinearSearch-error.java");
            // InputStream is = new FileInputStream("test/LinearSearch.java");
            // InputStream is = new FileInputStream("test/LinkedList-error.java");
            // InputStream is = new FileInputStream("test/LinkedList.java");
            // InputStream is = new FileInputStream("test/MoreThan4-error.java");
            // InputStream is = new FileInputStream("test/MoreThan4.java");
            // InputStream is = new FileInputStream("test/QuickSort-error.java");
            // InputStream is = new FileInputStream("test/QuickSort.java");
            // InputStream is = new FileInputStream("test/TreeVisitor-error.java");
            // InputStream is = new FileInputStream("test/TreeVisitor.java");
            // InputStream is = new FileInputStream("test/IndexRange-error.java");
            // InputStream is = new FileInputStream("test/IndexRange.java");
            // InputStream is = new FileInputStream("test/Initialization-error.java");
            // InputStream is = new FileInputStream("test/Initialization.java");
			
            Node root = new MiniJavaParser(is).Goal();
			root.accept(new SymbolTableVisitor(),MClassList.instance);
			root.accept(new TypeCheckVisitor(), MClassList.instance);
			if (ErrorPrinter.getSize() > 0) {
                System.out.println("Type error");
            } else {
                System.out.println("Program type checked successfully");
            }
		} catch (TokenMgrError e){
    		e.printStackTrace();
    	} catch (ParseException e){
    		e.printStackTrace();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }
}