package subway.fixture.station;

import org.springframework.test.util.ReflectionTestUtils;

import subway.station.Station;

public class StationEntityFixture {
	private final String name;

	private StationEntityFixture(String name) {
		this.name = name;
	}

	public static Station 정류장_생성(String name) {
		return StationEntityFixture.builder().name(name).build();
	}

	public static Station 강남역() {
		Station 강남역 = StationEntityFixture.builder()
			.name("강남역")
			.build();

		ReflectionTestUtils.setField(강남역, "id", 1L);
		return 강남역;
	}

	public static Station 양재역() {
		Station 양재역 = StationEntityFixture.builder()
			.name("양재역")
			.build();

		ReflectionTestUtils.setField(양재역, "id", 2L);
		return 양재역;
	}

	public static Station 논현역() {
		Station 논현역 = StationEntityFixture.builder()
			.name("논현역")
			.build();

		ReflectionTestUtils.setField(논현역, "id", 3L);
		return 논현역;
	}

	public static Station 불광역() {
		Station 불광역 = StationEntityFixture.builder()
			.name("불광역")
			.build();

		ReflectionTestUtils.setField(불광역, "id", 4L);
		return 불광역;
	}

	public static Station 홍제역() {
		Station 홍제역 = StationEntityFixture.builder()
			.name("홍제역")
			.build();

		ReflectionTestUtils.setField(홍제역, "id", 5L);
		return 홍제역;
	}

	public static Station 녹번역() {
		Station 녹번역 = StationEntityFixture.builder()
			.name("녹번역")
			.build();

		ReflectionTestUtils.setField(녹번역, "id", 6L);
		return 녹번역;
	}

	public static Station 교대역() {
		Station 교대역 = StationEntityFixture.builder()
			.name("교대역")
			.build();

		ReflectionTestUtils.setField(교대역, "id", 7L);
		return 교대역;
	}

	public static Station 남부터미널역() {
		Station 남부터미널역 = StationEntityFixture.builder()
			.name("남부터미널역")
			.build();

		ReflectionTestUtils.setField(남부터미널역, "id", 8L);
		return 남부터미널역;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String name;

		Builder() {
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Station build() {
			return new Station(name);
		}
	}
}
