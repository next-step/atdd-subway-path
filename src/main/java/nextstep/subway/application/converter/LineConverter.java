package nextstep.subway.application.converter;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.entity.Line;

import static nextstep.subway.application.converter.StationConverter.convertToStationResponses;

public class LineConverter {

    public static Line convertToLine(LineRequest request) {
        return new Line(
                request.getName(),
                request.getColor());
    }

    public static LineResponse convertToLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                convertToStationResponses(line.getSortedAllSections()));
    }
}
