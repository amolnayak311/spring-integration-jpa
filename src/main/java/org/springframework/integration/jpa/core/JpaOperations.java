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

import java.util.List;

/**
 * The Interface containing all the JpaOperations those will be executed by
 * the Jpa Spring Integration adapters
 * @author Amol Nayak
 *
 */
public interface JpaOperations {

	/**
	 * Find an Entity of given type with the given primary key type
	 * @param <T>
	 * @param entityType
	 * @param id
	 * @return
	 */
	<T> T find(Class<T> entityType,Object id);
	
	/**
	 * Executes the provided query to return a list of results
	 * @param query
	 * @param source the Parameter source for this query to be executed, if none then set as null
	 * @return
	 */
	List<?> getResultListForQuery(String query,JpaQLParameterSource source);
	
	/**
	 * Executes the provided query to return a list of results 
	 * @param query
	 * @param expectedElementType
	 * @param source the Parameter source for this query to be executed, if none then set as null
	 * @return
	 */
	List<?> getResultListForQuery(String query,JpaQLParameterSource source,int fromRecord,int maxNumberOfRecord);
	
	
	/**
	 * Executes the provided query to return a single element
	 * @param query
	 * @param source the Parameter source for this query to be executed, if none then set as null
	 * @return
	 */
	Object getSingleResultForQuery(String query,JpaQLParameterSource source);
	
	/**
	 * Executes the given update statement and use the given parameter source to set the required placeholders
	 * @param updateQuery
	 * @param source
	 * @return
	 */
	int executeUpdate(String updateQuery,JpaQLParameterSource source); 
	
	
	/**
	 * The entity to be merged with the entity manager
	 * @param <T>
	 * @param entity
	 * @return
	 */
	<T> T merge(T entity);
	
	/**
	 * Persists the entity 
	 * @param entity
	 */
	void persist(Object entity);
	
}
