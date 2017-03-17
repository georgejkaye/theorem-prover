package reasoning2;

/**
 * A LogicExpression combining two subexpressions and a terminal
 * @author George Kaye
 *
 */

public class Construction implements LogicExpression {

	private LogicExpression left;
	private LogicExpression right;
	private Terminal terminal;
	private int negations;

	/**
	 * Create a new construction with no negations
	 * @param left the left subexpression
	 * @param terminal the terminal
	 * @param right the right subexpression
	 */
	
	public Construction(LogicExpression left, Terminal terminal, LogicExpression right) {
		this.left = left;
		this.terminal = terminal;
		this.right = right;
		this.negations = 0;
	}

	/**
	 * Create a new construction with a number of negations
	 * @param left the left subexpression
	 * @param terminal the terminal
	 * @param right the right subexpression
	 * @param negations the number of negations
	 */
	
	public Construction(LogicExpression left, Terminal terminal, LogicExpression right, int negations) {
		this.left = left;
		this.terminal = terminal;
		this.right = right;
		this.negations = negations;
	}

	public String toString() {

		String result = "";

		for (int i = 0; i < negations; i++) {
			result += "¬";
		}

		return result + "(" + left.toString() + " " + terminal.toString() + " " + right.toString() + ")";

	}

	@Override
	public ExpressionType getType() {
		return ExpressionType.CONSTRUCTION;
	}

	@Override
	public LogicExpression getLeft() {
		return left;
	}

	@Override
	public LogicExpression getRight() {
		return right;
	}

	@Override
	public int getNegations() {
		return negations;
	}

	@Override
	public int getDepth() {

		int leftDepth = left.getDepth();
		int rightDepth = right.getDepth();

		if (left.getType() == ExpressionType.CONSTRUCTION) {
			if (left.getTerminal().equals(terminal)) {
				leftDepth--;
			}
		}

		if (right.getType() == ExpressionType.CONSTRUCTION) {
			if (right.getTerminal().equals(terminal)) {
				rightDepth--;
			}
		}

		return 1 + Math.max(leftDepth, rightDepth);
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
		return negations + left.hashCode() + right.hashCode() + terminal.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Construction))
			return false;
		if (obj == this)
			return true;

		Construction exp = (Construction) obj;
		if (exp.getNegations() == negations && exp.getLeft().equals(left) && exp.getRight().equals(right) && exp.getTerminal() == terminal){
			return true;
		}

		return false;

	}
	
}
