package subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import subway.line.LineRequest;
import subway.line.LineResponse;
import subway.station.Station;
import subway.station.StationResponse;
import util.RestAssuredUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(value = "/table_truncate.sql")
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {
    private static Long SINSA_STATION_ID;
    private static Long GWANGGYO_STATION_ID;
    private static Long SUSEO_STATION_ID;
    private static Long LINE_SHINBUNDANG_ID;
    private static SectionRequest SECTION_TWO;
    private static SectionRequest SECTION_ERROR;

    @BeforeEach
    void setFixture() {
        SINSA_STATION_ID = RestAssuredUtil.post(new Station("신사역"), "stations")
                .as(StationResponse.class).getId();
        GWANGGYO_STATION_ID = RestAssuredUtil.post(new Station("광교역"), "stations")
                .as(StationResponse.class).getId();
        SUSEO_STATION_ID = RestAssuredUtil.post(new Station("수서역"), "stations")
                .as(StationResponse.class).getId();
        LINE_SHINBUNDANG_ID = RestAssuredUtil.post(
                new LineRequest(0L, "신분당선", "bg-red-600", 10L, SINSA_STATION_ID, GWANGGYO_STATION_ID),
                "/lines").as(LineResponse.class).getId();
        SECTION_TWO = new SectionRequest(GWANGGYO_STATION_ID, SUSEO_STATION_ID, 30L);
        SECTION_ERROR = new SectionRequest(SINSA_STATION_ID, SUSEO_STATION_ID, 40L);
    }

    /**
     * When 지하철 구간을 생성하면
     * Then 지하철 노선 조회 시 생성한 구간을 찾을 수 있다
     */
    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        ExtractableResponse<Response> response
                = RestAssuredUtil.post(SECTION_TWO, "/lines/" + LINE_SHINBUNDANG_ID + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        LineResponse res
                = RestAssuredUtil.get("/lines/" + LINE_SHINBUNDANG_ID).as(LineResponse.class);
        assertThat(res.getStations().get(0).getName()).isEqualTo("신사역");
        assertThat(res.getStations().get(1).getName()).isEqualTo("광교역");
        assertThat(res.getStations().get(2).getName()).isEqualTo("수서역");
    }

    /**
     * When 신규 구간의 상행역과 노선의 하행종점역이 같지 않으면
     * Then IllegalArgumentException이 발생한다.
     */
    @DisplayName("신규 구간의 상행역과 노선의 하행종점역이 같아야 한다.")
    @Test
    void cant_add_section_when_upStation_in_new_section_is_not_equal_line_downStation() {
        // then
        RestAssured.given().log().all()
                .body(SECTION_ERROR)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + LINE_SHINBUNDANG_ID + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 구간을 삭제하면
     * Then 지하철 노선 조회 시 삭제된 구간을 찾을 수 없다
     */
    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // when
        RestAssuredUtil.post(SECTION_TWO, "/lines/" + LINE_SHINBUNDANG_ID + "/sections");
        RestAssuredUtil.delete("/lines/" + LINE_SHINBUNDANG_ID + "/sections" + "?stationId=" + SUSEO_STATION_ID);

        // then
        List<String> stationNames
                = RestAssuredUtil.get("/lines/" + LINE_SHINBUNDANG_ID)
                .jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).doesNotContain("수서역");
    }

    /**
     * When 하행 종점역이 아닌 구간을 제거하면
     * Then IllegalArgumentException이 발생한다.
     */
    @DisplayName("하행 종점역이 아닌 지하철 구간을 제거할 수 없다.")
    @Test
    void cantDeleteNotDownStation() {
        // when
        RestAssuredUtil.post(SECTION_TWO, "/lines/" + LINE_SHINBUNDANG_ID + "/sections");

        // then
        RestAssured
                .given()
                .param("stationId", GWANGGYO_STATION_ID)
                .when()
                .delete("/lines/" + LINE_SHINBUNDANG_ID + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 구간이 한개 이하일 떄 구간 삭제 요청을 하면
     * Then IllegalArgumentException이 발생한다.
     */
    @DisplayName("지하철 구간이 한 개 이하일 때 구간을 삭제할 수 없다.")
    @Test
    void cantDeleteUnderOneSection() {
        // then
        RestAssured
                .given()
                .param("stationId", GWANGGYO_STATION_ID)
                .when()
                .delete("/lines/" + LINE_SHINBUNDANG_ID + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
