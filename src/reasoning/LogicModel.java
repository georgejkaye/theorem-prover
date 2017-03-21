package reasoning;

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

		if (model.size() != 0) {
			return result.substring(0, result.length() - 2);
		}

		if(result.equals("")){
			return "none";
		}
		
		return result;
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
		model.put(new Atom(exp.getName()), polarity);
	}
	
	/**
	 * Get the model object
	 * @return the model object
	 */
	
	public HashMap<LogicExpression, Boolean> getModel(){
		return this.model;
	}

	/**
	 * Get the number of elements in this model
	 * 
	 * @return the number of elements in this model
	 */

	public int getSize() {
		return model.size();
	}

	/**
	 * Merge a model into this one
	 * @param newModel the new model
	 */
	
	public void mergeModel(LogicModel newModel){
		
		HashMap<LogicExpression, Boolean> newMap = newModel.getModel();
		
		for(Map.Entry<LogicExpression, Boolean> entry : newMap.entrySet()){
			
			addAtom(entry.getKey(), entry.getValue());
			
		}
		
	}
	
	/**
	 * Find out if this model contains an atom
	 * @param atom the atom
	 * @return whether it is contained in this model
	 */
	
	public boolean contains(LogicExpression atom){
		return model.containsKey(atom);
	}

}
