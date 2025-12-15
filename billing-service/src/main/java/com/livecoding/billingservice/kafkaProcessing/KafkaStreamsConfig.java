package com.livecoding.billingservice.kafkaprocessing;

import com.livecoding.events.BillCreatedEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.apache.kafka.streams.StreamsBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;

@Configuration
@Slf4j
public class KafkaStreamsConfig {

    @Bean
    public KStream<String, BillCreatedEvent> kStream(StreamsBuilder builder) {
        KStream<String, BillCreatedEvent> stream = builder.stream(
                "bill-created-topic",
                Consumed.with(Serdes.String(), new JacksonJsonSerde<>(BillCreatedEvent.class))
        );

        // Filtre >500€
        stream.filter((key, event) -> event.getTotalAmount() > 500)
                .foreach((key, event) -> log.info("💡 Stream: Facture > 500€: {}", event));

        return stream;
    }
}
