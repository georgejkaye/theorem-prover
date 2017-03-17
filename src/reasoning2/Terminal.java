package reasoning2;

public enum Terminal {
	NOT, AND, OR, IMPLICATION, EQUIVALENCE;

	public String toString() {
		switch (this) {
		case NOT:
			return "¬";
		case AND:
			return "&&";
		case OR:
			return "||";
		case IMPLICATION:
			return "->";
		case EQUIVALENCE:
			return "<->";
		default:
			return "";
		}
	}

	public static Terminal fromString(String chars) {
		switch (chars) {
		case "¬":
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
