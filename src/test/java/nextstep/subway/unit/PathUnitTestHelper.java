package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

public final class PathUnitTestHelper {

    public static final int STATIONS_SIZE_YANGJAE_TO_YEOKSAM = 3;
    public static final int SHORTEST_DISTANCE_YANGJAE_TO_YEOKSAM = 9;

    public static Station 교대역;
    public static Station 강남역;
    public static Station 역삼역;
    public static Station 남부터미널역;
    public static Station 양재역;
    public static Station 삼성역;
    public static Station 석촌역;
    public static Station 단대오거리역;
    public static List<Line> lines = new ArrayList<>();

    public static void givens() {
        createStations();
        createLines();
    }

    private static void createStations() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        남부터미널역 = new Station("남부터미널역");
        양재역 = new Station("양재역");
        삼성역 = new Station("삼성역");
        석촌역 = new Station("석촌역");
        단대오거리역 = new Station("단대오거리역");

        ReflectionTestUtils.setField(교대역, "id", 1L);
        ReflectionTestUtils.setField(강남역, "id", 2L);
        ReflectionTestUtils.setField(역삼역, "id", 3L);
        ReflectionTestUtils.setField(남부터미널역, "id", 4L);
        ReflectionTestUtils.setField(양재역, "id", 5L);
        ReflectionTestUtils.setField(삼성역, "id", 6L);
        ReflectionTestUtils.setField(석촌역, "id", 7L);
        ReflectionTestUtils.setField(단대오거리역, "id", 8L);
    }

    private static void createLines() {
        Line 신분당선 = new Line("신분당선", "bg-red-600");
        Line 이호선 = new Line("2호선", "bg-green-600");
        Line 삼호선 = new Line("3호선", "bg-orange-600");
        Line 팔호선 = new Line("8호선", "bg-pink-600");

        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 5));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 2));
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 4));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 3));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));
        팔호선.addSection(new Section(팔호선, 석촌역, 단대오거리역, 5));

        lines.add(신분당선);
        lines.add(이호선);
        lines.add(삼호선);
        lines.add(팔호선);
    }

    public static PathRequest createPathRequest() {
        PathRequest pathRequest = new PathRequest();
        ReflectionTestUtils.setField(pathRequest, "source", 5L);
        ReflectionTestUtils.setField(pathRequest, "target", 3L);
        return pathRequest;
    }
}
