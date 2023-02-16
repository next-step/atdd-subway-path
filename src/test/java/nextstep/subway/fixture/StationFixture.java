package nextstep.subway.fixture;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class StationFixture {
    private static final Field stationIdField = ReflectionUtils.findField(Station.class, "id");
    private static long testStationId = 1L;

    public static final Station 강남역 = new Station("강남역");
    public static final Station 양재역 = new Station("양재역");
    public static final Station 청계산역 = new Station("청계산역");
    public static final Station 정자역 = new Station("정자역");
    public static final Station 양재시민의숲역 = new Station("양재시민의숲역");

    static {
        지하철역일련번호주입(강남역);
        지하철역일련번호주입(양재역);
        지하철역일련번호주입(청계산역);
        지하철역일련번호주입(정자역);
        지하철역일련번호주입(양재시민의숲역);
    }

    private static void 지하철역일련번호주입(Station station) {
        stationIdField.setAccessible(true);
        ReflectionUtils.setField(stationIdField, station, testStationId++);
        stationIdField.setAccessible(false);
    }
}
