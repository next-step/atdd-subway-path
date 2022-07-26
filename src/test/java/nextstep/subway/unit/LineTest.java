package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.LineTestFixtures;
import nextstep.subway.utils.StationTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @DisplayName("노선에 구간 등록하기")
    @Test
    void addSection() {

        //given
        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "red");
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");
        final int distance = 10;

        //when
        신분당선.addSection(강남역, 시청역, distance);

        //then
        assertThat(신분당선.getSections().size()).isEqualTo(1);
    }

    @DisplayName("노선의 모든 지하철역 조회")
    @Test
    void getStations() {

        //given
        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "red");
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");
        final Station 구로디지털단지역 = StationTestFixtures.지하철역_생성("구로디지털단지역");

        final int 첫번째구간_거리 = 10;
        final int 두번째구간_거리 = 5;

        신분당선.addSection(강남역, 시청역, 첫번째구간_거리);
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

        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "red");

        final int 첫번째구간_거리 = 10;
        final int 두번째구간_거리 = 5;

        신분당선.addSection(강남역, 시청역, 첫번째구간_거리);
        신분당선.addSection(시청역, 구로디지털단지역, 두번째구간_거리);

        //when
        신분당선.removeSection(구로디지털단지역);

        //then
        assertThat(신분당선.getSections().size()).isEqualTo(1);
    }


}
