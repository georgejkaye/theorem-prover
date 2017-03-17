package reasoning2;

public class TheoremProver {

	public static boolean debug = false;
	public static boolean time = false;
	public static String fileName;
	public static String mode;
	public static String input;
	public static LogicExpression parsedExpression;

	public static void main(String[] args) {

		fileName = "";
		mode = "";

		try {
			fileName = args[0];
			mode = args[1];
			
			if(args.length == 3){
				if(args[2].equals("-debug")){
					debug = true;
				} else if(args[2].equals("-time")) {
					time = true;
				}
			} else if(args.length == 4){
				
				if(args[2].equals("-time")) {
					time = true;
				}
			
				if(args[3].equals("-debug")){
					debug = true;
				}
				
				
			}
			

			
		} catch (ArrayIndexOutOfBoundsException e) {
			if (debug) {
				fileName = "src/test/test9.txt";
				mode = "-res";
			} else {
				System.out.println("Usage: java TheoremProver <file> <mode> <time> <debug>");
				System.out.println("Modes supported:");
				System.out.println("   -cnf    Convert an expression into clause normal form");
				System.out.println("   -res    Perform a resolution proof");
				System.out.println("   -sat    Use DPLL to find a model");
				System.out.println("Other arguments (optional):");
				System.out.println("   -time   Compute time taken for algorithms");
				System.out.println("   -debug  Use debug mode");
				System.exit(1);
			}
		}
		
		input = ExpressionParser.parseFile(fileName);
		
		if (debug)
			System.out.println("File successfully located");

		if (debug)
			System.out.println("Input expression: " + input);
		
		parseExpression();
		
		if (mode.equals("-cnf")) {

			if(debug)
				System.out.println("Converting to clause normal form");
			
			convertToCNF();

		}
		
		if (mode.equals("-res")){
			
			if(debug)
				System.out.println("Performing a resolution proof");
			
			resolutionProof();
		}
		
		if (mode.equals("-sat")){
			
			if(debug)
				System.out.println("Performing DPLL to find a model");
			
			davisPutnam();
		}
	}

	/**
	 * Parse an expression
	 */
	
	public static void parseExpression(){
		
		parsedExpression = ExpressionParser.parseExpression(input);

		if (debug)
			System.out.println("Parsed expression: " + parsedExpression);
	}
	
	/**
	 * Convert an expression into clause normal form
	 */
	
	public static void convertToCNF() {
		
		ClauseNormalForm cnf = LogicMethods.transform(parsedExpression);

		System.out.println("Clause Normal Form: " + cnf);
	}
	
	/**
	 * Perform a resolution proof
	 */
	
	public static void resolutionProof(){
		
		parsedExpression = LogicMethods.negate(parsedExpression);
		
		if(debug)
			System.out.println("After negation: " + parsedExpression);
		
		ClauseNormalForm cnf = LogicMethods.transform(parsedExpression);
		
	    if(debug)
	    	System.out.println("Clause normal form: " + cnf);
	    
	    ResolutionProof proof = LogicMethods.resolutionProof(cnf);
	    
	    System.out.println(proof);
	    
	    if(proof.getResult() == Result.SATISFIABLE){
	    	System.out.println("Initial expression is UNSATISFIABLE");
	    }
	    else{
	    	System.out.println("Initial expression is SATISFIABLE");
	    }
	    
	}
	
	public static void davisPutnam(){
		
		ClauseNormalForm cnf = LogicMethods.transform(parsedExpression);
		
		 if(debug)
		    	System.out.println("Clause normal form: " + cnf);
		
		LogicModel model = LogicMethods.davisPutnam(cnf);
	}

}
