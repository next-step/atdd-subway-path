package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.fixture.LineFixture;
import nextstep.subway.acceptance.fixture.StationFixture;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.section.SectionRequest;
import nextstep.subway.dto.station.StationResponse;
import nextstep.subway.entity.Station;
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
    class 구간_생성 {
        /**
         * When 노선의 하행역 신규 구간을 등록하면
         * Then 노선 조회 시 등록한 구간이 조회된다.
         */
        @DisplayName("노선의 하행역에 구간을 추가한다.")
        @Test
        void 하행_구간_등록_성공() {
            // when
            ExtractableResponse<Response> 구간_생성_응답 = createSection(이호선_ID, 선릉역_ID, 역삼역_ID, 5);

            assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // then
            LineResponse 이호선 = LineFixture.getLine(이호선_ID).as(LineResponse.class);
            List<Long> 이호선_역_리스트 = 이호선.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

            assertThat(이호선_역_리스트).containsExactly(강남역_ID, 역삼역_ID, 선릉역_ID);
        }

        /**
         * When 노선의 상행역에 신규 구간을 등록하면
         * Then 노선 조회 시 등록한 구간이 조회된다.
         */
        @DisplayName("노선의 상행역에 구간을 추가한다.")
        @Test
        void 상행_구간_등록_성공() {
            // when
            ExtractableResponse<Response> 구간_생성_응답 = createSection(이호선_ID, 선릉역_ID, 강남역_ID, 5);

            assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // then
            LineResponse 이호선 = LineFixture.getLine(이호선_ID).as(LineResponse.class);
            List<Long> 이호선_역_리스트 = 이호선.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

            assertThat(이호선_역_리스트).containsExactly(강남역_ID, 선릉역_ID, 역삼역_ID);
        }

        /**
         * When 노선의 중간에 신규 구간을 등록하면
         * Then 노선 조회 시 등록한 구간이 조회된다.
         */
        @DisplayName("노선의 중간에 구간을 추가한다.")
        @Test
        void 중간_구간_등록_성공() {
            // when
            ExtractableResponse<Response> 구간_생성_응답 = createSection(이호선_ID, 선릉역_ID, 강남역_ID, 5);

            assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // then
            LineResponse 이호선 = LineFixture.getLine(이호선_ID).as(LineResponse.class);
            List<Long> 이호선_역_리스트 = 이호선.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

            assertThat(이호선_역_리스트).containsExactly(강남역_ID, 선릉역_ID, 역삼역_ID);
        }

        /**
         * When 노선에 신규 구간을 등록하면
         * Then 연결할 수 없는 구간이면 에러가 발생한다.
         */
        @DisplayName("연결할 수 없는 구간 등록시 에러가 발생한다.")
        @Test
        void 연결_불가_구간_등록_실패() {
            // given
            Long 삼성역_ID = StationFixture.createStation("삼성역").as(StationResponse.class).getId();

            // when
            ExtractableResponse<Response> 구간_생성_응답 = createSection(이호선_ID, 삼성역_ID, 선릉역_ID, 15);

            // then
            assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * When 노선에 신규 구간을 등록하면
         * Then 이미 등록되어있는 역 존재시 에러가 발생한다.
         */
        @DisplayName("이미 등록되어있는 역은 노선에 등록될 수 없다.")
        @Test
        void 동일한_역_구간_등록_실패() {
            // when
            ExtractableResponse<Response> 구간_생성_응답 = createSection(이호선_ID, 역삼역_ID, 강남역_ID, 15);

            // then
            assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }


    @Nested
    class 구간_삭제 {
        /**
         * Given 구간을 생성하고
         * When 노선의 하행 구간을 삭제하면
         * Then 해당 구간의 정보는 삭제된다.
         */
        @DisplayName("노선의 하행 구간을 삭제한다.")
        @Test
        void 하행_구간_삭제_성공() {
            // given
            ExtractableResponse<Response> 구간_생성_응답 = createSection(이호선_ID, 선릉역_ID, 역삼역_ID, 15);

            assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // when
            ExtractableResponse<Response> 구간_삭제_응답 = deleteSection(이호선_ID, 선릉역_ID);

            // then
            assertThat(구간_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        /**
         * Given 구간을 생성하고
         * When 노선의 상행 구간을 삭제하면
         * Then 해당 구간의 정보는 삭제된다.
         */
        @DisplayName("노선의 상행 구간을 삭제한다.")
        @Test
        void 상행_구간_삭제_성공() {
            // given
            ExtractableResponse<Response> 구간_생성_응답 = createSection(이호선_ID, 선릉역_ID, 역삼역_ID, 15);

            assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // when
            ExtractableResponse<Response> 구간_삭제_응답 = deleteSection(이호선_ID, 강남역_ID);

            // then
            assertThat(구간_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        /**
         * Given 구간을 생성하고
         * When 노선의 중간 구간을 삭제하면
         * Then 해당 구간의 정보는 삭제된다.
         */
        @DisplayName("노선의 중간 구간을 삭제한다.")
        @Test
        void 중간_구간_삭제_성공() {
            // given
            ExtractableResponse<Response> 구간_생성_응답 = createSection(이호선_ID, 선릉역_ID, 역삼역_ID, 15);

            assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // when
            ExtractableResponse<Response> 구간_삭제_응답 = deleteSection(이호선_ID, 역삼역_ID);

            // then
            assertThat(구간_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        /**
         * When 구간을 삭제하면
         * Then 존재하지 않는 구간일 시 에러가 발생한다.
         */
        @DisplayName("노선에 존재하는 구간만 삭제 가능하다.")
        @Test
        void invalidDownStation() {
            // given
            Long 서초역_ID = StationFixture.createStation("서초역").as(StationResponse.class).getId();

            // when
            ExtractableResponse<Response> response = deleteSection(이호선_ID, 서초역_ID);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * When 구간을 삭제하면
         * Then 유일한 구간일 시 에러가 발생한다.
         */
        @DisplayName("노선의 유일한 구간은 삭제가 불가하다.")
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
