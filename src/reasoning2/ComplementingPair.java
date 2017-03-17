package reasoning2;

import java.util.HashSet;

public class ComplementingPair {

	private HashSet<LogicExpression> c1;
	private HashSet<LogicExpression> c2;
	
	public ComplementingPair(HashSet<LogicExpression> c1, HashSet<LogicExpression> c2){
		
		this.c1 = c1;
		this.c2 = c2;

	}
	
	public String toString(){
		return c1.toString() + ", " + c2.toString();
	}
	
	public HashSet<LogicExpression> getFirst(){
		return this.c1;
	}
	
	public HashSet<LogicExpression> getSecond(){
		return this.c2;
	}
	
	
}
