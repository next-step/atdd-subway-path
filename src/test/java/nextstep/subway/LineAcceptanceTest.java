package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.CommonAcceptanceTest;
import nextstep.subway.common.LineRestAssuredCRUD;
import nextstep.subway.common.StationRestAssuredCRUD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends CommonAcceptanceTest {
    private static Long 강남역Id;
    private static Long 양재역Id;

    @BeforeEach
    void createDefaultStations() {
        강남역Id = extractResponseId(StationRestAssuredCRUD.createStation("강남역"));
        양재역Id = extractResponseId(StationRestAssuredCRUD.createStation("양재역"));
    }

    Long extractResponseId(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getLong("id");
    }

    List<String> extractResponseNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성하고 노선 목록을 조회한다")
    @Test
    void createLine() {
        //when
        String 신분당선 = "신분당선";
        ExtractableResponse<Response> createResponse = LineRestAssuredCRUD.createLine(신분당선, "bg-red-600");
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = extractResponseNames(LineRestAssuredCRUD.showLineList());
        assertThat(lineNames).contains(신분당선);

    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("2개의 지하철 노선을 생성하고 노선 목록을 조회한다")
    @Test
    void createAndShowTwoLineList() {
        String 서현역 = "서현역";
        String 신분당선 = "신분당선";
        String 수인분당선 = "수인분당선";

        //given
        Long 서현역Id = extractResponseId(StationRestAssuredCRUD.createStation(서현역));

        LineRestAssuredCRUD.createLine(신분당선, "bg-red-600");
        LineRestAssuredCRUD.createLine(수인분당선, "bg-yellow-600");

        //when
        List<String> names = extractResponseNames(LineRestAssuredCRUD.showLineList());

        // then
        assertThat(names).containsAll(List.of(신분당선, 수인분당선));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 생성하고 노선을 조회한다")
    @Test
    void createAndShowLine() {
        //given
        String 신분당선 = "신분당선";
        ExtractableResponse<Response> createResponse = LineRestAssuredCRUD.createLine(신분당선, "bg-red-600");

        Long createdId = extractResponseId(createResponse);

        //when
        String name = LineRestAssuredCRUD.showLine(createdId).jsonPath().getString("name");

        // then
        assertThat(name).isEqualTo(신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다")
    @Test
    void createAndModifyLine() {
        //given
        String createStationName = "신분당선";
        ExtractableResponse<Response> createResponse = LineRestAssuredCRUD.createLine(createStationName, "bg-red-600");

        Long createdId = extractResponseId(createResponse);

        //when
        String modifyStationName = "수인분당선";
        ExtractableResponse<Response> modifyResponse = LineRestAssuredCRUD.modifyLine(createdId, modifyStationName, "bg-yellow-600");

        // then
        assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        String name = LineRestAssuredCRUD.showLine(createdId).jsonPath().getString("name");
        assertThat(name).isEqualTo(modifyStationName);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 생성하고 삭제한다")
    @Test
    void createAndDeleteLine() {
        //given
        String 신분당선 = "신분당선";
        ExtractableResponse<Response> createResponse = LineRestAssuredCRUD.createLine("신분당선", "bg-red-600");

        Long createdId = extractResponseId(createResponse);

        //when
        ExtractableResponse<Response> deleteResponse = LineRestAssuredCRUD.deleteLine(createdId);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<String> names = extractResponseNames(LineRestAssuredCRUD.showLineList());
        assertThat(names).doesNotContain(신분당선);
    }
}
