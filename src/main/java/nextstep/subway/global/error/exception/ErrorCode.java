package nextstep.subway.global.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NEW_SECTION_LENGTH_MUST_BE_SMALLER_THAN_EXISTING_SECTION_LENGTH(HttpStatus.BAD_REQUEST.value(), "추가하는 구간이 기존의 구간의 역 사이의 길이보다 크거나 같으면 등록을 할 수 없습니다"),
    NOT_STATION_OF_END_SECTION(HttpStatus.BAD_REQUEST.value(), "노선의 마지막 구간의 하행선이 아닙니다"),
    ALREADY_EXISTED_STATIONS_OF_NEW_SECTION(HttpStatus.BAD_REQUEST.value(), "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다"),
    NOT_EXISTS_STATIONS_OF_SECTION(HttpStatus.BAD_REQUEST.value(), "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다"),
    CAN_NOT_REMOVE_ONLY_ONE_SECTION(HttpStatus.BAD_REQUEST.value(), "구간이 하나인 노선에서 마지막 구간은 삭제할 수 없습니다"),
    START_STATION_MUST_NOT_SAME_END_STATION(HttpStatus.BAD_REQUEST.value(), "출발역과 도착역이 같은 경우에는 경로를 찾을 수 없습니다"),
    STATIONS_NOT_CONNECTED(HttpStatus.BAD_REQUEST.value(), "출발역과 도착역이 연결이 되어 있지 않은 경우에는 경로를 찾을 수 없습니다"),
    NOT_EXISTS_STATIONS(HttpStatus.BAD_REQUEST.value(), "존재하지 않은 출발역이나 도착역을 조회 할 경우에는 경로를 찾을 수 없습니다");

    private int status;
    private String errorMessage;

    ErrorCode(int status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
