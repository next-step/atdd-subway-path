package subway.fixture.line;

import static subway.fixture.station.StationEntityFixture.*;

import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import subway.line.Line;
import subway.line.Section;
import subway.station.Station;

public class LineEntityFixture {
	public static final Integer DISTANCE = 10;

	private final String name;
	private final String color;

	private LineEntityFixture(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Line 노선_생성(String name, String color) {
		return LineEntityFixture.builder()
			.name(name)
			.color(color)
			.build();
	}

	public static Line 신분당선() {
		Line line = LineEntityFixture.builder()
			.name("신분당선")
			.color("빨간색")
			.build();

		ReflectionTestUtils.setField(line, "id", 1L);
		return line;
	}

	public static Line 삼호선() {
		Line line = LineEntityFixture.builder()
			.name("삼호선")
			.color("주황색")
			.build();
		ReflectionTestUtils.setField(line, "id", 2L);
		return line;
	}

	public static Line 이호선() {
		Line line = LineEntityFixture.builder()
			.name("이호선")
			.color("빨간색")
			.build();
		ReflectionTestUtils.setField(line, "id", 3L);
		return line;
	}

	public static Line 신분당선_1구간_추가() {
		Line line = 신분당선();
		Station 강남역 = 강남역();
		Station 양재역 = 양재역();
		Section section = new Section(line, 강남역, 양재역, DISTANCE);

		line.addSection(section);

		return line;
	}

	public static Line 신분당선_2구간_추가() {
		Line line = 신분당선();
		Station 강남역 = 강남역();
		Station 양재역 = 양재역();
		Station 논현역 = 논현역();

		Section section1 = new Section(line, 강남역, 양재역, DISTANCE);
		Section section2 = new Section(line, 양재역, 논현역, DISTANCE);

		line.addSection(section1);
		line.addSection(section2);

		return line;
	}

	public static Line 삼호선_1구간_추가() {
		Line line = 삼호선();
		Station 불광역 = 불광역();
		Station 녹번역 = 녹번역();
		Section section = new Section(line, 불광역, 녹번역, DISTANCE);

		line.addSection(section);

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
