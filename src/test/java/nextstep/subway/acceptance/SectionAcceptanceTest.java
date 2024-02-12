package nextstep.subway.acceptance;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;

@DirtiesContext(classMode = BEFORE_CLASS)
public class SectionAcceptanceTest extends BaseAcceptanceTest {
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        createStation(역삼역);
        createStation(선릉역);
        createStation(강남역);
        createStation(왕십리역);
    }

    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    @Test
    void test_특정_노선에_구간을_등록하면_노선_조회시_등록한_구간을_확인할_수_있다() {
        //when
        LineResponse lineResponse = createLine(getRequestParam_신분당선());
        //then
        LineResponse response = when()
            .get("/lines/" + lineResponse.getId())
            .then().log().all().extract().jsonPath().getObject(".", LineResponse.class);
        List<SectionResponse> sectionsResponse = response.getSections();
        assertAll(
            () -> assertThat(sectionsResponse).hasSize(1),
            () -> assertThat(sectionsResponse.get(0).getUpStationId()).isEqualTo(1),
            () -> assertThat(sectionsResponse.get(0).getDownStationId()).isEqualTo(2),
            () -> assertThat(sectionsResponse.get(0).getDistance()).isEqualTo(10)
        );
    }

    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    @Test
    void 노선이_주어졌을때_해당_노선의_하행_종점역과_새로_등록하려는_구간의_상행_종점역이_같으면_해당_구간을_등록할_수_있다() throws JsonProcessingException {
        //given
        LineResponse lineResponse = createLine(getRequestParam_신분당선());

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

    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    @Test
    void 노선이_주어졌을때_해당_노선의_하행_종점역과_새로_등록하려는_구간의_상행_종점역이_다르면_에러를_반환한다() throws JsonProcessingException {
        //given
        LineResponse lineResponse = createLine(getRequestParam_신분당선());

        //when
        SectionRequest sectionRequest = new SectionRequest(1L, 4L, 10);
        given().body(mapper.writeValueAsString(sectionRequest))
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .when().post("/lines/" + lineResponse.getId() + "/sections").then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    @Test
    void 노선에_등록된_구간이_2개_이상_있을때_마지막_구간을_제거하면_노선_조회시_제거된_마지막_구간의_상행역이_전체_노선의_하행종점역이_된다() throws JsonProcessingException {
        //given
        LineResponse lineResponse = createLine(getRequestParam_신분당선());
        SectionRequest sectionRequest = new SectionRequest(2L, 4L, 10);
        addSection(lineResponse, sectionRequest);

        //when
        when()
            .delete("/lines/" + lineResponse.getId() + "/sections")
            .then().statusCode(HttpStatus.SC_NO_CONTENT);

        //then
        LineResponse lineResponseAfterRemoveSection = when().get("/lines/" + lineResponse.getId()).then().log().all().extract().jsonPath().getObject(".", LineResponse.class);
        List<SectionResponse> sectionsResponse = lineResponseAfterRemoveSection.getSections();

        assertAll(
            () -> assertThat(sectionsResponse).hasSize(1),
            () -> assertThat(sectionsResponse.get(sectionsResponse.size() - 1).getDownStationId()).isEqualTo(sectionRequest.getUpStationId())
        );
    }

    private void addSection(LineResponse lineResponse, SectionRequest sectionRequest) throws JsonProcessingException {
        given().body(mapper.writeValueAsString(sectionRequest))
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .when().post("/lines/" + lineResponse.getId() + "/sections").then().log().all();
    }

}
