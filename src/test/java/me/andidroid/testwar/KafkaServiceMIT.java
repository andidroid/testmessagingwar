package me.andidroid.testwar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.junit.jupiter.Container;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.containsString;
import static io.restassured.http.ContentType.TEXT;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.yasson.internal.serializer.ShortTypeSerializer;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.kafka.KafkaConsumerClient;
import org.microshed.testing.kafka.KafkaProducerClient;

@Tag("MicroShedTest")
@MicroShedTest
@SharedContainerConfig(MicroshedSharedContainerConfiguration.class)
public class KafkaServiceMIT
{
    
    @KafkaProducerClient(valueSerializer = StringSerializer.class)
    public static KafkaProducer<String, String> producer;
    
    //
    @KafkaConsumerClient(valueDeserializer = StringDeserializer.class, topics = "entetiesTopic", groupId = "test", properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
    public static KafkaConsumer<String, String> consumer;
    
    @Test
    public void testGetTestEvents()
    {
        System.out.println("execute get test");
        
        producer.send(new ProducerRecord<String, String>("entetiesTopic", "id=1"));
        
        System.out.println("Waiting to receive ready order from Kafka");
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
        System.out.println("Polled " + records.count() + " records from Kafka:");
        assertEquals(1, records.count(), "Expected to poll exactly 1 order from Kafka");
        for(ConsumerRecord<String, String> record: records)
        {
            String readyOrder = record.value();
            assertEquals(true, readyOrder.startsWith("read"));
        }
        consumer.commitAsync();
    }
}
