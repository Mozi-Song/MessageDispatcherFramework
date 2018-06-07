package util.util;

import java.io.PrintStream;

public class Logger {
	public static PrintStream out = System.out;
	public static void log(String s){
		System.out.println(s);
	}
}
