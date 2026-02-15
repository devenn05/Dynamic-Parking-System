package com.project.notification_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // This is the URL that UptimeRobot will hit: https://your-app.koyeb.app/
    @GetMapping("/")
    public String healthCheck() {
        return "Notification Service is Running!";
    }
}