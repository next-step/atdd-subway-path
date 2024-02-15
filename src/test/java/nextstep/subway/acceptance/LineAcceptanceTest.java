package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.fixture.LineTestFixture;
import nextstep.subway.fixture.SectionTestFixture;
import nextstep.subway.fixture.StationTestFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest{

    @DisplayName("지하철 노선 맨 앞에 역을 등록한다.")
    @Test
    void addFirst() {
        long 강남역_아이디 = StationTestFixture.createStationFromName("강남역").jsonPath().getLong("id");
        long 역삼역_아이디 = StationTestFixture.createStationFromName("역삼역").jsonPath().getLong("id");
        long 교대역_아이디 = StationTestFixture.createStationFromName("교대역").jsonPath().getLong("id");
        long 이호선_아이디 = LineTestFixture.createLine("2호선", "green", 강남역_아이디, 역삼역_아이디).jsonPath().getLong("id");
        SectionTestFixture.createSection(강남역_아이디, 역삼역_아이디, 10, 이호선_아이디);

        long newStartStationId = RestAssured.given().log().all()
                .contentType("application/json")
                .body(new HashMap<String, String>() {{
                    put("stationId", String.valueOf(교대역_아이디));
                    put("nextStationId", String.valueOf(강남역_아이디));
                    put("distance", String.valueOf(2));
                }})
                .when().post("/lines/{lineId}/stations", 이호선_아이디)
                .then().log().all()
                .extract().jsonPath().getLong("startStation.id");

        assertThat(newStartStationId).isEqualTo(교대역_아이디);
    }

    @DisplayName("지하철 노선 중간에 역을 등록한다.")
    @Test
    void addBetween() {
        long 강남역_아이디 = StationTestFixture.createStationFromName("강남역").jsonPath().getLong("id");
        long 역삼역_아이디 = StationTestFixture.createStationFromName("역삼역").jsonPath().getLong("id");
        long 교대역_아이디 = StationTestFixture.createStationFromName("교대역").jsonPath().getLong("id");
        long 이호선_아이디 = LineTestFixture.createLine("2호선", "green", 강남역_아이디, 역삼역_아이디).jsonPath().getLong("id");
        SectionTestFixture.createSection(강남역_아이디, 역삼역_아이디, 10, 이호선_아이디);

        ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .contentType("application/json")
                .body(new HashMap<String, String>() {{
                    put("stationId", String.valueOf(교대역_아이디));
                    put("nextStationId", String.valueOf(역삼역_아이디));
                    put("distance", String.valueOf(2));
                }})
                .when().post("/lines/{lineId}/stations", 이호선_아이디)
                .then().log().all().extract();

        List<Long> sections = extract.jsonPath().getList("sections.sections.id", Long.class);

        assertThat(sections).hasSize(2);
    }

    @DisplayName("지하철 노선의 중간역을 제거한다.")
    @Test
    void removeBetween() {
        long 강남역_아이디 = StationTestFixture.createStationFromName("강남역").jsonPath().getLong("id");
        long 역삼역_아이디 = StationTestFixture.createStationFromName("역삼역").jsonPath().getLong("id");
        long 교대역_아이디 = StationTestFixture.createStationFromName("교대역").jsonPath().getLong("id");
        long 이호선_아이디 = LineTestFixture.createLine("2호선", "green", 강남역_아이디, 역삼역_아이디).jsonPath().getLong("id");
        SectionTestFixture.createSection(강남역_아이디, 역삼역_아이디, 10, 이호선_아이디);
        SectionTestFixture.createSection(교대역_아이디, 강남역_아이디, 2, 이호선_아이디);

        ValidatableResponse validatableResponse = RestAssured.given().log().all()
                .when().delete("/lines/{lineId}/stations?stationId={stationId}", 이호선_아이디, 강남역_아이디)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("지하철 노선의 출발역을 제거한다.")
    @Test
    void removeFirst() {
        long 강남역_아이디 = StationTestFixture.createStationFromName("강남역").jsonPath().getLong("id");
        long 역삼역_아이디 = StationTestFixture.createStationFromName("역삼역").jsonPath().getLong("id");
        long 교대역_아이디 = StationTestFixture.createStationFromName("교대역").jsonPath().getLong("id");
        long 이호선_아이디 = LineTestFixture.createLine("2호선", "green", 강남역_아이디, 역삼역_아이디).jsonPath().getLong("id");
        SectionTestFixture.createSection(강남역_아이디, 역삼역_아이디, 10, 이호선_아이디);
        SectionTestFixture.createSection(교대역_아이디, 강남역_아이디, 2, 이호선_아이디);

        RestAssured.given().log().all()
                .when().delete("/lines/{lineId}/stations?stationId={stationId}", 이호선_아이디, 교대역_아이디)
                .then().log().all()
                .statusCode(204);
    }

}
