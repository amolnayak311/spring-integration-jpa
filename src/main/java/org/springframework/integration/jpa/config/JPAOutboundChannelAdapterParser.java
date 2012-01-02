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

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

/**
 * The parser for JPA outbound channel adapter
 * @author Amol Nayak
 *
 */
public class JPAOutboundChannelAdapterParser extends
	AbstractOutboundChannelAdapterParser {

	/* (non-Javadoc)
	 * @see org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser#parseConsumer(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	protected AbstractBeanDefinition parseConsumer(Element element,
			ParserContext parserContext) {
		BeanDefinitionBuilder messageHandlerFactoryBuilder = JPAOutboundEndpointParserHelper.getMessageHandlerBuilder(
				element, parserContext);		
		return messageHandlerFactoryBuilder.getBeanDefinition();
	}
}
