package org.umd.mobileintents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Sets of {@link Constraint}s.
 * 
 * Viewed as a conjunction of all the constituent constraints.  Allows
 * serialization to different formats with an extensible semantics.
 *
 */
 class ConstraintSet {
     private List<Formula> mConstraints;
     
     public ConstraintSet() {
	 mConstraints = new HashMap<String,String>();
	 return;
     }
     
     /**
      * Add a constraint to the set.
      * 
      * @param c The constraint which should be added to the set
      */
     public void addConstraint(Constraint c)
	 mConstraints.put(key, value);
     }


     /**
      * Serialize this constraint set to a JSON object which can be
      * passed to the web service.
      */
     public JSONArray serializeToJson() {
	 JSONObject constraintSet = new JSONObject();
	 //constraintSet.put(
	 return null;
     }
 }
