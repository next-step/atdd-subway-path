package subway.acceptance.assertions;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import subway.dto.LineResponse;
import subway.dto.StationResponse;
import subway.exception.dto.ErrorResponse;

public class PathAssertions {

    public static void 경로_조회_성공_검증(ExtractableResponse<Response> response, HttpStatus httpStatus,
        Long distance, List<String> stationNames) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());

        LineResponse 조회한_경로_응답 = response.as(LineResponse.class);
        assertThat(조회한_경로_응답.getDistance()).isEqualTo(distance);
        assertThat(조회한_경로_응답.getStations().stream()
            .map(StationResponse::getName)
            .collect(Collectors.toList()))
            .containsAll(stationNames);
    }

    public static void 경로_조회_실패_검증(ExtractableResponse<Response> response, HttpStatus httpStatus, String message) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());

        ErrorResponse 경로_오류_응답 = response.as(ErrorResponse.class);
        assertThat(경로_오류_응답.getMessage()).isEqualTo(message);
    }

}
