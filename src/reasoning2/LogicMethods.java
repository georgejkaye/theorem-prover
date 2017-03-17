package reasoning2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reasoning2.LogicExpression.ExpressionType;

/**
 * Class containing methods to maniplicate one or more logic expressions
 * 
 * @author George Kaye
 *
 */

public class LogicMethods {

	public static final Atom TRUE = new Atom("T");
	public static final Atom FALSE = new Atom("FALSE");

	/**
	 * Return the negation of an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the negation of the expression
	 */

	public static LogicExpression negate(LogicExpression exp) {

		int negs = exp.getNegations();

		switch (exp.getType()) {
		case ATOM:
			return new Atom(exp.getName(), negs + 1);
		case CONSTRUCTION:
			return new Construction(exp.getLeft(), exp.getTerminal(), exp.getRight(), negs + 1);
		default:
			return exp;
		}

	}

	/**
	 * Return the conjunction of two expressions
	 * 
	 * @param exp1
	 *            the first expressions
	 * @param exp2
	 *            the second expression
	 * @return the conjunction of the two expressions
	 */

	public static LogicExpression conj(LogicExpression exp1, LogicExpression exp2) {

		return new Construction(exp1, Terminal.AND, exp2);

	}

	/**
	 * Return the disjunction of two expressions
	 * 
	 * @param exp1
	 *            the first expression
	 * @param exp2
	 *            the second expression
	 * @return the disjunction of the two expressions
	 */

	public static LogicExpression disj(LogicExpression exp1, LogicExpression exp2) {

		return new Construction(exp1, Terminal.OR, exp2);

	}

	/**
	 * Return an implication between two expressions
	 * 
	 * @param exp1
	 *            the first expression
	 * @param exp2
	 *            the second expression
	 * @return an expression of the first expression implies the second one
	 */

	public static LogicExpression implies(LogicExpression exp1, LogicExpression exp2) {

		return new Construction(exp1, Terminal.IMPLICATION, exp2);

	}

	/**
	 * Return an equivalence between two expressions
	 * 
	 * @param exp1
	 *            the first expression
	 * @param exp2
	 *            the second expression
	 * @return an expression showing the two expressions are equivalent
	 */

	public static LogicExpression equivalent(LogicExpression exp1, LogicExpression exp2) {

		return new Construction(exp1, Terminal.EQUIVALENCE, exp2);

	}

	/**
	 * Combine one or two expressions with a given terminal If terminal is null
	 * and one of the expressions is not, attempts to return the non null
	 * expression
	 * 
	 * @param exp1
	 *            the first expression
	 * @param exp2
	 *            the second expression
	 * @param terminal
	 *            the terminal
	 * @return the combined expression
	 */

	public static LogicExpression combine(LogicExpression exp1, LogicExpression exp2, Terminal terminal) {

		switch (terminal) {
		case AND:
			return conj(exp1, exp2);
		case OR:
			return disj(exp1, exp2);
		case IMPLICATION:
			return implies(exp1, exp2);
		case EQUIVALENCE:
			return equivalent(exp1, exp2);
		default:
			return null;
		}

	}

	/**
	 * Performs double negative elimination on an expression
	 * 
	 * @param exp
	 *            the input expression
	 * @return the result, or the expression if dne is not possible
	 */

	public static LogicExpression dne(LogicExpression exp) {

		int negs = exp.getNegations();

		if (negs % 2 == 0) {

			switch (exp.getType()) {
			case ATOM:
				return new Atom(exp.getName(), negs - 2);
			case CONSTRUCTION:
				return new Construction(exp.getLeft(), exp.getTerminal(), exp.getRight(), negs - 2);
			default:
				return null;
			}

		}

		return exp;

	}

