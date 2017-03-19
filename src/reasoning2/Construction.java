package reasoning2;

import java.util.ArrayList;

/**
 * A LogicExpression combining several subexpressions all connected by the same
 * terminal
 * 
 * @author George Kaye
 *
 */

public class Construction implements LogicExpression {

	private ArrayList<LogicExpression> terms;
	private Terminal terminal;
	private int negations;

	/**
	 * Create a new construction with no negations
	 * 
	 * @param left
	 *            the left subexpression
	 * @param terminal
	 *            the terminal
	 * @param right
	 *            the right subexpression
	 */

	public Construction(ArrayList<LogicExpression> terms, Terminal terminal) {
		this.terms = terms;
		this.terminal = terminal;
		this.negations = 0;
	}

	/**
	 * Create a new construction with a number of negations
	 * 
	 * @param left
	 *            the left subexpression
	 * @param terminal
	 *            the terminal
	 * @param right
	 *            the right subexpression
	 * @param negations
	 *            the number of negations
	 */

	public Construction(ArrayList<LogicExpression> terms, Terminal terminal, int negations) {
		this.terms = terms;
		this.terminal = terminal;
		this.negations = negations;
	}

	public String toString() {

		String result = "";

		for (int i = 0; i < negations; i++) {
			result += "-";
		}

		result += "(" + terms.get(0);

		for (int i = 1; i < terms.size(); i++) {
			result += (" " + terminal.toString() + " " + terms.get(i).toString());
		}

		return result + ")";

	}

	@Override
	public ExpressionType getType() {
		return ExpressionType.CONSTRUCTION;
	}

	@Override
	public ArrayList<LogicExpression> getTerms() {
		return terms;
	}

	@Override
	public int getNegations() {
		return negations;
	}

	@Override
	public int getDepth() {

		int deepest = 0;

		for (LogicExpression term : terms) {
			if (term.getDepth() > deepest) {
				deepest = term.getDepth();
			}
		}

		return 1 + deepest;
	}

	@Override
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int hashCode() {
		return negations + terms.hashCode() + terminal.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Construction))
			return false;
		if (obj == this)
			return true;

		Construction exp = (Construction) obj;
		if (exp.getNegations() == negations && exp.getTerms().equals(terms) && exp.getTerminal() == terminal) {
			return true;
		}

		return false;

	}

	@Override
	public LogicExpression addToFront(LogicExpression term, Terminal terminal) {

		int i = 0;
		
		ArrayList<LogicExpression> newTerms = new ArrayList<>();
		newTerms.addAll(terms);

		try {
			if (term.getTerminal() == terminal) {

				for (LogicExpression exp : term.getTerms()) {
					newTerms.add(i, exp);
					i++;
				}
			}

			else {
				newTerms.add(0, term);
			}
		} catch (NullPointerException e) {
			newTerms.add(0, term);
		}

		return new Construction(newTerms, this.terminal);

	}

	@Override
	public LogicExpression addToBack(LogicExpression term, Terminal terminal) {

		ArrayList<LogicExpression> newTerms = new ArrayList<>();
		newTerms.addAll(terms);

		try {
			
			if (term.getTerminal() == terminal && term.getNegations() == 0) {

				for (LogicExpression exp : term.getTerms()) {
					newTerms.add(exp);
				}
			}

			else {
				newTerms.add(term);
			}
		} catch (NullPointerException e) {
			newTerms.add(term);
		}

		return new Construction(newTerms, this.terminal);

	}

	@Override
	public int getSize() {
		return terms.size();
	}

}
