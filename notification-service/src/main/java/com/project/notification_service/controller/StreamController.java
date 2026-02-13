package com.project.notification_service.controller;

import com.project.notification_service.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.security.sasl.SaslServer;
import java.awt.*;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
@CrossOrigin("https://parking-system-frontend-t9wu.onrender.com/")
public class StreamController {
    private final SseService sseService;

    @GetMapping(path = "/subscribe/{lotId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long lotId){
        return sseService.subscribe(lotId);
    }
}
