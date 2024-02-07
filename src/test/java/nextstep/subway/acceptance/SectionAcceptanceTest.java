package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.fixture.LineFixture;
import nextstep.subway.acceptance.fixture.StationFixture;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.section.SectionRequest;
import nextstep.subway.dto.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;


import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = {"/truncate.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class SectionAcceptanceTest {
    private static Long 강남역_ID;
    private static Long 역삼역_ID;
    private static Long 선릉역_ID;
    private static Long 이호선_ID;


    @BeforeEach
    void setUp() {
        강남역_ID = StationFixture.createStation("강남역").as(StationResponse.class).getId();
        역삼역_ID = StationFixture.createStation("역삼역").as(StationResponse.class).getId();
        선릉역_ID = StationFixture.createStation("선릉역").as(StationResponse.class).getId();
        이호선_ID = LineFixture.createLine("2호선", "green", 10, 강남역_ID, 역삼역_ID)
            .as(LineResponse.class).getId();
    }

    @Nested
    class createSection {
        /**
         * When 노선에 신규 구간을 등록하면
         * Then 노선 조회 시 등록한 구간이 조회된다.
         */
        @DisplayName("지하철 구간을 생성한다")
        @Test
        void success() {
            // when
            ExtractableResponse<Response> response = createSection(이호선_ID, 선릉역_ID, 역삼역_ID, 15);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // then
            LineResponse line = LineFixture.getLine(이호선_ID).as(LineResponse.class);
            List<Long> stationIds = line.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

            assertThat(stationIds).contains(강남역_ID, 역삼역_ID, 선릉역_ID);
        }

        /**
         * When 노선에 신규 구간을 등록하면
         * Then 신규 구간의 상행역과 노선의 하행역이 일치하지 않아 에러가 발생한다.
         */
        @DisplayName("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다.")
        @Test
        void invalidUpStation() {
            // when
            ExtractableResponse<Response> sectionResponse = createSection(이호선_ID, 선릉역_ID, 강남역_ID, 15);

            // then
            assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * When 노선에 신규 구간을 등록하면
         * Then 신규 구간의 상행역과 노선의 하행역이 일치하지 않아 에러가 발생한다.
         */
        @DisplayName("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.")
        @Test
        void duplicateDownStation() {
            // when
            ExtractableResponse<Response> sectionResponse = createSection(이호선_ID, 역삼역_ID, 선릉역_ID, 15);

            // then
            assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }


    @Nested
    class deleteSection {
        /**
         * Given 구간을 생성하고
         * When 생성한 구간을 삭제하면
         * Then 해당 구간의 정보는 삭제된다.
         */
        @DisplayName("지하철 구간을 삭제한다")
        @Test
        void success() {
            // given
            ExtractableResponse<Response> createResponse = createSection(이호선_ID, 선릉역_ID, 역삼역_ID, 15);

            assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // when
            ExtractableResponse<Response> response = deleteSection(이호선_ID, 선릉역_ID);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        /**
         * Given 구간을 생성하고
         * When 노선 시 생성된 구간을 삭제하면
         * Then 노선의 마지막 구간이 아니기에 에러가 발생한다.
         */
        @DisplayName("지하철 노선에 등록된 마지막 구간만 제거할 수 있다.")
        @Test
        void invalidDownStation() {
            // given
            ExtractableResponse<Response> createResponse = createSection(이호선_ID, 선릉역_ID, 역삼역_ID, 15);

            assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // when
            ExtractableResponse<Response> response = deleteSection(이호선_ID, 역삼역_ID);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * When 노선 시 생성된 구간을 삭제하면
         * Then 등록된 구간이 1개라서 에러가 발생한다.
         */
        @DisplayName("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.")
        @Test
        void notExistsOtherSection() {
            // when
            ExtractableResponse<Response> response = deleteSection(이호선_ID, 역삼역_ID);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

    }

    private ExtractableResponse<Response> createSection(
        Long lineId, Long downStationId, Long upStationId, Integer distance
    ) {
        return RestAssured
            .given().log().all()
            .body(new SectionRequest(downStationId, upStationId, distance))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return RestAssured
            .given().log().all()
            .queryParam("stationId", stationId)
            .when().delete("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }

}