	/**
	 * Performs commutativity conjunction on an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression commConj(LogicExpression exp) {

		return new Construction(exp.getRight(), Terminal.AND, exp.getLeft());
	}

	/**
	 * Performs associativity conjunction on an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression assocConj(LogicExpression exp) {

		return new Construction(new Construction(exp.getLeft(), Terminal.AND, exp.getRight().getLeft()), Terminal.AND,
				exp.getRight().getRight());

	}

	/**
	 * Performs distributivity conjunction on an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression distConj(LogicExpression exp) {

		return new Construction(new Construction(exp.getLeft(), Terminal.AND, exp.getRight().getLeft()), Terminal.OR,
				new Construction(exp.getLeft(), Terminal.AND, exp.getRight().getRight()));
	}

	/**
	 * Performs commutativity disjunction on an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression commDisj(LogicExpression exp) {

		return new Construction(exp.getRight(), Terminal.OR, exp.getLeft());
	}

	/**
	 * Performs associativity disjunction on an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression assocDisj(LogicExpression exp) {

		return new Construction(new Construction(exp.getLeft(), Terminal.OR, exp.getRight().getLeft()), Terminal.OR,
				exp.getRight().getRight());

	}

	/**
	 * Performs reverse associativity disjunction on an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression assocDisjRev(LogicExpression exp) {
		return new Construction(exp.getLeft().getLeft(), Terminal.OR,
				new Construction(exp.getLeft().getRight(), Terminal.OR, exp.getRight()));
	}

	/**
	 * Performs distributivity disjunction on an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression distDisj(LogicExpression exp) {

		return new Construction(new Construction(exp.getLeft(), Terminal.OR, exp.getRight().getLeft()), Terminal.AND,
				new Construction(exp.getLeft(), Terminal.OR, exp.getRight().getRight()));
	}

	/**
	 * Performs De Morgan's Law on an expression
	 * 
	 * @param exp
	 *            the input expression
	 * @return the result
	 */

	public static LogicExpression deMorgans(LogicExpression exp) {

		switch (exp.getTerminal()) {
		case AND:
			return new Construction(negate(exp.getLeft()), Terminal.OR, negate(exp.getRight()));
		case OR:
			return new Construction(negate(exp.getLeft()), Terminal.AND, negate(exp.getRight()));
		default:
			return exp;
		}

	}

	/**
	 * Transforms an implication into a disjunction
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression implication(LogicExpression exp) {

		return new Construction(negate(exp.getLeft()), Terminal.OR, exp.getRight(), exp.getNegations());

	}

	/**
	 * Transforms an equivalence into a conjunction
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression equivalence(LogicExpression exp) {

		return new Construction(new Construction(exp.getLeft(), Terminal.IMPLICATION, exp.getRight()), Terminal.AND,
				new Construction(exp.getRight(), Terminal.IMPLICATION, exp.getLeft()), exp.getNegations());
	}

	/**
	 * Is one expression a negation of another?
	 * 
	 * @param exp1
	 *            the first expression
	 * @param exp2
	 *            the second expression
	 * @return whether they are negated
	 */

	public static boolean isNegated(LogicExpression exp1, LogicExpression exp2) {

		if (exp1.toString().equals("¬" + exp2.toString()) || exp2.toString().equals("¬" + exp1.toString())) {
			return true;
		}

		return false;
	}

	/**
	 * Transform an expression into Clause Normal Form
	 * 
	 * @param exp
	 *            the expression
	 * @return the expression in Clause Normal Form
	 */

	public static ClauseNormalForm transform(LogicExpression exp) {

		exp = resolveArrows(exp);

		if (TheoremProver.debug)
			System.out.println("After resolving implications and equivalences: " + exp);

		exp = performDeMorgans(exp);

		if (TheoremProver.debug)
			System.out.println("After performing De Morgans: " + exp);

		exp = shuffleExpression(exp);
		
		if (TheoremProver.debug)
			System.out.println("After performing Distributivity Disjunction: " + exp);

		exp = simplifyDisj(exp);

		if (TheoremProver.debug) {
			System.out.println("After simplifying disjuntions: " + exp);
		}

		exp = simplifyConj(exp);

		if (TheoremProver.debug)
			System.out.println("After simplifying conjunctions: " + exp);

		exp = simplifyFalsehoods(exp);

		if (TheoremProver.debug)
			System.out.println("After simplifying falsehoods: " + exp);

		return formClauses(exp);

	}

