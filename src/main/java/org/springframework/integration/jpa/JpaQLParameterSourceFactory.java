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

import org.springframework.integration.jpa.core.JpaQLParameterSource;

/**
 * Factory interface that will be used to generate a JPAQLParameterSource
 * used to provide argument values for the given JPA QL
 *  
 * @author Amol Nayak
 *
 */
public interface JpaQLParameterSourceFactory {

	/**
	 * Creates the parameter source with the given message payload that came in to the adapter
	 * @param obj
	 * @return
	 */
	JpaQLParameterSource createSource(Object obj);
	
	/**
	 * Get all the parameter names those are configured in the configuration
	 * @return
	 */
	Collection<String> getAllParameterNames();
}
