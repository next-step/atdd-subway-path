package nextstep.subway.unit.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.model.Distance;
import nextstep.subway.line.domain.model.Line;
import nextstep.subway.station.domain.model.Station;

class LineTest {
    private Line line;
    private Station upStation;
    private Station downStation;
    private Distance distance;

    @BeforeEach
    void setUp() {
        this.line = new Line(1L, "초기 노선", "bg-red-500");
        this.upStation = new Station(1L, "초기 상행");
        this.downStation = new Station(2L, "초기 하행");
        this.distance = new Distance(100);
    }

    @DisplayName("구간 정보 수정")
    @Test
    void edit() {
        String changedName = "변경된 이름";
        String changedColor = "bg-red-500";

        line.edit(changedName, changedColor);

        assertThat(line.getName()).isEqualTo(changedName);
        assertThat(line.getColor()).isEqualTo(changedColor);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        line.addSection(
            upStation, downStation, distance
        );

        assertThat(line.getStations().size()).isEqualTo(2);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        line.addSection(
            upStation, downStation, distance
        );

        assertThat(line.getStations())
            .containsAll(Arrays.asList(upStation, downStation));
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void deleteSection() {
        Station newDownStation = new Station(1L, "새로운 구간의 하행");
        line.addSection(upStation, downStation, distance);
        line.addSection(downStation, newDownStation, distance);

        line.deleteSection(newDownStation.getId());

        assertThat(line.getStations().size())
            .isEqualTo(2);
    }
}
