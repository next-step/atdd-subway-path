package subway.fixture.line;

import java.util.List;

import subway.fixture.section.SectionEntityFixture;
import subway.fixture.station.StationEntityFixture;
import subway.line.Line;
import subway.station.Station;

public class LineEntityFixture {
	private final String name;
	private final String color;

	private LineEntityFixture(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Line 신분당선() {
		return LineEntityFixture.builder()
			.name("신분당선")
			.color("빨간색")
			.build();
	}

	public static Line 삼호선() {
		return LineEntityFixture.builder()
			.name("삼호선")
			.color("주황색")
			.build();
	}

	public static Line 신분당선_1구간_추가() {
		Station 강남역 = StationEntityFixture.강남역();
		Station 양재역 = StationEntityFixture.양재역();
		Integer distance = SectionEntityFixture.DISTANCE;

		Line line = 신분당선();

		line.addSection(강남역, 양재역, distance);

		return line;
	}

	public static Line 신분당선_2구간_추가() {
		Station 강남역 = StationEntityFixture.강남역();
		Station 양재역 = StationEntityFixture.양재역();
		Station 논현역 = StationEntityFixture.논현역();
		Integer distance = SectionEntityFixture.DISTANCE;

		Line line = 신분당선();

		line.addSection(강남역, 양재역, distance);
		line.addSection(양재역, 논현역, distance);

		return line;
	}

	public static Line 삼호선_1구간_추가() {
		Station 불광역 = StationEntityFixture.불광역();
		Station 녹번역 = StationEntityFixture.녹번역();
		Integer distance = SectionEntityFixture.DISTANCE;

		Line line = 삼호선();

		line.addSection(불광역, 녹번역, distance);

		return line;
	}

	public static List<Line> 모든_노선_리스트() {
		Line 신분당선 = 신분당선_1구간_추가();
		Line 삼호선 = 삼호선_1구간_추가();

		return List.of(신분당선, 삼호선);
	}

	public static class Builder {
		private String name;
		private String color;

		Builder() {
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder color(String color) {
			this.color = color;
			return this;
		}

		public Line build() {
			return new Line(name, color);
		}
	}
}
