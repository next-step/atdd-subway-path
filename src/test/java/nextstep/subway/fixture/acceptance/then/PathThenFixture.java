package nextstep.subway.fixture.acceptance.then;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.function.Executable;

public abstract class PathThenFixture {

    public static Executable 최적경로_도출순서_확인(ExtractableResponse<Response> response, long... stations) {

        return () -> assertThat(response.jsonPath().getList("stations.id"))
            .containsExactly(
                Long.valueOf(stations[0]).intValue(),
                Long.valueOf(stations[1]).intValue(),
                Long.valueOf(stations[2]).intValue(),
                Long.valueOf(stations[3]).intValue()
            );
    }

    public static Executable 최적경로_가중치_확인(ExtractableResponse<Response> 경로조회_결과, int distance) {
        return () -> assertThat(경로조회_결과.jsonPath().getInt("distance")).isEqualTo(distance);
    }

}
