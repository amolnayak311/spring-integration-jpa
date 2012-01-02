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
package org.springframework.integration.jpa.core;

/**
 * The parameter source for the JPA Qls. 
 * 
 * @author Amol Nayak
 *
 */
public interface JpaQLParameterSource {

	/**
	 * Gets the value for the provided parameter name
	 * @param parameter
	 * @return
	 */
	Object getValue(String parameter);
}
