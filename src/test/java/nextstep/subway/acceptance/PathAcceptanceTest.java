package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import nextstep.subway.fixture.LineTestFixture;
import nextstep.subway.fixture.SectionTestFixture;
import nextstep.subway.fixture.StationTestFixture;
import nextstep.subway.paths.Path;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest{
    private long 강남역;
    private long 교대역;
    private long 사당역;
    private long 신림역;

    private long 일호선;
    private long 이호선;
    private long 삼호선;
    private long 신분당선;

    @BeforeEach
    public void setUp() {
        강남역 = StationTestFixture.createStationFromName("강남역").jsonPath().getLong("id");
        교대역 = StationTestFixture.createStationFromName("교대역").jsonPath().getLong("id");
        사당역 = StationTestFixture.createStationFromName("사당역").jsonPath().getLong("id");
        신림역 = StationTestFixture.createStationFromName("신림역").jsonPath().getLong("id");

        이호선 = LineTestFixture.createLine("이호선", "green", 강남역, 교대역).jsonPath().getLong("id");
        SectionTestFixture.createSection(강남역, 교대역, 10, 이호선);

        삼호선 = LineTestFixture.createLine("삼호선", "blue", 교대역, 사당역).jsonPath().getLong("id");
        SectionTestFixture.createSection(교대역, 사당역, 3, 삼호선);

        일호선 = LineTestFixture.createLine("일호선", "red", 사당역, 신림역).jsonPath().getLong("id");
        SectionTestFixture.createSection(사당역, 신림역, 1, 일호선);

        신분당선 = LineTestFixture.createLine("신분당선", "purple", 신림역, 강남역).jsonPath().getLong("id");
        SectionTestFixture.createSection(신림역, 강남역, 2, 신분당선);
    }

    @DisplayName("출발역으로부터 도착역까지의 경로에 있는 역 목록")
    @Test
    void findPath() {
        Path path = RestAssured.given().log().all().
                when().
                get("/paths?source={source}&target={target}", 강남역, 사당역).
                then().
                log().all().extract().as(Path.class);

        List<Long> ids = path.getPath().stream().map(Station::getId).collect(Collectors.toList());
        assertThat(ids).containsExactly(강남역, 신림역, 사당역);
        assertThat(path.getDistance()).isEqualTo(3);
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void findPathWithSameSourceAndTarget() {
        RestAssured.given().log().all().
                when().
                get("/paths?source={source}&target={target}", 강남역, 강남역).
                then().
                log().all().
                statusCode(400);
    }

    @DisplayName("존재하지 않는 역인 경우")
    @Test
    void findPathWithNotExistStation() {
        long 잠실역 = StationTestFixture.createStationFromName("잠실역").jsonPath().getLong("id");
        RestAssured.given().log().all().
                when().
                get("/paths?source={source}&target={target}", 강남역, 잠실역).
                then().
                log().all().
                statusCode(400);
    }

    @DisplayName("존재하지 않는 노선인 경우")
    @Test
    void findPathWithNotExistLine() {
        long 잠실역 = StationTestFixture.createStationFromName("잠실역").jsonPath().getLong("id");
        long 김포공항역 = StationTestFixture.createStationFromName("김포공항역").jsonPath().getLong("id");
        long 김포공항선 = LineTestFixture.createLine("김포공항선", "pink", 잠실역, 김포공항역).jsonPath().getLong("id");
        SectionTestFixture.createSection(잠실역, 김포공항역, 10, 김포공항선);

        RestAssured.given().log().all().
                when().
                get("/paths?source={source}&target={target}", 강남역, 잠실역).
                then().
                log().all().
                statusCode(400);
    }



}
