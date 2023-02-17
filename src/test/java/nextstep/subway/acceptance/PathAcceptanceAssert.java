package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.PathSteps.최단_경로_조회;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

public class PathAcceptanceAssert {

    protected static void 최단_경로_조회_검증(
            final ExtractableResponse<Response> response,
            final List<Long> path,
            final Long distance
    ) {
        JsonPath jsonPath = response.jsonPath();
        Assertions.assertAll(
                () -> assertThat(jsonPath.getList("stations.id", Long.class))
                        .containsExactly(path.toArray(Long[]::new)),
                () -> assertThat(jsonPath.getLong("distance")).isEqualTo(distance)
        );
    }

    protected static void 경로_조회시_출발역과_도착역이_일치하면_예외_발생(final Long source, final Long target) {
        // when
        var response = 최단_경로_조회(source, target);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    protected static void 경로_조회시_출발역과_도착역이_연결되어_있지_않으면_예외_발생(final Long source, final Long target) {
        // when
        var response = 최단_경로_조회(source, target);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    protected static void 경로_조회시_존재하지_않는_역이면_예외_발생(final long source, final Long target) {
        // when
        var response = 최단_경로_조회(source, target);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
