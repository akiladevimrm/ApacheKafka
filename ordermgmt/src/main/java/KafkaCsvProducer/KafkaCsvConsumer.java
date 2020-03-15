package KafkaCsvProducer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class KafkaCsvConsumer {
    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(KafkaCsvConsumer.class.getName());
        String bootstrapServers = "127.0.0.1:9092";
        //String grp_id="third_app";
        String topic = "myTopic";
        //Creating consumer properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY, "1");
        properties.setProperty("enable.auto.commit", "true");
        properties.setProperty("auto.commit.interval.ms", "10000000");
        properties.setProperty("auto.offset.reset", "earliest");
        properties.setProperty("session.timeout.ms", "30000000");
        properties.setProperty("security.protocol", "SASL_SSL");
        properties.setProperty("sasl.mechanism", "SCRAM-SHA-256");
        properties.setProperty("offsets.topic.replication.factor","1");

        //creating consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        //Subscribing
        consumer.subscribe(topic);
        Thread.sleep(15000);
        //polling

            ConsumerRecords<String, String> records = (ConsumerRecords<String, String>) consumer.poll((10000000)).get("Book1");
            System.out.println(records);
            //   logger.info("Partition:" + record.partition()+",Offset:"+record.offset());
            for (ConsumerRecord<String, String> record : records.records()) {
                logger.info("Key: " + record.key() + ", Value:" + record.value());
                logger.info("Partition:" + record.partition() + ",Offset:" + record.offset());
            }

    }
}
