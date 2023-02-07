package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.function.Function;
import java.util.function.LongFunction;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public static LineRequest of(String name, String color, long upStationId, long downStationId, int distance1) {
        return new LineRequest(name, color, upStationId, downStationId, distance1);
    }

    public Line toEntity(LongFunction<Station> findStationFunction) {
        Line line = Line.of(name, color);

        if (canCreateSection()) {
            line.addSection(toSection(findStationFunction));
        }
        return line;
    }

    private boolean canCreateSection() {
        return this.getUpStationId() != null && this.getDownStationId() != null && this.getDistance() != 0;
    }

    private Section toSection(LongFunction<Station> findStationFunction) {
        Station upStation = findStationFunction.apply(upStationId);
        Station downStation = findStationFunction.apply(downStationId);

        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }
}
