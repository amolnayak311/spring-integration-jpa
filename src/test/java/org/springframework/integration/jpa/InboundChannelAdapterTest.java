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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.jpa.entity.Student;
import org.springframework.integration.jpa.entity.StudentReadStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test for the JPA inbound adapter test 
 * @author Amol Nayak
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:inbound-channel-adapter-test.xml")
public class InboundChannelAdapterTest {
	
	@Autowired
	@Qualifier("jpaInboundAdapterChannel")
	private SubscribableChannel channelOne;
	
	@Autowired
	@Qualifier("jpaInboundAdapterChannelTwo")
	private SubscribableChannel channelTwo;
	
	@Before
	public void setup() {
		MessageHandler handler = new MessageHandler() {
			
			public void handleMessage(Message<?> message) throws MessagingException {
				@SuppressWarnings("unchecked")
				List<Student> students = (List<Student>)message.getPayload();
				if(!students.isEmpty()) {
					System.out.println("\n\n Read students");
					for(Student student:students) {
						System.out.print("Roll Number: " + student.getRollNumber());
						System.out.print(", First Name: " + student.getFirstName());
						System.out.print(", Last Name: " + student.getLastName());
						System.out.println(", Gender: " + student.getGender());
					}
				}				
			}
		};
		channelOne.subscribe(handler);
		
		channelTwo.subscribe(new MessageHandler() {	
			@SuppressWarnings("unchecked")
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println("\n\n Reading read status of the student");
				List<StudentReadStatus> students = (List<StudentReadStatus>)message.getPayload();
				if(!students.isEmpty()) {
					System.out.println("\n\n Read students");
					for(StudentReadStatus stat:students) {
						System.out.print("Roll Number: " + stat.getRollNumber());
						System.out.println(", Read at : " + stat.getReadAt());						
					}
				}
			}
		});
	}
	
	
	@Test
	public void withoutUpdate() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
