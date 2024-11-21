package com.haru.doyak.harudoyak.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haru.doyak.harudoyak.dto.notification.SseDataDTO;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SseDataConverter implements AttributeConverter<SseDataDTO, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SseDataDTO sseDataDTO) {
        try {
            return objectMapper.writeValueAsString(sseDataDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("fail to convert SseDataDTO object to JSON", e);
        }
    }

    @Override
    public SseDataDTO convertToEntityAttribute(String string) {
        try {
            return objectMapper.readValue(string, SseDataDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to convert JSON to SseDataDTO object", e);
        }
    }
}
