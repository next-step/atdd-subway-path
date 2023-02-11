package nextstep.subway.common;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class StationFixtures {

	public static final Station 동대문 = new Station("동대문");
	public static final Long 동대문_ID = 1L;

	public static final Station 동대문역사문화공원 = new Station("동대문역사문화공원");
	public static final Long 동대문역사문화공원_ID = 2L;

	public static final Station 충무로 = new Station("충무로");
	public static final Long 충무로_ID = 3L;

	public static final Long 서울역_ID = 4L;

	public static final Station 서울역 = new Station("서울역");

	public static final Long 숙대입구역_ID = 5L;

	public static final Station 숙대입구역 = new Station("숙대입구역");

	public static final Long 혜화역_ID = 6L;

	public static final Station 혜화역 = new Station("혜화역");

	public static final Long 등록되지않은역_ID = 50L;

	public static final Station 등록되지않은역 = new Station("등록되지않은역");

	public static Station withId(Station station, Long id) throws IllegalAccessException {
		Field idField = ReflectionUtils.findField(station.getClass(), "id");
		idField.setAccessible(true);
		idField.set(station, id);

		return station;
	}
}
