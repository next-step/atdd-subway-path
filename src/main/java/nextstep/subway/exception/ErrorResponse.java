package nextstep.subway.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 에러의 응답 body를 공통 포맷에 맞게 변경하여 리턴합니다.
 */
@Getter
@Slf4j
public class ErrorResponse {
    private final String message;

    @Builder
    public ErrorResponse(String message) {
        this.message = message;
    }

    /**
     * Subway 비즈니스 로직의 논리적 예외 발생 시 responseBody 형식을 설정합니다.
     */
    public static <T extends SubwayException> ErrorResponse from(final T exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .build();
    }
}
