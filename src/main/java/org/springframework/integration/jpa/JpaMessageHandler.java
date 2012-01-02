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

import java.util.Collections;

import org.springframework.integration.Message;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.jpa.core.JpaOperations;
import org.springframework.integration.jpa.core.JpaQLParameterSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

/**
 * The message handler for &lt;jpa:outbound-channel-adapter/&gt and &lt;jpa:outbound-gateway/&gt; 
 * @author Amol Nayak
 *
 */
public class JpaMessageHandler extends AbstractReplyProducingMessageHandler {

	private JpaOperations jpaOperations;
	private String jpaQl;
	private JpaQLParameterSourceFactory requestParameterSourceFactory = new ExpressionEvaluatingJPAQlParameterSourceFactory();
	private JpaQLParameterSourceFactory replyParameterSourceFactory = new ExpressionEvaluatingJPAQlParameterSourceFactory();
	private boolean produceReply;	//false for outbound-channel-adapter, true for outbound-gateway
	
	private String select;		//relevant for for outbound-gateway
	private int maxRowsPerPoll;		//relevant only for outbound-gateway for the max number of records to fetch 
	
	public JpaMessageHandler(JpaOperations jpaOperations) {
		Assert.notNull(jpaOperations,"Provide JPA Operations instance should be non null");
		this.jpaOperations = jpaOperations;		
	}	

	protected Object handleRequestMessage(Message<?> message) {
		Object payload;
		if(jpaQl != null) {
			//Execute the JPA Ql
			if(logger.isDebugEnabled())
				logger.debug("Executing update by he given JPA QL Against the given message payload");
			int recordsUpdated = jpaOperations.executeUpdate(jpaQl, requestParameterSourceFactory.createSource(message));
			if(produceReply) {
				//Kept in sync with the behavior of JDBC outbound adapter
				LinkedCaseInsensitiveMap<Integer> map = new LinkedCaseInsensitiveMap<Integer>(1);
				map.put("UPDATED", recordsUpdated);
				payload = Collections.singletonList(map);				
			} else
				return null;			
			
		} else {
			//The given payload is itself a JPA Entity and is to be merged as is, there is not insert JPA QL.
			if(logger.isDebugEnabled())
				logger.debug("Given payload possibly a JPA entity, attempting to merge it with the entity manager");
			Object entity = message.getPayload();
			entity = jpaOperations.merge(entity);
			if(produceReply)
				payload = entity;
			else
				return null;
		}
		//if we are here its because this is an outbound gateway and produceReply is true 
		if(StringUtils.hasText(select)) {
			//Select has a text, so execute it and gets its result list as the payload with the max number of recs
			//payload will be the JPA entity or the map containing the number of rows updated
			JpaQLParameterSource source = replyParameterSourceFactory.createSource(payload);
			payload = jpaOperations.getResultListForQuery(select,source, 0, maxRowsPerPoll);				
		}
		return MessageBuilder.withPayload(payload).copyHeaders(message.getHeaders()).build();
	}
	
	public void setJpaQL(String jpaQl) {
		if(StringUtils.hasText(jpaQl))
			this.jpaQl = jpaQl;
	}

	
	public void setRequestParameterSourceFactory(JpaQLParameterSourceFactory requestParameterSourceFactory) {
		if(requestParameterSourceFactory != null)
			this.requestParameterSourceFactory = requestParameterSourceFactory;
	}
	
	

	public void setReplyParameterSourceFactory(JpaQLParameterSourceFactory replyParameterSourceFactory) {
		if(replyParameterSourceFactory != null)
			this.replyParameterSourceFactory = replyParameterSourceFactory;
	}

	public void setProduceReply(boolean produceReply) {
		this.produceReply = produceReply;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public void setMaxRowsPerPoll(int maxRowsPerPoll) {
		this.maxRowsPerPoll = maxRowsPerPoll;
	}
	
}
