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

    public static void 구간연산_성공_검증(ExtractableResponse<Response> HTTP응답, HttpStatus httpStatus,
        Long distance, List<Long> stationIds) {
        assertThat(HTTP응답.statusCode()).isEqualTo(httpStatus.value());

        LineResponse 구간등록응답 = HTTP응답.as(LineResponse.class);
        assertThat(구간등록응답.getDistance()).isEqualTo(distance);

        List<StationResponse> 구간지하철역리스트 = 구간등록응답.getStations();
        for (int i = 0; i < 구간지하철역리스트.size(); i++) {
            assertThat(구간지하철역리스트.get(i).getId()).isEqualTo(stationIds.get(i));
        }
    }

    public static void 구간연산_실패_검증(ExtractableResponse<Response> HTTP응답, SubwayErrorCode errorCode) {
        assertThat(HTTP응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse 오류응답 = HTTP응답.as(ErrorResponse.class);
        assertThat(오류응답.getMessage()).isEqualTo(errorCode.getMessage());
    }

}
