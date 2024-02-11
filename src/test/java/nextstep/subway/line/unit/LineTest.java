package nextstep.subway.line.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.exception.SectionAddFailureException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private Line line;

    @BeforeEach
    void setup() {
        line = Line.of("수인분당선", "yellow");
    }

    @DisplayName("상행역, 하행역, 거리가 주어지면 해당 노선에 구간이 추가된다")
    @Test
    void addSection() {
        line.addSection(new Station("수원역"), new Station("고색역"), 10);

        assertThat(line.getAllSections().isEmpty()).isFalse();
        assertThat(line.getAllSections().size()).isEqualTo(1);
    }

    @DisplayName("노선 가운데 새로운 역을 추가할 수 있다")
    @Test
    void addSectionToMiddle() {
        Station upStation = new Station("수원역");
        line.addSection(upStation, new Station("오목천역"), 10);
        line.addSection(upStation, new Station("고색역"), 4);

        assertThat(line.getAllSections().size()).isEqualTo(2);
    }

    @DisplayName("이미 존재하는 구간과 동일한 구간을 추가하면 예외를 던진다")
    @Test
    void cannotAddExistingSection() {
        Station 수원역 = new Station("수원역");
        Station 고색역 = new Station("고색역");
        line.addSection(수원역, 고색역, 10);

        assertThatThrownBy(() -> line.addSection(수원역, 고색역, 10))
            .isInstanceOf(SectionAddFailureException.class)
            .hasMessageContaining("이미 존재하는 구간입니다.");
    }

    @DisplayName("추가하려는 구간의 하행역이 이미 존재하는 역이면 예외를 던진다")
    @Test
    void cannotAddExistingStation() {
        Station 수원역 = new Station("수원역");
        Station 고색역 = new Station("고색역");
        Station 오목천역 = new Station("오목천역");

        line.addSection(수원역, 오목천역, 10);
        line.addSection(수원역, 고색역, 4);

        assertThatThrownBy(() -> line.addSection(오목천역, 수원역, 10))
            .isInstanceOf(SectionAddFailureException.class)
            .hasMessageContaining("새로운 구간의 하행역이 기존 노선에 이미 존재합니다.");
    }

    @DisplayName("해당 노선의 모든 역을 확인할 수 있다")
    @Test
    void getStations() {
        line.addSection(new Station("수원역"), new Station("고색역"), 10);

        assertThat(line.getStations().isEmpty()).isFalse();
        assertThat(line.getStations().stream().map(Station::getName)).contains("수원역", "고색역");
    }

    @DisplayName("주어진 구간을 제거할 수 있다")
    @Test
    void removeSection() {
        Station downStation = new Station("고색역");
        line.addSection(new Station("수원역"), downStation, 10);
        line.addSection(downStation, new Station("오목천역"), 10);

        line.removeSectionByStation(downStation);

        assertThat(line.getStations().isEmpty()).isFalse();
        assertThat(line.getAllSections().size()).isEqualTo(1);
        assertThat(line.getStations().stream().map(Station::getName)).isNotIn("고색역");
    }
}
