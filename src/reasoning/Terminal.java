package reasoning;

/**
 * An enum containing all the terminals
 * @author George Kaye
 *
 */

public enum Terminal {
	NOT, AND, OR, IMPLICATION, EQUIVALENCE;

	public String toString() {
		switch (this) {
		case NOT:
			return "-";
		case AND:
			return "&";
		case OR:
			return "|";
		case IMPLICATION:
			return "->";
		case EQUIVALENCE:
			return "<->";
		default:
			return "";
		}
	}

	/**
	 * Determine what terminal something is from a string
	 * @param chars the input string
	 * @return the terminal
	 */
	
	public static Terminal fromString(String chars) {
		switch (chars) {
		case "-":
			return NOT;
		case "&":
			return AND;
		case "|":
			return OR;
		case "->":
			return IMPLICATION;
		case "<->":
			return EQUIVALENCE;
		default:
			return null;
		}
	}
}
