package nextstep.subway.acceptance;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static nextstep.subway.acceptance.LineAcceptanceTest.BASIC_DISTANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;

public class SectionAcceptanceTest extends BaseAcceptanceTest {
    private Long 역삼역_ID;

    private Long 선릉역_ID;

    private Long 강남역_ID;

    private Long 왕십리역_ID;

    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();
        역삼역_ID = 지하철_역_생성(역삼역);
        선릉역_ID = 지하철_역_생성(선릉역);
        강남역_ID = 지하철_역_생성(강남역);
        왕십리역_ID = 지하철_역_생성(왕십리역);
    }

    @Test
    void test_특정_노선에_구간을_등록하면_노선_조회시_등록한_구간을_확인할_수_있다() {
        //when
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));
        //then
        LineResponse response = 지하철_노선_조회(lineResponse.getId());
        List<SectionResponse> sectionsResponse = response.getSections();
        assertAll(
            () -> assertThat(sectionsResponse).hasSize(1),
            () -> assertThat(sectionsResponse.get(0).getUpStationId()).isEqualTo(1),
            () -> assertThat(sectionsResponse.get(0).getDownStationId()).isEqualTo(2),
            () -> assertThat(sectionsResponse.get(0).getDistance()).isEqualTo(10)
        );
    }

    @Test
    void 노선이_주어졌을때_해당_노선의_하행_종점역과_새로_등록하려는_구간의_상행_종점역이_같으면_해당_구간을_마지막_구간에_등록할_수_있다() {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));

        //when
        SectionRequest sectionRequest = new SectionRequest(선릉역_ID, 왕십리역_ID, 10);
        addSection(lineResponse, sectionRequest);

        //then
        LineResponse lineAfterResponse = 지하철_노선_조회(lineResponse.getId());
        List<SectionResponse> sectionResponses = lineAfterResponse.getSections();
        assertAll(
            () -> assertThat(sectionResponses).hasSize(2),
            () -> assertThat(sectionResponses.get(sectionResponses.size() - 1).getUpStationId()).isEqualTo(sectionRequest.getUpStationId()),
            () -> assertThat(sectionResponses.get(sectionResponses.size() - 1).getDownStationId()).isEqualTo(sectionRequest.getDownStationId()),
            () -> assertThat(sectionResponses.get(sectionResponses.size() - 1).getDistance()).isEqualTo(BASIC_DISTANCE)
        );
    }

    @DisplayName("given 특정 노선에 구간이 1개 이상 등록되었을 때\n"
                 + "   when 기존 구간 중 특정 구간의 상행역과 등록하려는 구간의 상행역이 같고 \n"
                 + "        등록하려는 구간의 길이가 특정 구간의 길이보다 짧으면\n"
                 + "   then 기존 구간 사이에 새 구간이 등록된다.")
    @Test
    void testAddSection_지하철_구간_중간에_역_추가() {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));

        //when
        Long newStationId = 왕십리역_ID;
        int newDistance = 4;
        SectionRequest newSectionRequest = new SectionRequest(역삼역_ID, newStationId, newDistance);
        addSection(lineResponse, newSectionRequest);

        //then
        LineResponse lineResponseAfterAddSection = 지하철_노선_조회(lineResponse.getId());

        List<SectionResponse> sections = lineResponseAfterAddSection.getSections();
        SectionResponse firstSection = sections.get(0);
        SectionResponse lastSection = sections.get(sections.size() - 1);
        assertAll(
            () -> assertThat(sections).hasSize(2),
            () -> assertThat(firstSection.getDownStationId()).isEqualTo(newStationId),
            () -> assertThat(firstSection.getDistance()).isEqualTo(newDistance),
            () -> assertThat(lastSection.getUpStationId()).isEqualTo(newStationId),
            () -> assertThat(lastSection.getDownStationId()).isEqualTo(선릉역_ID),
            () -> assertThat(lastSection.getDistance()).isEqualTo(BASIC_DISTANCE - newDistance)
        );
    }

    @DisplayName("given 특정 노선에 구간이 1개 이상 등록되었을 때\n"
                 + "   when 새 역을 상행종점역으로 등록하면 \n"
                 + "   then 해당 노선의 상행종점역이 변경된다.")
    @Test
    void testAddSection_상행종점역_추가() {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));

        //when
        SectionRequest sectionRequest = new SectionRequest(왕십리역_ID, 역삼역_ID, 5);
        addSection(lineResponse, sectionRequest);

        //then
        LineResponse lineAfterResponse = 지하철_노선_조회(lineResponse.getId());
        List<SectionResponse> sectionResponses = lineAfterResponse.getSections();
        assertAll(
            () -> assertThat(sectionResponses).hasSize(2),
            () -> assertThat(sectionResponses.get(0).getUpStationId()).isEqualTo(왕십리역_ID),
            () -> assertThat(sectionResponses.get(0).getDownStationId()).isEqualTo(역삼역_ID),
            () -> assertThat(sectionResponses.get(0).getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("given 특정 노선에 구간이 1개 이상 등록되었을 때\n"
                 + "   when 등록하려는 역이 기존 노선에 있다면\n"
                 + "   then 예외를 반환한다.")
    @Test
    void testAddSection_구간_추가시_기존에_등록되어있으면_예외_반환() {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));
        SectionRequest initialSectionRequest = new SectionRequest(선릉역_ID, 강남역_ID, 5);
        addSection(lineResponse, initialSectionRequest);
        //when
        SectionRequest conflictingSectionRequest = new SectionRequest(강남역_ID, 선릉역_ID, 7);

        given().body(conflictingSectionRequest)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .when().post("/lines/" + lineResponse.getId() + "/sections").then().statusCode(HttpStatus.SC_BAD_REQUEST);

    }

    @Test
    void testAddSection_해당_노선의_하행_종점역과_새로_등록하려는_구간의_상행_종점역이_다를_때_등록하려는_구간의_길이가_구간_사이에_들어올_수_없으면_에러를_반환한다() {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));

        //when
        SectionRequest sectionRequest = new SectionRequest(역삼역_ID, 왕십리역_ID, 15);
        given().body(sectionRequest).pathParam("lineId", lineResponse.getId())
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .when().post("/lines/{lineId}/sections").then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void testDeleteSection_노선에_등록된_구간이_2개_이상_있을때_마지막_구간을_제거하면_노선_조회시_제거된_마지막_구간의_상행역이_전체_노선의_하행종점역이_된다() {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));
        SectionRequest sectionRequest = new SectionRequest(선릉역_ID, 왕십리역_ID, 10);
        addSection(lineResponse, sectionRequest);

        //when
        given()
            .pathParam("lineId", lineResponse.getId())
            .queryParam("stationId", 왕십리역_ID)
        .when()
            .delete("/lines/{lineId}/sections")
            .then().statusCode(HttpStatus.SC_NO_CONTENT);

        //then
        LineResponse lineResponseAfterRemoveSection = when().get("/lines/" + lineResponse.getId()).then().log().all().extract().jsonPath().getObject(".", LineResponse.class);
        List<SectionResponse> sectionsResponse = lineResponseAfterRemoveSection.getSections();

        assertAll(
            () -> assertThat(sectionsResponse).hasSize(1),
            () -> assertThat(sectionsResponse.get(sectionsResponse.size() - 1).getDownStationId()).isEqualTo(sectionRequest.getUpStationId())
        );
    }

    @Test
    void testDeleteSection_지하철_노선에_구간이_1개인_경우_역을_삭제할_수_없다() {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));

        //when & then
        given()
            .pathParam("lineId", lineResponse.getId())
            .queryParam("stationId", 선릉역_ID)
        .when()
            .delete("/lines/{lineId}/sections")
            .then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @DisplayName("given 특정 노선에 구간이 2개 이상 등록되었을 때\n"
                 + "   when 노선에 등록된 역 중 가운데 역을 제거하면\n"
                 + "   then 해당 역이 노선에서 제거되고\n"
                 + "        전후 구간이 하나의 구간으로 합쳐진다.")
    @Test
    void testDeleteSection_노선에_등록된_역_중_가운데_역을_제거하면_역이_제거된다() {
        // given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));
        int distance = 7;
        SectionRequest sectionRequest = new SectionRequest(선릉역_ID, 왕십리역_ID, distance);
        addSection(lineResponse, sectionRequest);

        // when
        given()
            .pathParam("lineId", lineResponse.getId())
            .queryParam("stationId", 선릉역_ID)
            .when()
            .delete("/lines/{lineId}/sections")
            .then().statusCode(HttpStatus.SC_NO_CONTENT);

        // then
        LineResponse lineResponseAfterDeleteSection = 지하철_노선_조회(lineResponse.getId());
        List<StationResponse> stations = lineResponseAfterDeleteSection.getStations();
        List<SectionResponse> sections = lineResponseAfterDeleteSection.getSections();
        assertAll(
            () -> assertThat(stations).hasSize(2),
            () -> assertThat(stations).extracting(StationResponse::getId).anySatisfy(id -> assertThat(id).isNotEqualTo(선릉역_ID)),
            () -> assertThat(sections).hasSize(1),
            () -> assertThat(sections.get(0).getUpStationId()).isEqualTo(역삼역_ID),
            () -> assertThat(sections.get(0).getDownStationId()).isEqualTo(왕십리역_ID),
            () -> assertThat(sections.get(0).getDistance()).isEqualTo(BASIC_DISTANCE + distance)
        );
    }

    @DisplayName("given 특정 노선에 구간이 2개 이상 등록되었을 때\n"
                 + "   when 노선의 상행종점역을 제거하면 \n"
                 + "   then 해당 노선의 상행종점역이 제거되고\n"
                 + "        그 다음 역이 전체 노선의 상행종점역이 된다.")
    @Test
    void testDeleteSection_노선에_등록된_역_중_상행종점역을_제거하면_역이_제거된다() {
        // given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));
        int distance = 7;
        SectionRequest sectionRequest = new SectionRequest(선릉역_ID, 왕십리역_ID, distance);
        addSection(lineResponse, sectionRequest);

        // when
        given()
            .pathParam("lineId", lineResponse.getId())
            .queryParam("stationId", 역삼역_ID)
            .when()
            .delete("/lines/{lineId}/sections")
            .then().statusCode(HttpStatus.SC_NO_CONTENT);

        // then
        LineResponse lineResponseAfterDeleteSection = 지하철_노선_조회(lineResponse.getId());
        List<StationResponse> stations = lineResponseAfterDeleteSection.getStations();
        List<SectionResponse> sections = lineResponseAfterDeleteSection.getSections();
        assertAll(
            () -> assertThat(stations).hasSize(2),
            () -> assertThat(stations).extracting(StationResponse::getId).anySatisfy(id -> assertThat(id).isNotEqualTo(역삼역_ID)),
            () -> assertThat(sections).hasSize(1),
            () -> assertThat(sections.get(0).getUpStationId()).isEqualTo(선릉역_ID),
            () -> assertThat(sections.get(0).getDownStationId()).isEqualTo(왕십리역_ID),
            () -> assertThat(sections.get(0).getDistance()).isEqualTo(distance)
        );
    }

    private void addSection(LineResponse lineResponse, SectionRequest sectionRequest) {
        given().body(sectionRequest)
            .pathParam("lineId", lineResponse.getId())
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .when().post("/lines/{lineId}/sections").then().log().all();
    }
}
