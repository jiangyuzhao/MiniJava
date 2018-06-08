package piglet.piglet2spiglet;

import piglet.syntaxtree.*;
import piglet.symboltable.MSpiglet;
import piglet.ParseException;
import piglet.PigletParser;
import piglet.TokenMgrError;
import java.util.regex.*;

import piglet.visitor.GJDepthFirst;
import piglet.visitor.Piglet2SpigletVisitor;

import java.io.*;
import java.util.Scanner;


public class Main { 
    public static void main(String[] args) {
    	try {
    		// InputStream is = new FileInputStream("test/BinaryTree.pg");
            InputStream is = new FileInputStream("test/BubbleSort.pg");
            // InputStream is = new FileInputStream("test/Factorial.pg");
            // InputStream is = new FileInputStream("test/LinearSearch.pg");
            // InputStream is = new FileInputStream("test/LinkedList.pg");
            // InputStream is = new FileInputStream("test/MoreThan4.pg");
            // InputStream is = new FileInputStream("test/QuickSort.pg");
            // InputStream is = new FileInputStream("test/TreeVisitor.pg");
			PrintStream out = new PrintStream("mine.spg");
			Scanner sc = new Scanner(is);
			String spigletCode = "";
			while(sc.hasNext()) {
				spigletCode += sc.nextLine() + "\n";
			}
			out.println(pig2spig(spigletCode));
		}
    	catch(TokenMgrError e){
    		//Handle Lexical Errors
    		e.printStackTrace();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    public static String pig2spig(String s) {
        InputStream in = new ByteArrayInputStream(s.getBytes());
        Node root = new NodeToken("rr");
        try {
            root = new PigletParser(in).Goal();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String patternString = "\\s*(TEMP)\\s*(\\d*)";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher( s );
        int max = 0;
        while (matcher.find()) {
            int tmpnum = Integer.valueOf(matcher.group(2).toString());
            if (tmpnum > max)
                max = tmpnum;
        }
        Piglet2SpigletVisitor t = new Piglet2SpigletVisitor(max);
        return root.accept(t).getCode().toString();
    }
}
