package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.beans.HasProperty.hasProperty;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.IllegalPathException;

public class PathTest {
    private static final Station 교대역 = new Station("교대역");
    private static final Station 남부터미널역 = new Station("남부터미널역");
    private static final Station 양재역 = new Station("양재역");
    private static final int 교대역_남부터미널역_사이_거리 = 10;
    private static final int 남부터미널역_양재역_사이_거리 = 12;
    @Test
    void testGetShortestPath_경로_사이의_모든_역과_최단거리를_반환한다() {
        //given
        Line 이호선 = new Line(1L, "이호선", "초록색");
        Section 교대역_남부터미널역 = new Section(교대역, 남부터미널역, 교대역_남부터미널역_사이_거리);
        Section 남부터미널역_양재역 = new Section(남부터미널역, 양재역, 남부터미널역_양재역_사이_거리);
        이호선.addSection(교대역_남부터미널역);
        이호선.addSection(남부터미널역_양재역);
        Path path = new Path();

        //when
        PathResponse response = path.getShortestPath(List.of(이호선), 교대역, 양재역);
        //then
        assertAll(
            () -> assertThat(response.getStations()).hasSize(3)
                                                    .extracting(StationResponse::getName)
                                                    .containsExactly("교대역", "남부터미널역", "양재역"),
            () -> assertThat(response.getDistance()).isEqualTo(교대역_남부터미널역_사이_거리 + 남부터미널역_양재역_사이_거리)
        );
    }

    @Test
    void testValidatePath_출발역과_도착역이_같으면_예외를_반환한다() {
        //given
        Line 이호선 = new Line(1L, "이호선", "초록색");
        Section 교대역_남부터미널역 = new Section(교대역, 남부터미널역, 교대역_남부터미널역_사이_거리);
        Section 남부터미널역_양재역 = new Section(남부터미널역, 양재역, 남부터미널역_양재역_사이_거리);
        이호선.addSection(교대역_남부터미널역);
        이호선.addSection(남부터미널역_양재역);
        Path path = new Path();

        //when & then
        assertThatThrownBy(() -> path.getShortestPath(List.of(이호선), 교대역, 교대역)).isInstanceOf(IllegalPathException.class);
    }
//
//    @Test
//    void testValidatePath_출발역_또는_도착역이_경로에_존재하지_않으면_예외를_반환한다() {
//        //given
//        Path path = new Path();
//        path.addConnectionBetweenStations(교대역, 남부터미널역, 교대역_남부터미널역_사이_거리);
//        path.addConnectionBetweenStations(남부터미널역, 양재역, 남부터미널역_양재역_사이_거리);
//        String 왕십리역 = "왕십리역";
//
//        //when & then
//        assertThatThrownBy(() -> path.getStationNamesAlongPath(교대역, 왕십리역)).isInstanceOf(IllegalPathException.class);
//    }
}
