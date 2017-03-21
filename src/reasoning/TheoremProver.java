package reasoning;

/**
 * Main class for the Theorem Prover
 * 
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
	public static String format;
	public static LogicExpression parsedExpression;

	public static void main(String[] args) {

		fileName = "";
		mode = "";
		format = "";

		try {
			fileName = args[2];
			mode = args[0];
			format = args[1];

			if (args.length == 4) {
				if (args[3].equals("-debug")) {
					debug = true;
				}

			}

		} catch (ArrayIndexOutOfBoundsException e) {
			wrongArguments();
		}

		if (!format.equals("-dimacs")) {
			input = ExpressionParser.parseFile(fileName);
		} else {
			input = ExpressionParser.parseDIMACS(fileName);
		}

		if (debug)
			System.out.println("File successfully located");

		System.out.println("Input expression:\n" + input);

		/**
		 * Simply convert the initial expression to clause normal form (no
		 * negation)
		 */

		if (mode.equals("-cnf")) {

			prepareCNF();
			System.exit(0);

		}

		/**
		 * Perform a resolution proof, negating the initial expression/setlusion
		 * and attempting to find a contradiction: if the negation is unsat the
		 * initial expression is sat
		 */

		if (mode.equals("-res")) {

			performResolution();
			System.exit(0);
		}

		/**
		 * Peforms DPLL on the parsed expression, DPLL does not require any
		 * negations since it finds a model of the initial expression
		 */

		if (mode.equals("-sat")) {

			performDPLL();
			System.exit(0);
		}

		/**
		 * Compare the time of DPLL and resolution, trying to disprove the
		 * negation with DPLL and then performing resolution
		 */

		if (mode.equals("-cmp")) {

			compareMethods();
			System.exit(0);

		}

		wrongArguments();
	}

	/**
	 * Display the cheatsheet if the arguments are wrong
	 */

	public static void wrongArguments() {
		System.out.println("Usage: java -jar TheoremProver.jar <mode> <format> <file> ");
		System.out.println("Modes supported:");
		System.out.println("   -cnf         Convert an expression into clause normal form");
		System.out.println("   -res         Perform a resolution proof");
		System.out.println("   -sat         Use DPLL to find a model");
		System.out.println("   -cmp         Compare the runtimes of Resolution and DPLL");
		System.out.println("Formats supported:");
		System.out.println("   -exp         Ordinary linear expression format");
		System.out.println("                (((A | B) & C) -> D)");
		System.out.println("   -set        	A set of premises and a conclusion");
		System.out.println("                {(A | B), (C)} : D");
		System.out.println("   -dimacs      DIMACS file format (DPLL and CNF only)");
		System.out.println("Other arguments (optional):");
		System.out.println("   -debug       Use debug mode");
		System.exit(1);
	}

	/**
	 * Prepare an input for conversion to clause normal form
	 */

	public static void prepareCNF() {
		if (format.equals("-set")) {

			if (debug)
				System.out.println("Parsing expression, combining premises with non-negated setlusion");

			parsedExpression = ExpressionParser.parseExpression(input, true, false);

		} else if (format.equals("-exp")) {

			if (debug)
				System.out.println("Parsing expression");

			parsedExpression = ExpressionParser.parseExpression(input, false, false);
		}

		if (debug)
			System.out.println("Parsed expression:\n" + parsedExpression);

		if (debug)
			System.out.println("Converting to clause normal form");

		if (!format.equals("-dimacs")) {
			convertToCNF(parsedExpression);
		} else {
			convertToCNF(input);
		}
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

		System.out.println("Clause Normal Form:\n" + cnf);

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

		System.out.println("Clause Normal Form:\n" + cnf);

		return cnf;
	}

	/**
	 * Perform resolution with the input string
	 */

	public static Result performResolution() {

		if (format.equals("-dimacs")) {
			System.out.println("Cannot perform resolution with DIMACS");
			System.exit(0);
		}

		if (!format.equals("-set")) {

			System.out.println("Parsing expression and negating it");

			parsedExpression = ExpressionParser.parseExpression(input, false, false);
			parsedExpression = LogicMethods.negate(parsedExpression);

		} else {

			System.out.println("Parsing expression and negating conclusion");

			parsedExpression = ExpressionParser.parseExpression(input, true, true);
		}

		if (debug)
			System.out.println("Parsed expression with negation:\n" + parsedExpression);

		if (debug)
			System.out.println("Converting to clause normal form");

		ClauseNormalForm cnf = convertToCNF(parsedExpression);

		System.out.println("Performing a resolution proof");

		Result result = resolutionProof(cnf);

		if (result == Result.UNSATISFIABLE) {
			System.out.println("Initial expression is UNSATISFIABLE");
		} else {
			System.out.println("Initial expression is SATISFIABLE");
		}

		return result;
	}

	/**
	 * Perform a resolution proof
	 * 
	 * @param cnf
	 *            the clause normal form
	 * @return the result
	 */

	public static Result resolutionProof(ClauseNormalForm cnf) {

		ResolutionProof proof = LogicMethods.resolutionProof(cnf);

		System.out.println(proof);

		if (proof.getResult() == Result.SATISFIABLE) {
			return Result.UNSATISFIABLE;
		} else {
			return Result.SATISFIABLE;
		}

	}

	/**
	 * Perform DPLL with the input string
	 */

	public static Result performDPLL() {

		if (format.equals("-exp")) {

			parsedExpression = ExpressionParser.parseExpression(input, false, false);

		} else if (format.equals("-set")) {

			parsedExpression = ExpressionParser.parseExpression(input, true, false);
		}

		if (debug)
			System.out.println("Converting to clause normal form");

		ClauseNormalForm cnf = null;

		if (format.equals("-dimacs")) {
			cnf = convertToCNF(input);

		} else {
			cnf = convertToCNF(parsedExpression);

		}

		System.out.println("Performing DPLL to find a model");

		Result result = davisPutnam(cnf);

		if (result == Result.UNSATISFIABLE) {
			System.out.println("Initial expression is UNSATISFIABLE");
		} else {
			System.out.println("Initial expression is SATISFIABLE");
		}

		return result;
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
			System.out.println("Model:\n" + model);
			return Result.SATISFIABLE;
		}
	}

	/**
	 * Compare the runtimes of resolution and DPLL
	 */

	public static void compareMethods() {

		if (format.equals("-dimacs")) {
			System.out.println("Cannot compare runtimes with DIMACS");
			System.exit(0);
		}

		if (format.equals("-exp")) {

			parsedExpression = ExpressionParser.parseExpression(input, false, true);
			parsedExpression = LogicMethods.negate(parsedExpression);

		} else if (format.equals("-set")) {

			parsedExpression = ExpressionParser.parseExpression(input, true, true);
		}

		ClauseNormalForm cnf = convertToCNF(parsedExpression);

		if (debug)
			System.out.println("Converting to clause normal form");

		System.out.println("Comparing the runtimes of Resolution and DPLL");

		long beforeDPLL = System.nanoTime();
		Result result = davisPutnam(cnf);
		long afterDPLL = System.nanoTime();
		long DPLLTime = afterDPLL - beforeDPLL;

		if (result == Result.UNSATISFIABLE) {
			System.out.println("DPLL: initial expression SATISFIABLE");
		} else {
			System.out.println("DPLL: initial expression UNSATISFIABLE");
		}

		long beforeRes = System.nanoTime();
		result = resolutionProof(cnf);
		long afterRes = System.nanoTime();
		long resTime = afterRes - beforeRes;

		if (result == Result.SATISFIABLE) {
			System.out.println("Resolution: initial expression SATISFIABLE");
		} else {
			System.out.println("Resolution: initial expression UNSATISFIABLE");
		}

		System.out.println("DPLL time: " + DPLLTime / 1000 + "ms");
		System.out.println("Resolution time: " + resTime / 1000 + "ms");
	}

}
