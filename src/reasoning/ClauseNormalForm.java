package reasoning;

import java.util.HashSet;

import reasoning.ComplementingPair;

/**
 * A class representing a clause normal form
 * 
 * @author George Kaye
 *
 */

public class ClauseNormalForm {

	private HashSet<HashSet<LogicExpression>> cnf;

	/**
	 * Create a new clause normal form
	 * 
	 * @param cnf
	 *            the clause of clauses
	 */

	public ClauseNormalForm(HashSet<HashSet<LogicExpression>> cnf) {

		this.cnf = cnf;

	}

	/**
	 * Get the actual clause normal form
	 * 
	 * @return the clause normal form
	 */

	public HashSet<HashSet<LogicExpression>> getCNF() {
		return this.cnf;
	}

	public String toString() {

		String set = "{";

		for (HashSet<LogicExpression> hs : cnf) {

			String clause = "{";

			for (LogicExpression exp : hs) {
				clause += exp;
				clause += ", ";
			}

			clause = clause.substring(0, clause.length() - 2);
			clause += "}";

			set += clause;
			set += ", ";
		}

		if (set.length() != 0) {
			set = set.substring(0, set.length() - 2);
		}

		set += "}";
		return set;

	}

	/**
	 * Find an unresolved pair of atoms in this clause normal form
	 * 
	 * @param resolved
	 *            the set of already resolved atoms
	 * @return a complementing pair
	 */

	public ComplementingPair findUnresolvedPair(HashSet<HashSet<LogicExpression>> resolved) {

		for (HashSet<LogicExpression> clause : cnf) {

			if (!resolved.contains(clause)) {

				for (LogicExpression exp : clause) {

					for (HashSet<LogicExpression> clause1 : cnf) {

						if (!clause1.equals(clause)) {
							for (LogicExpression exp1 : clause1) {

								if (LogicMethods.isNegated(exp, exp1)) {
									return new ComplementingPair(clause, clause1);
								}

							}
						}
					}
				}

			}

		}

		return null;

	}

	/**
	 * Finds if an expression has a complementing one in this clause normal form
	 * 
	 * @param at
	 *            the expression
	 * @return whether there is a complementing expresison
	 */

	public boolean hasComplement(LogicExpression at) {

		int complement = 1;

		if (at.getNegations() == 1) {
			complement = 0;
		}

		for (HashSet<LogicExpression> hs : cnf) {
			for (LogicExpression exp : hs) {
				if (exp.getName().equals(at.getName()) && exp.getNegations() == complement) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Add a clause to this clause normal form
	 * 
	 * @param c
	 *            the clause to add
	 * @return the new clause normal form
	 */

	public ClauseNormalForm add(HashSet<LogicExpression> c) {

		cnf.add(c);
		return this;
	}

	/**
	 * Equals method
	 */

	public boolean equals(Object obj) {
		if (!(obj instanceof ClauseNormalForm))
			return false;
		if (obj == this)
			return true;

		ClauseNormalForm cnf1 = (ClauseNormalForm) obj;
		if (cnf1.getCNF().equals(cnf)) {
			return true;
		}

		return false;

	}

	/**
	 * Get all the unique atoms in this clause normal form
	 * 
	 * @return the set of atoms contained in this cnf
	 */

	public HashSet<LogicExpression> getAtoms() {

		HashSet<LogicExpression> atoms = new HashSet<>();

		for (HashSet<LogicExpression> clause : cnf) {
			for (LogicExpression atom : clause) {
				if (!LogicMethods.containsNegation(atom, atoms)) {
					atoms.add(new Atom(atom.getName()));
				}
			}
		}

		return atoms;

	}
}
