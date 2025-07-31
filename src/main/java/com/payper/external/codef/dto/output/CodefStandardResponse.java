package com.payper.external.codef.dto.output;

// 토큰 발급하는 것 외에 모든 응답은 이 형태로 반환됨
public record CodefStandardResponse<T> (Result result, T data) {

}
