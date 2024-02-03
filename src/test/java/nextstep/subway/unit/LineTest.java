package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class LineTest {

    private Line line;
    private Station 강남역;
    private Station 선릉역;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        강남역 = new Station("강남역");
        선릉역 = new Station("선릉역");
    }

    @Test
    @DisplayName("지하철 노선의 구간을 생성한다.")
    void addSection() {
        Section section = new Section(line, 강남역, 선릉역, 10);
        line.addSection(section);
        assertThat(line.sectionsSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("지하철 노선의 구간을 조회한다.")
    void getStations() {
        Section section = new Section(line, 강남역, 선릉역, 10);
        line.addSection(section);
        assertThat(line.sections()).hasSize(1)
                .extracting("line.name", "upStation.name", "downStation.name", "distance")
                .containsExactly(
                        tuple("2호선", "강남역", "선릉역", 10)
                );
    }

    @Test
    @DisplayName("지하철 노선의 구간을 제거한다.")
    void removeSection() {
        Section section = new Section(line, 강남역, 선릉역, 10);
        line.removeSection(section);
        assertThat(line.sectionsSize()).isEqualTo(0);
    }

}
