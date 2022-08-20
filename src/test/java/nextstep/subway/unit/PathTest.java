package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathSearch;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    private final int 교대역_강남역_거리 = 1;
    private final int 강남역_양재역_거리 = 2;
    private final int 교대역_남부터미널역_거리 = 3;
    private final int 남부터미널역_양재역_거리 = 10;


    private Station 온수역;
    private Station 오류동역;
    private Station 개봉역;
    private Station 구일역;
    private Station 구로역;
    private Station 신도림역;

    private Station 대림역;
    private Station 구로디지털단지역;

    private Station 천왕역;
    private Station 광명사거리역;
    private Station 철산역;
    private Station 가산디지털단지역;
    private Station 남구로역;

    private Line 일호선;
    private Line 칠호선;

    private PathSearch pathSearch;

    public void 강남라인_세팅() {

        교대역 = 지하철역_생성_요청(1L, "교대역");
        강남역 = 지하철역_생성_요청(2L, "강남역");
        양재역 = 지하철역_생성_요청(3L,"양재역");
        남부터미널역 = 지하철역_생성_요청(4L,"남부터미널역");

        이호선 = 지하철_노선_생성_요청(1L, "2호선", "green", 교대역, 강남역, 교대역_강남역_거리);
        신분당선 = 지하철_노선_생성_요청(2L, "신분당선", "red", 강남역, 양재역, 강남역_양재역_거리);
        삼호선 = 지하철_노선_생성_요청(3L, "3호선", "orange", 교대역, 남부터미널역, 교대역_남부터미널역_거리);

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 남부터미널역_양재역_거리);

        pathSearch = new PathSearch();
        List<Line> lines = new ArrayList<>();
        lines.add(이호선);
        lines.add(신분당선);
        lines.add(삼호선);
        pathSearch.addPaths(lines);

    }

    public void 강서라인_세팅() {
        온수역 = 지하철역_생성_요청(1L, "온수역");
        오류동역 = 지하철역_생성_요청(2L, "오류동역 =");
        개봉역 = 지하철역_생성_요청(3L, "개봉역");
        구일역 = 지하철역_생성_요청(4L, "구일역");
        구로역 = 지하철역_생성_요청(5L, "구로역");
        신도림역 = 지하철역_생성_요청(6L, "신도림역");

        대림역 = 지하철역_생성_요청(7L, "대림역 =");
        구로디지털단지역 = 지하철역_생성_요청(8L, "구로디지털단지역");

        천왕역 = 지하철역_생성_요청(9L, "천왕역 =");
        광명사거리역 = 지하철역_생성_요청(10L, "광명사거리역");
        철산역 = 지하철역_생성_요청(11L, "철산역 =");
        가산디지털단지역 = 지하철역_생성_요청(12L, "가산디지털단지역");
        남구로역 = 지하철역_생성_요청(13L, "남구로역");

        일호선 = 지하철_노선_생성_요청(1L, "1호선", "blue", 온수역, 오류동역, 2);
        이호선 = 지하철_노선_생성_요청(2L, "2호선", "green", 신도림역, 대림역, 2);
        칠호선 = 지하철_노선_생성_요청(3L, "7호선", "brown", 온수역, 천왕역, 4);

        지하철_노선에_지하철_구간_생성_요청(일호선, 오류동역, 개봉역, 5);
        지하철_노선에_지하철_구간_생성_요청(일호선, 개봉역, 구일역, 3);
        지하철_노선에_지하철_구간_생성_요청(일호선, 구일역, 구로역, 6);
        지하철_노선에_지하철_구간_생성_요청(일호선, 구로역, 신도림역, 7);

        지하철_노선에_지하철_구간_생성_요청(이호선, 대림역, 구로디지털단지역, 2);

        지하철_노선에_지하철_구간_생성_요청(칠호선, 천왕역, 광명사거리역, 4);
        지하철_노선에_지하철_구간_생성_요청(칠호선, 광명사거리역, 철산역, 3);
        지하철_노선에_지하철_구간_생성_요청(칠호선, 철산역, 가산디지털단지역, 2);
        지하철_노선에_지하철_구간_생성_요청(칠호선, 가산디지털단지역, 남구로역, 4);
        지하철_노선에_지하철_구간_생성_요청(칠호선, 남구로역, 대림역, 5);

        pathSearch = new PathSearch();
        List<Line> lines = new ArrayList<>();
        lines.add(일호선);
        lines.add(이호선);
        lines.add(칠호선);
        pathSearch.addPaths(lines);

    }

    @DisplayName("출발역-도착역 사이 최단경로 조회(강남라인)")
    @Test
    void findShortestPathGangNam() {
        // given
        강남라인_세팅();
        // when
        List<String> shortestPath = pathSearch.getShortestPath(교대역, 양재역);
        Double distance = pathSearch.getShortestPathDistance(교대역, 양재역);


        // then
        assertThat(shortestPath.size()).isEqualTo(3);
        assertThat(distance).isEqualTo(교대역_강남역_거리 + 강남역_양재역_거리);
    }

    @DisplayName("출발역-도착역 사이 최단경로 조회(강서라인)")
    @Test
    void findShortestPathGangSeo() {
        강서라인_세팅();
        // when
        Double 온수출발_오류도착_최단거리 = pathSearch.getShortestPathDistance(온수역, 오류동역);
        Double 온수출발_구로디지털단지도착_최단거리 = pathSearch.getShortestPathDistance(온수역, 구로디지털단지역);

        // then
        assertThat(온수출발_오류도착_최단거리).isEqualTo(2);
        assertThat(온수출발_구로디지털단지도착_최단거리).isEqualTo(24);

    }

    public Station 지하철역_생성_요청(Long id, String name) {
        return new Station(id, name);
    }

    public Line 지하철_노선_생성_요청(Long id, String name, String color, Station upStation, Station downStation, int distance) {
        Line line = new Line(id, name, color);
        line.addSection(new Section(line, upStation, downStation, distance));

        return line;
    }

    public Section 지하철_노선에_지하철_구간_생성_요청(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line, upStation, downStation, distance);
        line.addSection(section);

        return section;
    }

}