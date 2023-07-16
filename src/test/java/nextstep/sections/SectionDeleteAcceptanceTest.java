package nextstep.sections;

import static org.assertj.core.api.Assertions.assertThat;

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
import nextstep.subway.SchemaInitSql;
import nextstep.subway.StationFixture;
import nextstep.subway.SubwayApplication;
import nextstep.subway.line.view.LineResponse;
import nextstep.subway.section.model.SectionCreateResponse;
import nextstep.subway.station.view.StationResponse;
import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.ErrorResponse;

@SchemaInitSql
@DisplayName("지하철 구간 삭제 기능")
@SpringBootTest(classes = SubwayApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class SectionDeleteAcceptanceTest {
    LineFixture lineFixture = new LineFixture();
    StationFixture stationFixture = new StationFixture();
    SectionFixture sectionFixture = new SectionFixture();

    StationResponse lineUpstationA = null;
    StationResponse lineDownstationB = null;
    LineResponse lineAB = null;

    StationResponse lineUpstationC = null;
    StationResponse lineDownstationD = null;
    LineResponse lineCD = null;

    SectionCreateResponse section = null;

    @BeforeEach
    public void beforeEach() {
        lineUpstationA = stationFixture.지하철역_생성("upstationA");
        lineDownstationB = stationFixture.지하철역_생성("downStationB");
        lineAB = lineFixture.노선생성("line-ab", "yellow", lineUpstationA.getId(), lineDownstationB.getId(), 10);

        lineUpstationC = stationFixture.지하철역_생성("upstationC");
        lineDownstationD = stationFixture.지하철역_생성("downStationD");
        lineCD = lineFixture.노선생성("line-cd", "blue", lineUpstationC.getId(), lineDownstationD.getId(), 5);
    }

    @DisplayName("구간이 1개만 있을때")
    @Nested
    class Given_no_section {

        @DisplayName("마지막 구간을 삭제하면")
        @Nested
        class When_remove_last_section {

            @DisplayName("예외가 발생한다")
            @Test
            void shouldThrowError() {
                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().delete(getDeleteSectionUrl(lineAB.getId(), lineDownstationB.getId()))
                                                                    .then().log().all()
                                                                    .extract();

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.as(ErrorResponse.class).getErrorCode()).isEqualTo(ErrorCode.SECTION_DELETE_FAIL_BY_LAST_SECTION_CANNOT_DELETED);
            }
        }
    }

    @DisplayName("구간이 있을때")
    @Nested
    class Given_sections {


        @DisplayName("하행 종점역이 아닌 역을 제거하면")
        @Nested
        class When_remove_not_last_downstation {

            @DisplayName("예외를 던진다")
            @Test
            void shouldThrowError() {
                section = sectionFixture.구간생성(lineAB.getId(), lineDownstationB.getId(), lineUpstationC.getId(), 4).as(SectionCreateResponse.class);

                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().delete(getDeleteSectionUrl(lineAB.getId(), lineDownstationB.getId()))
                                                                    .then().log().all()
                                                                    .extract();

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.as(ErrorResponse.class).getErrorCode()).isEqualTo(ErrorCode.ONLY_LAST_DOWNSTATION_CAN_DELETED);
            }
        }

        @DisplayName("하행 종점역을 제거하면")
        @Nested
        class When_remove_last_downstation {

            @DisplayName("구간이 제거된다")
            @Test
            void deleteSection() {
                section = sectionFixture.구간생성(lineAB.getId(), lineDownstationB.getId(), lineUpstationC.getId(), 4).as(SectionCreateResponse.class);

                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().delete(getDeleteSectionUrl(lineAB.getId(), lineUpstationC.getId()))
                                                                    .then().log().all()
                                                                    .extract();

                assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            }
        }
    }

    private String getDeleteSectionUrl(Long lineId, Long stationId) {
        return "/lines/" + lineId + "/sections?stationId=" + stationId;
    }

    @Nested
    class Given_노선에_구간이_하나일때 {


        @Nested
        class When_구간의_상행역을_제거하면 {

            @DisplayName("예외가 발생한다")
            @Test
            void shouldThrowError() {
                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().delete(getDeleteSectionUrl(lineAB.getId(), lineUpstationA.getId()))
                                                                    .then().log().all()
                                                                    .extract();

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }

        @Nested
        class When_구간의_하행역을_제거하면 {

            @DisplayName("예외가 발생한다")
            @Test
            void shouldThrowError() {
                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().delete(getDeleteSectionUrl(lineAB.getId(), lineDownstationB.getId()))
                                                                    .then().log().all()
                                                                    .extract();

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }
    }

    /**
     * given 노선에 구간이 2개 이상일때
     * when 중간역을 제거하면
     * then 삭제된다
     */
    @Nested
    class Given_노선에_구간이_2개이상일때 {

        @Nested
        class When_중간역을_제거하면 {

            @DisplayName("삭제된다")
            @Test
            void then() {
                section = sectionFixture.구간생성(lineAB.getId(), lineDownstationB.getId(), lineUpstationC.getId(), 4).as(SectionCreateResponse.class);

                ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                    .when().delete(getDeleteSectionUrl(lineAB.getId(), lineDownstationB.getId()))
                                                                    .then().log().all()
                                                                    .extract();

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            }
        }
    }

}
