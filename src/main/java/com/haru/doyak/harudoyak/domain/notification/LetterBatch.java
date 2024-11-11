package com.haru.doyak.harudoyak.domain.notification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class LetterBatch {

    private final NotificationService notificationService;
    // 임시 편지 리스트
    private final Map<Long, List<String>> userLetters = new HashMap<>();

    @Transactional
    public void addLetterForUser(Long userId, String letterContent, String message) {
        userLetters.computeIfAbsent(userId, k -> new ArrayList<>()).add(letterContent);
        notificationService.customNotify(userId, userLetters.get(userId),message, "letter");
    }
}
