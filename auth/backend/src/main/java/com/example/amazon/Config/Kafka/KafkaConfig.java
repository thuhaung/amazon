package com.example.amazon.Config.Kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${kafka.topic.email-verification}")
    private String emailVerificationTopic;
    @Value("${kafka.topic.user-auth}")
    private String userAuthTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);

        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic emailVerificationTopic() {
        return TopicBuilder.name(emailVerificationTopic)
                .partitions(1)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic userAuthTopic() {
        return TopicBuilder.name(userAuthTopic)
                .partitions(1)
                .replicas(2)
                .build();
    }
}
