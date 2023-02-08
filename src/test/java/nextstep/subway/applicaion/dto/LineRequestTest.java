package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineRequestTest {

    @Test
    void 노선_생성() {
        String name = "이름";
        String color = "색깔";
        LineRequest request = LineRequest.of(name, color, 0L, 1L, 10);

        Line line = request.toEntity(id -> new Station(id, "역"));

        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
        assertThat(line.getSections().get(0).getUpStationId()).isEqualTo(0L);
        assertThat(line.getSections().get(0).getDownStationId()).isEqualTo(1L);
    }
}
