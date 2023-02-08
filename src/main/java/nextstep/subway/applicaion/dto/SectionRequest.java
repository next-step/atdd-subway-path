package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.function.LongFunction;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section toEntity(LongFunction<Station> stationFindFunction) {
        return Section.builder()
                .upStation(stationFindFunction.apply(upStationId))
                .downStation(stationFindFunction.apply(downStationId))
                .distance(distance)
                .build();
    }
}
