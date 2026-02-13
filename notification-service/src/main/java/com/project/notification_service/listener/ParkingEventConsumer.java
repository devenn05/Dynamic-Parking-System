package com.project.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.notification_service.dto.LotUpdateDto;
import com.project.notification_service.dto.SessionUpdateDto;
import com.project.notification_service.dto.SlotUpdateDto;
import com.project.notification_service.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParkingEventConsumer {

    private final ObjectMapper objectMapper;
    private final SseService sseService;
    private final StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "parking-updates", groupId = "notification-group-1")
    public void consume(String message) {
        // --- ADD THIS LOG TO SEE EVERYTHING ---
        log.info("RAW MESSAGE FROM KAFKA: {}", message);

        try {
            // 1. Check for LOT CREATED/UPDATED/DELETED first
            if (message.contains("LOT_CREATED") || message.contains("LOT_UPDATED") || message.contains("LOT_DELETED")) {
                LotUpdateDto lotUpdate = objectMapper.readValue(message, LotUpdateDto.class);
                log.info("Processing LOT Registry update: {}", lotUpdate.getType());
                sseService.broadcast(lotUpdate);
            }
            // 2. Check for Session Update (checking for sessionId)
            else if (message.contains("sessionId")) {
                SessionUpdateDto sessionUpdate = objectMapper.readValue(message, SessionUpdateDto.class);
                log.info("Processing SESSION update: {}", sessionUpdate.getType());
                sseService.broadcast(sessionUpdate);
            }
            // 3. Finally, check for Slot Updates (availableSlots)
            // Note: Now that Lot events are handled above, this won't collide.
            else if (message.contains("availableSlots")) {
                SlotUpdateDto slotUpdate = objectMapper.readValue(message, SlotUpdateDto.class);

                // Safety check to prevent the null-key error
                if (slotUpdate.getLotId() == null) {
                    log.warn("Discarding SlotUpdate with NULL Lot ID");
                    return;
                }

                log.info("Processing SLOT update for lot {}", slotUpdate.getLotId());

                String redisKey = "lot:" + slotUpdate.getLotId() + ":count";
                redisTemplate.opsForValue().set(redisKey, String.valueOf(slotUpdate.getAvailableSlots()));

                sseService.broadcast(slotUpdate);
            }
            else {
                log.warn("Received message that matches no known pattern!");
            }

        } catch (Exception e) {
            log.error("Failed to parse JSON: {}. Message was: {}", e.getMessage(), message);
        }
    }
}