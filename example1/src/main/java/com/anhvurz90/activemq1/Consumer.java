package com.anhvurz90.activemq1;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer {

    private static String url = "tcp://localhost:61616";
    
    private static String subject = "TESTQUEUE";
    
    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory jmsConnectionFactory
            = new ActiveMQConnectionFactory(url);
        
        jmsConnectionFactory.setUserName("admin");
        jmsConnectionFactory.setPassword("admin");
        
        Connection connection = jmsConnectionFactory.createConnection();
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(subject);
        MessageConsumer consumer = session.createConsumer(destination);
        
        Message message = consumer.receive();
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            System.out.println("Received message: '" + textMessage.getText() + "'");
        }
        
        connection.close();
    }
}
