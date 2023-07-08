package nextstep.line;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import subway.SchemaInitSql;
import subway.StationInitSql;
import subway.SubwayApplication;
import subway.line.domain.Line;
import subway.line.service.LineService;
import subway.line.view.LineCreateRequest;
import subway.line.view.LineModifyRequest;
import subway.line.view.LineResponse;

@SchemaInitSql
@StationInitSql
@SpringBootTest(classes = SubwayApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Test
    void createLine() {
        LineResponse lineResponse = 노선생성("신분당선", "bg-red-600", 1, 2, 10);

        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    private LineResponse 노선생성(String name, String color, long upStationId, long downStationId, int distance) {
        return lineService.createStation(new LineCreateRequest(name, color, upStationId, downStationId, distance));
    }

    @Test
    void modifyLine() {
        LineResponse createLineResponse = 노선생성("신분당선", "bg-red-600", 1, 2, 10);

        lineService.modifyLine(createLineResponse.getId(), new LineModifyRequest("테스트", "blue"));

        Line line = lineService.getLine(createLineResponse.getId());

        assertThat(line.getName()).isEqualTo("테스트");
        assertThat(line.getColor()).isEqualTo("blue");
    }

}
