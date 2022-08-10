package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAssertion {

    public static void 구간의_거리를_확인한다(Section section, Integer distance) {
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    public static  void 지하철_역이_나열된다(Line line, Station... stations) {
        assertThat(line.getStations()).containsExactly(stations);
    }
    public static  void 지하철_역이_나열된다(ExtractableResponse<Response> response, Long... ids) {
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(ids);
    }
}
