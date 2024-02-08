package nextstep.study;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PathAcceptanceTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.execute();

        StationFactory.createStation("교대역");    // 1L
        StationFactory.createStation("강남역");    // 2L
        StationFactory.createStation("양재역");    // 3L
        StationFactory.createStation("남부터미널역");// 4L

        LineFactory.createLineWithDistance("2호선", 1L, 2L, 10);  // 1L
        LineFactory.createLineWithDistance("신분당선", 2L, 3L, 10);// 2L
        LineFactory.createLineWithDistance("3호선", 1L, 4L, 2);  // 3L
        SectionFactory.createSection(3L, 4L, 3L, 3);
    }

    /**
     * Given: 출발역은 교대역, 도착역은 양재역으로 설정하고
     * When : 최소 경로를 조회하면
     * Then : 경로는 (교대역, 남부터미널역, 양재역), 총 거리는 5가 되어야 한다.
     */
    @Test
    void findShortestPathSuccess() {
        // given
        Long gyodaeId = 1L;
        Long yangjaeId = 3L;

        // when
        ExtractableResponse<Response> response = PathFactory.findShortestPath(gyodaeId, yangjaeId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stations = response.jsonPath().getList("stations.name", String.class);
        assertThat(stations).containsExactly("교대역", "남부터미널역", "양재역");
        int distance = response.jsonPath().getInt("distance");
        assertThat(distance).isEqualTo(5);
    }
}
