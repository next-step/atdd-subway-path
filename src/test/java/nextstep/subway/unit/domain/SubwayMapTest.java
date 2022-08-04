package nextstep.subway.unit.domain;


import nextstep.subway.applicaion.dto.ShortestPath;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.SubwayMap;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.CommonCode;
import nextstep.subway.exception.code.PathCode;
import nextstep.subway.exception.code.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubwayMapTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private List<Line> lines;
    private SubwayMap subwayMap;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station(11L, "교대역");
        강남역 = new Station(12L, "강남역");
        양재역 = new Station(13L, "양재역");
        남부터미널역 = new Station(14L, "남부터미널역");

        이호선 = new Line("이호선", "red");
        신분당선 = new Line("신분당선", "yellow");
        삼호선 = new Line("삼호선", "green");

        이호선.addSection(교대역, 강남역, 10);
        신분당선.addSection(강남역, 양재역, 10);
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);

        lines = new ArrayList<>();
        lines.addAll(List.of(이호선, 신분당선, 삼호선));

        subwayMap = new SubwayMap(lines);
    }

    @Test
    void 최소경로_조회() {
        // when
        ShortestPath shortestPath = subwayMap.getShortestPath(교대역, 양재역);

        // then
        최소경로_순서_고려하여_검증(shortestPath, "교대역", "남부터미널역", "양재역");
        assertThat(shortestPath.getDistance()).isEqualTo(5);
    }

    @Test
    void 출발역과_도착역이_같은_경우() {
        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            subwayMap.getShortestPath(교대역, 교대역);
        });

        // then
        에러코드_확인(exception, CommonCode.PARAM_INVALID);
    }

    @Test
    void 출발역과_도착역이_연결이_되어_있지_않은_경우() {
        // given
        Station 기흥역 = new Station(99L, "기흥역");
        Station 신갈역 = new Station(999L, "신갈역");
        Line 에버라인 = new Line("에버라인", "green");
        에버라인.addSection(기흥역, 신갈역, 10);
        lines.add(에버라인);
        SubwayMap newSbwayMap = new SubwayMap(lines);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            newSbwayMap.getShortestPath(교대역, 기흥역);
        });

        // then
        에러코드_확인(exception, PathCode.NOT_LINKED);
    }


    private void 에러코드_확인(final CustomException exception, final ResponseCode responseCode) {
        assertThat(exception.getResponseCode()).isEqualTo(responseCode);
    }

    private List<String> getStationNames(final ShortestPath shortestPath) {
        return shortestPath.getStations().stream()
                           .map(Station::getName)
                           .collect(Collectors.toList());
    }

    private void 최소경로_순서_고려하여_검증(final ShortestPath shortestPath, String... path) {
        List<String> stationNames = getStationNames(shortestPath);
        assertThat(stationNames.size()).isEqualTo(path.length);
        assertThat(stationNames).containsExactly(path);
    }
}