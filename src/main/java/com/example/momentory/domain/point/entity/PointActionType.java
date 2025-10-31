package com.example.momentory.domain.point.entity;

public enum PointActionType {

    SIGNUP("회원가입"),
    UPLOAD("사진 업로드"),
    EDIT_USE("편집 기능 사용"),
    RECEIVE_LIKE("좋아요 받기"),
    FOLLOW_GAINED("팔로워 증가"),
    LEVELUP("레벨업 보상"),
    ROULETTE("룰렛 실행"),
    BUY_ITEM("아이템 구매"),
    ROULETTE_REWARD("룰렛 보상");

    private final String description;

    PointActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
