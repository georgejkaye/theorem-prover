package reasoning;

import java.util.HashSet;

/**
 * A class to represent a complementing pair of literals
 * @author George Kaye
 *
 */

public class ComplementingPair {

	private HashSet<LogicExpression> c1;
	private HashSet<LogicExpression> c2;
	
	/**
	 * Create a new complementing pair of literals
	 * @param c1 the first literal
	 * @param c2 the second literal
	 */
	
	public ComplementingPair(HashSet<LogicExpression> c1, HashSet<LogicExpression> c2){
		
		this.c1 = c1;
		this.c2 = c2;

	}
	
	public String toString(){
		return c1.toString() + ", " + c2.toString();
	}
	
	/**
	 * Get the first literal
	 * @return the first literal
	 */
	
	public HashSet<LogicExpression> getFirst(){
		return this.c1;
	}
	
	/**
	 * Get the second literal
	 * @return the second literal
	 */
	
	public HashSet<LogicExpression> getSecond(){
		return this.c2;
	}
	
	
}
