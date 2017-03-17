package reasoning2;

import java.util.HashSet;

/**
 * A class representing a line of a proof
 * @author George Kaye
 *
 */

public class ProofLine {

	private HashSet<LogicExpression> main;
	private String operation;
	
	/**
	 * Create a new proof line with an expression and operation
	 * @param main the expression of this line
	 * @param operation the operation resulting in this line
	 */
	
	public ProofLine(HashSet<LogicExpression> main, String operation){
		this.main = main;
		this.operation = operation;
	}
	
	public String toString(){
		
		int j = 20 - main.toString().length();
		String spaces = "";
		
		for(int i = 0; i < j; i++){
			spaces += " ";
		}
		
		return main + spaces + operation;
	}
	
	/**
	 * Get the expression from this line
	 * @return the expression
	 */
	
	public HashSet<LogicExpression> getMain(){
		return this.main;
	}
	
}
