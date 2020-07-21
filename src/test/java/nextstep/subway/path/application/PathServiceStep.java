package nextstep.subway.path.application;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;

public class PathServiceStep {

    public static LineResponse 테스트를_위해_시간을_고정한_LineResponse를_생성한다(Long id, String name, String color,
        List<LineStationResponse> responses) {
        return new LineResponse(id, name, color, LocalTime.now(), LocalTime.now(), 5, responses,
            LocalDateTime.now(), LocalDateTime.now()
        );
    }
}
