package com.theaigames.go.testsuite;

import com.theaigames.go.Processor;
import com.theaigames.go.field.Field;

public class Testsuite {
	
	public void dbgTestKoRule(Field field) {
		field.addMove(2, 0, 1);
		field.addMove(1, 1, 1);
		field.addMove(2, 2, 1);
		
		field.addMove(3, 0, 2);
		field.addMove(2, 1, 2);
		field.addMove(3, 2, 2);
		field.addMove(4, 1, 2);
		field.addMove(3, 1, 1);
		field.addMove(2, 1, 2);
		System.out.println(field.getLastError());

		/* TODO: Assert field state */
	}
	
	public void dbgTestCapture(Field field) {
		field.addMove(18, 1, 2);
		field.addMove(18, 2, 2);
		
		field.addMove(18, 0, 1);
		field.addMove(17, 1, 1);
		field.addMove(17, 2, 1);
		field.addMove(18, 3, 1);
		/* TODO: Assert field state */
	}
	
	public void dbgTestSuicideRule(Field field) {
		/* Test suicide with one stone */
		field.addMove(8, 0, 1);
		field.addMove(7, 1, 1);
		field.addMove(8, 2, 1);
		field.addMove(9, 1, 1);
		field.addMove(8, 1, 2);
		System.out.println(field.getLastError());
		/* TODO: Assert field state */
		
		/* Test suicide with a group of stones */
		field.addMove(8, 8, 1);
		field.addMove(9, 8, 1);
		field.addMove(7, 9, 1);
		field.addMove(10, 9, 1);	
		field.addMove(8, 10, 1);
		field.addMove(10, 10, 1);
		field.addMove(9, 11, 1);

		field.addMove(8, 9, 2);
		field.addMove(9, 9, 2);
		field.addMove(9, 10, 2); /* Triggers a violation of suicide rule */
		System.out.println(field.getLastError());
		
		field.addMove(9, 10, 1); /* Should work fine */
		System.out.println(field.getLastError());
		/* TODO: Assert field state */
		
		field.addMove(1, 12, 1);
		field.addMove(2, 12, 1);
		field.addMove(3, 12, 1);
		
		field.addMove(0, 13, 1);
		field.addMove(1, 13, 2);
		field.addMove(2, 13, 2);
		field.addMove(3, 13, 2);
		field.addMove(4, 13, 1);
		
		field.addMove(0, 14, 1);
		field.addMove(1, 14, 2);
		field.addMove(3, 14, 2);
		field.addMove(4, 14, 1);
		
		field.addMove(0, 15, 1);
		field.addMove(1, 15, 2);
		field.addMove(2, 15, 2);
		field.addMove(3, 15, 2);
		field.addMove(4, 15, 1);
		
		field.addMove(1, 16, 1);
		field.addMove(2, 16, 1);
		field.addMove(3, 16, 1);
		
		field.addMove(2, 14, 1);
	}
	
	public void dbgTestScore(Field field) {
		field.addMove(6, 6, 2);
		field.addMove(7, 6, 2);
		field.addMove(8, 6, 2);
		
		field.addMove(8, 7, 2);
		field.addMove(8, 8, 2);
		field.addMove(8, 9, 2);
		
		field.addMove(6, 7, 1);
		field.addMove(6, 8, 1);
		field.addMove(6, 9, 1);

		field.addMove(6, 10, 1);
		field.addMove(7, 10, 1);
		field.addMove(8, 10, 1);
		
		field.addMove(7, 9, 1);

		/* TODO: Assert field state */
	}
}