	/**
	 * Perform deMorgans on an expression recursively, also performing double
	 * negative elimination if necessary
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression performDeMorgans(LogicExpression exp) {

		while (exp.getNegations() >= 2) {

			exp = dne(exp);

		}

		if (exp.getType() == LogicExpression.ExpressionType.ATOM) {
			return exp;
		}

		if (exp.getNegations() == 1) {
			exp = deMorgans(exp);
		}

		return new Construction(performDeMorgans(exp.getLeft()), exp.getTerminal(), performDeMorgans(exp.getRight()),
				exp.getNegations());
	}

	/**
	 * Resolve any implications and equivalences in an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression resolveArrows(LogicExpression exp) {

		while (exp.getNegations() >= 2) {

			exp = dne(exp);

		}

		if (exp.getType() == LogicExpression.ExpressionType.ATOM) {
			return exp;
		}

		if (exp.getTerminal() == Terminal.IMPLICATION) {
			exp = implication(exp);
		}

		if (exp.getTerminal() == Terminal.EQUIVALENCE) {
			exp = equivalence(exp);
		}

		return new Construction(resolveArrows(exp.getLeft()), exp.getTerminal(), resolveArrows(exp.getRight()),
				exp.getNegations());

	}

	/**
	 * Perform distributivity disjunction repeatedly on an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression shuffleExpression(LogicExpression exp) {

		System.out.println("now shuffling: " + exp);

		if (exp.getDepth() == 1) {
			return exp;
		}

		if (exp.getType() == ExpressionType.ATOM) {
			return exp;
		}

		if (exp.getTerminal() == Terminal.OR) {

			if (exp.getRight().getType() == ExpressionType.ATOM
					&& exp.getLeft().getType() == ExpressionType.CONSTRUCTION) {
				exp = commDisj(exp);
				System.out.println("   flipped: " + exp);
			}

			if (exp.getDepth() != 1) {

				System.out.println("   left: " + exp.getLeft());
				System.out.println("   terminal: " + exp.getTerminal());
				System.out.println("   right: " + exp.getRight());

				LogicExpression left = shuffleExpression(exp.getLeft());
				LogicExpression right = shuffleExpression(exp.getRight());

				System.out.println("   new left: " + left);
				System.out.println("   new right: " + right);

				LogicExpression beforeDist = new Construction(left, Terminal.OR, right);
				System.out.println("   before: " + beforeDist);

				LogicExpression dist = distDisj(beforeDist);

				System.out.println("   after: " + dist);

				return dist;

			}

			return exp;
		}

		if (exp.getTerminal() == Terminal.AND) {
			LogicExpression newCons = new Construction(shuffleExpression(exp.getLeft()), Terminal.AND,
					shuffleExpression(exp.getRight()));
			System.out.println("   returning: " + newCons);
			return newCons;
		}

		return null;

	}

	public static boolean ensureInForm(LogicExpression exp) {

		if (exp.getTerminal() == Terminal.OR) {
			return false;
		}

		if (exp.getLeft().getType() == ExpressionType.ATOM) {
			return ensureInForm(exp.getLeft());
		}

		if (exp.getRight().getType() == ExpressionType.ATOM) {
			return ensureInForm(exp.getRight());
		}

		if (exp.getLeft().getDepth() == 1 && exp.getLeft().getTerminal() == Terminal.OR) {
			if (exp.getRight().getDepth() == 1 && exp.getRight().getTerminal() == Terminal.OR) {
				return true;
			}
		}

		LogicExpression left = exp.getLeft();
		LogicExpression right = exp.getRight();

		return (ensureInForm(left) && ensureInForm(right));

	}

	/**
	 * Simplify the disjunctions in an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression simplifyDisj(LogicExpression exp) {

		if (exp.getType() == ExpressionType.ATOM) {
			return exp;
		}

		if (exp.getTerminal() == Terminal.OR) {

			if (exp.getLeft().toString().equals(exp.getRight().toString())) {
				exp = exp.getLeft();
			} else if (exp.getLeft().toString().equals("¬" + exp.getRight().toString())
					|| exp.getRight().toString().equals("¬" + exp.getLeft().toString())) {
				return TRUE;
			}
		}

		if (exp.getType() == ExpressionType.ATOM) {
			return exp;
		}

		return combine(simplifyDisj(exp.getLeft()), simplifyDisj(exp.getRight()), exp.getTerminal());

	}

	/**
	 * Simplify the conjunctions in an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression simplifyConj(LogicExpression exp) {

		if (exp.getType() == LogicExpression.ExpressionType.ATOM) {
			return exp;
		}

		if (exp.getTerminal() == Terminal.AND) {

			if (exp.getLeft().toString().equals("T")) {
				exp = exp.getRight();
			} else if (exp.getRight().toString().equals("T")) {
				exp = exp.getLeft();
			} else if (exp.getLeft().toString().equals(exp.getRight().toString())
					|| exp.getRight().toString().equals(exp.getLeft().toString())) {
				exp = exp.getLeft();
			} else if (exp.getLeft().toString().equals("¬" + exp.getRight().toString())
					|| exp.getRight().toString().equals("¬" + exp.getLeft().toString())) {
				return FALSE;
			}
		}

		return combine(simplifyConj(exp.getLeft()), simplifyConj(exp.getRight()), exp.getTerminal());
	}

	/**
	 * Simplify the falsehoods in an expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression simplifyFalsehoods(LogicExpression exp) {

		if (exp.getType() == ExpressionType.ATOM) {
			return exp;
		}

		if (exp.getTerminal() == Terminal.OR) {

			if (exp.getLeft().toString().equals("FALSE")) {
				exp = exp.getRight();
			}

			if (exp.getRight().toString().equals("FALSE")) {
				exp = exp.getLeft();
			}

		}

		return combine(simplifyFalsehoods(exp.getLeft()), simplifyFalsehoods(exp.getRight()), exp.getTerminal());
	}

	/**
	 * Forms a clause normal form from an expression in conjunction normal form
	 * 
	 * @param exp
	 *            the expression
	 * @return the clause normal form
	 */

