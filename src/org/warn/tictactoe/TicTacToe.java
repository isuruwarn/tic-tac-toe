package org.warn.tictactoe;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;


/**
 * This implementation assumes that the user will always make the first move. Therefore the 
 * user will use crosses and the computer will use naughts.
 *
 */
public class TicTacToe {
	

	private static final String UNUSED = "UNUSED";
	private static final String CROSS = "CROSS";
	private static final String NAUGHT = "NAUGHT";
	private static final String UNUSED_SYMBOL = "| _ ";
	private static final String CROSS_SYMBOL = "| X ";
	private static final String NAUGHT_SYMBOL = "| O ";
	private static final String EXIT = "EXIT";
	private static final String WINNINGCOMBO1 = "0 1 2";
	private static final String WINNINGCOMBO2 = "3 4 5";
	private static final String WINNINGCOMBO3 = "6 7 8";
	private static final String WINNINGCOMBO4 = "0 3 6";
	private static final String WINNINGCOMBO5 = "1 4 7";
	private static final String WINNINGCOMBO6 = "2 5 8";
	private static final String WINNINGCOMBO7 = "0 4 8";
	private static final String WINNINGCOMBO8 = "2 4 6";
	private List<String> squares;
	private List<String> winningCombos;
	private List<String> possibleWinningCombos;
	private String nextMove;
	private String previousMove;
	
	
	
	public TicTacToe() {
		initializeSquares();
		setWinningCombos();
	}
	
	
	
	public static void main(String[] args) {
		TicTacToe tictactoe = new TicTacToe();
		tictactoe.play();
	}
	
	
	
	public void initializeSquares() {
		squares = new LinkedList<String>();
		for(int i=0; i<9; i++) {
			squares.add(UNUSED);
		}
	}
	
	
	
	public void setWinningCombos() {
		winningCombos = new LinkedList<String>();
		winningCombos.add(WINNINGCOMBO1);
		winningCombos.add(WINNINGCOMBO2);
		winningCombos.add(WINNINGCOMBO3);
		winningCombos.add(WINNINGCOMBO4);
		winningCombos.add(WINNINGCOMBO5);
		winningCombos.add(WINNINGCOMBO6);
		winningCombos.add(WINNINGCOMBO7);
		winningCombos.add(WINNINGCOMBO8);
	}
	
	
	
	public void play() {
		
		System.out.println("=== TIC TAC TOE ===\n");
		System.out.println("Please enter the square number, or type EXIT to close.\n");
		
		printSquares();
		int unusedSquares = getUnusedSquares().size();
		
		while(unusedSquares!= 0) {
			
			nextMove = null;
			possibleWinningCombos = null;
			getNextUserMove();
			printSquares();
			
			//check if user has won
			boolean userHasWon = playerHasWon(getCrosses());
			if(userHasWon) {
				System.out.println("\n\n Congratulations! You WIN!!");
				break;
			}
			
			calculateNextMove(getCrosses());
			calculateNextMove(getNaughts());
			makeNextMove();
			
			if(unusedSquares>1) {
				printSquares();
			}
			
			//check if comp has won
			boolean compHasWon = playerHasWon(getNaughts());
			if(compHasWon) {
				System.out.println("\n\n Sorry! You LOSE!!");
				break;
			}
			
			unusedSquares = getUnusedSquares().size();
		}
		
		System.out.println("\n=== GAME OVER ===");
	}
	
	
	
	public void calculateNextMove(List<Integer> entries) {
		
		possibleWinningCombos = new LinkedList<String>();
		String [] arrWinningCombo = null;
		int entriesInSequence = 0;
		int possibleEntriesInSequence = 0;
		String indexOfEntry = "";
		String winningCombo = "";
		String winningComboElement = "";
		
		for(int i=0; i<winningCombos.size(); i++) {
			
			//next winning combo
			winningCombo = winningCombos.get(i);
			arrWinningCombo = winningCombo.split(" ");
			entriesInSequence=0;
			possibleEntriesInSequence=0;
			
			for(int j=0; j<entries.size(); j++) {
				
				//get next entry
				indexOfEntry = String.valueOf(entries.get(j));
				
				//if entered index exists in current winning combo, investigate further
				if( winningCombo.indexOf(indexOfEntry) != -1 ) {
					
					entriesInSequence++;
					possibleEntriesInSequence=0;
					
					//check if other two squares are available
					for(int k=0; k<arrWinningCombo.length; k++) {
						
						winningComboElement = arrWinningCombo[k];
						
						//ensure we skip the current entry
						if( !winningComboElement.equals(indexOfEntry) ) {
							
							if( squareIsUnused(Integer.parseInt(winningComboElement)) ) {
								possibleEntriesInSequence++;
							}
						}
					} // end searching other two squares in winning sequence
					
				}
				
			} // end looping through entries
			
			//if winning is possible using current combo , add combo to list of possibleWinningCombos 
			if( (entriesInSequence + possibleEntriesInSequence) == 3 ) {
				possibleWinningCombos.add(winningCombo);
				
				// specify nextMove if winning is possible with just one move
				if(possibleEntriesInSequence==1) {
					setNextMove(winningCombo); 
				}
			}
			
		} // end looping through winning combos
		
		
		// make adjustment for the first move
		int naughts = getNaughts().size();
		int adjustedNextMove = 4;
		boolean squareIsAvailable = squareIsUnused(adjustedNextMove);
		if( naughts==0 && squareIsAvailable ) {
			nextMove = String.valueOf(adjustedNextMove);
		}
		
		// if neither player can win with the next move, choose any square from the possible winning combos
		else if( nextMove==null && possibleWinningCombos.size()>0 ) {
			setNextMove(possibleWinningCombos.get(0));
		}
		
	}
	
	
	
