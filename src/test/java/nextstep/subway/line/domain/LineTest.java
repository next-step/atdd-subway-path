package nextstep.subway.line.domain;

import nextstep.subway.line.exception.DistanceMaximumException;
import nextstep.subway.line.exception.NoOtherStationException;
import nextstep.subway.line.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineTest {

    //setup
    private Station 석촌역 = new Station("석촌역");
    private Station 송파역 = new Station("송파역");
    private Station 남한산성입구역 = new Station("남한산성입구역");
    private Station 산성역 = new Station("산성역");
    private Station 단대오거리역 = new Station("단대오거리역");
    private Line pinkLine;

    @BeforeEach
    void setup() {
        석촌역 = new Station("석촌역");
        ReflectionTestUtils.setField(석촌역, "id", 1L);
        송파역 = new Station("송파역");
        ReflectionTestUtils.setField(송파역, "id", 2L);
        남한산성입구역 = new Station("남한산성입구역");
        ReflectionTestUtils.setField(남한산성입구역, "id", 3L);
        산성역 = new Station("산성역");
        ReflectionTestUtils.setField(산성역, "id", 4L);
        단대오거리역 = new Station("산성역");
        ReflectionTestUtils.setField(단대오거리역, "id", 5L);
        pinkLine = Line.of("8호선", "pink-001", 석촌역, 송파역, 10);
        ReflectionTestUtils.setField(pinkLine, "id", 1L);
    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void getStations() {
        assertThat(pinkLine.getAllStations().get(0).getName()).isEqualTo(석촌역.getName());
        assertThat(pinkLine.getAllStations().get(1).getName()).isEqualTo(송파역.getName());
    }

    @DisplayName("노선을 등록한다.")
    @Test
    void addSection() {
        pinkLine.addSection(석촌역, 남한산성입구역, 4);

        assertEquals(pinkLine.getAllStations().size(), 3);
    }

    @DisplayName("상행역과 하행역이 존재하지 않는 경우 예외처리")
    @Test
    void notFoundStationException() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> pinkLine.addSection(산성역, 단대오거리역, 2));
    }

    @DisplayName("상행역과 하행역이 존재하지 않는 경우 예외처리")
    @Test
    void duplicateException() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> pinkLine.addSection(산성역, 단대오거리역, 2));
    }

    @DisplayName("새로 등록하려는 구간 거리가 기존 구간 보다 크거나 같은 경우")
    @Test
    void distanceMaximumException() {
        assertThatExceptionOfType(DistanceMaximumException.class)
                .isThrownBy(() -> pinkLine.addSection(단대오거리역, 송파역, 10));
    }

    @DisplayName("등록한 중간 구간의 역을 제거한다.")
    @Test
    void removeSection() {
        pinkLine.addSection(석촌역, 남한산성입구역, 4);

        pinkLine.deleteSection(남한산성입구역.getId());

        assertEquals(pinkLine.getAllStations().size(), 2);
    }

    @DisplayName("등록한 구간의 상행 종점역을 제거한다.")
    @Test
    void removeFirstSection() {
        pinkLine.addSection(석촌역, 남한산성입구역, 4);

        pinkLine.deleteSection(석촌역.getId());

        assertEquals(pinkLine.getAllStations().size(), 2);
    }

    @DisplayName("등록한 구간의 종점역을 제거한다.")
    @Test
    void removeFinishSection() {
        pinkLine.addSection(석촌역, 남한산성입구역, 4);

        pinkLine.deleteSection(송파역.getId());

        assertEquals(pinkLine.getAllStations().size(), 2);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        assertThatExceptionOfType(NoOtherStationException.class)
                .isThrownBy(() -> pinkLine.deleteSection(석촌역.getId()));
    }
}
