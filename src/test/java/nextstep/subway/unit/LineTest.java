package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @DisplayName("노선에 구간 등록하기")
    @Test
    void addSection() {

        //given
        final String 강남역_이름 = "강남역_이름";
        final Station 강남역 = new Station(강남역_이름);

        final String 시청역_이름 = "시청역_이름";
        final Station 시청역 = new Station(시청역_이름);

        final String 신분당선_이름 = "신분당선_이름";
        final String red = "red";
        final Line 신분당선 = Line.makeLine(신분당선_이름, red);

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
        final String 강남역_이름 = "강남역_이름";
        final Station 강남역 = new Station(강남역_이름);

        final String 시청역_이름 = "시청역_이름";
        final Station 시청역 = new Station(시청역_이름);

        final String 구로디지털단지역_이름 = "구로디지털단지역_이름";
        final Station 구로디지털단지역 = new Station(구로디지털단지역_이름);

        final String 신분당선_이름 = "신분당선_이름";
        final String red = "red";
        final Line 신분당선 = Line.makeLine(신분당선_이름, red);

        final int 첫번째구간_거리 = 10;
        final int 두번째구간_거리 = 5;

        신분당선.addSection(강남역, 시청역, 첫번째구간_거리);
        신분당선.addSection(시청역, 구로디지털단지역, 두번째구간_거리);

        //when
        List<Station> 지하철역리스트 = 신분당선.getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        지하철역리스트.add(0, 신분당선.getSections().get(0).getUpStation());

        //then
        assertThat(지하철역리스트).containsExactly(강남역, 시청역, 구로디지털단지역);
    }

    @DisplayName("노선의 구간 제거하기")
    @Test
    void removeSection() {

        //given
        final String 강남역_이름 = "강남역_이름";
        final Station 강남역 = new Station(강남역_이름);

        final String 시청역_이름 = "시청역_이름";
        final Station 시청역 = new Station(시청역_이름);

        final String 구로디지털단지역_이름 = "구로디지털단지역_이름";
        final Station 구로디지털단지역 = new Station(구로디지털단지역_이름);

        final String 신분당선_이름 = "신분당선_이름";
        final String red = "red";
        final Line 신분당선 = Line.makeLine(신분당선_이름, red);

        final int 첫번째구간_거리 = 10;
        final int 두번째구간_거리 = 5;

        신분당선.addSection(강남역, 시청역, 첫번째구간_거리);
        신분당선.addSection(시청역, 구로디지털단지역, 두번째구간_거리);

        //when
        신분당선.getSections().remove(신분당선.getSections().size() - 1);

        //then
        assertThat(신분당선.getSections().size()).isEqualTo(1);
    }
}
