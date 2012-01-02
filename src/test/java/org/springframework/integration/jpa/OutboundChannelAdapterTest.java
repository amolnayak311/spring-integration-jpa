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

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.jpa.entity.Gender;
import org.springframework.integration.jpa.entity.Student;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Amol Nayak
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:outbound-channel-adapter-test.xml")
@TransactionConfiguration(defaultRollback=false)
public class OutboundChannelAdapterTest {

	@Autowired
	@Qualifier("jpaOutboundChannel")
	private MessageChannel outboundDirectChannel;
	
	@Autowired
	@Qualifier("jpaOutboundExecutorChannel")
	private MessageChannel outboundExecutorChannel;
	
	@Autowired
	@Qualifier("jpaOutboundJpaQLChannel")
	private MessageChannel outboundJpaQlChannel;
	
	
	
	@Test
	@Transactional(propagation=Propagation.REQUIRED)
	public void sendEntityOverExecutorChannel() throws Exception {
		Calendar dateOfBirth = Calendar.getInstance();
		dateOfBirth.set(1984, 0, 31);
		Student student = new Student()
					.withFirstName("First Executor")
					.withLastName("Last Executor")
					.withGender(Gender.MALE)
					.withDateOfBirth(dateOfBirth.getTime())
					.withLastUpdated(new Date());
		System.out.println("Sending the Person entiy over the executor channel");
		outboundExecutorChannel.send(MessageBuilder.withPayload(student).build());
		Thread.sleep(3000);
	}
	
	@Test
	@Transactional(propagation=Propagation.REQUIRED)
	public void sendEntityOverDirectChannel() {
		Calendar dateOfBirth = Calendar.getInstance();
		dateOfBirth.set(1984, 0, 31);
		Student student = new Student()
					.withFirstName("First Direct")
					.withLastName("Last Direct")
					.withGender(Gender.MALE)
					.withDateOfBirth(dateOfBirth.getTime())
					.withLastUpdated(new Date());
		System.out.println("Sending the Person entiy over the direct channel");
		outboundDirectChannel.send(MessageBuilder.withPayload(student).build());					
	}
	
	@Test
	@Transactional(propagation=Propagation.REQUIRED)
	public void updateLastName() {
		System.out.println("Updating last name of the person");
		outboundJpaQlChannel.send(MessageBuilder.withPayload("Updated Last Name").build());
	}
	
	
}
