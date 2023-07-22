package nextstep.path;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.line.LineFixture;
import nextstep.sections.SectionFixture;
import nextstep.subway.SchemaInitSql;
import nextstep.subway.StationFixture;
import nextstep.subway.SubwayApplication;
import nextstep.subway.line.view.LineResponse;
import nextstep.subway.station.view.StationResponse;

@SchemaInitSql
@DisplayName("경로 조회 기능")
@SpringBootTest(classes = SubwayApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class PathAcceptanceTest {

    /**
     * given 출발역과 도착역의 id가 주어졌을때
     * when 경로를 조회하면
     * then 출발역과 도착역 사이의 역 목록이 조회된다
     * then 경로의 거리가 조회된다
     */

    StationFixture stationFixture = new StationFixture();
    LineFixture lineFixture = new LineFixture();
    SectionFixture sectionFixture = new SectionFixture();

    /**
     *           C  --(2)-- C2 --(3)--  A --(10)-- B (lineAB)
     *           | (6)                  | (5)
     *
     * D --(2)-- F -------(13)-------   E (lineDE)
     */
    @Nested
    class Given_출발역과_도착역이_주어졌을때 {

        @Nested
        class When_경로를_조회하면 {

            @Test
            void 조회된다() {
                StationResponse stationA = stationFixture.지하철역_생성("stationA");
                StationResponse stationB = stationFixture.지하철역_생성("stationB");
                StationResponse stationC = stationFixture.지하철역_생성("stationC");
                StationResponse stationC2 = stationFixture.지하철역_생성("stationC2");

                LineResponse lineAB = lineFixture.노선생성("lineAB", "b", stationA.getId(), stationB.getId(), 10);

                sectionFixture.구간생성(lineAB.getId(), stationC.getId(), stationA.getId(), 5);
                sectionFixture.구간생성(lineAB.getId(), stationC.getId(), stationC2.getId(), 2);


                StationResponse stationD = stationFixture.지하철역_생성("stationD");
                StationResponse stationE = stationFixture.지하철역_생성("stationE");
                StationResponse stationF = stationFixture.지하철역_생성("stationF");
                LineResponse lineDE = lineFixture.노선생성("lineDE", "y", stationD.getId(), stationE.getId(), 15);

                sectionFixture.구간생성(lineDE.getId(), stationD.getId(), stationF.getId(), 2);
                sectionFixture.구간생성(lineDE.getId(), stationC.getId(), stationF.getId(), 6);

                sectionFixture.구간생성(lineDE.getId(), stationA.getId(), stationE.getId(), 5);

                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                .when().get(getPathUrl(stationC2.getId(), stationE.getId()))
                                                                .then().log().all().extract();

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                Assertions.assertThat(response.jsonPath().getList("stations")).isNotEmpty();
                Assertions.assertThat(response.jsonPath().getInt("distance")).isEqualTo(8);
            }

            private String getPathUrl(Long sourceStationId, Long targetStationId) {
                return String.format("/paths?source=%d&target=%d", sourceStationId, targetStationId);
            }
        }
    }

}
