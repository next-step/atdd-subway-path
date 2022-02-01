package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line line;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void init() {
        final int distance = 10;
        upStation = new Station("수원역");
        downStation = new Station("사당역");
        line = new Line("1호선","파랑색", upStation, downStation, distance);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        final int newDistance = 5;
        final Station newDownStation = new Station("성균관대역");
        line.addSection(downStation, newDownStation, newDistance);

        assertThat(line.getSections().size()).isEqualTo(2);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        HashSet<Station> stations = new HashSet<>();
        for (Section section : line.getSections()) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(stations).containsExactly(upStation, downStation);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        final int newDistance = 5;
        final Station newDownStation = new Station("성균관대역");
        line.addSection(downStation, newDownStation, newDistance);

        // when
        line.deleteSection(newDownStation);

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
    }
}
