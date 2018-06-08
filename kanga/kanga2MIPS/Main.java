package kanga.kanga2MIPS;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.Scanner;

import kanga.ParseException;
import kanga.KangaParser;
import kanga.TokenMgrError;

import kanga.syntaxtree.*;
import kanga.symboltable.Context;
import kanga.visitor.Kanga2MIPSVisitor;

public class Main {
	public static void main(String[] args) {
    	try {
    		// InputStream is = new FileInputStream("test/BinaryTree.kg");
            // InputStream is = new FileInputStream("test/BubbleSort.kg");
            // InputStream is = new FileInputStream("test/Factorial.kg");
            // InputStream is = new FileInputStream("test/LinearSearch.kg");
            // InputStream is = new FileInputStream("test/LinkedList.kg");
            // InputStream is = new FileInputStream("test/MoreThan4.kg");
            // InputStream is = new FileInputStream("test/QuickSort.kg");
            InputStream is = new FileInputStream("test/TreeVisitor.kg");

			Scanner sc = new Scanner(is);
			String code = "";
			while(sc.hasNext()) {
				code += sc.nextLine() + "\n";
			}
			kanga2mips(code);
		}
    	catch(TokenMgrError e){
    		//Handle Lexical Errors
    		e.printStackTrace();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    public static String kanga2mips(String s) {
    	InputStream in = new ByteArrayInputStream(s.getBytes());
		try {
            StringBuilder out = new StringBuilder();
            new Context(out); // global context & ofstream

			Node root = new KangaParser(in).Goal();
			root.accept(new Kanga2MIPSVisitor(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return Context.out.toString();
	}
}
