package reasoning;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A class to represent a resolution proof
 * @author George Kaye
 *
 */

public class ResolutionProof {
	
	private ArrayList<ProofLine> lines;
	private Result result;
	
	/**
	 * Create an empty resolution proof
	 */
	
	public ResolutionProof(){
		lines = new ArrayList<>();
	}
	
	/**
	 * Create a new resolution proof
	 * @param lines the lines of the proof
	 * @param result the result of the proof
	 */
	
	public ResolutionProof(ArrayList<ProofLine> lines, Result result){
		this.lines = lines;
		this.result = result;
	}
	
	/**
	 * Fancy toString method
	 */
	
	public String toString(){
		
		String returnString = "======= RESOLUTION PROOF ========";
		
		int lineNo = 1;
		
		for(ProofLine line : lines){
			returnString += ("\n" + lineNo + ": " + line);
			lineNo++;
		}
		
		if(result == Result.SATISFIABLE){
			returnString += "\nNo more clauses to resolve";
			returnString += "\nNegation satisfiable";
		}
		else if(result == Result.UNSATISFIABLE) {
			returnString += "\nContradiction found!";
			returnString += "\nNegation unsatisfiable";
		}
		
		returnString += "\n======== PROOF CONCLUDES ========";
		
		return returnString;
	}
	
	/**
	 * Get the line number for a given line, -1 if line is not in proof
	 * @param query
	 * @return
	 */
	
	public int getLine(HashSet<LogicExpression> query){
		
		int i = 1;
		
		for(ProofLine line : lines){
			if(line.getMain().equals(query)){
				return i;
			}
			
			i++;
		}
		
		return -1;
		
	}
	
	/**
	 * Add a line to the proof
	 * @param newLine the line to add
	 */
	
	public void addLine(ProofLine newLine){
		lines.add(newLine);
	}
	
	/**
	 * Set the result of a proof
	 * @param result the result
	 */
	
	public void setResult(Result result){
		this.result = result;
	}
	
	/**
	 * Get the result of a proof
	 * @return the result
	 */
	
	public Result getResult(){
		return this.result;
	}
	
}
