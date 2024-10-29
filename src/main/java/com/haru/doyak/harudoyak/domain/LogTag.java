package com.haru.doyak.harudoyak.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LogTag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long logTagId;
    private Long logId;
    private Long tagId;
}