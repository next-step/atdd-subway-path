package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GraphTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;


    @BeforeEach
    public void setup() {

        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line(1L, "2호선", "green");
        삼호선 = new Line(2L, "3호선", "orange");
        신분당선 = new Line(3L, "신분당선", "red");

        이호선.addSection(new Section(이호선, 1L, 교대역, 강남역, 10));
        삼호선.addSection(new Section(삼호선, 2L, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 3L, 남부터미널역, 양재역, 3));
        신분당선.addSection(new Section(신분당선, 4L, 강남역, 양재역, 10));

    }


    @DisplayName("노선 생성 후 1개 구간에 대한 거리 및 경로 테스트")
    @Test
    void OneSectionGraphTest() {

        //Given
        Station magok = new Station(1L, "마곡역");
        Station balsan = new Station(2L, "발산역");
        Line line = new Line("5호선", "purple");
        Section section = new Section(line, magok, balsan, 10);
        line.addSection(section);

        // When
        CommonGraph graph = new SubwayGraph(List.of(line));
        int distance = graph.getShortestDistance(magok, balsan);
        List<Station> stations = graph.getShortestPath(magok, balsan);

        //Then
        assertThat(distance).isEqualTo(10);
        assertThat(stations).hasSize(2);

    }

    /**
     * 교대역    --- *2호선* ---   강남역  (남부 - 교대 : 2) , (교대 - 강남 : 10)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재 (남부 - 양재 : 3 ), (강남 - 양재 : 10)
     */

    @DisplayName("출발역와 도착역 까지의 경로와 최단거리 조회 테스트")
    @Test
    void findShortestPathAndDistance() {
        //When
        CommonGraph graph = new SubwayGraph(List.of(이호선, 삼호선, 신분당선));

        //Then
        int distance = graph.getShortestDistance(교대역, 양재역);
        List<Station> stations = graph.getShortestPath(교대역, 양재역);

        assertThat(distance).isEqualTo(5);
        assertThat(stations).hasSize(3);
        assertThat(stations).contains(교대역, 남부터미널역, 양재역);

    }

    /// 실패 테스트 ///

    @DisplayName("같은 역 입력해서 실패하기")
    @Test
    void failInputSameStation() {
        //When
        CommonGraph graph = new SubwayGraph(List.of(이호선, 삼호선, 신분당선));

        //Then
        assertThatThrownBy(() -> graph.getShortestPath(교대역, 교대역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("연결되어 있지 않은 역을 입력해서 실패하기")
    @Test
    void failInputDoNotLinkedStation() {
        //Given
        Station 마곡역 = new Station(5L, "마곡역");
        Station 발산역 = new Station(6L, "발산역");
        Line 오호선 = new Line(4L, "오호선", "purple");
        오호선.addSection(new Section(오호선, 10L, 마곡역, 발산역, 1));

        //When
        CommonGraph graph = new SubwayGraph(List.of(이호선, 삼호선, 오호선, 신분당선));

        //Then
        assertThatThrownBy(() -> graph.getShortestDistance(교대역, 마곡역))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("등록되지 않은 역을 입력해서 실패하기")
    @Test
    void failInputDoNotRegistStation() {
        //Given
        Station 마곡역 = new Station(5L, "마곡역");
        //When
        CommonGraph graph = new SubwayGraph(List.of(이호선, 삼호선, 신분당선));
        //Then
        assertThatThrownBy(() -> graph.getShortestPath(교대역, 마곡역))
                .isInstanceOf(IllegalArgumentException.class);

    }

}
