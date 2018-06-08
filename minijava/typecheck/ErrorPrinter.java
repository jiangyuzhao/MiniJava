package minijava.typecheck;


public class ErrorPrinter {
    public static ErrorPrinter instance = new ErrorPrinter();
    private static int size = 0;
    public void printError(int line,int column,String content) {
        System.out.println("line:" + line + " column:" + column + '\n' + content);
        size ++;
    }
    public void printError(int line,int column,String content, int flag) {
        System.out.println("WARNING:\n" + "line:" + line + " column:" + column + '\n' + content + '\n');
    }
    public static int getSize() {
    	return size;
    }
}
