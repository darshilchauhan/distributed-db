package adb.project;

import Java.util.*;
import Java.io.*;

public void main(String[] args){
	
	String filename = args[1];
	BufferedReader reader;
	reader = new BufferedReader(new FileReader(filename));
	int tick = 0;
	String line = reader.readLine();
	List<Operation> ops;
	while (line != null) {
		Operation op = new Operation(line, tick);
		line = reader.readLine();
		tick++;
	}
	Simulate(ops);
}

public void Simulate(ops){
	for (Operation op:ops){
		op.process();
	}
}