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

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.line.LineFixture;
import nextstep.subway.SchemaInitSql;
import nextstep.subway.StationFixture;
import nextstep.subway.SubwayApplication;
import nextstep.subway.line.view.LineResponse;
import nextstep.subway.station.view.StationResponse;
import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.ErrorResponse;

@SchemaInitSql
@DisplayName("지하철 구간 등록 기능")
@SpringBootTest(classes = SubwayApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class SectionCreateAcceptanceTest {
    LineFixture lineFixture = new LineFixture();
    StationFixture stationFixture = new StationFixture();
    SectionFixture sectionFixture = new SectionFixture();

    StationResponse lineUpstationA = null;
    StationResponse lineDownstationB = null;
    LineResponse lineAB = null;
    int sectionABDistance = 10;

    StationResponse lineUpstationC = null;
    StationResponse lineDownstationD = null;
    LineResponse lineCD = null;



    @BeforeEach
    public void beforeEach() {
        lineUpstationA = stationFixture.지하철역_생성("upstationA");
        lineDownstationB = stationFixture.지하철역_생성("downStationB");
        lineAB = lineFixture.노선생성("line-ab", "yellow", lineUpstationA.getId(), lineDownstationB.getId(), sectionABDistance);

        lineUpstationC = stationFixture.지하철역_생성("upstationC");
        lineDownstationD = stationFixture.지하철역_생성("downStationD");
        lineCD = lineFixture.노선생성("line-cd", "blue", lineUpstationC.getId(), lineDownstationD.getId(), 5);
    }

    @DisplayName("새로운 구간의 상행역이 노선의 하행 종점역이 아닐 때")
    @Nested
    class Given_section_upstation_is_not_lines_downstation {

        @DisplayName("구간을 등록하면")
        @Nested
        class When_create_section {

            @DisplayName("오류가 발생한다")
            @Test
            void shouldThrowError() {
                ExtractableResponse<Response> response = sectionFixture.구간생성(lineAB.getId(), lineUpstationA.getId(), lineUpstationC.getId(), 3);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.as(ErrorResponse.class).getErrorCode()).isEqualTo(ErrorCode.SECTION_CREATE_FAIL_BY_UPSTATION);
            }
        }
    }

    @DisplayName("given_새로운 구간의 하행역이 노선에 존재할때")
    @Nested
    class Given_section_downstation_on_same_line {

        @DisplayName("when_구간을 등록하면")
        @Nested
        class When_create_section {

            @DisplayName("then_오류가 발생한다")
            @Test
            void shouldThrowError() {
                ExtractableResponse<Response> response = sectionFixture.구간생성(lineAB.getId(), lineDownstationB.getId(), lineUpstationA.getId(), 3);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.as(ErrorResponse.class).getErrorCode()).isEqualTo(ErrorCode.SECTION_CREATE_FAIL_BY_DOWNSTATION);
            }
        }
    }

    @Nested
    class Given_2개의_노선이_있을때 {

        @DisplayName("given_상행역은 노선의 하행으로, 하행역은 다른 노선의 역으로 설정하면")
        @Nested
        class When_구간을_등록하면 {


            @DisplayName("then_구간을 등록할 수 있다")
            @Test
            void registerSection() {
                ExtractableResponse<Response> response = sectionFixture.구간생성(lineAB.getId(), lineDownstationB.getId(), lineUpstationC.getId(), 4);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }
        }
    }

    /**
     *
     * given 구간이 있을때
     * when 구간 사이에 역을 추가하면
     * then 구간이 추가된다
     */
    @DisplayName("구간이 있을때")
    @Nested
    class GivenSection {

        @DisplayName("기존 구간에서 상행역이 같지만 길이가 짧은 역을 추가할 경우")
        @Nested
        class WhenRegisterSameUpStationAndLessDistance {


            @DisplayName("구간이 추가된다 ")
            @Test
            void sectionShouldAdd() {
                ExtractableResponse<Response> response = sectionFixture.구간생성(lineAB.getId(), lineUpstationA.getId(), lineUpstationC.getId(), 5);
            }
        }

        @DisplayName("기존 구간에서 상행역이 같지만 길이가 긴 역을 추가할 경우")
        @Nested
        class WhenRegisterSameUpStationAndLongerDistance {


            @DisplayName("추가할 수 없다")
            @Test
            void sectionShouldAdd() {
                ExtractableResponse<Response> response = sectionFixture.구간생성(lineAB.getId(), lineUpstationA.getId(), lineUpstationC.getId(), sectionABDistance + 1);

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }

        @DisplayName("추가되는 역이 이미 노선에 등록되어 있으면")
        @Nested
        class WhenRegisterSameStations {

            @DisplayName("추가할 수 없다")
            @Test
            void sectionCouldNotAdd() {
                ExtractableResponse<Response> response = sectionFixture.구간생성(lineAB.getId(), lineUpstationA.getId(), lineDownstationB.getId(), 3);

                Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }
    }
}
