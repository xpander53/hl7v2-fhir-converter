package com.ibm.whi.core.expression;

import java.util.List;
import java.util.Map;
import com.ibm.whi.core.message.InputData;

/**
 * Each expression defines how to extract the value for a field. The execute method defines the
 * extraction process.
 * 
 *
 * @author pbhallam
 */

public interface Expression {
  /**
   * Defines the return type of the object value evaluated/extracted.
   * 
   * @return
   */
  String getType();

  /**
   * Specifies the default value for the field that should be returned if the evaluated value is
   * null
   * 
   * @return {@link GenericResult}
   */
  GenericResult getDefaultValue();

  /**
   * List of String values that would be passed to the Evaluator to extract value.
   * 
   * @return
   */
  List<String> getspecs();

  /**
   * Evaluates the expression and returns the GenericResult Object
   * 
   * @param {@link DataSource} input data
   * @param contextValues - Map of values for variables
   * @return {@link GenericResult}
   */
  GenericResult evaluate(InputData dataSource, Map<String, GenericResult> contextValues);

  /**
   * Data Extractor defines how to resolve variable and extract values for the spec
   * 
   * @return
   */




}
