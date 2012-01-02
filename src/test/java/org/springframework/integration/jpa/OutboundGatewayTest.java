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
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.core.SubscribableChannel;
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
@ContextConfiguration(locations="classpath:outbound-gateway-test.xml")
@TransactionConfiguration(defaultRollback=false)
public class OutboundGatewayTest {

	@Autowired
	@Qualifier("jpaOutboundGateway")
	private MessageChannel outboundDirectChannel;
	
	@Autowired
	@Qualifier("jpaReplyChannel")
	private SubscribableChannel replyChannel;
	
	@Autowired
	@Qualifier("jpaOutboundGateway1")
	private MessageChannel outboundDirectChannel1;
	
	@Autowired
	@Qualifier("jpaReplyChannel1")
	private SubscribableChannel replyChannel1;
	
	@Before
	public void setup() {
		replyChannel.subscribe(new MessageHandler() {			
			public void handleMessage(Message<?> message) throws MessagingException {
				Student student = (Student)message.getPayload();
				System.out.println("Got Student with roll number " + student.getRollNumber() + 
						" and with name " + student.getFirstName());
			}
		});
		
		replyChannel1.subscribe(new MessageHandler() {			
			public void handleMessage(Message<?> message) throws MessagingException {
				Object payload = message.getPayload();
				if(payload instanceof List<?>) {
					Object[] values = (Object[])((List<?>) payload).get(0);
					System.out.println("Roll Number is " + values[0] + ", First Name is " + values[1]);
				}
			}
		});
	}
	
	
	//@Test
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
	public void sendEntityToSelectGateway() {
		Calendar dateOfBirth = Calendar.getInstance();
		dateOfBirth.set(1984, 0, 31);
		Student student = new Student()
					.withFirstName("First Direct")
					.withLastName("Last Direct")
					.withGender(Gender.MALE)
					.withDateOfBirth(dateOfBirth.getTime())
					.withLastUpdated(new Date());
		System.out.println("Sending the Person entity to sleect gateway");
		outboundDirectChannel1.send(MessageBuilder.withPayload(student).build());					
	}
}
