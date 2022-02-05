package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.IllegalUpdatingStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LineTest {

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;

    @BeforeEach
    public void setUp() {
        강남역 = new Station("강남역");
        판교역 = new Station("판교역");
        정자역 = new Station("정자역");
    }

    @DisplayName("역 사이에 새로운 역을 추가할 경우 - 상행선 동일")
    @Test
    void addSectionBetweenStationsSameUpStation() {
        // given
        int 총_길이 = 10;
        Line 신분당선 = new Line("신분당선", "red", 강남역, 정자역, 총_길이);

        // when
        신분당선.addSection(강남역, 판교역, 6);

        // then
        assertThat(신분당선.getAllStations()).containsExactly(강남역, 판교역, 정자역);
        assertThat(신분당선.getTotalDistance()).isEqualTo(총_길이);
    }

    @DisplayName("역 사이에 새로운 역을 추가할 경우 - 하행선 동일")
    @Test
    void addSectionBetweenStationsSameDownStation() {
        // given
        int 총_길이 = 10;
        Line 신분당선 = new Line("신분당선", "red", 강남역, 정자역, 총_길이);

        // when
        신분당선.addSection(판교역, 정자역, 4);

        // then
        assertThat(신분당선.getAllStations()).containsExactly(강남역, 판교역, 정자역);
        assertThat(신분당선.getTotalDistance()).isEqualTo(총_길이);
    }

    @DisplayName("역 사이에 새로운 역을 추가할 경우 - 길이가 길거나 같을 시 실패")
    @Test
    void addSectionBetweenStationsWithLongDistanceFail() {
        // given
        int 길이 = 10;
        Line 신분당선 = new Line("신분당선", "red", 강남역, 정자역, 길이);

        // when, then
        assertThatExceptionOfType(IllegalUpdatingStateException.class)
                .isThrownBy(() -> 신분당선.addSection(강남역, 정자역, 길이));
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSectionOnLastDownStation() {
        // given
        Line 신분당선 = new Line("신분당선", "red", 강남역, 판교역, 8);

        // when
        Section section = 신분당선.addSection(판교역, 정자역, 3);

        // then
        assertThat(section.getUpStation()).isEqualTo(판교역);
        assertThat(section.getDownStation()).isEqualTo(정자역);
        assertThat(section.getDistance()).isGreaterThan(0);
    }

    @DisplayName("노선 총 길이 조회")
    @Test
    void getTotalDistance() {
        // given
        int 강남_판교_길이 = 8;
        int 판교_정자_길이 = 3;
        int 총_길이 = 강남_판교_길이 + 판교_정자_길이;
        Line 신분당선 = new Line("신분당선", "red", 강남역, 판교역, 강남_판교_길이);
        신분당선.addSection(판교역, 정자역, 판교_정자_길이);

        // when
        int totalDistanceResult = 신분당선.getTotalDistance();

        // then
        assertThat(totalDistanceResult).isEqualTo(총_길이);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Line 신분당선 = new Line("신분당선", "red", 강남역, 판교역, 8);
        신분당선.addSection(판교역, 정자역, 3);

        // when
        List<Station> stations = 신분당선.getAllStations();

        // then
        assertThat(stations).containsExactly(강남역, 판교역, 정자역);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        Line 신분당선 = new Line("신분당선", "red", 강남역, 판교역, 8);
        신분당선.addSection(판교역, 정자역, 3);

        // when
        신분당선.removeSection(정자역);

        // then
        assertThat(신분당선.getAllStations()).containsExactly(강남역, 판교역);
    }
}
