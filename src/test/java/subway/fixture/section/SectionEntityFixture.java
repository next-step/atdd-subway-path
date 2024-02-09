package subway.fixture.section;

import subway.fixture.line.LineEntityFixture;
import subway.fixture.station.StationEntityFixture;
import subway.line.Line;
import subway.line.Section;
import subway.station.Station;

public class SectionEntityFixture {
	public static final Integer DISTANCE = 10;

	private final Line line;
	private final Station upStation;
	private final Station downStation;
	private final Integer distance;

	private SectionEntityFixture(Line line, Station upStation, Station downStation, Integer distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static Section 구간_엔티티() {
		return SectionEntityFixture.builder()
			.line(LineEntityFixture.신분당선())
			.upStation(StationEntityFixture.강남역())
			.downStation(StationEntityFixture.양재역())
			.distance(DISTANCE)
			.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Line line;
		private Station upStation;
		private Station downStation;
		private Integer distance;

		Builder() {
		}

		public Builder line(Line line) {
			this.line = line;
			return this;
		}

		public Builder upStation(Station station) {
			this.upStation = station;
			return this;
		}

		public Builder downStation(Station station) {
			this.downStation = station;
			return this;
		}

		public Builder distance(Integer distance) {
			this.distance = distance;
			return this;
		}

		public Section build() {
			return new Section(line, upStation, downStation, distance);
		}
	}
}
