package nextstep.subway.unit;

import nextstep.subway.domain.station.Station;

public class StationFixture {
    public static final Station 교대역 = makeStation(1L, "교대역");
    public static final Station 강남역 = makeStation(2L, "강남역");
    public static final Station 역삼역 = makeStation(3L, "역삼역");
    public static final Station 선릉역 = makeStation(4L, "선릉역");
    public static final Station 삼성역 = makeStation(5L, "삼성역");

    private static Station makeStation(final Long id, final String name) {
        final var station = new Station(name);
        injectId(station, id);
        return station;
    }

    private static void injectId(final Station station, final Long id) {
        try {
            final var field = Station.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(station, id);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("해당하는 필드를 찾을 수 없습니다." + e.getMessage());
        }
    }
}
