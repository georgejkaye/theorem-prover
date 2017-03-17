package reasoning2;

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
	 * Get the left expression of this construction, null if an atom
	 * @return the left expression of this construction
	 */
	
	public LogicExpression getLeft();

	/**
	 * Get the right expression of this construction, null if an atom
	 * @return the right expression of this construction
	 */
	
	public LogicExpression getRight();

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
	 * @return
	 */
	
	public int getDepth();
	
}

