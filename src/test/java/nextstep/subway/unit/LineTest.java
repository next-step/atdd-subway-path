package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.LineTestFixtures;
import nextstep.subway.utils.StationTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private Station 강남역;
    private Station 시청역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        final int 구간_거리 = 10;
        강남역 = StationTestFixtures.지하철역_생성("강남역");
        시청역 = StationTestFixtures.지하철역_생성("시청역");
        신분당선 = LineTestFixtures.노선_생성("신분당선", "red", 강남역, 시청역, 구간_거리);
    }

    @DisplayName("지하철 노선의 기존 구간 사이에 새로운 구간을 등록")
    @Test
    void addSection() {

        //given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");
        final int 두번째_구간_거리 = 7;

        //when
        신분당선.addSection(강남역, 을지로역, 두번째_구간_거리);

        //then
        assertThat(신분당선.sectionSize()).isEqualTo(2);
    }

    @DisplayName("지하철 노선의 기존 구간 사이에 새롭게 등록될 구간의 거리가 더 클 경우")
    @Test
    void throwsExceptionIfAddSectionExistSectionGreatorThanSectionDistance() {

        //given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");
        final int 두번째_구간_거리 = 11;

        //then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 을지로역, 두번째_구간_거리)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선의 기존 구간 사이에 새롭게 등록될 구간의 거리가 동일할 경우")
    @Test
    void throwsExceptionIfAddSectionExistSectionEqualsThanSectionDistance() {

        // given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");
        final int 두번째_구간_거리 = 10;

        // then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 을지로역, 두번째_구간_거리)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선의 기존 구간 사이에 새롭게 등록될 구간의 거리가 0 혹은 음수일 경우")
    @Test
    void throwsExceptionIfAddSectionDistanceZeroOrNegative() {

        // given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");
        final int 두번째_구간_거리 = 0;

        // then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 을지로역, 두번째_구간_거리)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선에 새롭게 등록하려는 구간이 존재할 경우")
    @Test
    void throwsExceptionIfEqualsAddSection() {

        // then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 시청역, 10)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선의 Down Section에 구간을 등록")
    @Test
    void addSectionToDownSection() {

        // given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");

        // when
        boolean isRegister = 신분당선.addSection(시청역, 을지로역, 5);

        // then
        assertThat(isRegister).isTrue();
        assertThat(신분당선.sectionSize()).isEqualTo(2);
    }

    @DisplayName("지하철 노선의 Top Section에 구간을 등록")
    @Test
    void addSectionToTopSection() {

        // given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");

        // when
        boolean isRegister = 신분당선.addSection(을지로역, 강남역, 5);

        // then
        assertThat(isRegister).isTrue();
        assertThat(신분당선.sectionSize()).isEqualTo(2);
    }

    @DisplayName("지하철 노선 구간에 일치하지 않은 상행, 하행을 가진 구간을 등록")
    @Test
    void throwsExceptionIfNotHasStations() {

        // given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");
        final Station 구로디지털단지역 = StationTestFixtures.지하철역_생성("구로디지털단지역");

        // then
        assertThatThrownBy(() -> 신분당선.addSection(을지로역, 구로디지털단지역, 5)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("노선의 모든 지하철역 조회")
    @Test
    void getStations() {

        //given
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");
        final Station 구로디지털단지역 = StationTestFixtures.지하철역_생성("구로디지털단지역");

        final int 첫번째구간_거리 = 10;
        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "red", 강남역, 시청역, 첫번째구간_거리);

        final int 두번째구간_거리 = 5;

        신분당선.addSection(시청역, 구로디지털단지역, 두번째구간_거리);

        //when
        List<Station> 지하철역리스트 = 신분당선.stations();

        //then
        assertThat(지하철역리스트).containsExactly(강남역, 시청역, 구로디지털단지역);
    }

    @DisplayName("지하철 노선의 상행 구간 제거")
    @Test
    void removeSectionToTopSection() {

        // given
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");
        final Station 구로디지털단지역 = StationTestFixtures.지하철역_생성("구로디지털단지역");

        final int 첫번째_구간_거리 = 10;
        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "green", 강남역, 시청역, 첫번째_구간_거리);
        final int 두번째_구간_거리 = 5;

        신분당선.addSection(구로디지털단지역, 강남역, 두번째_구간_거리);

        // when
        boolean isRemoved = 신분당선.removeSection(구로디지털단지역);

        // then
        assertThat(isRemoved).isTrue();
    }

    @DisplayName("지하철 노선의 하행 구간 제거")
    @Test
    void removeSectionToDownSection() {

        //given
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");
        final Station 구로디지털단지역 = StationTestFixtures.지하철역_생성("구로디지털단지역");
        final int 첫번째구간_거리 = 10;

        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "red", 강남역, 시청역, 첫번째구간_거리);

        final int 두번째구간_거리 = 5;
        신분당선.addSection(시청역, 구로디지털단지역, 두번째구간_거리);

        //when
        신분당선.removeSection(구로디지털단지역);

        //then
        assertThat(신분당선.getSections().size()).isEqualTo(1);
    }

    @DisplayName("지하철 노선의 중간 구간 제거")
    @Test
    void removeSectionToMiddleSection() {

        // given
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");
        final Station 구로디지털단지역 = StationTestFixtures.지하철역_생성("구로디지털단지역");

        final int 첫번째_구간_거리 = 10;
        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "green", 강남역, 시청역, 첫번째_구간_거리);
        final int 두번째_구간_거리 = 5;

        신분당선.addSection(시청역, 구로디지털단지역, 두번째_구간_거리);

        // when
        boolean isRemoved = 신분당선.removeSection(시청역);

        // then
        assertThat(isRemoved).isTrue();
        assertThat(신분당선.getSections().get(0).getDistance().getValue()).isEqualTo(15);
    }

    @DisplayName("지하철 노선의 구간이 1개일 때 제거 오류")
    @Test
    void throwsExceptionRemoveSectionIfSectionCountOne() {

        // given
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");

        final int 첫번째_구간_거리 = 10;
        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "green", 강남역, 시청역, 첫번째_구간_거리);

        // then
        assertThatThrownBy(() -> 신분당선.removeSection(강남역))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("제거하려는 구간이 지하철 노선에 존재하지 않을경우 오류")
    @Test
    void throwsExceptionRemoveSectionIfHasNotSection() {

        // given
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");
        final Station 구로디지털단지역 = StationTestFixtures.지하철역_생성("구로디지털단지역");

        final int 첫번째_구간_거리 = 10;
        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "green", 강남역, 시청역, 첫번째_구간_거리);

        // then
        assertThatThrownBy(() -> 신분당선.removeSection(구로디지털단지역))
                .isInstanceOf(IllegalStateException.class);
    }

}
