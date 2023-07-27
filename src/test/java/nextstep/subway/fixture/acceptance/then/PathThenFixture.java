package nextstep.subway.fixture.acceptance.then;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.function.Executable;

public abstract class PathThenFixture {

    public static Executable 최적경로_도출순서_확인(ExtractableResponse<Response> response,
        long... stations) {

        return () -> assertThat(response.jsonPath().getList("stations.id"))
            .containsExactly(
                Long.valueOf(stations[0]).intValue(),
                Long.valueOf(stations[1]).intValue(),
                Long.valueOf(stations[2]).intValue(),
                Long.valueOf(stations[3]).intValue()
            );
    }

    public static Executable 최적경로_이동거리_확인(ExtractableResponse<Response> 경로조회_결과, int distance) {
        return () -> assertThat(경로조회_결과.jsonPath().getInt("distance")).isEqualTo(distance);
    }

    public static AbstractStringAssert<?> 출발지와_목적지가_같은역일때_에러메세지_검사(
        ExtractableResponse<Response> 경로조회_결과) {
        return assertThat(경로조회_결과.jsonPath().getString("message"))
            .isEqualTo("출발지와 목적지가 같은 역일 수 없습니다.");
    }

    public static AbstractStringAssert<?> 출발지또는_목적지가_존재하지않을때_에러메세지_검사(
        ExtractableResponse<Response> 경로조회_결과, long stationId) {
        return assertThat(경로조회_결과.jsonPath().getString("message"))
            .isEqualTo(stationId + " 번호에 해당하는 지하철역이 존재하지 않습니다.");
    }

    public static AbstractStringAssert<?> 출발지또는_목적지가_이어져있지않을때_에러메세지_검사(
        ExtractableResponse<Response> 경로조회_결과) {
        return assertThat(경로조회_결과.jsonPath().getString("message"))
            .isEqualTo("출발지와 목적지의 각 구간이 이어져있지 않습니다.");
    }

}
