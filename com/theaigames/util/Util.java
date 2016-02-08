package com.theaigames.util;

public final class Util {

	private Util() {
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);  
	}
	
	/**
	 * Compares two boards
	 * @param args : int[][] b1, int[][] b2
	 * @return : true if boards are equal
	 */
	public static Boolean compareBoards(int[][] b1, int[][] b2) {
		for (int x = 0; x < b1.length; x++) {
			for (int y = 0; y < b1[0].length; y++) {
				if (b1[x][y] != b2[x][y]) return false;
			}
		}
		return true;
	}
	
	/**
	 * Dumps a board to stdout
	 * @param args : 
	 * @return : 
	 */
	public static void dumpBoard(int[][] board, String label) {
		System.out.print("\n\ndump '" + label + " ':\n");
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				System.out.print(board[x][y]);
				if (x < board[0].length-1) {
					System.out.print(", ");
				}
			}
			System.out.print("\n");
		}
	}
}