package nextstep.subway.exception;

public enum SubwayExceptionMessage {

    SECTION_CANNOT_ADD("구간을 추가할 수 없습니다."),
    SECTION_LONGER("추가하려는 구간의 길이가 더 길어 추가할 수 없습니다."),
    STATION_CANNOT_REMOVE("역을 삭제할 수 없습니다."),
    PATH_CANNOT_FIND("경로를 조회할 수 없습니다")
    ;


    private final String message;

    SubwayExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
