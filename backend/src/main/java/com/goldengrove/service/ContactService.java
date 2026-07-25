package com.goldengrove.service;

import com.goldengrove.dto.ContactRequest;
import com.goldengrove.entity.ContactMessage;
import com.goldengrove.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;

    public void submit(ContactRequest request) {
        ContactMessage message = ContactMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .build();
        contactMessageRepository.save(message);
    }
}
