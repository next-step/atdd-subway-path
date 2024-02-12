package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    @DisplayName("최단 경로 조회 성공")
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

    /**
     * Given: 출발역과 도착역 모두 교대역으로 설정하고
     * When : 최소 경로를 조회하면
     * Then : 실패한다.
     */
    @DisplayName("최단 경로 조회 실패")
    @Test
    void findShortestPathFail() {
        // given
        Long gyodaeId = 1L;

        // when
        ExtractableResponse<Response> response = PathFactory.findShortestPath(gyodaeId, gyodaeId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).contains("출발역과 도착역이 동일합니다.");
    }

    /**
     * Given: 3호선 (교대역 - 2 - 남부터미널역 - 3 - 양재역)
     * When : (남부터미널역 - 1 - 가락시장역) 구간 추가
     *        (남부터미널역) 구간 삭제
     *        를 동시에 하면
     * Then : 교대역부터 양재역까지의 경로를 조회했을 때
     *        (교대역 - 3 - 가락시장역 - 2 - 양재역)이 되어야 하고 거리는 5가 되어야 한다.
     */
    @DisplayName("동시성 테스트")
    @Test
    void concurrentSectionAddAndRemove() throws InterruptedException {
        // given
        Long line3Id = 3L;
        Long gyodaeId = 1L;
        Long yangjaeId = 3L;
        Long nambooterminalId = 4L;
        Long garakmarketId = 5L;
        StationFactory.createStation("가락시장역");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable addSectionTask = () -> {
            try {
                SectionFactory.createSection(line3Id, nambooterminalId, garakmarketId, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable removeSectionTask = () -> {
            try {
                SectionFactory.deleteSection(line3Id, nambooterminalId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // when
        executor.submit(addSectionTask);
        executor.submit(removeSectionTask);
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // then
        ExtractableResponse<Response> response = PathFactory.findShortestPath(gyodaeId, yangjaeId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stations = response.jsonPath().getList("stations.name", String.class);
        assertThat(stations).containsExactly("교대역", "가락시장역", "양재역");
        int distance = response.jsonPath().getInt("distance");
        assertThat(distance).isEqualTo(5);

    }
}
