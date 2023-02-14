package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.PathSteps.최단_경로_조회;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.path.json.JsonPath;
import java.util.List;
import org.junit.jupiter.api.Assertions;

public class PathAcceptanceAssert {

    protected static void 최단_경로_조회_검증(
            final Long source,
            final Long target,
            final List<Long> path,
            final Long distance
    ) {
        var response = 최단_경로_조회(source, target);

        JsonPath jsonPath = response.jsonPath();
        Assertions.assertAll(
                () -> assertThat(jsonPath.getList("stations.id", Long.class))
                        .containsExactly(path.toArray(Long[]::new)),
                () -> assertThat(jsonPath.getLong("distance")).isEqualTo(distance)
        );
    }

}
