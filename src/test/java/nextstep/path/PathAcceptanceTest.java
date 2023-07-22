package nextstep.path;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
     *
     *
     * G -- (1) --- H (lineGH)
     */

    private StationResponse stationA;
    private StationResponse stationB;
    private StationResponse stationC;
    private StationResponse stationC2;
    private StationResponse stationD;
    private StationResponse stationE;
    private StationResponse stationG;
    private StationResponse stationH;

    @BeforeEach
    public void setup() {
        stationA = stationFixture.지하철역_생성("stationA");
        stationB = stationFixture.지하철역_생성("stationB");
        stationC = stationFixture.지하철역_생성("stationC");
        stationC2 = stationFixture.지하철역_생성("stationC2");

        // a- b
        LineResponse lineAB = lineFixture.노선생성("lineAB", "b", stationA.getId(), stationB.getId(), 10);

        /**
         * c - c2 - a
         */
        sectionFixture.구간생성(lineAB.getId(), stationC.getId(), stationA.getId(), 5);
        sectionFixture.구간생성(lineAB.getId(), stationC.getId(), stationC2.getId(), 2);

        stationD = stationFixture.지하철역_생성("stationD");
        stationE = stationFixture.지하철역_생성("stationE");

        // d- e
        StationResponse stationF = stationFixture.지하철역_생성("stationF");
        LineResponse lineDE = lineFixture.노선생성("lineDE", "y", stationD.getId(), stationE.getId(), 15);

        // d - f - e
        sectionFixture.구간생성(lineDE.getId(), stationD.getId(), stationF.getId(), 2);

        // c - f
        sectionFixture.구간생성(lineDE.getId(), stationC.getId(), stationF.getId(), 6);

        // a - e
        sectionFixture.구간생성(lineDE.getId(), stationA.getId(), stationE.getId(), 5);

        stationG = stationFixture.지하철역_생성("stationG");
        stationH = stationFixture.지하철역_생성("stationH");

        LineResponse lineGH = lineFixture.노선생성("lineGH", "z", stationG.getId(), stationH.getId(), 15);
    }

    @Nested
    class Given_출발역과_도착역이_서로_다를때 {

        @Nested
        class When_경로를_조회하면 {

            @Test
            void 조회된다() {
                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                .when().get(getPathUrl(stationC2.getId(), stationE.getId()))
                                                                .then().log().all().extract();

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                Assertions.assertThat(response.jsonPath().getList("stations")).isNotEmpty();
                Assertions.assertThat(response.jsonPath().getInt("distance")).isEqualTo(8);
            }
        }
    }

    @Nested
    class Given_출발역과_도착역이_같을때 {
        @Nested
        class When_경로를_조회하면 {

            @Test
            void 오류가발생한다() {
                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().get(getPathUrl(stationC2.getId(), stationC2.getId()))
                                                                    .then().log().all().extract();

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }
    }

    @Nested
    class Given_출발역과_도착역이_연결되어있지않을때 {

        @Nested
        class When_경로를_조회하면 {

            @Test
            void 오류가발생한다() {
                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().get(getPathUrl(stationC2.getId(), stationH.getId()))
                                                                    .then().log().all().extract();

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }


        }
    }

    @Nested
    class Given_출발역이_존재하지_않을때 {

        @Nested
        class When_경로를_조회하면 {

            @Test
            void 오류가발생한다() {
                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().get(getPathUrl(Long.MAX_VALUE, stationC2.getId()))
                                                                    .then().log().all().extract();

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }
    }

    @Nested
    class Given_도착역이_존재하지_않을때 {

        @Nested
        class When_경로를_조회하면 {

            @Test
            void 오류가발생한다() {
                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().get(getPathUrl(stationC2.getId(), Long.MAX_VALUE))
                                                                    .then().log().all().extract();

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }
    }

    private String getPathUrl(Long sourceStationId, Long targetStationId) {
        return String.format("/paths?source=%d&target=%d", sourceStationId, targetStationId);
    }
}
