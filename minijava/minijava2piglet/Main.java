package minijava.minijava2piglet;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.symboltable.MClass;
import minijava.symboltable.MClassList;
import minijava.symboltable.MPiglet;
import minijava.syntaxtree.Node;
import minijava.visitor.GJDepthFirst;

import minijava.typecheck.ErrorPrinter;

import minijava.visitor.SymbolTableVisitor;
import minijava.visitor.TypeCheckVisitor;
import minijava.visitor.MinijavaToPigletVisitor;

import java.io.*;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		try {
			// InputStream is = new FileInputStream("test/BinaryTree.java");
			// InputStream is = new FileInputStream("test/BubbleSort.java");
			InputStream is = new FileInputStream("test/Factorial.java");
			// InputStream is = new FileInputStream("test/LinearSearch.java");
			// InputStream is = new FileInputStream("test/LinkedList.java");
			// InputStream is = new FileInputStream("test/MoreThan4.java");
			// InputStream is = new FileInputStream("test/QuickSort.java");
			// InputStream is = new FileInputStream("test/TreeVisitor.java");
			// InputStream is = new FileInputStream("test/IndexRange.java");
			// InputStream is = new FileInputStream("test/Initialization.java");

			// InputStream is = new FileInputStream("test/ClassAsParam.java");
			// InputStream is = new FileInputStream("test/TooManyParams.java");
			
			// InputStream is = new FileInputStream("test/AnimalDog/AD.java"); 
			// 20
			// InputStream is = new FileInputStream("test/AnimalDog/2Age.java"); 
			// 0
			// InputStream is = new FileInputStream("test/AnimalDog/UselessExtend.java");
			// 10
			// InputStream is = new FileInputStream("test/AnimalDog/jyz.java");
			// 0
			// InputStream is = new FileInputStream("test/AnimalDog/AllFromParent.java");
			// 10
			
			PrintStream out = new PrintStream("mine.pg");
			String code = "";
			Scanner sc = new Scanner(is);
			while (sc.hasNext()) {
				code += sc.nextLine() + "\n";
			}
			out.println(mini2pig(code));
			/*
			 * TODO: Implement your own Visitors and other classes.
			 * 
			 */
		}
		catch(TokenMgrError e){
			//Handle Lexical Errors
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static String mini2pig(String s) {
		InputStream in = new ByteArrayInputStream(s.getBytes());
		Node root = new minijava.syntaxtree.NodeToken("rr");
		try {
			root = new MiniJavaParser(in).Goal();
		} catch (minijava.ParseException e) {
			e.printStackTrace();
			return "ERROR";
		}
		root.accept(new SymbolTableVisitor(), MClassList.instance);
		root.accept(new TypeCheckVisitor(), MClassList.instance);
		if (ErrorPrinter.getSize() > 0) {
			System.out.println("Type Error");
			return "ERROR";
		}
		MClassList.instance.completeClass();
		MClassList.instance.allocTemp(20);
		MPiglet ans = root.accept(new MinijavaToPigletVisitor(), MClassList.instance);
		return ans.getCode().toString();
	}
}