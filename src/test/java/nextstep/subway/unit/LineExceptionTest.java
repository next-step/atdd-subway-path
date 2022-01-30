package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.InvalidSectionDistanceException;
import nextstep.subway.exception.StationsExistException;
import nextstep.subway.exception.StationsNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineExceptionTest {

    final int 강남_판교_거리 = 7;
    Station 강남역;
    Station 판교역;
    Station 양재역;
    Line 신분당선;

    @BeforeEach
    void setLine() {
        강남역 = new Station("강남역");
        판교역 = new Station("판교역");
        양재역 = new Station("양재역");
        신분당선 = new Line("신분당선", "yellow", 강남역, 판교역, 강남_판교_거리);
    }

    @DisplayName("역 사이에 새로운 역 등록 기존 역의 사이길이 보다 작아야 한다")
    @Test
    void addStationBetweenStationsDistanceExceptionBasedOnUpStation() {
        // given
        Section 강남_양재 = new Section(신분당선, 강남역, 양재역, 강남_판교_거리);

        // when, then
        assertThatThrownBy(() -> 신분당선.addSection(강남_양재))
                .isInstanceOf(InvalidSectionDistanceException.class);
    }

    @DisplayName("역 사이에 새로운 역 등록 기존 역의 사이길이 보다 작아야 한다")
    @Test
    void addStationBetweenStationsDistanceExceptionBasedOnDownStation() {
        // given
        Section 양재_판교 = new Section(신분당선, 양재역, 판교역, 강남_판교_거리);

        // when, then
        assertThatThrownBy(() -> 신분당선.addSection(양재_판교))
                .isInstanceOf(InvalidSectionDistanceException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addStationExceptionWhenStationsAreAlreadyAdded() {
        // given
        Section 강남_판교 = new Section(신분당선, 강남역, 판교역, 강남_판교_거리 - 1);

        // when, then
        assertThatThrownBy(() -> 신분당선.addSection(강남_판교))
                .isInstanceOf(StationsExistException.class);
    }

    @DisplayName("상행역과 하행역 최소 하나는 노선에 존재하지 않으면 추가할 수 없음")
    @Test
    void addStationExceptionWhenAtLeastOneStationExists() {
        // given
        Station 김포공항역 = new Station("김포공항역");
        Station 가양역 = new Station("가양역");
        Section 김포공항_가양 = new Section(신분당선, 김포공항역, 가양역, 강남_판교_거리 - 1);

        // when, then
        assertThatThrownBy(() -> 신분당선.addSection(김포공항_가양))
                .isInstanceOf(StationsNotExistsException.class);
    }

}