	/**
	 * Checks if provided list contains a winning combo (i.e. whether a player has won)
	 * 
	 * @param entries - list of either naughts or crosses that have been entered thus far
	 * @return boolean
	 */
	public boolean playerHasWon(List<Integer> entries) {
		
		int entriesInSequence = 0;
		String winningCombo = "";
		
		for(int i=0; i<winningCombos.size(); i++) {
			winningCombo = winningCombos.get(i);
			entriesInSequence = 0;
			for(int j=0; j<entries.size(); j++) {
				String indexOfEntry = String.valueOf(entries.get(j));
				if( winningCombo.indexOf(indexOfEntry) != -1 ) {
					entriesInSequence++;
				}
			}
			if(entriesInSequence==3) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	public void setNextMove(String winningCombo) {
		
		String [] arrWinningCombo = winningCombo.split(" ");
		
		for(int k=0; k<arrWinningCombo.length; k++) {
			String winningComboElement = arrWinningCombo[k];
			if( squareIsUnused(Integer.parseInt(winningComboElement)) ) {
				nextMove = winningComboElement;
			}
		}
	}
	
	
	
	public void makeNextMove() {
		try {
			squares.set(Integer.valueOf(nextMove), NAUGHT);
			previousMove = nextMove;
			System.out.println("\nComp move:");
		} 
		catch(NumberFormatException e) {}
	}
	
	
	
	/**
	 * Prompts user for the next move and updates the squares list
	 */
	public void getNextUserMove() {
		try {
			System.out.print("\n\nYour move: ");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String nextUserMove = in.readLine();
		
			if(nextUserMove.equalsIgnoreCase(EXIT)) {
				System.exit(0);
			} else {
				int indexOfNextMove = Integer.parseInt(nextUserMove); //Todo: use regex to validate input
				if(validInput(indexOfNextMove)) {
					squares.set(indexOfNextMove, CROSS);
					previousMove = nextUserMove;
				} else {
					getNextUserMove();
				}
			}
		}
		catch(NumberFormatException e) {
			System.out.println("Please enter a numrical value!");
			getNextUserMove();
		}
		catch(IOException e) {
			System.out.println("An error has ocurred! Please try again..");
		}
	}
	
	
	
	/**
	 * Validates user input
	 * 
	 * @param indexOfNextMove - the square entered by the user
	 * @return true if square is unused
	 */
	public boolean validInput(int indexOfNextMove) {
		
		//checks the whether the input is within the valid range
		if(indexOfNextMove<0 || indexOfNextMove>8) {
			System.out.println("Please select a number between 0 and 8!");
			return false;
		}
		
		//checks whether the entered square is available
		List<Integer> unsedSquares = getUnusedSquares();
		if( ! unsedSquares.contains(Integer.valueOf(indexOfNextMove)) ) {
			System.out.println("Please select an empty square!");
			return false;
		}
		
		return true;
	}
	
	
	
	public List<Integer> getCrosses() {
		List<Integer> crosses = new LinkedList<Integer>();
		for(int i=0; i<9; i++) {
			if(squares.get(i).equals(CROSS)) {
				crosses.add(i);
			}
		}
		return crosses;
	}
	
	
	
	public List<Integer> getNaughts() {
		List<Integer> naughts = new LinkedList<Integer>();
		for(int i=0; i<9; i++) {
			if(squares.get(i).equals(NAUGHT)) {
				naughts.add(i);
			}
		}
		return naughts;
	}
	
	
	
	public List<Integer> getUnusedSquares() {
		List<Integer> unusedSquares = new LinkedList<Integer>();
		for(int i=0; i<9; i++) {
			if(squareIsUnused(i)) {
				unusedSquares.add(i);
			}
		}
		return unusedSquares;
	}
	
	
	
	public void printSquares() {
		
		for(int i=0; i<9; i++) {
			
			String row = "";
			
			if(squareIsUnused(i)) {
				row = UNUSED_SYMBOL;
			} else if(squares.get(i).equals(CROSS)) {
				row = CROSS_SYMBOL;
			} else if(squares.get(i).equals(NAUGHT)) {
				row = NAUGHT_SYMBOL;
			}
			
			// go to next line
			if( i==2 || i==5 || i==8 ) {
				row += "\n";
			}
			
			System.out.print(row);
		}
	}
	
	
	
	public String getNextMove() {
		return nextMove;
	}
	
	
	
	public List<String> getPossibleWinningCombos() {
		return possibleWinningCombos;
	}
	
	
	
	/**
	 * Returns true if the specified square is UNUSED
	 * 
	 * @param int squareIndex
	 * @return
	 */
	public boolean squareIsUnused(int squareIndex) {
		return squares.get(squareIndex).equals(UNUSED);
	}
	
}
