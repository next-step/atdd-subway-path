package nextstep.line;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.SchemaInitSql;
import nextstep.subway.SubwayApplication;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.service.LineManageService;
import nextstep.subway.line.service.LineReadService;
import nextstep.subway.line.view.LineCreateRequest;
import nextstep.subway.line.view.LineModifyRequest;
import nextstep.subway.line.view.LineResponse;
import nextstep.subway.station.service.StationService;
import nextstep.subway.station.view.StationRequest;
import nextstep.subway.station.view.StationResponse;


@Rollback
@Transactional
@SchemaInitSql
@SpringBootTest(classes = SubwayApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class LineManageServiceTest {

    @Autowired
    private LineManageService lineManageService;

    @Autowired
    private LineReadService lineReadService;

    @Autowired
    private StationService stationService;
    private StationResponse stationA;
    private StationResponse stationB;

    @BeforeEach
    public void setup() {
        stationA = createStation("stationA");
        stationB = createStation("stationB");
    }

    @Test
    void createLine() {

        LineResponse lineResponse = 노선생성("신분당선", "bg-red-600", stationA.getId(), stationB.getId(), 10);

        Assertions.assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    private StationResponse createStation(String stationName) {
        return stationService.saveStation(new StationRequest(stationName));
    }

    private LineResponse 노선생성(String name, String color, long upStationId, long downStationId, int distance) {
        return lineManageService.createLine(new LineCreateRequest(name, color, upStationId, downStationId, distance));
    }

    @Test
    void modifyLine() {
        stationA = createStation("stationA");
        stationB = createStation("stationB");

        LineResponse createLineResponse = 노선생성("신분당선", "bg-red-600", stationA.getId(), stationB.getId(), 10);

        lineManageService.modifyLine(createLineResponse.getId(), new LineModifyRequest("테스트", "blue"));

        Line line = lineReadService.getLine(createLineResponse.getId());

        Assertions.assertThat(line.getName()).isEqualTo("테스트");
        Assertions.assertThat(line.getColor()).isEqualTo("blue");
    }

}
