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

    @DisplayName("지하철 노선의 구간 사이에 구간 등록하기")
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

    @DisplayName("지하철 노선 사이에 구간 등록 시 기존 구간의 거리보다 클 경우")
    @Test
    void throwsExceptionIfAddSectionExistSectionGreatorThanSectionDistance() {

        //given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");
        final int 두번째_구간_거리 = 11;

        //then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 을지로역, 두번째_구간_거리)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선 사이에 구간 등록 시 기존 구간의 거리와 동일할 경우")
    @Test
    void throwsExceptionIfAddSectionExistSectionEqualsThanSectionDistance() {

        // given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");
        final int 두번째_구간_거리 = 10;

        // then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 을지로역, 두번째_구간_거리)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선 사이에 구간 등록 시 구간의 거리가 0 혹은 음수일경우")
    @Test
    void throwsExceptionIfAddSectionDistanceZeroOrNegative() {

        // given
        final Station 을지로역 = StationTestFixtures.지하철역_생성("을지로역");
        final int 두번째_구간_거리 = 0;

        // then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 을지로역, 두번째_구간_거리)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선의 구간과 등록할 구간이 같을경우")
    @Test
    void throwsExceptionIfEqualsAddSection() {

        // then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 시청역, 10)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선의 Top Section에 구간을 등록할 경우")
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

    @DisplayName("지하철 노선의 Down Section에 구간을 등록할 경우")
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

    @DisplayName("지하철 노선의 구간 중 일치하지 않은 상행, 하행을 가진 구간을 등록할 경우")
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

    @DisplayName("노선의 구간 제거하기")
    @Test
    void removeSection() {

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


}
