package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class PathNotFoundException extends SubwayException {
    public PathNotFoundException(Long sourceId, Long targetId) {
        super(createMessage(sourceId, targetId), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static String createMessage(Long sourceId, Long targetId) {
        if (sourceId.equals(targetId)) {
            return String.format("출발역과 도착역(ID: %d)이 동일하여 경로를 찾을 수 없습니다.", sourceId);
        }
        return String.format("출발역(ID: %d)에서 도착역(ID: %d)까지의 경로를 찾을 수 없습니다.", sourceId, targetId);
    }
}
