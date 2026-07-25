package com.goldengrove.controller;

import com.goldengrove.dto.ContactRequest;
import com.goldengrove.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> submit(@Valid @RequestBody ContactRequest request) {
        contactService.submit(request);
        return Map.of("message", "Thank you! We will get back to you soon.");
    }
}
