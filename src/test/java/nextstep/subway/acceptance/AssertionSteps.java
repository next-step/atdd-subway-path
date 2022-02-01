package nextstep.subway.acceptance;

import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertionSteps {

    public static void 상태_코드_검증(int responseStatusCode, HttpStatus httpStatus) {
        assertThat(responseStatusCode).isEqualTo(httpStatus.value());
    }

    public static void 지하철_역_목록_순서_일치_검증(List<Long> idsOfStations, Long... expected) {
        assertThat(idsOfStations).containsExactly(expected);
    }
}
