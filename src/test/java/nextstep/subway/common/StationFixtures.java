package nextstep.subway.common;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

import nextstep.subway.domain.Station;

public class StationFixtures {

	public static final Station 동대문 = new Station("동대문");
	public static final Long 동대문_ID = 1L;

	public static final Station 동대문역사문화공원 = new Station("동대문역사문화공원");
	public static final Long 동대문역사문화공원_ID = 2L;

	public static final Station 충무로 = new Station("충무로");
	public static final Long 충무로_ID = 3L;

	public static final Long 등록되지않은_역_ID = 4L;

	public static final Station 등록되지않은_역 = new Station("등록되지 않은 역");

	public static Station withId(Station station, Long id) throws IllegalAccessException {
		Field idField = ReflectionUtils.findField(station.getClass(), "id");
		idField.setAccessible(true);
		idField.set(station, id);

		return station;
	}
}
