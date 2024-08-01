package nextstep.subway.common.constant;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCode {
    STATION_NOT_FOUND(400, "STATION_NOT_FOUND", "역을 찾을 수 없습니다."),
    LINE_NOT_FOUND(400, "LINE_NOT_FOUND", "노선을 찾을 수 없습니다."),
    SECTION_NOT_FOUND(400, "SECTION_NOT_FOUND", "구간을 찾을 수 없습니다."),
    PATH_NOT_FOUND(400, "PATH_NOT_FOUND", "경로를 찾을 수 없습니다."),
    PATH_DUPLICATE_STATION(400, "PATH_DUPLICATE_STATION", "출발역과 도착역은 동일할 수 없습니다."),
    PATH_NOT_FOUND_SOURCE_STATION(400, "PATH_NOT_FOUND_SOURCE_STATION", "출발역과 연결된 경로를 찾을 수 없습니다."),
    PATH_NOT_FOUND_TARGET_STATION(400, "PATH_NOT_FOUND_TARGET_STATION", "도착역과 연결된 경로를 찾을 수 없습니다."),
    SECTION_FIRST_STATION_NOT_FOUND(400, "SECTION_FIRST_STATION_NOT_FOUND", "처음 구간을 찾을 수 없습니다."),
    SECTION_LAST_STATION_NOT_FOUND(400, "SECTION_LAST_STATION_NOT_FOUND", "마지막 구간을 찾을 수 없습니다."),
    SECTION_UP_STATION_NOT_FOUND(400, "SECTION_UP_STATION_NOT_FOUND", "상행 구간의 역을 찾을 수 없습니다."),
    SECTION_DOWN_STATION_NOT_FOUND(400, "SECTION_DOWN_STATION_NOT_FOUND", "하행 구간의 역을 찾을 수 없습니다."),
    SECTION_DISTANCE_TOO_SHORT(400, "SECTION_DISTANCE_TOO_SHORT", "구간의 길이는 최소 1 이상이어햐 합니다."),
    SECTION_DISTANCE_LESS_THAN_EXISTING(400, "SECTION_DISTANCE_LESS_THAN_EXISTING", "새로운 구간의 길이는 기존의 구간 길이보다 길어야합니다."),
    SECTION_NOT_MATCH(400, "SECTION_NOT_MATCH", "새로운 구간의 상행역은 등록되어 있는 하행 종점역이어야 합니다."),
    SECTION_ALREADY_EXIST(400, "SECTION_ALREADY_EXIST", "새로운 구간의 상행역과 하행역이 이미 등록되어 있습니다."),
    SECTION_NOT_PERMISSION_NOT_LAST_DESCENDING_STATION(400, "SECTION_NOT_PERMISSION_NOT_LAST_DESCENDING_STATION", "하행 종점역만 삭제할 수 있습니다."),
    SECTION_NOT_PERMISSION_COUNT_TOO_LOW(400, "SECTION_NOT_PERMISSION_COUNT_TOO_LOW", "구간은 최소 1개 이상이어야 합니다."),
    ERROR_MESSAGE(499, "ERROR_MESSAGE", "관리자에게 문의하세요.");

    private static final Map<String, ErrorCode> ERROR_CODE_MAP = new HashMap<>();

    static {
        for (ErrorCode errorCode : ErrorCode.values()) {
            ERROR_CODE_MAP.put(errorCode.getCode(), errorCode);
        }
    }

    private final int status;
    private final String code;
    private final String description;

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }


    ErrorCode(int status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }

    public static ErrorCode getCollectedErrorResponse(String code) {
        return ERROR_CODE_MAP.getOrDefault(code, ERROR_MESSAGE);
    }
}
