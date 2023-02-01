package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineResponseTest {

    @Test
    void 노선_응답_생성() {
        long downStationId = 1L;
        long upStationId = 0L;

        Line line = createLine();
        line.addSection(createSection(upStationId, downStationId));

        LineResponse response = LineResponse.from(line);
        List<StationResponse> stations = response.getStations();

        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations.get(0).getId()).isEqualTo(upStationId);
        assertThat(stations.get(1).getId()).isEqualTo(downStationId);
    }

    private static Section createSection(long upStationId, long downStationId) {
        return Section.builder()
                .upStation(createStation(upStationId))
                .downStation(createStation(downStationId))
                .distance(10)
                .build();
    }

    private static Line createLine() {
        return Line.of("이름", "색깔");
    }

    private static Station createStation(Long id) {
        return new Station(id, "이름");
    }
}