package reasoning2;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent a model created by the Davis-Putnam procedure
 * 
 * @author George Kaye
 *
 */

public class LogicModel {

	private HashMap<LogicExpression, Boolean> model;

	/**
	 * Create a new (empty) logic model
	 */

	public LogicModel() {
		this.model = new HashMap<>();
	}

	public String toString() {

		String result = "";

		for (Map.Entry<LogicExpression, Boolean> entry : model.entrySet()) {

			String bool = "T";

			if (entry.getValue().equals(new Boolean(false))) {
				bool = "F";
			}

			result += "I(" + entry.getKey().getName() + ") = " + bool + ", ";

		}

		return result.substring(0, result.length() - 2);
	}

	/**
	 * Add an atom to the model with an associated polarity
	 * 
	 * @param exp
	 *            the expression
	 * @param polarity
	 *            the polarity
	 */

	public void addAtom(LogicExpression exp, boolean polarity) {
		model.put(exp, polarity);
	}

}
