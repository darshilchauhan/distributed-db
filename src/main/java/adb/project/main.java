package adb.project;

public class Main {
	public static void main(String[] args){
	
		String fileName = args[1];
		TransactionManager manager = new TransactionManager(fileName);
		simulate(manager);
	}
	
	public static void simulate(TransactionManager TM){
		while(TM.processNextOperation());
	}
}
