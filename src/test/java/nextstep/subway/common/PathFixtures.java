package nextstep.subway.common;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class PathFixtures {

	public static final Long 존재하지않은역_ID = 20L;

	public static Station 교대역() {
		return new Station("교대역");
	}

	public static Long 교대역_ID = 1L;

	public static Station 강남역() {
		return new Station("강남역");
	}

	public static Long 강남역_ID = 2L;

	public static Station 양재역() {
		return new Station("양재역");
	}

	public static Long 양재역_ID = 3L;

	public static Station 남부터미널역() {
		return new Station("남부터미널역");
	}

	public static Long 남부터미널역_ID = 4L;

	public static Station 동대문역() {
		return new Station("동대문");
	}

	public static Long 동대문역_ID = 5L;

	public static Station 혜화역() {
		return new Station("혜화");
	}

	public static Long 혜화역_ID = 6L;

	public static Long 이호선_ID = 1L;

	public static Long 삼호선_ID = 2L;

	public static Long 신분당선_ID = 3L;

	public static Long 사호선_ID = 4L;

	public static Station withId(Station station, Long id) throws IllegalAccessException {
		Field idField = ReflectionUtils.findField(station.getClass(), "id");
		idField.setAccessible(true);
		idField.set(station, id);

		return station;
	}

	public static Line withId(Line line, Long id) throws IllegalAccessException {
		Field idField = ReflectionUtils.findField(line.getClass(), "id");
		idField.setAccessible(true);
		idField.set(line, id);

		return line;
	}
}
