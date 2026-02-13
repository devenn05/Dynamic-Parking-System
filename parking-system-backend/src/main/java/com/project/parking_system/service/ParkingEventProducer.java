package com.project.parking_system.service;

import com.project.parking_system.dto.kafka.SessionUpdateDto;
import com.project.parking_system.dto.kafka.LotUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.project.parking_system.dto.kafka.SlotUpdateDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingEventProducer {
    private static final String TOPIC = "parking-updates";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Slot Update
    public void sendUpdate(SlotUpdateDto update) {
        publish(update.getLotId(), update);
    }

    // Session Update
    public void sendUpdate(SessionUpdateDto update) {
        publish(update.getLotId(), update);
    }

    // Lot Update
    public void sendUpdate(LotUpdateDto update){
        publish(update.getLot().getId(), update);
    }

    // Private helper to avoid code duplication
    private void publish(Long lotId, Object payload) {
        log.info("Publishing event to Kafka: Lot ID {}", lotId);
        kafkaTemplate.send(TOPIC, String.valueOf(lotId), payload);
    }
}
