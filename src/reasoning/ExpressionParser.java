package reasoning;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class containing parsing methods
 * 
 * @author George Kaye
 *
 */

public class ExpressionParser {

	/**
	 * Parse a file into a string
	 * 
	 * @param path
	 *            the path of a file
	 * @return the string of the file's contents
	 */

	public static String parseFile(String path) {

		String expression = "";

		try {

			BufferedReader reader = new BufferedReader(new FileReader(path));

			expression = reader.readLine();

			reader.close();

		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Something bad happened");
			System.exit(1);
		}

		return expression;

	}

	/**
	 * Parses in a file in DIMACS format
	 * 
	 * @param path
	 *            the path
	 * @return the string of DIMACS input
	 */

	public static String parseDIMACS(String path) {
		String expression = "";

		try {

			BufferedReader reader = new BufferedReader(new FileReader(path));

			String line = null;

			while (((line = reader.readLine()) != null)) {
				if (!(line.charAt(0) == 'c') && !(line.charAt(0) == 'p')) {
					expression += " " + line;
				}
			}

			reader.close();

		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Something bad happened");
			System.exit(1);
		}

		return expression;
	}

	/**
	 * Parse a logic expression from a string, negating a conclusion if required
	 * 
	 * @param expression
	 *            the expression as a string
	 * @param conc
	 *            if the expression has a conclusion to combine with premises
	 * @param negate
	 *            whether to negate the conclusion
	 * @return the expression as a LogicExpression
	 */

	public static LogicExpression parseExpression(String expression, boolean conc, boolean negate) {

		LogicExpression lastExpression = null;
		Terminal lastTerminal = null;
		Matcher matcher = null;

		if (conc) {

			int i = 0;
			int j = 0;

			LogicExpression result = null;

			while (i < expression.length()) {

				if (expression.charAt(i) == '}' || expression.charAt(i) == '{' || expression.charAt(i) == ' ') {
					i++;
				} else if (expression.charAt(i) == ':') {
					i++;

					while (expression.charAt(i) == ' ') {
						i++;
					}

					LogicExpression conclusion = parseExpression(expression.substring(i, expression.length()), false,
							false);

					if (negate) {
						conclusion = LogicMethods.negate(conclusion);
					}

					if (result == null) {
						return conclusion;
					} else {
						return LogicMethods.combine(result, conclusion, Terminal.AND);

					}
				} else {

					j = i;

					while (expression.charAt(j) != ',' && expression.charAt(j) != '}') {
						j++;
					}

						LogicExpression newOne = parseExpression(expression.substring(i, j), false, false);

						if (result != null) {
							result = LogicMethods.combine(result, newOne, Terminal.AND);
						} else {
							result = newOne;
						}

					i = j;
					i++;
				}
			}
		}

		// regular expressions for parts of the expression
		Pattern literals = Pattern.compile("[A-Z]+[0-9]*");
		Pattern terminals = Pattern.compile("&|\\||->|<->");

		int i = 0;

		while (i < expression.length()) {

			if (expression.charAt(i) == ' ') {
				i++;
			} else if (expression.charAt(i) == '-' && expression.charAt(i + 1) != '>') {
				i++;
			} else if (expression.charAt(i) == '(') {

				int j = i + 1;
				boolean foundEndBracket = false;

				int netBrackets = 1;

				while (!foundEndBracket) {

					if (expression.charAt(j) == '(') {

						netBrackets++;

					} else if (expression.charAt(j) == ')') {
						netBrackets--;
					}

					if (netBrackets == 0) {
						foundEndBracket = true;
					}

					j++;

				}

				LogicExpression subExpression = ExpressionParser.parseExpression(expression.substring(i + 1, j - 1),
						false, false);

				int k = 1;

				try {
					while (expression.charAt(i - k) == '-') {
						subExpression = LogicMethods.negate(subExpression);
						k++;
					}
				} catch (StringIndexOutOfBoundsException e) {
				}

				if (lastExpression != null && subExpression != null && lastTerminal != null) {
					lastExpression = LogicMethods.combine(lastExpression, subExpression, lastTerminal);
				} else {
					lastExpression = subExpression;
				}

				i = j;

			} else {

				int literalStart = expression.length();
				int literalEnd = expression.length();
				int terminalStart = expression.length();
				int terminalEnd = expression.length();

				try {
					matcher = literals.matcher(expression.substring(i));
					matcher.find();
					literalStart = matcher.start() + i;
					literalEnd = matcher.end() + i;
				} catch (IllegalStateException e) {
				}

				try {
					matcher = terminals.matcher(expression.substring(i));
					matcher.find();
					terminalStart = matcher.start() + i;
					terminalEnd = matcher.end() + i;
				} catch (IllegalStateException e) {
				}

				if (literalStart < terminalStart) {

					LogicExpression newExpression = new Atom(expression.substring(literalStart, literalEnd));
					int j = 1;

					try {
						while (expression.charAt(literalStart - j) == '-') {
							newExpression = LogicMethods.negate(newExpression);
							j++;
						}
					} catch (StringIndexOutOfBoundsException e) {

					}

					if (lastTerminal != null) {
						lastExpression = LogicMethods.combine(lastExpression, newExpression, lastTerminal);
					} else {
						lastExpression = newExpression;
					}

					i = literalEnd;

				} else {

					lastTerminal = Terminal.fromString(expression.substring(terminalStart, terminalEnd));
					i = terminalEnd;
				}

			}

		}

		return lastExpression;
	}

	/**
	 * Convert a string of DIMACS into a clause normal form
	 * @param string the string of DIMACS
	 * @return the clause normal form from that string
	 */
	
	public static ClauseNormalForm convertDIMACS(String string) {

		HashMap<Integer, LogicExpression> legend = new HashMap<>();

		HashSet<HashSet<LogicExpression>> cnf = new HashSet<>();
		HashSet<LogicExpression> clause = new HashSet<>();

		int i = 0;

		while (i < string.length()) {

			if (string.charAt(i) == ' ' || string.charAt(i) == '-') {
				i++;
			} else if (string.charAt(i) == '0') {
				cnf.add(clause);
				clause = new HashSet<>();
				i++;
			} else {

				Integer y = new Integer(Integer.parseInt(string.substring(i, i + 1)));

				boolean negated = false;

				try {
					if (string.charAt(i - 1) == '-') {
						negated = true;
					}
				} catch (StringIndexOutOfBoundsException e) {
				}

				if (!legend.containsKey(y)) {
					legend.put(y, new Atom("X" + y));
				}

				if (negated) {
					clause.add(LogicMethods.negate(legend.get(y)));
				} else {
					clause.add(legend.get(y));
				}

				i++;

			}

		}
		
		return new ClauseNormalForm(cnf);

	}

}
