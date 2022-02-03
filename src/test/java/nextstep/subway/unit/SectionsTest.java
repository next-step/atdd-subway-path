package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private Line 신분당선;
    private Station 강남역;
    private Station 양재역;
    private int distance;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        distance = 10;
        신분당선.addSection(강남역, 양재역, distance);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection() {
        // given
        Station 신논현역 = new Station("신논현역");

        // when
        신분당선.addSection(신논현역, 강남역, distance);

        // then
        assertThat(신분당선.getStations()).containsExactly(신논현역, 강남역, 양재역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSection2() {
        // given
        Station 중간역 = new Station("중간역");

        // when
        신분당선.addSection(강남역, 중간역, 6);

        // then
        assertThat(신분당선.getStations()).containsExactly(강남역, 중간역, 양재역);
        assertThat(신분당선.getSections().stream().mapToInt(Section::getDistance).sum()).isEqualTo(distance);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 실패 - 신규 역 사이 길이가 기존 역 사이 길이보다 길거나 같으면 실패")
    @Test
    void addSectionFail() {
        // given
        Station 중간역 = new Station("중간역");

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 중간역, distance))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> 신분당선.addSection(중간역, 양재역, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 실패 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionFail2() {
        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 양재역, 6))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 실패 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionFail3() {
        // given
        Station 대림역 = new Station("대림역");
        Station 신풍역 = new Station("신풍역");

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(대림역, 신풍역, 6))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선에서 상행 종점역을 제거")
    @Test
    void deleteSection() {
        // given
        Station 정자역 = new Station("정자역");
        신분당선.addSection(양재역, 정자역, distance);

        // when
        신분당선.deleteSection(강남역);

        // then
        assertThat(신분당선.getStations()).containsExactly(양재역, 정자역);
        assertThat(신분당선.getSections().get(0).getUpStation()).isEqualTo(양재역);
        assertThat(신분당선.getSections().get(0).getDownStation()).isEqualTo(정자역);
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(distance);
    }

    @DisplayName("지하철 노선에서 구간이 1개일 때, 구간을 제거 - 실패")
    @Test
    void deleteSectionFail() {
        // when & then
        assertThatThrownBy(() -> 신분당선.deleteSection(강남역))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> 신분당선.deleteSection(양재역))
                .isInstanceOf(IllegalArgumentException.class);
    }
}