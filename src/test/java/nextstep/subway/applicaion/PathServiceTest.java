package nextstep.subway.applicaion;

import static nextstep.subway.unit.LineUnitSteps.노선_구간_추가;
import static nextstep.subway.unit.StationUnitSteps.역_추가;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.LineSaveRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PathServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private PathService pathService;

    @Test
    @DisplayName("최단거리의 경로와 거리를 구한다.")
    void shortestPath() {
        final long 교대역 = 역_생성("교대역");
        final long 강남역 = 역_생성("강남역");
        final long 남부터미널역 = 역_생성("남부터미널역");
        final long 양재역 = 역_생성("양재역");

        노선_생성("이호선", "green", 교대역, 강남역, 10);
        노선_생성("삼호선", "orange", 교대역, 남부터미널역, 3);
        노선_생성("신분당선", "red", 강남역, 양재역, 5);

        pathService.getShortestPath(강남역, 남부터미널역);
    }

    private LineResponse 노선_생성(final String name, final String color, final long upStationId, final long downStationId
        , final int distance) {
        LineSaveRequest request = new LineSaveRequest(name, color, upStationId, downStationId, distance);
        return lineService.saveLine(request);
    }

    private long 역_생성(final String stationName) {
        final Station station = stationRepository.save(new Station(stationName));
        return station.getId();
    }
}