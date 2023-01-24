package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineInfo;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("호선 객체 테스트")
class LineTest {

    private Line line;
    private Station upStation;
    private Station downStation;
    private Section section;

    @BeforeEach
    void setUp() {
        line = new Line(new LineInfo("2호선", "green"));
        upStation = new Station("강남역");
        downStation = new Station("잠실역");
        section = new Section(line, upStation, downStation, 10);
    }

    @DisplayName("호선의 구간을 생성한다.")
    @Test
    void addSection() {

        line.addSection(section);

        assertAll(
                () -> assertThat(line.getSections()).hasSize(1),
                () -> assertThat(line.getSections().get(0).getLine().getLineInfo()).isEqualTo(new LineInfo("2호선", "green")),
                () -> assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("강남역"),
                () -> assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("잠실역"),
                () -> assertThat(line.getSections().get(0).getDistance()).isEqualTo(10)
        );
    }

    @Test
    void getStations() {
    }

    @DisplayName("호선의 구간을 추가 후 제거한다.")
    @Test
    void removeSection() {

        line.addSection(section);

        line.removeSection(downStation);

        assertThat(line.getSections()).isEmpty();
    }
}
