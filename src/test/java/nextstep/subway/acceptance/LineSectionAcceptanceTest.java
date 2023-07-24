package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.response.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.steps.LineSteps.*;
import static nextstep.subway.acceptance.steps.SectionSteps.지하철_노선_구간_등록;
import static nextstep.subway.acceptance.steps.SectionSteps.지하철_노선_구간_제거_요청;
import static nextstep.subway.acceptance.steps.StationSteps.createStationAndGetInfo;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private static final int DEFAULT_DISTANCE = 10;
    private Long 신분당선, 신사역, 논현역, 신논현역, 강남역, 광교역;

    @BeforeEach
    void setting(){
        신사역 = createStationAndGetInfo("신사역").getId();
        논현역 = createStationAndGetInfo("논현역").getId();
        신논현역 = createStationAndGetInfo("신논현역").getId();
        강남역 = createStationAndGetInfo("강남역").getId();
        광교역 = createStationAndGetInfo("광교역").getId();

        신분당선 = createLine("신분당선", "bg-red-600", 논현역, 강남역, DEFAULT_DISTANCE)
                                                                                            .jsonPath()
                                                                                            .getObject("id",Long.class);

    }

    /**
     * When 구간을 생성한다. - 노선의 상행 종점역 구간을 등록할 때
     * Then 생성한 노선의 상행 종점역 변경된다.
     * */
    @DisplayName("구간 등록. 노선의 상행 종점역 구간을 등록")
    @Test
    void createFirstSectionTest() {
        ExtractableResponse<Response> sectionResponse = 지하철_노선_구간_등록(신분당선, 신사역, 논현역, DEFAULT_DISTANCE);

        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<StationResponse> list = 지하철_노선_역_정보_조회();

        assertThat(list).hasSize(3);
    }

    /**
     * When 구간을 생성한다. - 노선의 하행 종점역 구간을 등록할 때
     * Then 생성한 노선의 히행 종점역 변경된다.
     * */
    @DisplayName("구간 등록. 노선의 하행 종점역 구간을 등록")
    @Test
    void createLastSectionTest() {
        ExtractableResponse<Response> sectionResponse = 지하철_노선_구간_등록(신분당선, 강남역, 광교역, DEFAULT_DISTANCE);

        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<StationResponse> list = 지하철_노선_역_정보_조회();

        assertThat(list).hasSize(3);
    }

    /**
     * When 구간을 생성한다. - 노선의 기존 구간 사이의 구간을 등록할 때
     * Then 생성한 노선의 역 목록 순서가 변경된다.
     * */
    @DisplayName("구간 등록. 기존 구간의 사이에 새로운 구간 등록")
    @Test
    void createMiddleSectionTest() {
        ExtractableResponse<Response> sectionResponse = 지하철_노선_구간_등록(신분당선, 논현역, 신논현역, DEFAULT_DISTANCE-1);
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> stationIds = 지하철_노선_역_아이디_조회();

        assertThat(stationIds).hasSize(3);
        assertThat(stationIds).containsExactly(논현역, 신논현역, 강남역);
    }

    /**
     * When 구간을 생성한다. - 기존 구간의 길이보다 긴 구간을 등록할 때
     * Then 에러를 던진다.
     * */
    @DisplayName("구간 등록. 기존 구간의 길이보다 긴 새로운 구간을 추가할 경우 에러")
    @Test
    void createMiddleSectionExceptionWhenToLongDistance() {
        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_등록(신분당선, 논현역, 신논현역,DEFAULT_DISTANCE+1);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 구간을 생성한다. - 구간의 상행선과 하행선 모두 노선의 포함되지 않은 구간을 등록할 때
     * Then 에러를 던진다.
     * */
    @DisplayName("구간 등록. 구간의 상행역과 하행역이 노선에 등록되어 있지 않은 경우에 에러")
    @Test
    void createSectionExceptionWhenNotMachUpStation() {
        ExtractableResponse<Response> response = 지하철_노선_구간_등록(신분당선, 신사역, 광교역, DEFAULT_DISTANCE);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 구간을 생성한다. - 이미 노선에 등록된 구간을 등록할 때
     * Then 에러를 던진다.
     * */
    @DisplayName("구간 등록. 해당 노선에 등록되어있는 노선일 경우에 에러")
    @Test
    void createSectionExceptionWhenAlreadyRegistered() {
        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_등록(신분당선, 논현역, 강남역, DEFAULT_DISTANCE);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * Given 구간을 생성한다.
     * When 구간을 제거한다. - 노선의 첫 구간을 제거할 때
     * Then 구간이 제거된다.
     * */
    @DisplayName("구간 제거 - 노선의 첫 구간을 제거")
    @Test
    void removeFirstSection() {
        // given
        Long 광교역 = createStationAndGetInfo("광교역").getId();
        지하철_노선_구간_등록(신분당선, 강남역, 광교역, DEFAULT_DISTANCE);

        // when
        지하철_노선_구간_제거_요청(신분당선, 논현역);

        // then
        List<Long> stationIds = 지하철_노선_역_아이디_조회();
        assertThat(stationIds).containsExactly(강남역,광교역);
    }

    /**
     * Given 구간을 생성한다.
     * When 구간을 제거한다. - 노선의 중간 구간을 제거할 때.
     * Then 구간이 제거된다.
     * */
    @DisplayName("구간 제거 - 노선의 중간 구간 제거")
    @Test
    void removeMiddleSection() {
        // given
        Long 광교역 = createStationAndGetInfo("광교역").getId();
        지하철_노선_구간_등록(신분당선, 강남역, 광교역, DEFAULT_DISTANCE);

        // when
        지하철_노선_구간_제거_요청(신분당선, 강남역);

        // then
        List<Long> stationIds = 지하철_노선_역_아이디_조회();
        assertThat(stationIds).containsExactly(논현역,광교역);
    }

    /**
     * Given 구간을 생성한다.
     * When 구간을 제거한다. - 노선의 마지막 구간을 제거할 때.
     * Then 구간이 제거된다.
     * */
    @DisplayName("구간 제거 - 마지막 구간 제거")
    @Test
    void removeLastSection() {
        // given
        Long 광교역 = createStationAndGetInfo("광교역").getId();
        지하철_노선_구간_등록(신분당선, 강남역, 광교역, DEFAULT_DISTANCE);

        // when
        지하철_노선_구간_제거_요청(신분당선, 광교역);

        // then
        List<Long> stationIds = 지하철_노선_역_아이디_조회();
        assertThat(stationIds).containsExactly(논현역,강남역);
    }

    /**
     * Given 구간을 생성한다.
     * When 구간을 제거한다. -해당 노선의 등록되지 않은 역을 삭제할 때
     * Then 에러를 던진다.
     * */
    @DisplayName("구간 제거. 해당 노선에 등록되지 않은 구간인 경우 에러")
    @Test
    public void removeLineSectionExceptionWhenNotMachLastSections(){
        // given
        Long 광교역 = createStationAndGetInfo("광교역").getId();
        지하철_노선_구간_등록(신분당선, 강남역, 광교역, DEFAULT_DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_제거_요청(신분당선, 신논현역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 구간을 제거한다. - 구간이 1개 이하인 노선의 구간을 제거할 때
     * Then 에러를 던진다.
     * */
    @DisplayName("구간 제거. 해당 노선의 구간이 1개인 경우 에러")
    @Test
    public void removeLineSectionExceptionWhenSectionsSizeOne(){
        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_제거_요청(신분당선, 강남역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}