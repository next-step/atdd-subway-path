package nextstep.subway.line.domain;

import javassist.NotFoundException;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {

    Line 신분당선;
    Station 강남역;
    Station 양재역;
    Station 판교역;
    Section 강남_양재;
    Section 양재_판교;

    @BeforeEach
    public void setUp() {
        신분당선 = new Line("신분당선", "red");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        판교역 = new Station("판교역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(양재역, "id", 2L);
        ReflectionTestUtils.setField(판교역, "id", 3L);
    }

    @Test
    void getStations() {
        // given
        강남_양재 = new Section(신분당선, 강남역, 양재역, 10);
        양재_판교 = new Section(신분당선, 양재역, 판교역, 10);
        ReflectionTestUtils.setField(신분당선, "sections", Arrays.asList(강남_양재, 양재_판교));

        // when
        List<Station> stations = 신분당선.getStations();

        // then
        지하철_노선에_지하철역_순서_정렬됨(stations, Arrays.asList(강남역, 양재역, 판교역));
    }

    @Test
    void addSection() {
        // when
        신분당선.addSection(강남역, 양재역, 100);

        // then
        지하철_노선에_지하철역_순서_정렬됨(신분당선.getStations(), Arrays.asList(강남역, 양재역));
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        Assertions.assertThatThrownBy(() -> {
            // given
            신분당선.addSection(강남역, 양재역, 100);

            // when
            신분당선.addSection(강남역, 판교역, 100);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        Assertions.assertThatThrownBy(() -> {
            // given
            신분당선.addSection(강남역, 양재역, 100);

            // when
            신분당선.addSection(양재역, 강남역, 100);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void removeSection() {
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
    }

    public static void 지하철_노선에_지하철역_순서_정렬됨(List<Station> stations, List<Station> expectedStations) {
        List<Long> stationIds = stations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

}
