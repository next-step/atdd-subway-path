package nextstep.subway.unit.fixture;

import nextstep.subway.domain.Station;

import java.lang.reflect.Field;

public class StationFixture {

    private static Long id = 1L;

    public static Station 지하철역_생성(String name) {
        Station station = new Station(name);

        Class<Station> stationClass = Station.class;
        Field stationIdField = null;
        try {
            stationIdField = stationClass.getDeclaredField("id");
            stationIdField.setAccessible(true);
            stationIdField.set(station, id++);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return station;
    }
}
