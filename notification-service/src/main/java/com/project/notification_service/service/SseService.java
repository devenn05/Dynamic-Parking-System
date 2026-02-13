package com.project.notification_service.service;

import com.project.notification_service.dto.LotUpdateDto;
import com.project.notification_service.dto.SessionUpdateDto;
import com.project.notification_service.dto.SlotUpdateDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    // K: Lot ID | V: List of User Connections watching this lot
    // Thread-safe map to handle concurrent users
    private final Map<Long, List<SseEmitter>> lotEmitters = new ConcurrentHashMap<>();

    /**
     * Called when a User opens the dashboard.
     * Keeps the connection OPEN.
     */
    public SseEmitter subscribe(Long lotId) {
        // Timeout: 0 = Infinity (Wait forever for updates)
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // 1. Register this user to the specific Lot ID
        lotEmitters.computeIfAbsent(lotId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // 2. Cleanup: If user closes tab, remove them from list
        emitter.onCompletion(() -> removeEmitter(lotId, emitter));
        emitter.onTimeout(() -> removeEmitter(lotId, emitter));
        emitter.onError((e) -> removeEmitter(lotId, emitter));

        return emitter;
    }

    /**
     * Called when Kafka receives a message.
     * Pushes data to all users watching this specific Lot.
     */
    public void broadcast(SlotUpdateDto update) {
        Long lotId = update.getLotId();
        List<SseEmitter> emitters = lotEmitters.get(lotId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("parking-update")
                            .data(update));
                } catch (IOException e) {
                    removeEmitter(lotId, emitter);
                }
            }
        }
    }
    public void broadcast(SessionUpdateDto update) {
        Long lotId = update.getLotId();
        List<SseEmitter> emitters = lotEmitters.get(lotId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("session-update")
                            .data(update));
                } catch (IOException e) {
                    removeEmitter(lotId, emitter);
                }
            }
        }
    }
    public void broadcast(LotUpdateDto update) {
        // We send lot registry updates to "0" because everyone needs them
        List<SseEmitter> globalEmitters = lotEmitters.get(0L);

        if (globalEmitters != null) {
            for (SseEmitter emitter : globalEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("lot-registry-update") // New event name
                            .data(update));
                } catch (IOException e) {
                    removeEmitter(0L, emitter);
                }
            }
        }
    }

    private void removeEmitter(Long lotId, SseEmitter emitter) {
        List<SseEmitter> emitters = lotEmitters.get(lotId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }
}