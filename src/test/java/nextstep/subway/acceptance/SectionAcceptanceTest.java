package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.request.LineRequest;
import nextstep.subway.domain.response.LineResponse;
import nextstep.subway.domain.response.SectionResponse;
import nextstep.subway.exception.ExceptionMessage;
import nextstep.subway.exception.ExceptionResponse;
import nextstep.subway.utils.StationTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.utils.LineTestUtil.createSubwayLine;
import static nextstep.subway.utils.LineTestUtil.getLine;
import static nextstep.subway.utils.SectionTestUtil.addSection;
import static nextstep.subway.utils.SectionTestUtil.deleteSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql("/truncate.sql")
public class SectionAcceptanceTest {

    long stationId1, stationId2, stationId3, lineId;
    int distance;

    @BeforeEach
    void setUp() {
        stationId1 = StationTestUtil.createStation("A").jsonPath().getLong("id");
        stationId2 = StationTestUtil.createStation("B").jsonPath().getLong("id");
        stationId3 = StationTestUtil.createStation("C").jsonPath().getLong("id");

        distance = 10;
        lineId = createSubwayLine(new LineRequest("2호선", "green", stationId1, stationId2, distance)).jsonPath().getLong("id");
    }

    /**
     * 구간 등록 기능
     * 지하철 노선에 구간을 등록하는 기능을 구현
     */
    @DisplayName("지하철 구간 등록")
    @Test
    void addSectionTest() {
        //given
        Map<String, Object> params = new HashMap<>();
        int distance = 10;
        params.put("upStationId", stationId2);
        params.put("downStationId", stationId3);
        params.put("distance", distance);

        //when
        ExtractableResponse<Response> response = addSection(params, lineId);

        // ERROR
        SectionResponse sectionResponse = response.as(SectionResponse.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(sectionResponse.getLine().getId()).isEqualTo(lineId),
                () -> assertThat(sectionResponse.getUpStation().getId()).isEqualTo(stationId2),
                () -> assertThat(sectionResponse.getDownStation().getId()).isEqualTo(stationId3),
                () -> assertThat(sectionResponse.getDistance()).isEqualTo(distance)
        );
    }

    /**
     * 구간 등록 실패 테스트
     * 새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이 아닐 경우
     * 구간을 등록할 수 없다.
     */
    @DisplayName("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다.")
//    @Test
    void addSectionExceptionTest1() {
        //given

        //when
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", stationId1);
        params.put("downStationId", stationId3);
        params.put("distance", 10);

        String exceptionMessage = addSection(params, lineId).as(ExceptionResponse.class).getMessage();

        //then
//        assertThat(exceptionMessage).isEqualTo(ExceptionMessage.UPSTATION_VALIDATION_EXCEPTION.getMessage());
    }

    /**
     * 구간 등록 실패 테스트
     * 이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.
     */
    @DisplayName("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.")
//    @Test
    void addSectionExceptionTest2() {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", stationId2);
        params.put("downStationId", stationId3);
        params.put("distance", 11);
        addSection(params, lineId);

        //when
        Map<String, Object> params3 = new HashMap<>();
        params3.put("upStationId", stationId3);
        params3.put("downStationId", stationId2);
        params3.put("distance", 13);
        String exceptionMessage = addSection(params3, lineId).as(ExceptionResponse.class).getMessage();

        //then
//        assertThat(exceptionMessage).isEqualTo(ExceptionMessage.DOWNSTATION_VALIDATION_EXCEPTION.getMessage());
    }

    /**
     * 새로운 구간의 상행역과 하행역은 같을 수 없다.
     */
    @DisplayName("새로운 구간의 상행역과 하행역은 같을 수 없다.")
//    @Test
    void addSectionExceptionTest3() {
        //given

        //when
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", stationId2);
        params.put("downStationId", stationId2);
        params.put("distance", 13);
        String exceptionMessage = addSection(params, lineId).as(ExceptionResponse.class).getMessage();

        //then
        assertThat(exceptionMessage).isEqualTo(ExceptionMessage.NEW_SECTION_VALIDATION_EXCEPTION.getMessage());
    }

