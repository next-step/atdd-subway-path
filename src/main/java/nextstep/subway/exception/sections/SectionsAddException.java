package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SectionsAddException extends BusinessException {

    private static final String ALREADY_BOTH_STATION_REGISTER_EXCEPTION = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다";
    private static final String NOT_FOUND_BOTH_STATION_EXCEPTION = "상행역과 하행역 모두 찾을 수 없습니다";
    private static final String SECTION_DISTANCE_EXCEPTION = "기존의 구간 길이보다 긴 신규구간을 중간에 추가할 수 없습니다";

    private SectionsAddException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public static SectionsAddException ALREADY_BOTH_STATION_REGISTER_EXCEPTION() {
        return new SectionsAddException(ALREADY_BOTH_STATION_REGISTER_EXCEPTION, HttpStatus.BAD_REQUEST);
    }

    public static SectionsAddException NOT_FOUND_BOTH_STATION_EXCEPTION() {
        return new SectionsAddException(NOT_FOUND_BOTH_STATION_EXCEPTION, HttpStatus.NOT_FOUND);
    }

    public static SectionsAddException SECTION_DISTANCE_EXCEPTION() {
        return new SectionsAddException(SECTION_DISTANCE_EXCEPTION, HttpStatus.BAD_REQUEST);
    }
}
