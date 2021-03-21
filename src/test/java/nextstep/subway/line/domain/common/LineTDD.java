package nextstep.subway.line.domain.common;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

public class LineTDD {

    public static Station 강남역, 양재역, 판교역, 정자역, 광교역;
    public static Line 신분당선;

    @BeforeEach
    public void setUp(){
        // given
        // 역 순서 : 강남역 - [양재역] - 판교역 - [정자역] - 광교역
        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        양재역 = new Station("양재역");
        ReflectionTestUtils.setField(양재역, "id", 2L);
        판교역 = new Station("판교역");
        ReflectionTestUtils.setField(판교역, "id", 3L);
        정자역 = new Station("정자역");
        ReflectionTestUtils.setField(정자역, "id", 4L);
        광교역 = new Station("광교역");
        ReflectionTestUtils.setField(광교역, "id", 5L);
        신분당선 = new Line("신분당선", "bg-red-600", 양재역, 정자역, 10 );
        ReflectionTestUtils.setField(신분당선, "id", 1L);
    }
}
