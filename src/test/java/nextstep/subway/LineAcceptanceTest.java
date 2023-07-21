package nextstep.subway;

import static nextstep.subway.JsonParser.아이디;
import static nextstep.subway.JsonParser.아이디_리스트;
import static nextstep.subway.JsonParser.이름;
import static nextstep.subway.JsonParser.이름_리스트;
import static nextstep.subway.JsonParser.컬러;
import static nextstep.subway.JsonParser.컬러_리스트;
import static nextstep.subway.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역이 노선 하행 종점인 구간을 등록하면
     * Then 구간의 하행역이 노선 하행 종점역으로 등록된다
     */
    @DisplayName("새로운 역을 하행 종점에 등록한다")
    @Test
    void insertSectionToDownTerminalStationSuccess() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 교대역_아이디 = 아이디(지하철역을_생성한다("교대역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);

        // when
        노선_구간을_등록한다(아이디(신분당선), 판교역_아이디, 교대역_아이디, 1);

        // then
        신분당선 = 지하철_노선_조회한다(아이디(신분당선));
        List<String> 스테이션_이름_리스트 = 스테이션_이름_리스트(신분당선);
        assertThat(스테이션_이름_리스트).containsExactly("강남역", "판교역", "교대역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 하행역이 노선 상행 종점인 구간을 등록하면
     * Then 구간의 상행역이 노선 상행 종점역으로 등록된다
     */
    @DisplayName("새로운 역을 상행 종점에 등록한다")
    @Test
    void insertSectionToUpTerminalStationSuccess() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 교대역_아이디 = 아이디(지하철역을_생성한다("교대역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);

        // when
        노선_구간을_등록한다(아이디(신분당선), 교대역_아이디, 강남역_아이디, 1);

        // then
        신분당선 = 지하철_노선_조회한다(아이디(신분당선));
        List<String> 스테이션_이름_리스트 = 스테이션_이름_리스트(신분당선);
        assertThat(스테이션_이름_리스트).containsExactly("교대역", "강남역", "판교역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역이 기존 구간의 상행역과 일치하고 똑같이 않은 구간을 등록하면
     * Then 기존 구간의 역을 기준으로 새로운 구간을 추가된다
     */
    @DisplayName("기존 구간의 역을 기준으로 새로운 구간을 추가")
    @Test
    void insertSectionToCenterSuccess() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 양재역_아이디 = 아이디(지하철역을_생성한다("양재역"));
        int 선릉역_아이디 = 아이디(지하철역을_생성한다("선릉역"));
        int 역삼역_아이디 = 아이디(지하철역을_생성한다("역삼역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);

        // when
        노선_구간을_등록한다(아이디(신분당선), 강남역_아이디, 양재역_아이디, 1);
        노선_구간을_등록한다(아이디(신분당선), 양재역_아이디, 역삼역_아이디, 3);
        노선_구간을_등록한다(아이디(신분당선), 양재역_아이디, 선릉역_아이디, 1);

        // then
        신분당선 = 지하철_노선_조회한다(아이디(신분당선));
        List<String> 스테이션_이름_리스트 = 스테이션_이름_리스트(신분당선);
        assertThat(스테이션_이름_리스트).containsExactly("강남역", "양재역", "선릉역", "역삼역", "판교역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역이 기존 구간의 상행역과 일치하고 똑같이 않은 구간을 등록할 경우
     * Then 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void insertSectionToCenterFailedByDistance() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 양재역_아이디 = 아이디(지하철역을_생성한다("양재역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);

        // when
        노선_구간을_등록한다(아이디(신분당선), 강남역_아이디, 양재역_아이디, 10);

        // then
        신분당선 = 지하철_노선_조회한다(아이디(신분당선));
        List<String> 스테이션_이름_리스트 = 스테이션_이름_리스트(신분당선);
        assertThat(스테이션_이름_리스트).containsExactly("강남역", "판교역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 역이 모두 포함한 구간을 등록할 경우
     * Then 등록을 할 수 없음
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void insertSectionToCenterFailedByExists() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 양재역_아이디 = 아이디(지하철역을_생성한다("양재역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        노선_구간을_등록한다(아이디(신분당선), 강남역_아이디, 양재역_아이디, 4);

        // when
        노선_구간을_등록한다(아이디(신분당선), 강남역_아이디, 판교역_아이디, 3);

        // then
        신분당선 = 지하철_노선_조회한다(아이디(신분당선));
        List<String> 스테이션_이름_리스트 = 스테이션_이름_리스트(신분당선);
        assertThat(스테이션_이름_리스트).containsExactly("강남역", "양재역", "판교역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 역이 모두 포함되어 있지 않는 구간을 등록할 경우
     * Then 등록을 할 수 없음
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void insertSectionToCenterFailedByNotExists() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 양재역_아이디 = 아이디(지하철역을_생성한다("양재역"));
        int 교대역_아이디 = 아이디(지하철역을_생성한다("교대역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);

        // when
        노선_구간을_등록한다(아이디(신분당선), 양재역_아이디, 교대역_아이디, 3);

        // then
        신분당선 = 지하철_노선_조회한다(아이디(신분당선));
        List<String> 스테이션_이름_리스트 = 스테이션_이름_리스트(신분당선);
        assertThat(스테이션_이름_리스트).containsExactly("강남역", "판교역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createSubwayLine() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));

        // when
        var 등록된_지하철노선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);

        // then
        assertAll(
                () -> assertThat(아이디(등록된_지하철노선)).isNotNull(),
                () -> assertThat(이름(등록된_지하철노선)).isEqualTo("신분당선"),
                () -> assertThat(컬러(등록된_지하철노선)).isEqualTo("bg-red-600"),
                () -> assertThat(스테이션_아이디_리스트(등록된_지하철노선)).contains(강남역_아이디, 판교역_아이디),
                () -> assertThat(스테이션_이름_리스트(등록된_지하철노선)).contains("강남역", "판교역")
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void showLines() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 사당역_아이디 = 아이디(지하철역을_생성한다("사당역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        var _2호선 = 지하철_노선_등록한다(
                "2호선",
                "bg-red-500",
                강남역_아이디,
                사당역_아이디,
                5);

        // when
        var 지하철_노선_목록 = 지하철_노선_목록_조회한다();

        // then
        assertAll(
                () -> assertThat(아이디_리스트(지하철_노선_목록)).contains(아이디(신분당선), 아이디(_2호선)),
                () -> assertThat(이름_리스트(지하철_노선_목록)).contains("신분당선", "2호선"),
                () -> assertThat(컬러_리스트(지하철_노선_목록)).contains("bg-red-600", "bg-red-500"),
                () -> assertThat(스테이션_아이디_리스트(지하철_노선_목록, 0)).contains(강남역_아이디, 판교역_아이디),
                () -> assertThat(스테이션_이름_리스트(지하철_노선_목록, 0)).contains("강남역", "판교역"),
                () -> assertThat(스테이션_아이디_리스트(지하철_노선_목록, 1)).contains(강남역_아이디, 사당역_아이디),
                () -> assertThat(스테이션_이름_리스트(지하철_노선_목록, 1)).contains("강남역", "사당역")
        );
    }

    private static ExtractableResponse<Response> 지하철_노선_조회한다(int 지하철노선_아이디) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + 지하철노선_아이디)
                .then().log().all()
                .extract();
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void searchLine() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        int 신분당선_아이디 = 아이디(신분당선);

        // when
        var 조회된_지하철노선 = 지하철_노선_조회한다(신분당선_아이디);

        // then
        assertAll(
                () -> assertThat(아이디(조회된_지하철노선)).isEqualTo(신분당선_아이디),
                () -> assertThat(이름(조회된_지하철노선)).isEqualTo("신분당선"),
                () -> assertThat(컬러(조회된_지하철노선)).isEqualTo("bg-red-600"),
                () -> assertThat(스테이션_아이디_리스트(조회된_지하철노선)).contains(강남역_아이디, 판교역_아이디),
                () -> assertThat(스테이션_이름_리스트(조회된_지하철노선)).contains("강남역", "판교역")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLine() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        int 수정될_지하철_노선_아이디 = 아이디(신분당선);

        // when
        지하철_노선_수정한다(수정될_지하철_노선_아이디, "다른분당선", "bg-red-500");

        // then
        var 수정된_지하철_노선 = 지하철_노선_조회한다(수정될_지하철_노선_아이디);
        assertAll(
                () -> assertThat(이름(수정된_지하철_노선)).isEqualTo("다른분당선"),
                () -> assertThat(컬러(수정된_지하철_노선)).isEqualTo("bg-red-500")
        );
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLne() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        int 신분당선_아이디 = 아이디(신분당선);

        // when
        지하철_노선_삭제한다(신분당선_아이디);

        // then
        var 지하철_노선_목록 = 지하철_노선_목록_조회한다();
        assertAll(
                () -> assertThat(아이디_리스트(지하철_노선_목록)).doesNotContain(신분당선_아이디),
                () -> assertThat(이름_리스트(지하철_노선_목록)).doesNotContain("신분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역이 생성한 노선의 하행 종점역이고 하행역 해당 노선에 등록되어 있지 않으면 지하철 구간을 등록하면
     * Then 해당 지하철 구간이 등록된다
     */
    @DisplayName("구간 등록 성공")
    @Test
    void registerSectionSuccess() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 정자역_아이디 = 아이디(지하철역을_생성한다("정자역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        int 신분당선_아이디 = 아이디(신분당선);

        // when
        노선_구간을_등록한다(신분당선_아이디, 판교역_아이디, 정자역_아이디, 1);

        // then
        var 변경된_신분당선 = 지하철_노선_조회한다(신분당선_아이디);
        assertAll(
                () -> assertThat(스테이션_아이디_리스트(변경된_신분당선))
                        .contains(강남역_아이디, 판교역_아이디, 정자역_아이디),
                () -> assertThat(스테이션_이름_리스트(변경된_신분당선))
                        .contains("강남역", "판교역", "정자역")
        );
    }


    /**
     * Given 지하철 노선을 생성하고
     * Given 구간도 추가하고
     * When 여러구간의 하행 종점역을 제거하면
     * Then 마지막 구간이 제거된다
     */
    @DisplayName("하행 종점역 제거 성공")
    @Test
    void removeBottomSectionSuccess() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 정자역_아이디 = 아이디(지하철역을_생성한다("정자역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        int 신분당선_아이디 = 아이디(신분당선);
        노선_구간을_등록한다(신분당선_아이디, 판교역_아이디, 정자역_아이디, 1);

        // when
        var 노선에서_구간_제거_결과 = 노선에서_구간_제거한다(정자역_아이디, 신분당선_아이디);

        // then
        var 조회된_신분당선 = 지하철_노선_조회한다(신분당선_아이디);
        assertAll(
                () -> assertThat(노선에서_구간_제거_결과.statusCode()).isEqualTo(
                        HttpStatus.NO_CONTENT.value()),
                () -> assertThat(스테이션_이름_리스트(조회된_신분당선)).doesNotContain("정자역"),
                () -> assertThat(스테이션_아이디_리스트(조회된_신분당선)).containsExactly(강남역_아이디, 판교역_아이디)
        );
    }


    /**
     * Given 지하철 노선을 생성하고
     * Given 구간도 추가하고
     * When 여러구간의 상행 중간역을 제거하면
     * Then 상행 종점역 구간이 제거된다
     */
    @DisplayName("상행 종점역 제거 성공")
    @Test
    void removeTopSectionSuccess() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 양재역_아이디 = 아이디(지하철역을_생성한다("양재역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        int 신분당선_아이디 = 아이디(신분당선);
        노선_구간을_등록한다(신분당선_아이디, 양재역_아이디, 판교역_아이디, 8);

        // when
        var 노선에서_구간_제거_결과 = 노선에서_구간_제거한다(강남역_아이디, 신분당선_아이디);

        // then
        var 조회된_신분당선 = 지하철_노선_조회한다(신분당선_아이디);
        assertAll(
                () -> assertThat(노선에서_구간_제거_결과.statusCode()).isEqualTo(
                        HttpStatus.NO_CONTENT.value()),
                () -> assertThat(스테이션_이름_리스트(조회된_신분당선)).containsExactly("양재역", "판교역"),
                () -> assertThat(스테이션_아이디_리스트(조회된_신분당선)).containsExactly(양재역_아이디, 판교역_아이디)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * Given 구간도 추가하고
     * When 종점이 아닌 역을 제거하면
     * Then 재비치 한다
     */
    @DisplayName("하행 종점역이 아닌 중간역을 제거될 경우 재비치를 한다")
    @Test
    void removeCenterStationSuccess() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        int 정자역_아이디 = 아이디(지하철역을_생성한다("정자역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        int 신분당선_아이디 = 아이디(신분당선);
        노선_구간을_등록한다(신분당선_아이디, 판교역_아이디, 정자역_아이디, 1);

        // when
        노선에서_구간_제거한다(판교역_아이디, 신분당선_아이디);

        // then
        var 조회된_신분당선 = 지하철_노선_조회한다(신분당선_아이디);
        assertAll(
                () -> assertThat(스테이션_이름_리스트(조회된_신분당선)).containsExactly("강남역", "정자역"),
                () -> assertThat(스테이션_아이디_리스트(조회된_신분당선)).contains(강남역_아이디, 정자역_아이디)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 구간이 하나인 노선의 하행 종점역을 역을 제거하면
     * Then 400 코드 응답
     */
    @DisplayName("구간이 하나여서 제거 실패")
    @Test
    void removeSectionFailedByOnyOneSection() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        int 신분당선_아이디 = 아이디(신분당선);

        // when
        var 노선에서_구간_제거_결과 = 노선에서_구간_제거한다(판교역_아이디, 신분당선_아이디);

        // then
        var 조회된_신분당선 = 지하철_노선_조회한다(신분당선_아이디);
        assertAll(
                () -> assertThat(노선에서_구간_제거_결과.statusCode()).isEqualTo(
                        HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(스테이션_이름_리스트(조회된_신분당선)).contains("판교역"),
                () -> assertThat(스테이션_아이디_리스트(조회된_신분당선)).contains(판교역_아이디)
        );
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 국간에 포함하지 않는 역을 제거 하면
     * Then 400 코드 응답
     */
    @DisplayName("구간이 포함 되지않은 역을 제거 시 예외 발생")
    @Test
    void removeSectionFailedByNotIncluded() {
        // given
        int 강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        int 판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        var 신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                10);
        int 신분당선_아이디 = 아이디(신분당선);
        int 교대역_아이디 = 아이디(지하철역을_생성한다("교대역"));

        // when
        var 노선에서_구간_제거_결과 = 노선에서_구간_제거한다(교대역_아이디, 신분당선_아이디);

        // then
        assertAll(
                () -> assertThat(노선에서_구간_제거_결과.statusCode()).isEqualTo(
                        HttpStatus.BAD_REQUEST.value())
        );
    }

    private static ExtractableResponse<Response> 지하철_노선_등록한다(String 노선이름, String 노선색상,
            int 상행종점역, int 하행종점역, int 거리) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", 노선이름);
        params.put("color", 노선색상);
        params.put("upStationId", 상행종점역);
        params.put("downStationId", 하행종점역);
        params.put("distance", 거리);

        return RestAssured
                .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(params).log()
                .all()
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 지하철_노선_목록_조회한다() {
        return RestAssured
                .given().accept(ContentType.JSON).log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 지하철_노선_수정한다(int 수정될_지하철_노선_아이디, String 수정될_이름,
            String 수정될_컬러) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", 수정될_이름);
        params.put("color", 수정될_컬러);

        return RestAssured
                .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(params)
                .when().put("/lines/" + 수정될_지하철_노선_아이디)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 지하철_노선_삭제한다(int 신분당선_아이디) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/" + 신분당선_아이디)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 노선_구간을_등록한다(int 노선_아이디, int 상행역, int 하행역,
            int 거리) {
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", 하행역);
        params.put("upStationId", 상행역);
        params.put("distance", 거리);
        return RestAssured
                .given().body(params).log().all()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when().post("/lines/" + 노선_아이디 + "/sections")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 노선에서_구간_제거한다(int 종점역_아이디, int 노선_아이디) {
        return RestAssured
                .given().log().all().param("stationId", 종점역_아이디)
                .when().delete("/lines/" + 노선_아이디 + "/sections")
                .then().log().all()
                .extract();
    }

    private static List<String> 스테이션_이름_리스트(ExtractableResponse<Response> 지하철_노선_목록, int 위치) {
        return 지하철_노선_목록.jsonPath().getList("stations[" + 위치 + "].name", String.class);
    }

    private static List<Integer> 스테이션_아이디_리스트(ExtractableResponse<Response> 지하철_노선_목록, int 위치) {
        return 지하철_노선_목록.jsonPath().getList("stations[" + 위치 + "].id", Integer.class);
    }

    private static List<String> 스테이션_이름_리스트(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.name", String.class);
    }

    private static List<Integer> 스테이션_아이디_리스트(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Integer.class);
    }
}