	public static ClauseNormalForm formClauses(LogicExpression exp) {

		String expression = exp.toString();
		int i = 0;

		HashSet<HashSet<LogicExpression>> cnf = new HashSet<>();
		HashSet<LogicExpression> clause = new HashSet<>();

		Pattern literals = Pattern.compile("[A-Z]+[0-9]*");
		Matcher matcher = null;

		while (i < expression.length()) {

			int literalStart = expression.length();
			int literalEnd = expression.length();

			try {
				matcher = literals.matcher(expression.substring(i));
				matcher.find();
				literalStart = matcher.start() + i;
				literalEnd = matcher.end() + i;
			} catch (IllegalStateException e) {
			}

			if (expression.charAt(literalStart - 1) == '¬') {
				clause.add(new Atom(expression.substring(literalStart, literalEnd), 1));
			} else {
				clause.add(new Atom(expression.substring(literalStart, literalEnd), 0));
			}

			i = literalEnd;

			try {
				while ((expression.charAt(i) == ')' || expression.charAt(i) == ' ')) {
					i++;
				}

				if (expression.charAt(i) == '&') {
					cnf.add(clause);
					clause = new HashSet<LogicExpression>();
				}

			} catch (StringIndexOutOfBoundsException e) {
			}

			i++;

		}

		cnf.add(clause);

		return new ClauseNormalForm(cnf);

	}

	/**
	 * Perform a resolution proof
	 * 
	 * @param cnf
	 *            the clause normal form
	 */

