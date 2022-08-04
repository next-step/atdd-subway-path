package nextstep.subway.unit.application;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.CommonCode;
import nextstep.subway.exception.code.PathCode;
import nextstep.subway.exception.code.ResponseCode;
import nextstep.subway.exception.code.StationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class PathServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private PathService pathService;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        교대역 = createStation("교대역");
        강남역 = createStation("강남역");
        양재역 = createStation("양재역");
        남부터미널역 = createStation("남부터미널역");

        이호선 = createLine("이호선", "red");
        신분당선 = createLine("신분당선", "yellow");
        삼호선 = createLine("삼호선", "green");

        이호선.addSection(교대역, 강남역, 10);
        신분당선.addSection(강남역, 양재역, 10);
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @Test
    void 최소경로_조회() {
        // when
        PathResponse response = pathService.getShortestPath(교대역.getId(), 양재역.getId());

        // then
        최소경로_순서_고려하여_검증(response, "교대역", "남부터미널역", "양재역");
        assertThat(response.getDistance()).isEqualTo(5);
    }

    @Test
    void 존재하지_않은_출발역이나_도착역을_조회할경우() {
        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            pathService.getShortestPath(교대역.getId(), 999L);
        });

        // then
        에러코드_확인(exception, StationCode.STATION_NOT_FOUND);
    }

    @Test
    void 출발역과_도착역이_연결이_되어_있지_않은_경우() {
        // given
        Station 기흥역 = createStation( "기흥역");
        Station 신갈역 = createStation("신갈역");
        Line 에버라인 = createLine("에버라인", "green");
        에버라인.addSection(기흥역, 신갈역, 10);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            pathService.getShortestPath(교대역.getId(), 기흥역.getId());
        });

        // then
        에러코드_확인(exception, PathCode.NOT_LINKED);
    }

    private void 에러코드_확인(final CustomException exception, final ResponseCode responseCode) {
        assertThat(exception.getResponseCode()).isEqualTo(responseCode);
    }

    private Station createStation(String name){
        return stationRepository.save(new Station(name));
    }

    private Line createLine(String name, String color){
        return lineRepository.save(new Line(name, color));
    }

    private List<String> getStationNames(final PathResponse response) {
        return response.getStations().stream()
                       .map(StationResponse::getName)
                       .collect(Collectors.toList());
    }

    private void 최소경로_순서_고려하여_검증(final PathResponse response, String... path) {
        assertThat(response.getStations().size()).isEqualTo(path.length);
        assertThat(getStationNames(response)).containsExactly(path);
    }
}
