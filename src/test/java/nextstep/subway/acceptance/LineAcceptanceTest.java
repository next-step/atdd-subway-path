package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.fixture.LineTestFixture;
import nextstep.subway.fixture.SectionTestFixture;
import nextstep.subway.fixture.StationTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 노선 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest{

    private long 강남역;
    private long 역삼역;
    private long 교대역;
    private long 이호선;

    @BeforeEach
    public void setUp() {
        강남역 = StationTestFixture.createStationFromName("강남역").jsonPath().getLong("id");
        역삼역 = StationTestFixture.createStationFromName("역삼역").jsonPath().getLong("id");
        교대역 = StationTestFixture.createStationFromName("교대역").jsonPath().getLong("id");
        이호선 = LineTestFixture.createLine("2호선", "green", 강남역, 역삼역).jsonPath().getLong("id");
        SectionTestFixture.createSection(강남역, 역삼역, 10, 이호선);
    }

    @DisplayName("지하철 노선 맨 앞에 역을 등록한다.")
    @Test
    void addFirst() {

        long newStartStationId = RestAssured.given().log().all()
                .contentType("application/json")
                .body(new HashMap<String, String>() {{
                    put("stationId", String.valueOf(교대역));
                    put("nextStationId", String.valueOf(강남역));
                    put("distance", String.valueOf(2));
                }})
                .when().post("/lines/{lineId}/stations", 이호선)
                .then().log().all()
                .extract().jsonPath().getLong("startStation.id");

        assertThat(newStartStationId).isEqualTo(교대역);
    }

    @DisplayName("지하철 노선 중간에 역을 등록한다.")
    @Test
    void addBetween() {

        ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .contentType("application/json")
                .body(new HashMap<String, String>() {{
                    put("stationId", String.valueOf(교대역));
                    put("nextStationId", String.valueOf(역삼역));
                    put("distance", String.valueOf(2));
                }})
                .when().post("/lines/{lineId}/stations", 이호선)
                .then().log().all().extract();

        List<Long> sections = extract.jsonPath().getList("sections.sections.id", Long.class);

        assertThat(sections).hasSize(2);
    }

    @DisplayName("지하철 노선의 중간역을 제거한다.")
    @Test
    void removeBetween() {

        SectionTestFixture.createSection(교대역, 강남역, 2, 이호선);

        ValidatableResponse validatableResponse = RestAssured.given().log().all()
                .when().delete("/lines/{lineId}/stations?stationId={stationId}", 이호선, 강남역)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("지하철 노선의 출발역을 제거한다.")
    @Test
    void removeFirst() {
        SectionTestFixture.createSection(교대역, 강남역, 2, 이호선);

        RestAssured.given().log().all()
                .when().delete("/lines/{lineId}/stations?stationId={stationId}", 이호선, 교대역)
                .then().log().all()
                .statusCode(204);
    }

}
