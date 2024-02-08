package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.common.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.acceptance.common.util.AcceptanceTest;
import nextstep.subway.interfaces.line.dto.LineResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@AcceptanceTest
public class LineAcceptanceTest {

    Line.RequestBody 신분당선;
    Line.RequestBody 분당선;

    @BeforeEach
    void setUpFixture() {
        신분당선 = Line.REQUEST_BODY();
        분당선 = Line.REQUEST_BODY();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        //when
        ExtractableResponse<Response> response = Line.Api.createLineBy(신분당선);
        LineResponse actual = response.as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> getResponse = Line.Api.retrieveLineBy(actual.getId());
        LineResponse line = getResponse.as(LineResponse.class);
        assertThat(actual).usingRecursiveComparison().ignoringFields("distance").isEqualTo(line);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        LineResponse expected_신분당선 = Line.Api.createLineBy(신분당선).as(LineResponse.class);
        LineResponse expected_분당선 = Line.Api.createLineBy(분당선).as(LineResponse.class);

        //when
        ExtractableResponse<Response> response = Line.Api.listLine();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        //then
        LineResponse[] lines = response.as(LineResponse[].class);
        assertThat(lines[0]).usingRecursiveComparison().ignoringFields("id", "distance").isEqualTo(expected_신분당선);
        assertThat(lines[1]).usingRecursiveComparison().ignoringFields("id", "distance").isEqualTo(expected_분당선);

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void retrieveLine() {
        // given
        LineResponse expected = Line.Api.createLineBy(신분당선).as(LineResponse.class);

        //when
        ExtractableResponse<Response> response = Line.Api.retrieveLineBy(expected.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        //then
        LineResponse line = response.as(LineResponse.class);
        assertThat(line).usingRecursiveComparison().ignoringFields("distance").isEqualTo(expected);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long lineId = Line.Api.createLineBy(분당선).jsonPath().getLong("id");
        String newName = "다른분당선";
        String newColor = "bg-red-600";

        //when
        ExtractableResponse<Response> response = Line.Api.updateLineBy(lineId, newName, newColor);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //then
        ExtractableResponse<Response> getResponse = Line.Api.retrieveLineBy(lineId);
        LineResponse line = getResponse.as(LineResponse.class);
        assertThat(line.getId()).isEqualTo(lineId);
        assertThat(line.getName()).isEqualTo(newName);
        assertThat(line.getColor()).isEqualTo(newColor);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        Long lineId = Line.Api.createLineBy(분당선).jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = Line.Api.deleteLineBy(lineId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //then
        ExtractableResponse<Response> getResponse = Line.Api.retrieveLineBy(lineId);
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


}
