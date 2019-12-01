package adb.project;

import Java.util.*;
import Java.io.*;

class Operation{
	final int timeStamp;
	final char type;
	final String transactionID;
	final int idx;
	final int val;
	Operation(String line, Integer tick){
		timeStamp = tick;
		String[] op = line.split("[()]");
		if(op[0].compareTo("begin") == 0){
			type = 'B';
			transactionID = op[1];
		}
		else if(op[0].compareTo("beginRO") == 0){
			type = 'B';
			transactionID = op[1];
		}
		else if(op[0].compareTo("R") == 0){
			type = 'R';
			String[] transaction = op[1].split(",");
			transactionID = transaction[0];
			idx = transaction[1];
		}
		else if(op[0].compareTo("W") == 0){
			type = 'W';
			String[] transaction = op[1].split(",");
			transactionID = transaction[0];
			idx = transaction[1];
			val = transaction[2];
		}
		else if(op[0].compareTo("dump")){
			type = 'D';
		}
		else if(op[0].compareTo("end")){
			type = 'E';
			transactionID = op[1];
		}
		else if(op[0].compareTo("fail")){
			type = 'F';
		}
		else if(op[0].compareTo("recover")){
			type = 'R';
		}
	}
}