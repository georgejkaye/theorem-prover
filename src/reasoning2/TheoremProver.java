package reasoning2;

import java.lang.management.*;

/**
 * Main class for the Theorem Prover
 * @author George Kaye
 *
 */

public class TheoremProver {

	public static boolean debug = false;
	public static boolean time = false;
	public static boolean dimacs = false;
	public static String fileName;
	public static String mode;
	public static String input;
	public static LogicExpression parsedExpression;

	public static void main(String[] args) {

		fileName = "";
		mode = "";

		try {
			fileName = args[1];
			mode = args[0];

			if (args.length == 3) {
				if (args[2].equals("-debug")) {
					debug = true;
				}

				if (args[2].equals("-dimacs")) {
					dimacs = true;
				}

			}

			if (args.length == 4) {
				if (args[2].equals("-debug") || args[3].equals("-debug")) {
					debug = true;
				}

				if (args[2].equals("-dimacs") || args[3].equals("-dimacs")) {
					dimacs = true;
				}

			}

		} catch (ArrayIndexOutOfBoundsException e) {
			if (debug) {
				fileName = "src/test/test11.txt";
				mode = "-cmp";
				dimacs = false;
			} else {
				wrongArguments();
			}
		}

		if (!dimacs) {
			input = ExpressionParser.parseFile(fileName);
		} else {
			input = ExpressionParser.parseDIMACS(fileName);
		}

		if (debug)
			System.out.println("File successfully located");

		System.out.println("Input expression: " + input);

		if (!dimacs) {
			parseExpression();
		}

		/**
		 * Simply convert the initial expression to clause normal form (no
		 * negation)
		 */

		if (mode.equals("-cnf")) {

			if (debug)
				System.out.println("Converting to clause normal form");

			convertToCNF(parsedExpression);

			System.exit(0);

		}

		/**
		 * Perform a resolution proof, negating the initial expression and
		 * attempting to find a contradiction: if the negation is unsat the
		 * initial expression is sat
		 */

		if (mode.equals("-res")) {

			if (dimacs) {
				System.out.println("Cannot perform resolution with DIMACS");
				System.exit(0);
			}

			if (debug)
				System.out.println("Performing a resolution proof");

			parsedExpression = LogicMethods.negate(parsedExpression);

			if (debug)
				System.out.println("After negation: " + parsedExpression);

			ClauseNormalForm cnf = convertToCNF(parsedExpression);

			if (debug)
				System.out.println("Clause normal form: " + cnf);

			Result result = resolutionProof(cnf);

			if (result == Result.UNSATISFIABLE) {
				System.out.println("Initial expression is UNSATISFIABLE");
			} else {
				System.out.println("Initial expression is SATISFIABLE");
			}

			System.exit(0);
		}

		/**
		 * Peforms DPLL on the parsed expression, DPLL does not require any
		 * negations since it finds a model of the initial expression
		 */

		if (mode.equals("-sat")) {

			if (debug)
				System.out.println("Performing DPLL to find a model");

			ClauseNormalForm cnf = null;

			if (dimacs) {
				cnf = ExpressionParser.convertDIMACS(input);

			} else {
				cnf = convertToCNF(parsedExpression);

			}

			if (debug)
				System.out.println("Clause normal form: " + cnf);

			Result result = davisPutnam(cnf);

			if (result == Result.UNSATISFIABLE) {
				System.out.println("Initial expression is UNSATISFIABLE");
			} else {
				System.out.println("Initial expression is SATISFIABLE");
			}

			System.exit(0);
		}

		/**
		 * Compare the time of DPLL and resolution, trying to disprove the
		 * negation with DPLL and then performing resolution
		 */

		if (mode.equals("-cmp")) {

			if (dimacs) {
				System.out.println("Cannot compare runtimes with DIMACS");
				System.exit(0);
			}

			if (debug)
				System.out.println("Comparing the runtimes of Resolution and DPLL");

			ClauseNormalForm negated = convertToCNF(LogicMethods.negate(parsedExpression));

			long beforeDPLL = System.nanoTime();
			Result result = davisPutnam(negated);
			long afterDPLL = System.nanoTime();
			long DPLLTime = afterDPLL - beforeDPLL;

			if (result == Result.UNSATISFIABLE) {
				System.out.println("DPLL: initial expression SATISFIABLE");
			} else {
				System.out.println("DPLL: initial expression UNSATISFIABLE");
			}

			long beforeRes = System.nanoTime();
			result = resolutionProof(negated);
			long afterRes = System.nanoTime();
			long resTime = afterRes - beforeRes;

			if (result == Result.SATISFIABLE) {
				System.out.println("Resolution: initial expression SATISFIABLE");
			} else {
				System.out.println("DPLL: initial expression UNSATISFIABLE");
			}

			System.out.println("DPLL time: " + DPLLTime);
			System.out.println("Resolution time: " + resTime);

			System.exit(0);

		}

		wrongArguments();
	}

