package com.maovares.ms_products.shared.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maovares.ms_products.shared.application.port.out.QueueService;
import com.maovares.ms_products.shared.domain.model.Event;

import java.util.Base64;

@Component
public class AzureQueueServiceAdapter implements QueueService {

    private final QueueClient queueClient;
    private final ObjectMapper objectMapper;

    public AzureQueueServiceAdapter(
            @Value("${azure.storage.queue.connection-string}") String connectionString,
            @Value("${azure.storage.queue.name}") String queueName,
            ObjectMapper objectMapper) {
        this.queueClient = new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .buildClient();
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendMessage(Event event) {
        try {
            String message = objectMapper.writeValueAsString(event);

            String base64Message = Base64.getEncoder().encodeToString(message.getBytes());

            queueClient.sendMessage(base64Message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }
}
