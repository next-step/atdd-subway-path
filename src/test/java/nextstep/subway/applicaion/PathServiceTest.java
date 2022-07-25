package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 관련 without Mock")
@Transactional
@SpringBootTest
class PathServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Autowired
    private PathService pathService;

    @DisplayName("두 지하철역 경로 찾기")
    @Test
    void findPath() {
        // given
        StationResponse 서울역 = stationService.saveStation(new StationRequest("서울역"));
        StationResponse 시청역 = stationService.saveStation(new StationRequest("시청역"));
        StationResponse 종각역 = stationService.saveStation(new StationRequest("종각역"));
        LineResponse 일호선 = lineService.saveLine(new LineRequest("일호선", "blue", 서울역.getId(), 시청역.getId(), 5));
        lineService.addSection(일호선.getId(), new SectionRequest(시청역.getId(), 종각역.getId(), 8));

        // when
        PathResponse response = pathService.findPath(서울역.getId(), 종각역.getId());

        // then
        assertThat(response.getStations()).hasSize(3);
        assertThat(response.getDistance()).isEqualTo(13);
    }

}