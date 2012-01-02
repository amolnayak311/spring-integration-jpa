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

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.jpa.core.JpaOperations;
import org.springframework.util.ClassUtils;

/**
 * The abstract factory for generating a JPAMessageHandler proxy with the transaction
 * interceptors as applicable
 * 
 * @author Amol Nayak
 *
 */
public class JpaMessageHandlerFactory extends AbstractFactoryBean<MessageHandler> {

	private JpaOperations jpaOperations;
	private String jpaQl;
	private JpaQLParameterSourceFactory requestParameterSourceFactory;
	private JpaQLParameterSourceFactory replyParameterSourceFactory;
	private Advice transactionAdvice;
	private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
	
	private boolean produceReply;	//false for outbound-channel-adapter, true for outbound-gateway
	
	private String select;		//relevant for for outbound-gateway
	private int maxRowsPerPoll;		//relevant only for outbound-gateway for the max number of records to fetch
	
	private MessageChannel outputChannel;


	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.AbstractFactoryBean#getObjectType()
	 */	
	public Class<?> getObjectType() {	
		return MessageHandler.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
	 */	
	protected MessageHandler createInstance() throws Exception {
		MessageHandler instance;
		JpaMessageHandler handler = new JpaMessageHandler(jpaOperations);
		handler.setJpaQL(jpaQl);
		handler.setRequestParameterSourceFactory(requestParameterSourceFactory);
		handler.setReplyParameterSourceFactory(replyParameterSourceFactory);
		handler.setSelect(select);
		handler.setMaxRowsPerPoll(maxRowsPerPoll);
		handler.setProduceReply(produceReply);
		handler.setOutputChannel(outputChannel);
		if(transactionAdvice != null) {
			ProxyFactory factory = new ProxyFactory(handler);
			factory.addAdvice(transactionAdvice);			
			instance = (MessageHandler)factory.getProxy(classLoader);
		} else {
			instance = handler;
		}		
		return instance;
	}
	
	public void setBeanClassLoader(ClassLoader classLoader) {		
		super.setBeanClassLoader(classLoader);
		this.classLoader = classLoader;
	}
	
	
	//---------Setters------	
	

	public void setJpaOperations(JpaOperations jpaOperations) {			
		this.jpaOperations = jpaOperations;
	}

	public void setJpaQl(String jpaQl) {
		this.jpaQl = jpaQl;
	}

	
	public void setRequestParameterSourceFactory(
			JpaQLParameterSourceFactory requestParameterSourceFactory) {
		this.requestParameterSourceFactory = requestParameterSourceFactory;
	}

	public void setReplyParameterSourceFactory(
			JpaQLParameterSourceFactory replyParameterSourceFactory) {
		this.replyParameterSourceFactory = replyParameterSourceFactory;
	}

	public void setTransactionAdvice(Advice transactionAdvice) {
		this.transactionAdvice = transactionAdvice;
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

	public void setOutputChannel(MessageChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	
}
