package subway.acceptance.path;

import subway.acceptance.line.LineFixture;
import subway.acceptance.line.LineSteps;
import subway.acceptance.line.SectionFixture;

import static subway.acceptance.station.StationFixture.getStationId;

public class PathFixture {

    public static void 이호선_삼호선_신분당선_A호선_생성() {
        var 이호선_요청 = LineFixture.generateLineCreateRequest("2호선", "bg-green-600", getStationId("강남역"), getStationId("교대역"), 10L);
        LineSteps.노선_생성_API(이호선_요청);

        var 삼호선_요청 = LineFixture.generateLineCreateRequest("3호선", "bg-amber-600", getStationId("교대역"), getStationId("남부터미널역"), 2L);
        var 삼호선_응답 = LineSteps.노선_생성_API(삼호선_요청);
        var 삼호선_URI = 삼호선_응답.header("Location");

        var 삼호선_끝에_구간_추가 = SectionFixture.구간_요청_만들기(getStationId("남부터미널역"), getStationId("양재역"), 3L);
        LineSteps.구간_추가_API(삼호선_URI, 삼호선_끝에_구간_추가);

        var 신분당선_요청 = LineFixture.generateLineCreateRequest("신분당선", "bg-hotpink-600", getStationId("강남역"), getStationId("양재역"), 10L);
        LineSteps.노선_생성_API(신분당선_요청);

        var A호선_요청 = LineFixture.generateLineCreateRequest("A호선", "bg-black-600", getStationId("건대역"), getStationId("성수역"), 7L);
        var A호선_응답 = LineSteps.노선_생성_API(A호선_요청);
        var A호선_URI = A호선_응답.header("Location");

        var A호선_끝에_구간_추가 = SectionFixture.구간_요청_만들기(getStationId("성수역"), getStationId("왕십리역"), 3L);
        LineSteps.구간_추가_API(A호선_URI, 삼호선_끝에_구간_추가);
        LineSteps.구간_추가_API(삼호선_URI, A호선_끝에_구간_추가);
    }
}
