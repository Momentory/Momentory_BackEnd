package com.example.momentory.domain.roulette.entity;

/**
 * 룰렛 상태
 */
public enum RouletteStatus {
    IN_PROGRESS,    // 진행 중 (마감일 전, 미인증)
    SUCCESS,        // 성공 (마감일 전 인증 완료)
    FAILED          // 실패 (마감일 경과, 미인증)
}