package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LineTest {
    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("신논현역");
        downStation = new Station("언주역");
        line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 10));
    }

    @DisplayName("노선의 하행 종점역에 새로운 구간을 정상적으로 추가했습니다.")
    @Test
    void addSectionAtDownStationInLine() {
        // given
        Station newStation = new Station("선정릉역");

        // when
        line.addSection(new Section(line, downStation, newStation, 6));

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("노선의 상행 종점역에 새로운 구간을 정상적으로 추가했습니다.")
    @Test
    void addSectionAtUpStationInLine() {
        // given
        Station newStation = new Station("선정릉역");

        // when
        line.addSection(new Section(line, newStation, upStation, 6));

        // then
        assertThat(line.getStations()).containsExactly(newStation, upStation, downStation);
    }

    @DisplayName("노선의 중간에 신규 역을 정상적으로 추가했습니다.")
    @Test
    void addSectionAtMiddleSection() {
        // given
        Station newStation = new Station("선정릉역");

        // when
        line.addSection(new Section(line, upStation, newStation, 6));

        // then
        assertThat(line.getStations()).containsExactly(upStation, newStation, downStation);
    }

    @ParameterizedTest(name = "노선의 중간에 길이가 {0}인 신규 역을 등록할 경우 노선의 기존 구간의 길이보다 크거나 같으면 예외처리 된다.")
    @ValueSource(ints = {10, 15})
    void exceptionDistanceOfNewSectionOverSectionOfExistingLine(int distance) {
        // given
        Station newStation = new Station("선정릉역");

        // when & then
        assertThatThrownBy(() -> line.addSection(new Section(line, upStation, newStation, distance)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록할 구간의 역이 노선에 이미 등록된 구간인 경우 예외처리 된다.")
    @Test
    void exceptionAlreadyNewSectionInLine() {
        // given
        Station newUpStation = new Station("신논현역");
        Station newDownStation = new Station("언주역");

        // when & then
        assertThatThrownBy(() -> line.addSection(new Section(line, newUpStation, newDownStation, 3)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록할 구간의 역 모두가 노선이 등록되어 있지 않은 역인 경우 예외처리 된다.")
    @Test
    void exceptionNewSectionNotExistInLine() {
        // given
        Station newUpStation = new Station("강남역");
        Station newDownStation = new Station("선릉역");

        // when & then
        assertThatThrownBy(() -> line.addSection(new Section(line, newUpStation, newDownStation, 3)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 역 목록이 정상 조회되었습니다.")
    @Test
    void getStations() {
        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(upStation, downStation);
    }

    @DisplayName("구간에서 하행 종점역이 정상적으로 삭제되었습니다.")
    @Test
    void removeSection() {
        // given
        Station newStation = new Station("선정릉역");
        line.addSection(new Section(line, downStation, newStation, 6));

        // when
        line.deleteSection(newStation);

        // then
        assertAll(
                () -> assertThat(line.getStations()).containsExactly(upStation, downStation),
                () -> assertThat(line.getStations()).doesNotContain(newStation)
        );
    }

    @DisplayName("구간이 한개만 존재하는 경우 구간 삭제를 하면 오류가 발생한다")
    @Test
    void removeSectionIsOneSectionException() {
        // when & then
        assertThatThrownBy(() -> line.deleteSection(downStation))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @DisplayName("노선에서 삭제를 하는 역이 하행 종점역이 아닌경우 오류가 발생한다")
    @Test
    void removeSectionNotMatchedEndStationException() {
        // given
        Station newStation = new Station("선정릉역");
        line.addSection(new Section(line, downStation, newStation, 6));
        
        // when & then
        assertThatThrownBy(() -> line.deleteSection(upStation))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 정보를 정상적으로 수정했습니다")
    @Test
    void updateLine() {
        // when
        line.updateLine("3호선", "bg-yellow-600");

        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("3호선"),
                () -> assertThat(line.getColor()).isEqualTo("bg-yellow-600")
        );
    }
}
