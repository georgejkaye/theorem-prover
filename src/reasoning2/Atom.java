package reasoning2;

import java.util.ArrayList;

/**
 * A logic expression comprising of just an atom and negations
 * @author George Kaye
 *
 */

public class Atom implements LogicExpression {

	private String name;
	private int negations;

	/**
	 * Create a new atom with no negations
	 * @param name the name of the atom
	 */
	
	public Atom(String name) {
		this.name = name;
		this.negations = 0;
	}

	/**
	 * Create a new atom with a name and negations
	 * @param name the name of the atom
	 * @param negations the number of negations
	 */
	
	public Atom(String name, int negations) {
		this.name = name;
		this.negations = negations;
	}

	public String toString() {

		String result = "";

		for (int i = 0; i < negations; i++) {
			result += "-";
		}

		return result + name;

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ExpressionType getType() {
		return ExpressionType.ATOM;
	}

	@Override
	public ArrayList<LogicExpression> getTerms() {
		ArrayList<LogicExpression> result = new ArrayList<>();
		result.add(this);
		return result;
	}


	@Override
	public int getNegations() {
		return negations;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	public Terminal getTerminal() {
		return null;
	}

	@Override
	public int hashCode() {
		return negations + name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Atom))
			return false;
		if (obj == this)
			return true;

		Atom exp = (Atom) obj;
		if (exp.getNegations() == negations && exp.getName().equals(name)) {
			return true;
		}

		return false;

	}

	@Override
	public LogicExpression addToFront(LogicExpression term, Terminal terminal) {
		ArrayList<LogicExpression> terms = new ArrayList<>();
		
		terms.add(term);
		terms.add(this);
		
		return new Construction(terms, terminal);
	}
	
	@Override
	public LogicExpression addToBack(LogicExpression term, Terminal terminal) {
		ArrayList<LogicExpression> terms = new ArrayList<>();
		
		terms.add(this);
		terms.add(term);
		
		return new Construction(terms, terminal);
	}

	@Override
	public int getSize() {
		return 1;
	}
}
