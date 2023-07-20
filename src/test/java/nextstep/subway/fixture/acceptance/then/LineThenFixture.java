package nextstep.subway.fixture.acceptance.then;

import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_목록_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.AbstractLongAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.ListAssert;

public abstract class LineThenFixture {

    public static AbstractLongAssert<?> 노선_응답값_id_검사(
        ExtractableResponse<Response> response, Long id) {
        return assertThat(response.jsonPath().getLong("id")).isEqualTo(id);
    }

    public static AbstractStringAssert<?> 노선_응답값_노선이름_검사(ExtractableResponse<Response> response, String name) {
        return assertThat(response.jsonPath().getString("name")).isEqualTo(name);
    }

    public static AbstractStringAssert<?> 노선_응답값_노선색_검사(ExtractableResponse<Response> response, String color) {
        return assertThat(response.jsonPath().getString("color")).isEqualTo(color);
    }

    public static ListAssert<Object> 노선_응답값_포함된_지하철역_크기_검사(ExtractableResponse<Response> response, int size) {
        return assertThat(response.jsonPath().getList("stations")).hasSize(size);
    }

    public static ListAssert<Object> 노선_응답값_포함된지하철역_id_검사(ExtractableResponse<Response> response, long upStationId, long downStationId) {
        return assertThat(response.jsonPath().getList("stations.id")).contains(
            Long.valueOf(upStationId).intValue(),
            Long.valueOf(downStationId).intValue()
        );
    }

    public static ListAssert<Object> 노선목록_조회시_생성한노선_id_포함_검사(ExtractableResponse<Response> 노선등록응답값) {
        return assertThat(지하철역_노선_목록_조회_요청().jsonPath().getList("id"))
            .contains(Long.valueOf(노선등록응답값.jsonPath().getLong("id")).intValue());
    }

    public static ListAssert<Object> 노선목록_노선이름_포함_검사(ExtractableResponse<Response> response, List<String> lineNames) {
        return assertThat(response.jsonPath().getList("name")).containsAll(lineNames);
    }

    public static ListAssert<Object> 노선목록_크기_검사(ExtractableResponse<Response> response, int size) {
        return assertThat(response.jsonPath().getList("")).hasSize(size);
    }

    public static AbstractStringAssert<?> 없는노선조회시_에러표출_검사(ExtractableResponse<Response> response) {
        return assertThat(response.jsonPath().getString("message")).isEqualTo(
            "해당하는 id 에 맞는 지하철 노선이 존재하지 않습니다.");
    }
}
