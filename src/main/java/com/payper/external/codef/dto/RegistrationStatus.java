package com.payper.external.codef.dto;

public enum RegistrationStatus {
    SUCCESS("등록 완료"),
    RESTORED("복원 완료"),
    ALREADY_EXISTS("이미 등록된 카드"),
    NO_MATCH("매칭되는 카드 없음"),
    FAILED("등록 실패");

    private final String description;

    RegistrationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
