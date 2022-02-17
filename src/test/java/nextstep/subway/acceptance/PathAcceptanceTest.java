package nextstep.subway.acceptance;

import static nextstep.subway.step.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.step.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.step.PathStep.경로_조회;
import static nextstep.subway.step.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.applicaion.dto.CreateLineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathAcceptanceTest extends AcceptanceTest {

    private Long 일호선;
    private Long 이호선;
    private Long 삼호선;
    private Long 사호선;
    private Long 오호선;

    private Long 교대역;
    private Long 강남역;
    private Long 역삼역;
    private Long 남부역;
    private Long 양재역;
    private Long 도곡역;
    private Long 미금역;
    private Long 판교역;

    /** test setting
     *         (사호선)            (오호선)
     * (일호선) [교대역] --- 10 --- [강남역] --- 5 --- [역삼역]
     *          ㅣ                 ㅣ
     *          9                 12
     *          ㅣ                 ㅣ
     * (이호선) [남부역] --- 3 --- [양재역] --- 6 --- [도곡역]
     *          ㅣ                 ㅣ
     *          11                15
     *          ㅣ                 ㅣ
     * (삼호선) [미금역] --- 20 --- [판교역]
     *
     */

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        남부역 = 지하철역_생성_요청("남부역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        도곡역 = 지하철역_생성_요청("도곡역").jsonPath().getLong("id");
        미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
        판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        CreateLineRequest lineOneRequest = new CreateLineRequest("1호선", "red", 교대역, 강남역, 10);
        일호선 = 지하철_노선_생성_요청(lineOneRequest).jsonPath().getLong("id");
        SectionRequest lineOne = new SectionRequest(강남역, 역삼역, 5);
        지하철_노선에_지하철_구간_생성_요청(일호선, lineOne);

        CreateLineRequest lineTwoRequest = new CreateLineRequest("2호선", "blue", 남부역, 양재역, 3);
        이호선 = 지하철_노선_생성_요청(lineTwoRequest).jsonPath().getLong("id");
        SectionRequest lineTwo = new SectionRequest(양재역, 도곡역, 6);
        지하철_노선에_지하철_구간_생성_요청(이호선, lineTwo);

        CreateLineRequest lineThreeRequest = new CreateLineRequest("3호선", "black", 미금역, 판교역, 20);
        삼호선 = 지하철_노선_생성_요청(lineThreeRequest).jsonPath().getLong("id");

        CreateLineRequest lineFourRequest = new CreateLineRequest("4호선", "green", 교대역, 남부역, 9);
        사호선 = 지하철_노선_생성_요청(lineFourRequest).jsonPath().getLong("id");
        SectionRequest lineFour = new SectionRequest(남부역, 미금역, 11);
        지하철_노선에_지하철_구간_생성_요청(사호선, lineFour);

        CreateLineRequest lineFiveRequest = new CreateLineRequest("5호선", "white", 강남역, 양재역, 12);
        오호선 = 지하철_노선_생성_요청(lineFiveRequest).jsonPath().getLong("id");
        SectionRequest lineFive = new SectionRequest(양재역, 판교역, 15);
        지하철_노선에_지하철_구간_생성_요청(오호선, lineFive);
    }

    @Test
    @DisplayName("최단 경로 조회")
    void shortestPath() {
        List<Long> stationsId = 경로_조회(교대역, 판교역).jsonPath().getList("stations");

        assertThat(stationsId).containsExactly(강남역, 양재역, 남부역, 미금역);
    }

}
