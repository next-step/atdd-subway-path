package subway.acceptance.assertions;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.dto.LineResponse;
import subway.dto.StationResponse;
import subway.exception.dto.ErrorResponse;
import subway.exception.error.SubwayErrorCode;

public class LineAssertions {

    public static void 구간추가후_검증(Line 노선) {
        assertThat(노선.getDistance()).isEqualTo(40L);

        Sections 구간리스트 = 노선.getSections();
        Section 마지막구간 = 노선.getSections().getLastSection();
        assertThat(구간리스트.getSize()).isEqualTo(2);
        assertThat(구간리스트.getStations().size()).isEqualTo(3);
        assertThat(마지막구간.getUpStation().getName()).isEqualTo("논현역");
        assertThat(마지막구간.getDownStation().getName()).isEqualTo("광교역");

        Assertions.assertThat(노선.getSections().getLastStation().getName()).isEqualTo("광교역");
        Assertions.assertThat(노선.getDistance()).isEqualTo(40L);
    }

    public static void 노선_응답_성공_검증(ExtractableResponse<Response> response, HttpStatus httpStatus,
        Long distance, List<Long> stationIds) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());

        LineResponse 구간_응답 = response.as(LineResponse.class);
        assertThat(구간_응답.getDistance()).isEqualTo(distance);

        List<StationResponse> 지하철역_목록 = 구간_응답.getStations();
        for (int i = 0; i < 지하철역_목록.size(); i++) {
            assertThat(지하철역_목록.get(i).getId()).isEqualTo(stationIds.get(i));
        }
    }

    public static void 노선_응답_실패_검증(ExtractableResponse<Response> response, SubwayErrorCode errorCode) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse 오류_응답 = response.as(ErrorResponse.class);
        assertThat(오류_응답.getMessage()).isEqualTo(errorCode.getMessage());
    }

    public static void 노선_목록_검증(ExtractableResponse<Response> response, HttpStatus httpStatus,
        List<String> lineNames) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());

        List<String> 노선_이름_목록 = response.jsonPath().getList("name", String.class);
        assertThat(노선_이름_목록).containsExactlyInAnyOrderElementsOf(lineNames);
    }

}