    /**
     * 구간 제거 기능
     * 지하철 노선에 구간을 제거하는 기능 구현
     */
    @DisplayName("구간 제거 기능")
    @Test
    void deleteSectionTest() {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", stationId2);
        params.put("downStationId", stationId3);
        params.put("distance", 11);
        addSection(params, lineId);

        //when
        deleteSection(lineId, stationId3);

        //then
        //section 수 = 1
        LineResponse lineResponse = getLine(lineId).as(LineResponse.class);
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    /**
     * 구간 제거 실패 테스트
     * 지하철 노선에 등록된 역(하행 종점역)만 제거할 수 있다. 즉, 마지막 구간만 제거할 수 있다.
     */
    @DisplayName("마지막 구간만 제거할 수 있다.")
    @Test
    void deleteSectionExceptionTest1() {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", stationId2);
        params.put("downStationId", stationId3);
        params.put("distance", 11);
        addSection(params, lineId);

        //when
        String message = deleteSection(lineId, stationId2).as(ExceptionResponse.class).getMessage();

        //then
        assertThat(message).isEqualTo(ExceptionMessage.DELETE_LAST_SECTION_EXCEPTION.getMessage());
    }

    /**
     * 구간 제거 실패 테스트
     * 지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.
     */
    @DisplayName("지하철 노선에 구간이 1개인 경우 역을 삭제할 수 없다.")
    @Test
    void deleteSectionExceptionTest2() {
        //given

        //when
        String message = deleteSection(lineId, stationId2).as(ExceptionResponse.class).getMessage();

        //then
        assertThat(message).isEqualTo(ExceptionMessage.DELETE_ONLY_ONE_SECTION_EXCEPTION.getMessage());
    }

    /**
     * Given 지하철 노선에 구간 추가하고
     * When 추가한 지하철 구간을 조회하면
     * Then 등록한 지하철 노선의 구간 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 구간 조회")
    @Test
    void showSection() {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", stationId2);
        params.put("downStationId", stationId3);
        params.put("distance", 11);
        addSection(params, lineId);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/lines/{lineId}/sections/{sectionId}", lineId, 1L)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        //then
        long id = response.jsonPath().getLong("id");
        long lineId = response.jsonPath().getLong("line.id");
        long upStationId = response.jsonPath().getLong("upStation.id");
        long downStationId = response.jsonPath().getLong("downStation.id");
        int sectionDistance = response.jsonPath().getInt("distance");

        assertAll(
                () -> assertThat(id).isEqualTo(1L),
                () -> assertThat(lineId).isEqualTo(1L),
                () -> assertThat(upStationId).isEqualTo(stationId1),
                () -> assertThat(downStationId).isEqualTo(stationId2),
                () -> assertThat(sectionDistance).isEqualTo(distance)
        );
    }

    /**
     * 노선에 역 추가시 노선 가운데 추가 할 수 있다.
     **/
    @DisplayName("노선 가운데 역 추가")
    @Test
    void insertSection() {
        // given
        // 노선 A-B

        // when
        // A-C를 추가하면
        Map<String, Object> param = new HashMap<>();
        param.put("upStationId", stationId1);
        param.put("downStationId", stationId3);
        param.put("distance", 4);
        LineResponse response = addSection(param, lineId).as(LineResponse.class);

        // then
        // A-C-B 가 된다.
        assertAll(
                () -> assertThat(response.getStations()).hasSize(3),
                () -> assertThat(response.getStations().stream().map(station -> station.getName())).contains("A", "B", "C")
        );
    }

    /**
     * 노선에 역 추가시 노선 처음에 추가 할 수 있다.
     **/
    @DisplayName("노선 처음에 역 추가")
    @Test
    void insertSectionToFirst() {
        // given
        // 노선 A-B

        // when
        // C-A를 추가하면
        Map<String, Object> param = new HashMap<>();
        param.put("upStationId", stationId3);
        param.put("downStationId", stationId1);
        param.put("distance", 4);
        LineResponse response = addSection(param, lineId).as(LineResponse.class);

        // then
        // C-A-B 가 된다.
        assertAll(
                () -> assertThat(response.getStations()).hasSize(3),
                () -> assertThat(response.getStations().stream().map(station -> station.getName())).contains("A", "B", "C")
        );
    }
}
