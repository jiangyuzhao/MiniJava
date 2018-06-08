// import kanga.TokenMgrError;

import java.io.*;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		try {
			InputStream in = new FileInputStream("test/Factorial.java");
			//PrintStream out = new PrintStream("MoreThan4.asm");
			//InputStream in = System.in;
			// PrintStream out = System.out;
			PrintStream out = new PrintStream("mine.s");
			Scanner sc = new Scanner(in);
			String code = "";
			while(sc.hasNext()) {
				code += sc.nextLine() + "\n";
			}

			code = minijava.minijava2piglet.Main.mini2pig(code);

			code = piglet.piglet2spiglet.Main.pig2spig(code);

			code = spiglet.spiglet2kanga.Main.spg2kan(code);

			code = kanga.kanga2MIPS.Main.kanga2mips(code);

			out.printf(code);
		}
		 catch (Exception e) {
			e.printStackTrace();
		}
	}
}