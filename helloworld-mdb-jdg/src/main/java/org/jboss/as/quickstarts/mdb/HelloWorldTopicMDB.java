/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.mdb;

import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.jboss.ejb3.annotation.ResourceAdapter;
import org.jboss.as.quickstarts.datagrid.helloworld.*;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.Cache;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * <p>
 * A simple Message Driven Bean that asynchronously receives and processes the messages that are sent to the topic.
 * </p>
 *
 * @author Serge Pagop (spagop@redhat.com)
 *
 */
@MessageDriven(name = "HelloWorldQTopicMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "HELLOWORLDMDBTopic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
        @ResourceAdapter(value="activemq-rar.rar")
public class HelloWorldTopicMDB implements MessageListener {

    private final static Logger LOGGER = Logger.getLogger(HelloWorldTopicMDB.class.toString());
    private final String key ="MBDTOPIC";

    @Inject
    DefaultCacheManager m;

    /**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message rcvMessage) {
        TextMessage msg = null;
        try {
            if (rcvMessage instanceof TextMessage) {
                msg = (TextMessage) rcvMessage;
                Cache<String, String> c = m.getCache();
                String text = "";
                if(c.get(key) != null) text = c.get(key);
                text = text +","+ msg.getText();
                // LOGGER.info("Received Message from topic: " + msg.getText());
                LOGGER.info("Received Message from topic: " + text);
                c.put(key, text);
                LOGGER.info("put back to cache: " + key + " " + text);
            } else {
                LOGGER.warning("Message of wrong type: " + rcvMessage.getClass().getName());
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
