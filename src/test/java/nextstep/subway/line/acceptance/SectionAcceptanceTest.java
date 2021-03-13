package nextstep.subway.line.acceptance;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.acceptance.LineRequestBuilder.*;
import static nextstep.subway.station.StationRequestBuilder.*;


@DisplayName("지하철 노선구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {


  private long 광교역;
  private long 광교중앙역;
  private long 신분당선;
  private ExtractableResponse<Response> createLineResponse;

   @BeforeEach
   void init(){
    광교역 = 지하철역_생성_요청("광교역").body().jsonPath().getLong("id");
    광교중앙역 = 지하철역_생성_요청("광교중앙역").body().jsonPath().getLong("id");
    createLineResponse = 지하철_노선_생성요청("신분당선", LineColor.RED,광교역,광교중앙역);
    신분당선 = createLineResponse.body().jsonPath().getLong("id");
  }

  @DisplayName("노선에 구간을 등록하면 구간이함께 조회된다")
  @Test
  void findSectionByLine(){
     //given 구간등록
     구간등록요청(신분당선,광교역,광교중앙역,30);
     long 상현역 =  지하철역_생성_요청("상현역").body().jsonPath().getLong("id");
     long 성복역 =  지하철역_생성_요청("성복역").body().jsonPath().getLong("id");
     구간등록요청(신분당선,광교중앙역,상현역,30);
     구간등록요청(신분당선,상현역,성복역,30);
     //when 노선조회요청
     ExtractableResponse<Response> response = 지하철_노선조회_요청(신분당선);
     //then 노선조회됨
     지하철_노선에서_구간_조회됨(response);
  }

  @DisplayName("지하철 노선에 구간을 등록한다")
  @Test
  void createSection(){

    //when 지하철 노선에 구간등록 요청
    long 상현역 =  지하철역_생성_요청("상현역").body().jsonPath().getLong("id");
    long 성복역 =  지하철역_생성_요청("성복역").body().jsonPath().getLong("id");
    ExtractableResponse<Response> sectionResponse = 구간등록요청(신분당선,광교중앙역,상현역,30);
    구간등록요청(신분당선,상현역,성복역,30);

    //then 지하철 노선에 구간등록 완료됨
    노선_구간_등록됨(sectionResponse);
  }

  @DisplayName("신규구간의 상행역이 노선의 종점역이 아닌 구간을 등록한다")
  @Test
  void createSectionWithoutEndSection() {
    //given 지하철 노선 생성됨
    long 동천역 =  지하철역_생성_요청("동천역").body().jsonPath().getLong("id");
    구간등록요청(신분당선,광교역,광교중앙역,30);

    //when 지하철 노선에 구간등록 요청
    ExtractableResponse<Response> sectionResponse =  구간등록요청(신분당선,동천역,광교중앙역,30);

    //then 지하철 노선에 구간등록 완료됨
    노선_구간_등록실패됨(sectionResponse);
  }


  @DisplayName("신규구간의 상행역이 노선의 종점역이 아닌 구간을 등록한다")
  @Test
  void createSectionWithDuplicatedSection() {
    //given 지하철 노선 생성됨
    long 상현역 =  지하철역_생성_요청("상현역").body().jsonPath().getLong("id");
    구간등록요청(신분당선,광교역,광교중앙역,30);
    구간등록요청(신분당선,광교중앙역,상현역,30);

    //when 지하철 노선에 구간등록 요청
    ExtractableResponse<Response> sectionResponse =  구간등록요청(신분당선,상현역,광교중앙역,30);

    //then 지하철 노선에 구간등록 완료됨
    노선_구간_등록실패됨(sectionResponse);
  }


  @DisplayName("노선의 구간을 삭제한다")
  @Test
  void deleteSection(){

    //given 지하철 노선 생성됨
    구간등록요청(신분당선,광교역,광교중앙역,30);
    long 상현역 =  지하철역_생성_요청("상현역").body().jsonPath().getLong("id");
    long 성복역 =  지하철역_생성_요청("성복역").body().jsonPath().getLong("id");
    구간등록요청(신분당선,광교중앙역,상현역,30);
    구간등록요청(신분당선,상현역,성복역,30);

    //when 노선의 구간 삭제요청함
    ExtractableResponse<Response> removeResponse =  구간삭제요청(신분당선,성복역);


    //then 노선의 구간 삭제됨
    노선_구간_삭제됨(removeResponse);
  }

  @DisplayName("노선의 종점이 아닌 구간을 삭제한다")
  @Test
  void deleteWithNoLastSection(){

    //given 지하철 노선 생성됨
    구간등록요청(신분당선,광교역,광교중앙역,30);
    long 상현역 =  지하철역_생성_요청("상현역").body().jsonPath().getLong("id");
    long 성복역 =  지하철역_생성_요청("성복역").body().jsonPath().getLong("id");
    구간등록요청(신분당선,광교중앙역,상현역,30);
    구간등록요청(신분당선,상현역,성복역,30);

    //when 노선의 구간 삭제요청함
    ExtractableResponse<Response> removeResponse =  구간삭제요청(신분당선,상현역);


    //then 노선의 구간 삭제됨
    노선_구간_삭제실패됨(removeResponse);
  }

  @DisplayName("1개남은 노선의 구간을 삭제한다")
  @Test
  void deleteWithLastSection(){

    //given 지하철 노선 생성됨1
    구간등록요청(신분당선,광교역,광교중앙역,30);


    //when 노선의 구간 삭제요청함
    ExtractableResponse<Response> removeResponse =  구간삭제요청(신분당선,광교중앙역);


    //then 노선의 구간 삭제됨
    노선_구간_삭제실패됨(removeResponse);
  }


}
