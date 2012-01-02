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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.integration.jpa.core.JpaQLParameterSource;


/**
 * The abstract super class for all the {@link JPAQLParameterSourceFactory}
 *  
 * @author Amol Nayak
 *
 */
public abstract class AbstractJPAQlParameterSourceFactory implements JpaQLParameterSourceFactory {

	protected Map<String, Object> staticParameters = new HashMap<String, Object>();

	protected AbstractJPAQlParameterSourceFactory(Map<String, Object> staticParameters) {
		if(staticParameters != null)
			this.staticParameters = staticParameters;
	}

	public Map<String, Object> getStaticParameters() {
		return staticParameters;
	}

	public void setStaticParameters(Map<String, Object> staticParameters) {
		this.staticParameters = staticParameters;
	}

	/**
	 * Implemented for {@link JPAQLParameterSourceFactory}  
	 */
	public final JpaQLParameterSource createSource(Object obj) {
		return createConcreteSource(obj);
	}

	/**
	 * Implemented for {@link JPAQLParameterSourceFactory}
	 */
	public final Collection<String> getAllParameterNames() {
		Collection<String> allParams = new ArrayList<String>();
		if(staticParameters != null && !staticParameters.isEmpty())
			allParams.addAll(staticParameters.keySet());
		
		Collection<String> subClassParams = getParameterNames();
		if(subClassParams != null && !subClassParams.isEmpty())
			allParams.addAll(subClassParams);
		
		return allParams;
	}
	
	/**
	 * The implementing classes should return an instance of itself
	 * @return
	 */
	protected abstract JpaQLParameterSource createConcreteSource(Object obj);
	
	/**
	 * Gets all the parameter names defined in the implementing subclass
	 * @return
	 */
	protected abstract Collection<String> getParameterNames();
	
}
