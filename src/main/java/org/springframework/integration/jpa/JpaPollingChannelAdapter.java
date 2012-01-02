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

import java.util.List;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.integration.Message;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.jpa.core.JpaOperations;
import org.springframework.integration.jpa.core.JpaQLParameterSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.StringUtils;

/**
 * Polling message source that produces messages from the result of the provided JPA QL.
 * It also executes an update after the select possibly to updated the state of selected records
 *    
 * @author Amol Nayak
 *
 */
public class JpaPollingChannelAdapter extends IntegrationObjectSupport implements MessageSource<Object>{

	private JpaOperations jpaOperations;
	private	String select;
	private int maxRowsPerPoll;
	//Used for creation of parameter source before execution of the update
	private JpaQLParameterSourceFactory parameterSourceFactory = new ExpressionEvaluatingJPAQlParameterSourceFactory();
	//Uses as a source for select query executed.
	private JpaQLParameterSource parameterSource;
	private String updateJpaql;
	private boolean updatePerRow;
	
	
	/**
	 * Check for mandatory attributes
	 */
	protected void onInit() throws Exception {
		if(!StringUtils.hasText(select))
			throw new BeanDefinitionStoreException("Non null, non empty select JPA Ql is mandatory");
		
		if(jpaOperations == null) 
			throw new BeanDefinitionStoreException("A Non null instance of JpaOperation is required");
	}
	
	public Message<Object> receive() {
		List<?> results = jpaOperations.getResultListForQuery(select, parameterSource, 0, maxRowsPerPoll);
		Message<Object> message = null;
		if(results != null && !results.isEmpty()) {
			if(StringUtils.hasText(updateJpaql)) {
				JpaQLParameterSource source;
				if(updatePerRow) {
					for(Object record:results) {
						source = parameterSourceFactory.createSource(record);
						jpaOperations.executeUpdate(updateJpaql, source);
					}
				} else {
					source = parameterSourceFactory.createSource(results);
					jpaOperations.executeUpdate(updateJpaql, source);
				}
				
			}
			message = MessageBuilder.withPayload((Object)results).build();
		}		
		return message;
	}
	
	//---Setters only
	public void setJpaOperations(JpaOperations jpaOperations) {
		this.jpaOperations = jpaOperations;
	}
	public void setSelect(String select) {
		this.select = select;
	}
	public void setMaxRowsPerPoll(int maxRowsPerPoll) {
		this.maxRowsPerPoll = maxRowsPerPoll;
	}
	public void setParameterSourceFactory(
			JpaQLParameterSourceFactory parameterSourceFactory) {
		this.parameterSourceFactory = parameterSourceFactory;
	}
	public void setParameterSource(JpaQLParameterSource parameterSource) {
		this.parameterSource = parameterSource;
	}
	public void setUpdateJpaql(String updateJpaql) {
		this.updateJpaql = updateJpaql;
	}
	public void setUpdatePerRow(boolean updatePerRow) {
		this.updatePerRow = updatePerRow;
	}	
}
