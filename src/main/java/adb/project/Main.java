package adb.project;

public class Main {
	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			String fileName = args[i];
			System.out.println("Processing file: " + fileName);
			TransactionManager manager = new TransactionManager(fileName);
			simulate(manager);
			System.out.println();
		}

	}

	public static void simulate(TransactionManager TM) {
		// System.out.println("executing next operation");
		while (TM.processNextOperation()) {
		}
	}
}