	/**
	 * Display the cheatsheet if the arguments are wrong
	 */

	public static void wrongArguments() {
		System.out.println("Usage: java TheoremProver <mode> <file> -debug (optional) -dimacs (optional)");
		System.out.println("Modes supported:");
		System.out.println("   -cnf    Convert an expression into clause normal form");
		System.out.println("   -res    Perform a resolution proof");
		System.out.println("   -sat    Use DPLL to find a model");
		System.out.println("   -cmp    Compare the runtimes of Resolution and DPLL");
		System.out.println("Other arguments (optional):");
		System.out.println("   -debug  Use debug mode");
		System.out.println("   -dimacs Parse a dimacs file");
		System.exit(1);
	}

	/**
	 * Parse an expression
	 */

	public static void parseExpression() {
		parsedExpression = ExpressionParser.parseExpression(input);

		if (debug)
			System.out.println("Parsed expression: " + parsedExpression);
	}

	/**
	 * Convert an expression into clause normal form
	 * 
	 * @param expression
	 *            the logic expression
	 * @return the clause normal form
	 */

	public static ClauseNormalForm convertToCNF(LogicExpression expression) {

		ClauseNormalForm cnf = LogicMethods.generateClauseNormalForm(expression);

		System.out.println("Clause Normal Form: " + cnf);

		return cnf;
	}

	/**
	 * Convert an expression in DIMACS to clause normal form
	 * 
	 * @param dimacs
	 *            the dimacs expression
	 * @return the clause normal form
	 */

	public static ClauseNormalForm convertToCNF(String dimacs) {

		ClauseNormalForm cnf = ExpressionParser.convertDIMACS(dimacs);

		System.out.println("Clause Normal Form: " + cnf);

		return cnf;
	}

	/**
	 * Perform a resolution proof
	 * 
	 * @param cnf
	 *            the clause normal form
	 * @return the result
	 */

	public static Result resolutionProof(ClauseNormalForm cnf) {

		if (debug)
			System.out.println("Clause normal form: " + cnf);

		ResolutionProof proof = LogicMethods.resolutionProof(cnf);

		System.out.println(proof);

		if (proof.getResult() == Result.SATISFIABLE) {
			return Result.UNSATISFIABLE;
		} else {
			return Result.SATISFIABLE;
		}

	}

	/**
	 * Perform DPLL
	 * 
	 * @param cnf
	 *            the clause normal form
	 * @return the result
	 */

	public static Result davisPutnam(ClauseNormalForm cnf) {

		LogicModel model = LogicMethods.davisPutnam(cnf);

		if (model == null) {
			return Result.UNSATISFIABLE;
		} else {
			System.out.println("Model: " + model);
			return Result.SATISFIABLE;
		}
	}

	/**
	 * Get CPU time in nanoseconds. Sourced from:
	 * http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking
	 */

	public static long getCPUTime() {

		ThreadMXBean bean = ManagementFactory.getThreadMXBean();

		System.out.println(bean.isCurrentThreadCpuTimeSupported());

		return bean.getCurrentThreadCpuTime();
	}

}
