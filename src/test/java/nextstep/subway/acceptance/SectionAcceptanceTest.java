package nextstep.subway.acceptance;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;

@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
public class SectionAcceptanceTest extends BaseAcceptanceTest {
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        지하철_역_생성(역삼역);
        지하철_역_생성(선릉역);
        지하철_역_생성(강남역);
        지하철_역_생성(왕십리역);
    }

    @Test
    void test_특정_노선에_구간을_등록하면_노선_조회시_등록한_구간을_확인할_수_있다() throws JsonProcessingException {
        //when
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선());
        //then
        LineResponse response = when()
            .get("/lines/" + lineResponse.getId())
            .then().log().all().extract().jsonPath().getObject(".", LineResponse.class);
        System.out.println(mapper.writeValueAsString(response));
        List<SectionResponse> sectionsResponse = response.getSections();
        assertAll(
            () -> assertThat(sectionsResponse).hasSize(1),
            () -> assertThat(sectionsResponse.get(0).getUpStationId()).isEqualTo(1),
            () -> assertThat(sectionsResponse.get(0).getDownStationId()).isEqualTo(2),
            () -> assertThat(sectionsResponse.get(0).getDistance()).isEqualTo(10)
        );
    }

    @Test
    void 노선이_주어졌을때_해당_노선의_하행_종점역과_새로_등록하려는_구간의_상행_종점역이_같으면_해당_구간을_마지막_구간에_등록할_수_있다() throws JsonProcessingException {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선());

        //when
        SectionRequest sectionRequest = new SectionRequest(2L, 4L, 10);
        addSection(lineResponse, sectionRequest);

        //then
        LineResponse lineAfterResponse = when().get("/lines/" + lineResponse.getId())
                                               .then().log().all().extract().jsonPath().getObject(".", LineResponse.class);
        List<SectionResponse> sectionResponses = lineAfterResponse.getSections();
        assertAll(
            () -> assertThat(sectionResponses).hasSize(2),
            () -> assertThat(sectionResponses.get(sectionResponses.size() - 1).getUpStationName()).isEqualTo("선릉역"),
            () -> assertThat(sectionResponses.get(sectionResponses.size() - 1).getDownStationName()).isEqualTo("왕십리역"),
            () -> assertThat(sectionResponses.get(sectionResponses.size() - 1).getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("given 특정 노선에 구간이 1개 이상 등록되었을 때\n"
                 + "   when 기존 구간 중 특정 구간의 상행역과 등록하려는 구간의 상행역이 같고 \n"
                 + "        등록하려는 구간의 길이가 특정 구간의 길이보다 짧으면\n"
                 + "   then 기존 구간 사이에 새 구간이 등록된다.")
    @Test
    void testAddSection_지하철_구간_중간에_역_추가() throws JsonProcessingException {
        //given
        int existingDistance = 10;
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선());
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, existingDistance);
        addSection(lineResponse, sectionRequest);

        //when
        Long newStationId = 2L;
        int newDistance = 4;
        SectionRequest newSectionRequest = new SectionRequest(1L, newStationId, newDistance);
        addSection(lineResponse, newSectionRequest);

        //then
        LineResponse lineResponseAfterAddSection = 지하철_노선_조회(lineResponse.getId());

        List<SectionResponse> sections = lineResponseAfterAddSection.getSections();
        SectionResponse firstSection = sections.get(0);
        SectionResponse lastSection = sections.get(sections.size() - 1);
        assertAll(
            () -> assertThat(sections).hasSize(2),
            () -> assertThat(firstSection.getDownStationId()).isEqualTo(2L),
            () -> assertThat(firstSection.getDistance()).isEqualTo(newDistance),
            () -> assertThat(lastSection.getUpStationId()).isEqualTo(2L),
            () -> assertThat(lastSection.getUpStationId()).isEqualTo(2L),
            () -> assertThat(lastSection.getDistance()).isEqualTo(existingDistance - newDistance)
        );
    }

    @DisplayName("given 특정 노선에 구간이 1개 이상 등록되었을 때\n"
                 + "   when 새 역을 상행종점역으로 등록하면 \n"
                 + "   then 해당 노선의 상행종점역이 변경된다.")
    @Test
    void testAddSection_상행종점역_추가() throws JsonProcessingException {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선());

        //when
        SectionRequest sectionRequest = new SectionRequest(4L, 1L, 5);
        addSection(lineResponse, sectionRequest);

        //then
        LineResponse lineAfterResponse = when().get("/lines/" + lineResponse.getId())
                                               .then().log().all().extract().jsonPath().getObject(".", LineResponse.class);
        List<SectionResponse> sectionResponses = lineAfterResponse.getSections();
        assertAll(
            () -> assertThat(sectionResponses).hasSize(2),
            () -> assertThat(sectionResponses.get(0).getUpStationId()).isEqualTo(4L),
            () -> assertThat(sectionResponses.get(0).getDownStationId()).isEqualTo(1L),
            () -> assertThat(sectionResponses.get(0).getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("given 특정 노선에 구간이 1개 이상 등록되었을 때\n"
                 + "   when 등록하려는 역이 기존 노선에 있다면\n"
                 + "   then 예외를 반환한다.")
    @Test
    void testAddSection_구간_추가시_기존에_등록되어있으면_예외_반환() throws JsonProcessingException {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선());
        SectionRequest sectionRequest1 = new SectionRequest(2L, 3L, 5);
        addSection(lineResponse, sectionRequest1);
        //when
        SectionRequest sectionRequest2 = new SectionRequest(3L, 2L, 7);

        given().body(mapper.writeValueAsString(sectionRequest2))
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .when().post("/lines/" + lineResponse.getId() + "/sections").then().statusCode(HttpStatus.SC_BAD_REQUEST);

    }

    @Test
    void 해당_노선의_하행_종점역과_새로_등록하려는_구간의_상행_종점역이_다를_때_등록하려는_구간의_길이가_구간_사이에_들어올_수_없으면_에러를_반환한다() throws JsonProcessingException {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선());

        //when
        SectionRequest sectionRequest = new SectionRequest(1L, 4L, 15);
        given().body(mapper.writeValueAsString(sectionRequest))
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .when().post("/lines/" + lineResponse.getId() + "/sections").then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void 노선에_등록된_구간이_2개_이상_있을때_마지막_구간을_제거하면_노선_조회시_제거된_마지막_구간의_상행역이_전체_노선의_하행종점역이_된다() throws JsonProcessingException {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선());
        SectionRequest sectionRequest = new SectionRequest(2L, 4L, 10);
        addSection(lineResponse, sectionRequest);

        //when
        when()
            .delete("/lines/" + lineResponse.getId() + "/sections?stationId=" + 4L)
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
    void 노선에_등록된_구간이_2개_이상_있을때_요청한_역이_기존_노선의_하행종점역과_다르면_구간을_삭제할_수_없다() {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선());

        //when & then
        when()
            .delete("/lines/" + lineResponse.getId() + "/sections?stationId=" + 4L)
            .then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void 지하철_노선에_상행_종점역과_하행_종점역만_있는_경우_구간이_1개인_경우_역을_삭제할_수_없다() {
        //given
        LineResponse lineResponse = 지하철_노선_생성(getRequestParam_신분당선());

        //when & then
        when()
            .delete("/lines/" + lineResponse.getId() + "/sections?stationId=" + 2L)
            .then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    private void addSection(LineResponse lineResponse, SectionRequest sectionRequest) throws JsonProcessingException {
        given().body(mapper.writeValueAsString(sectionRequest))
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .when().post("/lines/" + lineResponse.getId() + "/sections").then().log().all();
    }
}
