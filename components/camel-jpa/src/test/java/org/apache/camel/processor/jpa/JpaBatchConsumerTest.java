/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor.jpa;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.examples.SendEmail;
import org.junit.jupiter.api.Test;

public class JpaBatchConsumerTest extends AbstractJpaTest {

    protected static final String SELECT_ALL_STRING = "select x from " + SendEmail.class.getName() + " x";

    @Test
    public void testBatchConsumer() throws Exception {
        // first create two records
        template.sendBody("jpa://" + SendEmail.class.getName(), new SendEmail("foo@beer.org"));
        template.sendBody("jpa://" + SendEmail.class.getName(), new SendEmail("bar@beer.org"));

        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        mock.message(0).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(0);
        mock.message(0).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
        mock.message(1).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(1);
        mock.message(1).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(true);
        mock.expectedPropertyReceived(Exchange.BATCH_SIZE, 2);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("jpa://" + SendEmail.class.getName() + "?delay=2000").to("mock:result");
            }
        };
    }

    @Override
    protected String routeXml() {
        return "org/apache/camel/processor/jpa/springJpaRouteTest.xml";
    }

    @Override
    protected String selectAllString() {
        return SELECT_ALL_STRING;
    }
}
