/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.jpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.jpa.core.JpaQLParameterSource;
import org.springframework.integration.util.AbstractExpressionEvaluator;

/**
 * The parameter source which executes the given expression to yield a value for the query parameters
 * the static parameters if any take precedence if a conflicting match is found
 * 
 * @author Amol Nayak
 *
 */
public class ExpressionEvaluatingJPAQlParameterSourceFactory extends
		AbstractJPAQlParameterSourceFactory {

	
	private Log logger = LogFactory.getLog(ExpressionEvaluatingJPAQlParameterSourceFactory.class);
	
	private Map<String, String> expressionMap;	
	
	/**
	 * The constructor that instantiates
	 * @param staticParameters
	 * @param payload
	 * @param expressionMap
	 */
	public ExpressionEvaluatingJPAQlParameterSourceFactory(
			Map<String, Object> staticParameters, Map<String, String> expressionMap) {
		super(staticParameters);
		this.expressionMap = expressionMap;
	}
	
	/**
	 * Default constructor that will not be instantiating the factory with any static parameters or expression 
	 */
	public ExpressionEvaluatingJPAQlParameterSourceFactory() {
		this(null,null);
	}

	

	public Map<String, String> getExpressionMap() {
		return expressionMap;
	}

	public void setExpressionMap(Map<String, String> expressionMap) {
		this.expressionMap = expressionMap;
	}

	/**
	 * Creates a concrete source of the {@link JPAQLParameterSource} with the provided payload
	 * @return returns the concrete instance of the implementing {@link JPAQLParameterSource}
	 * 
	 */
	protected JpaQLParameterSource createConcreteSource(Object obj) {		
		return new ExpressionEvaluatingJPAQLParameterSource(getStaticParameters(), expressionMap, obj);
	}


	protected Collection<String> getParameterNames() {		
		return expressionMap.keySet();
	}
	
	private class ExpressionEvaluatingJPAQLParameterSource extends AbstractExpressionEvaluator
					implements JpaQLParameterSource {
		private Map<String, Object> staticParameters = new HashMap<String, Object>();
		private Map<String, String> expressionMap = new HashMap<String, String>();
		private Map<String, Object> evaluatedExpressionCache = new HashMap<String, Object>();
		private Object payload;
		
		private ExpressionEvaluatingJPAQLParameterSource(
				Map<String, Object> staticParameters,
				Map<String, String> expressionMap, Object payload) {
			super();
			if(staticParameters != null)
				this.staticParameters = staticParameters;
			if(expressionMap != null)
				this.expressionMap = expressionMap;
			this.payload = payload;
		}

		public Object getValue(String parameter) {
			String expression;
			if(staticParameters.containsKey(parameter)) {
				return staticParameters.get(parameter);
			} else if (evaluatedExpressionCache.containsKey(parameter)) {
				return evaluatedExpressionCache.get(parameter);
			} else if(expressionMap.containsKey(parameter)) {
				expression = expressionMap.get(parameter);
			} else {
				expression = parameter;
			}
			if(payload instanceof Collection<?>) {
				expression = "#root.![" + expression + "]"; 
			}
			Object value = evaluateExpression(expression, payload);
			evaluatedExpressionCache.put(parameter, value);
			if(logger.isDebugEnabled())
				logger.debug("Evaluated expression " + expression + " to value " + value);
			return value;
		}		
	}	
}
