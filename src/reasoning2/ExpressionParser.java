package reasoning2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class containing parsing methods
 * @author George Kaye
 *
 */

public class ExpressionParser {

	/**
	 * Parse a file into a string
	 * @param path the path of a file
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
	 * Parse a logic expression from a string
	 * @param expression the expression as a string
	 * @return the expression as a LogicExpression
	 */
	
	public static LogicExpression parseExpression(String expression) {

		LogicExpression lastExpression = null;
		Terminal lastTerminal = null;
		Matcher matcher = null;

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

				LogicExpression subExpression = ExpressionParser.parseExpression(expression.substring(i + 1, j - 1));

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

}
