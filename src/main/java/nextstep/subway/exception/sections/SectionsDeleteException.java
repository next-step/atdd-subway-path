package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SectionsDeleteException extends BusinessException {

    private static final String NOT_FOUND_SECTION_EXCEPTION = "해당 구간을 찾을 수 없습니다";
    private static final String NOT_FOUND_STATION_EXCEPTION = "해당 역을 찾을 수 없어 구간을 삭제할 수 없습니다";
    private static final String CANT_DELETE_LAST_ONE_SECTION_EXCEPTION = "노선의 마지막 하나 남은 구간은 삭제할 수 없습니다";

    private SectionsDeleteException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public static SectionsDeleteException NOT_FOUND_LAST_SECTION_EXCEPTION() {
        return new SectionsDeleteException(NOT_FOUND_SECTION_EXCEPTION, HttpStatus.NOT_FOUND);
    }

    public static SectionsDeleteException NOT_FOUND_STATION_EXCEPTION() {
        return new SectionsDeleteException(NOT_FOUND_STATION_EXCEPTION, HttpStatus.NOT_FOUND);
    }

    public static SectionsDeleteException CANT_DELETE_LAST_ONE_SECTION_EXCEPTION() {
        return new SectionsDeleteException(CANT_DELETE_LAST_ONE_SECTION_EXCEPTION, HttpStatus.BAD_REQUEST);
    }
}
