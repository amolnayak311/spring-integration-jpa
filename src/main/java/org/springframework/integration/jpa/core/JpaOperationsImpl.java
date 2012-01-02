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
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Parameter;
import javax.persistence.Query;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.util.StringUtils;

/**
 * Class similar to JPA template limited to the operations required for the JPA adapters/gateway
 * not using JpaTemplate as the class is deprecated since Spring 3.1
 * @author Amol Nayak
 *
 */
public class JpaOperationsImpl extends EntityManagerFactoryAccessor implements JpaOperations,InitializingBean {	

	public JpaOperationsImpl(EntityManagerFactory factory) {		
		setEntityManagerFactory(factory);
	}
	
	public JpaOperationsImpl() {
		//Default no arg constructor
	}	

	public void afterPropertiesSet() throws Exception {
		if(getEntityManagerFactory() == null)
			throw new BeanDefinitionStoreException("JpaOperationsImpl needs to be set with a non null Entity Manager Factory");		
	}

	public <T> T find(Class<T> entityType, Object id) {		
		return createOrGetTransactionalEntityManager().find(entityType, id);
	}

	public List<?> getResultListForQuery(String query,JpaQLParameterSource source) {
			return getResultListForQuery(query,source,-1,-1);
	}	

	public List<?> getResultListForQuery(String queryString,JpaQLParameterSource source,
			int fromRecord, int maxNumberOfRecord) {
		Query query = getQuery(queryString,source);
		if(fromRecord >= 0)
			query.setFirstResult(fromRecord);
		if(maxNumberOfRecord > 0)
			query.setMaxResults(maxNumberOfRecord);			
			
		return query.getResultList();
	}

	public Object getSingleResultForQuery(String queryString,JpaQLParameterSource source) {
		Query query = getQuery(queryString,source);
		return query.getSingleResult();
	}	

	public int executeUpdate(String updateQuery, JpaQLParameterSource source) {
		Query query = createOrGetTransactionalEntityManager().createQuery(updateQuery);
		setParametersIfRequired(updateQuery, source, query);		
		return query.executeUpdate();
	}

	/** Given a JPA QL, this methods gets all the parameters defined in this query and 
	 * use the {@link JPAQLParameterSource} to find their values and set them
	 * 
	 * @param queryString
	 * @param source
	 * @param query
	 */
	private void setParametersIfRequired(String queryString,
			JpaQLParameterSource source, Query query) {
		Set<Parameter<?>> parameters = query.getParameters();
		
		if(parameters != null && !parameters.isEmpty()) {
			if(source != null) {
				for(Parameter<?> param:parameters) {
					String paramName = param.getName();
					//For now we just support named parameters, positional parameters are not supported, 
					//TODO: Do we support positional parameters some way?
					if(!StringUtils.hasText(paramName)) {
						throw new JpaOperationFailedException("Expecting a parameter name but not found one, " +
								"JPA QLs with named parameters are supported," +
								" have you used positional parameters?")
						.withOffendingJPAQl(queryString);
					}
					Object paramValue = source.getValue(paramName);
					query.setParameter(paramName, paramValue);				
				}
			} else {
				throw new IllegalArgumentException("Query has parameters but no parameter source provided");
			}
			
		}
	}

	public <T> T merge(T entity) {
		return createOrGetTransactionalEntityManager().merge(entity);
	}

	public void persist(Object entity) {
		createOrGetTransactionalEntityManager().persist(entity);		
	}
	
	/**
	 * @param query
	 * @param source
	 * @return
	 */
	private Query getQuery(String queryString,JpaQLParameterSource source) {
		Query query = 
			createOrGetTransactionalEntityManager().createQuery(queryString);
		setParametersIfRequired(queryString, source, query);
		return query;
	}
	
	/**
	 * The method gets a transaction entity manager and returns a new entity manager if no transactional entity manager found
	 * @return
	 */
	private EntityManager createOrGetTransactionalEntityManager() {
		EntityManager em = getTransactionalEntityManager();
		if(em == null)
			em = createEntityManager();
		return em;
	}
	
}
