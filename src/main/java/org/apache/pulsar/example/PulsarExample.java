package org.apache.pulsar.example;

import org.apache.pulsar.client.api.*;

public class PulsarExample {



    public static void main(String[] args) throws PulsarClientException {

        String pulsarBrokerRootUrl = "pulsar://localhost:6650";
        PulsarClient client = PulsarClient.create(pulsarBrokerRootUrl);
        String topic = "persistent://sample/standalone/ns1/my-topic";
        Producer producer = client.createProducer(topic);

        System.out.println("\n\n######## Producing Messages Below ########\n\n");

        byte[] bytes;
        final int luckyNumber = 5;
        MessageId lucykNumberMessageId = null;
        for (int i = 0; i < 10; i++) {
            if (i == luckyNumber) {
                bytes = "lucky-five".getBytes();
                lucykNumberMessageId = producer.send(bytes);
                System.out.println("Lucky number 5 Message Id is: " + lucykNumberMessageId);

            } else {
                bytes = "my-message".getBytes();
                MessageId id = producer.send(bytes);
                System.out.println("Not lucky message: " + id);
            }
        }

        producer.close();

        System.out.println("\n\n######## Consuming Messages Below ########\n\n");

        String subscription = "my-subscription";
        Consumer consumer = client.subscribe(topic, subscription);

        while (true) {

            // Wait for a message
            Message msg = consumer.receive();

            String response = new String(msg.getData());


            // Acknowledge the message so that it can be deleted by broker
            if (lucykNumberMessageId !=  null && msg.getMessageId().equals(lucykNumberMessageId)) {
                System.out.printf("Lucky number 5 id %s, message %s: \n", msg.getMessageId(), response);
                consumer.acknowledge(msg);
            } else {
                System.out.println("Not lucky response: " + msg.getMessageId());
                consumer.acknowledge(msg);
            }

        }
    }
}
