package nextstep.subway.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class PathFixture {
    /**              (10)
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선* (2)               *신분당선* (5)
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
                    (3)          **/


    public static Station 강남역 = new Station("강남역");
    public static Station 교대역 = new Station("교대역");
    public static Station 양재역 = new Station("양재역");
    public static Station 남부터미널역 = new Station("남부터미널역");

    public static int 교대역_강남역_거리 = 10;
    public static int 강남역_양재역_거리 = 5;
    public static int 교대역_남부터미널역_거리 = 2;
    public static int 남부터미널역_양재역_거리 = 3;

    public static Line 이호선 = Line.of("2호선", "bg-green-600", 교대역, 강남역, 교대역_강남역_거리);
    public static Line 삼호선 = Line.of("3호선", "bg-orange-500", 교대역, 남부터미널역, 교대역_남부터미널역_거리);
    public static Line 신분당선 = Line.of("신분당선", "bg-red-500", 강남역, 양재역, 강남역_양재역_거리);

    public static List<Line> 노선_목록 = new ArrayList<>();

    static {
        삼호선.addSection(남부터미널역, 양재역, 남부터미널역_양재역_거리);

        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(교대역, "id", 2L);
        ReflectionTestUtils.setField(양재역, "id", 3L);
        ReflectionTestUtils.setField(남부터미널역, "id", 4L);

        노선_목록.addAll(Arrays.asList(이호선, 삼호선, 신분당선));
    }


}
