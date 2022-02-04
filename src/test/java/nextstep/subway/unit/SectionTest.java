package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("구간 관리")
public class SectionTest {
    private static final Line DEFAULT_LINE = new Line("2호선", "bg-green-700");
    private static final int DEFAULT_DISTANCE = 5;

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;

    @BeforeEach
    void setUp() {
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Station 삼성역 = createStation("삼성역");
    }

    @DisplayName("구간 생성")
    @Test
    void createSection() {
        // when
        Section section = createSection(강남역, 역삼역);

        // then
        assertThat(section).isNotNull();
    }

    @DisplayName("구간 생성")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void createSection(int distnace) {
        // when, then
        assertThatThrownBy(() -> createSection(강남역, 역삼역, distnace))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간의 길이는 0 이하가 될 수 없습니다.");
    }

    @DisplayName("기존 구간 중간에 추가될 새로운 구간 거리가 기존 거리보다 작으면 수정")
    @CsvSource(value = {"5:3:2", "10:5:5"}, delimiter = ':')
    @ParameterizedTest()
    void updateDistance(int defaultDistance, int newDistance, int changeDistance) {
        // given
        Section section = createSection(강남역, 삼성역, defaultDistance);
        Section newSection = createSection(강남역, 역삼역, newDistance);

        // when
        section.changeDownStationToNewUpStations(newSection);

        // then
        assertThat(section.getDistance()).isEqualTo(changeDistance);
    }

    @DisplayName("기존 구간 중간에 추가될 새로운 구간 거리가 기존 거리보다 같거나 크면 예외처리")
    @CsvSource(value = {"5:5", "1:2"}, delimiter = ':')
    @ParameterizedTest()
    void updateOverDistance(int defaultDistance, int overDistance) {
        // given
        Section section = createSection(강남역, 삼성역, defaultDistance);
        Section newSection = createSection(강남역, 역삼역, overDistance);

        // when, then
        assertThatThrownBy(() -> section.changeDownStationToNewUpStations(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새로운 구간의 길이가 기존 구간 사이의 길이보다 큽니다.");
    }

    private Station createStation(String name) {
        return new Station(name);
    }

    private Section createSection(Station upStation, Station downStation) {
        return createSection(upStation, downStation, DEFAULT_DISTANCE);
    }

    private Section createSection(Station upStation, Station downStation, int distance) {
        return new Section(DEFAULT_LINE, upStation, downStation, distance);
    }
}
