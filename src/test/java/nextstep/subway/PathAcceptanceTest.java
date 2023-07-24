package nextstep.subway;

import static nextstep.subway.JsonParser.아이디;
import static nextstep.subway.LineAcceptanceUtils.경로_조회;
import static nextstep.subway.LineAcceptanceUtils.경로_조회_결과_검증;
import static nextstep.subway.LineAcceptanceUtils.노선_구간을_등록한다;
import static nextstep.subway.LineAcceptanceUtils.지하철_노선_등록한다;
import static nextstep.subway.StationAcceptanceTest.지하철역을_생성한다;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest {

    public static final int 강남역에서_판교역까지의_거리 = 10;
    public static final int 강남역에서_양재역까지의_거리 = 1;
    public static final int 도곡역에서_수서역까지의_거리_3호선 = 5;
    public static final int 도곡역에서_수서역까지의_거리_분당선 = 4;
    public static final int 강남역에서_수서역까지의_최단거리 = 7;

    int 강남역_아이디;
    int 판교역_아이디;
    int 수서역_아이디;
    ExtractableResponse<Response> 신분당선;


    /**
     * 강남역    --- *2호선* --- 선릉역
     * |                        |
     * *신분당선*               *분당선*
     * |                        |
     * 양재역    --- *3호선* --- 도곡역  --- *3호선* ---수서역
     *                          |                  |
     *                              --- *분당선* ---
     */
    @BeforeEach
    public void setUp() {
        강남역_아이디 = 아이디(지하철역을_생성한다("강남역"));
        판교역_아이디 = 아이디(지하철역을_생성한다("판교역"));
        신분당선 = 지하철_노선_등록한다(
                "신분당선",
                "bg-red-600",
                강남역_아이디,
                판교역_아이디,
                강남역에서_판교역까지의_거리);

        int 양재역_아이디 = 아이디(지하철역을_생성한다("양재역"));
        int 신분당선_아이디 = 아이디(신분당선);
        노선_구간을_등록한다(신분당선_아이디, 강남역_아이디, 양재역_아이디, 강남역에서_양재역까지의_거리);

        int 도곡역_아이디 = 아이디(지하철역을_생성한다("도곡역"));
        var _3호선 = 지하철_노선_등록한다("3호선",
                "#82C341",
                양재역_아이디,
                도곡역_아이디,
                2
        );
        수서역_아이디 = 아이디(지하철역을_생성한다("수서역"));
        노선_구간을_등록한다(아이디(_3호선), 도곡역_아이디, 수서역_아이디, 도곡역에서_수서역까지의_거리_3호선);

        int 선릉역_아이디 = 아이디(지하철역을_생성한다("선릉역"));
        지하철_노선_등록한다("2호선",
                "#0052A4",
                강남역_아이디,
                선릉역_아이디,
                2
        );

        var 분당선 = 지하철_노선_등록한다("분당선",
                "#82C341",
                선릉역_아이디,
                도곡역_아이디,
                2
        );
        노선_구간을_등록한다(아이디(분당선), 도곡역_아이디, 수서역_아이디, 도곡역에서_수서역까지의_거리_분당선);
    }


    /**
     * Given 여러 지하철 노선을 생성하고
     * When 출발역과 도착역의 경로를 조회하면
     * Then 출발역으로부터 도착역까지의 경로에 있는 역 목록과 조회한 경로 구간의 거리를 응답한다
     */
    @DisplayName("역과 역 최단 경로 조회")
    @Test
    void findShortestPathBetweenStations() {
        // when
        var 경로_조회_결과 = 경로_조회(강남역_아이디, 수서역_아이디);

        // then
        List<String> 예상_스테이션_이름_리스트 = List.of("강남역", "양재역", "도곡역", "수서역");
        경로_조회_결과_검증(경로_조회_결과, 예상_스테이션_이름_리스트, 강남역에서_수서역까지의_최단거리);
    }
}
