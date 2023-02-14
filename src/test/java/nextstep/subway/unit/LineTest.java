package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private Station 강남역;
    private Station 판교역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        판교역 = new Station("판교역");
        신분당선 = new Line("신분당선", "red");
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // when
        신분당선.addSection(강남역, 판교역, 10);

        // then
        assertThat(신분당선.getSections()).containsExactly(new Section(신분당선, 강남역, 판교역, 10));
    }

    @DisplayName("지하철 노선의 역 사이에 새로운 역을 등록")
    @Test
    void addLineSectionWithinSection() {
        // given
        신분당선.addSection(강남역, 판교역, 10);
        Station 양재역 = new Station("양재역");
        Station 청계산입구역 = new Station("청계산입구역");

        // when
        신분당선.addSection(강남역, 양재역, 4);
        신분당선.addSection(청계산입구역, 판교역, 3);

        // then
        assertThat(신분당선.getSections()).containsExactlyInAnyOrder(
                new Section(신분당선, 강남역, 양재역, 4),
                new Section(신분당선, 양재역, 청계산입구역, 3),
                new Section(신분당선, 청계산입구역, 판교역, 3)
        );
    }

    @DisplayName("지하철 노선의 새로운 상행 종점역 구간을 등록")
    @Test
    void addLineSectionBeforeFirstStation() {
        // given
        신분당선.addSection(강남역, 판교역, 10);
        Station 신논현역 = new Station("신논현역");

        // when
        신분당선.addSection(신논현역, 강남역, 2);

        // then
        assertThat(신분당선.getSections()).containsExactlyInAnyOrder(
                new Section(신분당선, 강남역, 판교역, 10),
                new Section(신분당선, 신논현역, 강남역, 2)
        );
    }

    @DisplayName("지하철 노선의 새로운 하행 종점역 구간을 등록")
    @Test
    void addLineSectionAfterFinalStation() {
        // given
        신분당선.addSection(강남역, 판교역, 10);
        Station 정자역 = new Station("정자역");

        // when
        신분당선.addSection(판교역, 정자역, 2);

        // then
        assertThat(신분당선.getSections()).containsExactlyInAnyOrder(
                new Section(신분당선, 강남역, 판교역, 10),
                new Section(신분당선, 판교역, 정자역, 2)
        );
    }

    @DisplayName("지하철 노선에 등록된 역들을 조회한다.")
    @Test
    void getStations() {
        // given
        신분당선.addSection(강남역, 판교역, 3);

        // when & then
        assertThat(신분당선.getStations()).containsExactly(강남역, 판교역);
    }

    @DisplayName("지하철 노선에 등록된 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        신분당선.addSection(강남역, 판교역, 3);

        // when
        신분당선.removeSection(판교역);

        // then
        assertThat(신분당선.getStations()).isEmpty();
    }
}
