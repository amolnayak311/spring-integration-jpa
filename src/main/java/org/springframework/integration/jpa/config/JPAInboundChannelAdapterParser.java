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
package org.springframework.integration.jpa.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.integration.jpa.JpaPollingChannelAdapter;
import org.springframework.integration.jpa.core.JpaOperationsImpl;
import org.w3c.dom.Element;

/**
 * The JPA Inbound Channel adapter parser
 *   
 * @author Amol Nayak
 *
 */
public class JPAInboundChannelAdapterParser 
				extends AbstractPollingInboundChannelAdapterParser{

	
	protected BeanMetadataElement parseSource(Element element,
			ParserContext parserContext) {
		BeanDefinitionBuilder builder = 
				BeanDefinitionBuilder.genericBeanDefinition(JpaPollingChannelAdapter.class);
		
		//Set the Attributes here
		//This is duplication,
		BeanDefinitionBuilder jpaOperations = 
				BeanDefinitionBuilder.genericBeanDefinition(JpaOperationsImpl.class);
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(jpaOperations, element,"entity-manager-factory");
		//This will register a new instance if one is already in place, need some means of
		//reusing the same instance with same emf
		String refName = BeanDefinitionReaderUtils.registerWithGeneratedName(jpaOperations.getBeanDefinition(),
				parserContext.getRegistry());
		builder.addPropertyReference("jpaOperations", refName);
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "select");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "max-rows-per-poll");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, 
									"update-sql-parameter-source-factory","parameterSourceFactory");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, 
									"select-sql-parameter-source","parameterSource");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element,
									"update", "updateJpaql");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "update-per-row");		
		return builder.getBeanDefinition();
	}
}
