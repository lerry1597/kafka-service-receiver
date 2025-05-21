package com.example.kafka;

import com.example.dto.MessageDTO;
import com.example.model.CheckoutEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaConsumerService {
    private static final Logger LOG = Logger.getLogger(KafkaConsumerService.class);

    @Inject
    EntityManager entityManager;
    ObjectMapper objectMapper = new ObjectMapper();

    @Incoming("input-topic")
    @Blocking
    public void consume(String data) throws JsonProcessingException {
        // Deserialize ke DTO
        MessageDTO messageDTO = objectMapper.readValue(data, MessageDTO.class);

        //Data Manipulation
        String productName = messageDTO.productName == null ? "" : messageDTO.productName.trim().toUpperCase();
        String status = messageDTO.status == null ? "" : messageDTO.status.trim().toUpperCase();

        // Mapping ke entity
        CheckoutEntity checkoutEntity = new CheckoutEntity();
        checkoutEntity.setProductName(productName);
        checkoutEntity.setStatus(status);
        checkoutEntity.setDateTime(messageDTO.getDateTime());
        checkoutEntity.setTotalAmount(messageDTO.getTotalAmount());

        // Simpan ke DB
        entityManager.persist(checkoutEntity);
        LOG.infof("Saved message: %s", messageDTO.productName);

    }
}