	@SuppressWarnings("unchecked") // types will always be correct
	public static ResolutionProof resolutionProof(ClauseNormalForm cnf) {

		boolean box = false;
		boolean same = false;

		HashSet<HashSet<LogicExpression>> resolved = new HashSet<>();
		ResolutionProof proof = new ResolutionProof();

		for (HashSet<LogicExpression> clause : cnf.getCNF()) {
			proof.addLine(new ProofLine(clause, ""));
		}

		// while the two terminating conditions do not hold
		while (!box && !same) {

			ComplementingPair pair = cnf.findUnresolvedPair(resolved);

			// if there are no complementing pairs we cannot resolve any further
			if (pair != null) {

				String resolvents = "";

				resolved.add((HashSet<LogicExpression>) pair.getFirst().clone());
				resolved.add((HashSet<LogicExpression>) pair.getSecond().clone());

				HashSet<LogicExpression> c1 = (HashSet<LogicExpression>) pair.getFirst().clone();
				HashSet<LogicExpression> c2 = (HashSet<LogicExpression>) pair.getSecond().clone();

				LogicExpression a = null;
				LogicExpression b = null;

				for (LogicExpression exp : c1) {

					for (LogicExpression exp1 : c2) {

						if (LogicMethods.isNegated(exp, exp1)) {

							resolvents = "Res " + proof.getLine(c1) + ", " + proof.getLine(c2);

							a = exp;
							b = exp1;

							break;
						}
					}

				}

				c1.remove(a);
				c2.remove(b);

				HashSet<LogicExpression> c = new HashSet<>();
				c.addAll(c1);
				c.addAll(c2);

				if (!cnf.getCNF().contains(c)) {
					proof.addLine(new ProofLine(c, resolvents));
					cnf.add(c);
				}

				// if there is no resolvent we have a contradiction!
				if (c.isEmpty()) {
					box = true;
					proof.setResult(Result.UNSATISFIABLE);

				}

			} else {
				same = true;
				proof.setResult(Result.SATISFIABLE);
			}

		}

		return proof;

	}

	public static LogicModel davisPutnam(ClauseNormalForm cnf) {

		LogicModel model = new LogicModel();

		HashSet<HashSet<LogicExpression>> cnf1 = cnf.getCNF();

		boolean empty = false;

		// unit propogation
		for (HashSet<LogicExpression> clause : cnf1) {

			if (clause.size() == 1) {

				for (LogicExpression exp : clause) {

					boolean trueAtom = true;

					if (exp.getNegations() == 1) {
						trueAtom = false;
					}

					model.addAtom(exp, trueAtom);

					cnf1 = reduceCNF(exp, cnf1);

				}

			}

		}

		// pure literal
		ArrayList<LogicExpression> onePolarity = new ArrayList<>();
		ArrayList<LogicExpression> twoPolarity = new ArrayList<>();

		for (HashSet<LogicExpression> clause : cnf1) {

			for (LogicExpression exp : clause) {

				if (!onePolarity.contains(exp) && !twoPolarity.contains(exp)) {
					onePolarity.add(exp);
				} else if (!twoPolarity.contains(exp) && onePolarity.contains(exp)) {

					for (HashSet<LogicExpression> clause1 : cnf1) {
						for (LogicExpression exp1 : clause1) {

							if (isNegated(exp, exp1)) {
								onePolarity.remove(exp);
								twoPolarity.add(exp);
								twoPolarity.add(exp1);
							}
						}
					}

				}

			}

		}

		return model;

	}

	/**
	 * Propogates a unit throughout a clause normal form, removing clauses that
	 * contain the positive version and removing the negative version from
	 * clauses where it appears
	 * 
	 * @param unit
	 *            the unit to be propogated
	 * @param cnf
	 *            the clause normal form (in raw form)
	 * @return the new raw clause normal form
	 */

	public static HashSet<HashSet<LogicExpression>> reduceCNF(LogicExpression unit,
			HashSet<HashSet<LogicExpression>> cnf) {

		HashSet<HashSet<LogicExpression>> newcnf = new HashSet<>();

		for (HashSet<LogicExpression> clause : cnf) {

			if (!clause.contains(unit)) {

				HashSet<LogicExpression> newclause = new HashSet<>();

				for (LogicExpression atom : clause) {

					if (!isNegated(atom, unit)) {

						newclause.add(atom);
					}
				}

				newcnf.add(newclause);

			}

		}

		return newcnf;

	}

}
