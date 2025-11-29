package com.example.ridehailing.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic rideRequestTopic() {
        return TopicBuilder.name("rideRequest")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic rideUpdatesTopic() {
        return TopicBuilder.name("rideUpdates")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentRequest() {
        return TopicBuilder.name("paymentRequest").build();
    }
}
