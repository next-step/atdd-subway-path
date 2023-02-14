package nextstep.subway.common;

import nextstep.subway.domain.Station;

public class PathFixtures {

	public static final Long 존재하지않은역 = 20L;

	public static Station 교대역() {
		return new Station("교대역");
	}

	public static Station 강남역() {
		return new Station("강남역");
	}

	public static Station 양재역() {
		return new Station("양재역");
	}

	public static Station 남부터미널역() {
		return new Station("남부터미널역");
	}

	public static Station 동대문역() {
		return new Station("동대문");
	}

	public static Station 혜화역() {
		return new Station("혜화");
	}

}
