package reasoning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reasoning.LogicExpression.ExpressionType;

/**
 * Class containing methods to maniplicate one or more logic expressions
 * 
 * @author George Kaye
 *
 */

public class LogicMethods {

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
			return new Construction(exp.getTerms(), exp.getTerminal(), negs + 1);
		default:
			return exp;
		}

	}

	/**
	 * Return a list of expressions, with each of them negated
	 * 
	 * @param list
	 *            the list of expressions
	 * @return the list of negated expressions
	 */

	public static ArrayList<LogicExpression> negate(ArrayList<LogicExpression> list) {

		ArrayList<LogicExpression> negated = new ArrayList<>();

		for (LogicExpression exp : list) {

			negated.add(negate(exp));

		}

		return negated;

	}

	/**
	 * Combine two expressions with a given terminal. If the terminal is the
	 * same as the one in the first expression the new expression is added to
	 * that one, otherwise a new construction is made combining the two
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

		ArrayList<LogicExpression> terms = new ArrayList<>();

		if (terminal.equals(Terminal.IMPLICATION) || terminal.equals(Terminal.EQUIVALENCE)) {

			terms.add(exp1);
			terms.add(exp2);

		} else if (exp1.getType() == ExpressionType.ATOM && exp2.getType() == ExpressionType.ATOM) {

			terms.add(exp1);
			terms.add(exp2);

		} else if (exp1.getType() == ExpressionType.ATOM) {

			if (exp2.getTerminal() == terminal && exp2.getNegations() == 0) {
				return exp2.addToFront(exp1, terminal);

			} else {
				terms.add(exp1);
				terms.add(exp2);
			}

		} else if (exp2.getType() == ExpressionType.ATOM) {

			if (exp1.getTerminal() == terminal && exp1.getNegations() == 0) {
				return exp1.addToBack(exp2, terminal);

			} else {
				terms.add(exp1);
				terms.add(exp2);
			}

		} else if (exp1.getTerminal() == terminal && exp1.getNegations() == 0) {
			return exp1.addToBack(exp2, terminal);
		} else {

			terms.add(exp1);
			terms.add(exp2);

		}

		return new Construction(terms, terminal);

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
				return new Construction(exp.getTerms(), exp.getTerminal(), negs - 2);
			default:
				return null;
			}

		}

		return exp;

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
			return new Construction(negate(exp.getTerms()), Terminal.OR);
		case OR:
			return new Construction(negate(exp.getTerms()), Terminal.AND);
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

		ArrayList<LogicExpression> terms = exp.getTerms();

		LogicExpression left = terms.get(0);
		LogicExpression right = terms.get(1);

		ArrayList<LogicExpression> newTerms = new ArrayList<>();

		newTerms.add(negate(left));
		newTerms.add(right);

		return new Construction(newTerms, Terminal.OR, exp.getNegations());

	}

	/**
	 * Transforms an equivalence into a conjunction
	 * 
	 * @param exp
	 *            the expression
	 * @return the result
	 */

	public static LogicExpression equivalence(LogicExpression exp) {

		ArrayList<LogicExpression> terms = exp.getTerms();
		ArrayList<LogicExpression> newTerms = new ArrayList<>();

		LogicExpression left = terms.get(0);
		LogicExpression right = terms.get(1);

		ArrayList<LogicExpression> lhs = new ArrayList<>();
		ArrayList<LogicExpression> rhs = new ArrayList<>();

		lhs.add(left);
		lhs.add(right);

		rhs.add(right);
		rhs.add(left);

		newTerms.add(new Construction(lhs, Terminal.IMPLICATION));
		newTerms.add(new Construction(rhs, Terminal.IMPLICATION));

		return new Construction(newTerms, Terminal.AND, exp.getNegations());
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

		if (exp1.toString().equals(Terminal.NOT.toString() + exp2.toString())
				|| exp2.toString().equals(Terminal.NOT.toString() + exp1.toString())) {
			return true;
		}

		return false;
	}

	/**
	 * Generate a clause normal form from a logic expression
	 * 
	 * @param exp
	 *            the expression
	 * @return the clause normal form
	 */

	public static ClauseNormalForm generateClauseNormalForm(LogicExpression exp) {

		// transform the expression into conjunctive normal form
		exp = transform(exp);

		if (TheoremProver.debug) {
			System.out.println("Conjunctive Normal Form: " + exp);
		}

		return findClauses(exp.toString());
	}

	/**
	 * Transform an expression into conjunctive normal form
	 * 
	 * @param exp
	 *            the expression
	 * @return the expression in conjunctive normal form
	 */

	public static LogicExpression transform(LogicExpression exp) {

		// algorithm adapted from
		// https://www.cs.jhu.edu/~jason/tutorials/convert-to-CNF.html

		// dne
		if (exp.getNegations() % 2 == 0 && exp.getNegations() > 1) {
			exp = dne(exp);
		}

		// single atom
		if (exp.getType() == ExpressionType.ATOM) {
			return exp;
		}

		// implication
		if (exp.getTerminal() == Terminal.IMPLICATION) {
			return transform(implication(exp));
		}

		// equivalence
		if (exp.getTerminal() == Terminal.EQUIVALENCE) {
			return transform(equivalence(exp));
		}

		// negations
		if (exp.getNegations() == 1) {
			return transform(deMorgans(exp));
		}

		// conjunction
		if (exp.getTerminal() == Terminal.AND) {

			ArrayList<LogicExpression> terms = exp.getTerms();
			LogicExpression result = null;

			// convert all subexpressions to cnf, then combine them with
			// conjunctions
			for (LogicExpression term : terms) {

				try {
					result = combine(result, transform(term), Terminal.AND);
				} catch (NullPointerException e) {
					result = transform(term);
				}
			}

			return result;
		}

		// disjunction
		if (exp.getTerminal() == Terminal.OR) {

			ArrayList<LogicExpression> terms = exp.getTerms();
			LogicExpression result = null;

			// convert all subexpressions to cnf, then combine them with
			// disjunctions
			for (LogicExpression term : terms) {

				try {
					result = combine(result, transform(term), Terminal.OR);
				} catch (NullPointerException e) {
					result = transform(term);
				}
			}

			ArrayList<LogicExpression> newTerms = result.getTerms();

			// converting a set of conjunctions connected by disjunctions to a
			// set of disjunctions connected by conjunctions
			for (int i = 0; i < result.getSize() - 1; i = i++) {

				ArrayList<LogicExpression> finalTerms = new ArrayList<>();

				LogicExpression expa = newTerms.get(0);
				LogicExpression expb = newTerms.get(1);

				ArrayList<LogicExpression> a = expa.getTerms();
				ArrayList<LogicExpression> b = expb.getTerms();

				for (LogicExpression ai : a) {
					for (LogicExpression bi : b) {

						LogicExpression ci = combine(ai, bi, Terminal.OR);
						finalTerms.add(ci);

					}
				}

				newTerms.remove(expa);
				newTerms.remove(expb);

				LogicExpression nextResult = new Construction(finalTerms, Terminal.AND);

				newTerms.add(0, nextResult);

			}

			return result;

		}

		return null;
	}

	/**
	 * Convert a string of an expression into clause normal form
	 * 
	 * @param expression
	 * @return
	 */

	public static ClauseNormalForm findClauses(String expression) {

		HashSet<HashSet<LogicExpression>> cnf = new HashSet<>();
		HashSet<LogicExpression> currentClause = new HashSet<>();

		Pattern literals = Pattern.compile("[A-Z]+[0-9]*");
		Pattern terminals = Pattern.compile("&|\\||->|<->");
		Matcher matcher = null;

		int i = 0;

		while (i < expression.length()) {

			int literalStart = expression.length();
			int literalEnd = expression.length();
			int terminalStart = expression.length();

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
			} catch (IllegalStateException e) {
			}

			if (literalStart < terminalStart) {

				i = literalStart;
				LogicExpression newTerm = new Atom(expression.substring(literalStart, literalEnd));

				try {
					if (expression.charAt(literalStart - 1) == '-') {
						newTerm = LogicMethods.negate(newTerm);
					}
				} catch (StringIndexOutOfBoundsException e) {

				}

				currentClause.add(newTerm);
				i = literalEnd;

			} else if (literalStart > terminalStart) {

				i = terminalStart;

				if (expression.charAt(i) == '|') {
					i++;
				} else if (expression.charAt(i) == '&') {
					cnf.add(currentClause);
					currentClause = new HashSet<>();
					i++;
				}

			} else {
				i++;
			}
		}

		cnf.add(currentClause);

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

	/**
	 * Perform DPLL on a clause normal form, returning a model if one exists of null if one does not
	 * @param cnf the clause normal form
	 * @return the model, or null if there is not one
	 */
	
	public static LogicModel davisPutnam(ClauseNormalForm cnf) {

		HashSet<LogicExpression> atoms = cnf.getAtoms();

		LogicModel model = new LogicModel();

		HashSet<HashSet<LogicExpression>> cnf1 = cnf.getCNF();

		// keep iterating until there are no more clauses in the clause normal
		// form
		while (!cnf1.isEmpty()) {

			// unit propogation
			// look for clauses of size 1 and propogate

			while (findSmallestClause(cnf1) == 1) {
				for (HashSet<LogicExpression> clause : cnf1) {

					if (clause.size() == 1) {

						for (LogicExpression exp : clause) {

							boolean trueAtom = true;

							if (exp.getNegations() == 1) {
								trueAtom = false;
							}
							
							model.addAtom(exp, trueAtom);

							if(TheoremProver.debug){
								System.out.println("Unit propogation: " + exp);
							}
							
							cnf1 = reduceCNF(exp, cnf1);

						}

					}

				}
			}

			if (TheoremProver.debug) {
				System.out.println("Model after Unit Propogation: " + model);
				System.out.println("Clause Normal Form after Unit Propogation: " + cnf1);
			}

			// pure literal
			// search for literals that only occur in one polarity

			ArrayList<LogicExpression> onePolarity = new ArrayList<>();
			ArrayList<LogicExpression> twoPolarity = new ArrayList<>();

			for (HashSet<LogicExpression> clause : cnf1) {

				for (LogicExpression exp : clause) {

					if (containsNegation(exp, onePolarity)) {
						twoPolarity.add(exp);
						twoPolarity.add(getNegation(exp, onePolarity));
						onePolarity.remove(getNegation(exp, onePolarity));
					} else if (!onePolarity.contains(exp) && !twoPolarity.contains(exp)) {
						onePolarity.add(exp);
					}

				}

			}

			for (LogicExpression atom : onePolarity) {

				boolean trueAtom = true;

				if (atom.getNegations() == 1) {
					trueAtom = false;
				}

				model.addAtom(atom, trueAtom);
				cnf1 = reduceCNF(atom, cnf1);
				
				if(TheoremProver.debug){
					System.out.println("Pure literal: " + atom);
				}

			}

			if (TheoremProver.debug) {
				System.out.println("Model after Pure Literal: " + model);
				System.out.println("Clause Normal Form after Pure Literal: " + cnf1);
			}

			if (!cnf1.isEmpty()) {

				// splitting
				// choose the first unassigned atom and set it to true/false

				for (LogicExpression atom : atoms) {

					if (TheoremProver.debug) {
						System.out.println("Splitting: " + atom);
					}

					HashSet<HashSet<LogicExpression>> cnfa = reduceCNF(atom, cnf1);
					HashSet<HashSet<LogicExpression>> cnfb = reduceCNF(new Atom(atom.getName(), 1), cnf1);

					// try to find a model with the atom as true
					ClauseNormalForm reduced = new ClauseNormalForm(cnfa);
					LogicModel left = davisPutnam(reduced);

					if (left != null) {
						model.addAtom(atom, true);
						model.mergeModel(left);

						cnf1 = reduceCNF(atom, cnf1);

						for (Map.Entry<LogicExpression, Boolean> entry : left.getModel().entrySet()) {
							int negations = 0;

							if (entry.getValue().equals(new Boolean(false))) {
								negations = 1;
							}

							cnf1 = reduceCNF(new Atom(entry.getKey().getName(), negations), cnf1);
						}

						break;
					} else {

						// if that doesn't work try to find one with the atom as
						// false
						reduced = new ClauseNormalForm(cnfb);
						LogicModel right = davisPutnam(reduced);

						if (right != null) {
							model.addAtom(atom, true);
							model.mergeModel(right);

							cnf1 = reduceCNF(atom, cnf1);

							for (Map.Entry<LogicExpression, Boolean> entry : right.getModel().entrySet()) {

								int negations = 0;

								if (entry.getValue().equals(new Boolean(false))) {
									negations = 1;
								}

								cnf1 = reduceCNF(new Atom(entry.getKey().getName(), negations), cnf1);

								break;
							}

						}
					}

				}

				if (cnf1.contains(new HashSet<LogicExpression>())) {
					return null;
				}

			}

		}

		if (model.getSize() < atoms.size()) {
			for (LogicExpression atom : atoms) {
				if (!model.contains(atom)) {
					model.addAtom(atom, true);
				}
			}
		}

		return model;

	}

	/**
	 * Find if an iterable contains the negation of an expression
	 * 
	 * @param exp
	 *            the expression
	 * @param list
	 *            the list
	 * @return whether there is a negation
	 */

	public static boolean containsNegation(LogicExpression exp, Iterable<LogicExpression> list) {

		for (LogicExpression exp1 : list) {
			if (isNegated(exp, exp1)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Return the negation of an element in an iterable (could have no
	 * negations)
	 * 
	 * @param exp
	 *            the expression
	 * @param list
	 *            the list
	 * @return the negation
	 */

	public static LogicExpression getNegation(LogicExpression exp, Iterable<LogicExpression> list) {

		for (LogicExpression exp1 : list) {
			if (isNegated(exp, exp1)) {
				return exp1;
			}
		}

		return null;
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

	/**
	 * Find the size of the smallest clause in a clause normal form
	 * 
	 * @param cnf
	 *            the clause normal form
	 * @return the size of the smallest clause
	 */

	public static int findSmallestClause(HashSet<HashSet<LogicExpression>> cnf) {

		int i = -1;

		for (HashSet<LogicExpression> clause : cnf) {

			int size = clause.size();

			if (i == -1 || size < i) {
				i = size;
			}

		}

		return i;

	}

}
