/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://jsftemplating.dev.java.net/cddl1.html or
 * jsftemplating/cddl1.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at jsftemplating/cddl1.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.layout.descriptors.handler;


/**
 *  <p>	This class holds OutputMapping value meta information for individual
 *	instances of Handler Objects.  This information is necessary to provide
 *	the location to store the output value for a specific invocation of a
 *	handler.  This is data consists of the name the Handler uses for the
 *	output, the OutputType, and optionally the OutputType key to use when
 *	storing/retrieving the output value.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class OutputMapping implements java.io.Serializable {

    /**
     *	<p> Constructor with targetKey as null.  This constructor will
     *	    throw an IllegalArgumentException if outputName or
     *	    targetOutputType is null.</p>
     *
     *	@param	outputName	    The name the Handler uses for output value
     *	@param	targetOutputType    OutputType that will store the output value
     *
     *	@see	OutputTypeManager
     *
     *	@throws	IllegalArumentException If outputName / targetOutputType is null
     */
    public OutputMapping(String outputName, String targetOutputType) {
	this(outputName, null, targetOutputType);
    }


    /**
     *	<p> Constructor with all values supplied as Strings.  This constructor
     *	    will throw an IllegalArgumentException if outputName or
     *	    targetOutputType is null.</p>
     *
     *	@param	outputName	    The name the Handler uses for output value
     *	@param	targetKey	    The key the OutputType will use
     *	@param	targetOutputType    OutputType that will store the output value
     *
     *	@see	OutputTypeManager
     *
     *	@throws	NullPointerException If outputName / targetOutputType is null
     */
    public OutputMapping(String outputName, String targetKey, String targetOutputType) {
	// Sanity checks...
	if ((outputName == null) || (outputName.length() == 0)) {
	    throw new NullPointerException("'outputName' is required!");
	}
	if (targetOutputType == null) {
	    throw new NullPointerException("'targetOutputType' is required!");
	}
	_outputName = outputName;
	_targetKey = targetKey;
	_targetOutputType = targetOutputType;
    }

    /**
     *	Accessor for outputName.
     */
    public String getOutputName() {
	return _outputName;
    }

    /**
     *	Accessor for targetKey.
     */
    public String getOutputKey() {
	return _targetKey;
    }

    /**
     *	Accessor for targetOutputType.
     */
    public OutputType getOutputType() {
	return OutputTypeManager.getInstance().getOutputType(_targetOutputType);
    }


    private String	_outputName = null;
    private String	_targetKey  = null;
    private String	_targetOutputType = null;
}
