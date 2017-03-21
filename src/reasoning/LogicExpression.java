package reasoning;

import java.util.ArrayList;

/**
 * An interface to represent a variety of logical expressions
 * @author George Kaye
 *
 */

public interface LogicExpression {
	
	public enum ExpressionType{ATOM, CONSTRUCTION};
	
	/**
	 * Get the type of this expression
	 * @return the type of this expression
	 */
	
	public ExpressionType getType();
	
	/**
	 * Get the name of this atom, null if an expression
	 * @return the name of this atom
	 */
	
	public String getName();
	
	/**
	 * Get the terms of this construction, null if an atom
	 * @return the left expression of this construction
	 */
	
	public ArrayList<LogicExpression> getTerms();

	/**
	 * Get the terminal of this construction, null if an atom
	 * @return the terminal of the construction
	 */
	
	public Terminal getTerminal();
	
	/**
	 * Get the number of negations this expression possesses
	 * @return the number of negations on this expression
	 */
	
	public int getNegations();
	
	/**
	 * Get the depth of this expression (atoms are depth 0)
	 * @return the depth
	 */
	
	public int getDepth();
	
	/**
	 * Get the size of this expression (how many elements it contains)
	 * @return the size
	 */
	
	public int getSize();
	
	/**
	 * Add a term to the front of the expression
	 * @param term the term to add
	 * @param terminal the terminal of the expression
	 * @return the new expression
	 */
	
	public LogicExpression addToFront(LogicExpression term, Terminal terminal);

	/**
	 * Add a term to the back of the expression
	 * @param term the term to add
	 * @param terminal the terminal of the expression
	 * @return the new expression
	 */
	
	public LogicExpression addToBack(LogicExpression term, Terminal terminal);
	
}

