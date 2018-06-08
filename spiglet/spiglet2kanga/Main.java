package spiglet.spiglet2kanga;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import spiglet.ParseException;
import spiglet.SpigletParser;
import spiglet.TokenMgrError;
import spiglet.syntaxtree.*;
import spiglet.symboltable.Context;
import spiglet.symboltable.Method;
import spiglet.symboltable.RegAssignment;

import spiglet.symboltable.FlowGraphPointer;

import spiglet.visitor.CreateFlowGraphVertexVisitor;
import spiglet.visitor.CreateFlowGraphVisitor;
import spiglet.visitor.Spiglet2KangaVisitor;

public class Main {
    public static void main(String[] args) {
    	try {
            // InputStream is = new FileInputStream("test/BinaryTree.spg");
            // InputStream is = new FileInputStream("test/BubbleSort.spg");
            // InputStream is = new FileInputStream("test/Factorial.spg");
            // InputStream is = new FileInputStream("test/LinearSearch.spg");
            // InputStream is = new FileInputStream("test/LinkedList.spg");
            // InputStream is = new FileInputStream("test/MoreThan4.spg");
            // InputStream is = new FileInputStream("test/QuickSort.spg");
            InputStream is = new FileInputStream("test/TreeVisitor.spg");

			Scanner sc = new Scanner(is);
			String code = "";
			while (sc.hasNext()) {
				code += sc.nextLine() + "\n";
			}
			// spg2kan(code, new PrintStream("mine.kg"));
            spg2kan(code);
    	} catch (TokenMgrError e){
    		//Handle Lexical Errors
    		e.printStackTrace();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }
    public static String spg2kan(String s) {
        InputStream in = new ByteArrayInputStream(s.getBytes());
        try {
            StringBuilder myout = new StringBuilder();
            new Context(myout); // global context & ofstream

            Node root = new SpigletParser(in).Goal();
            root.accept(new CreateFlowGraphVertexVisitor(), FlowGraphPointer.getInstance());
            root.accept(new CreateFlowGraphVisitor(), FlowGraphPointer.getInstance());
            new RegAssignment().LinearScan();
            root.accept(new Spiglet2KangaVisitor(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Context.out.toString();
    }
}
