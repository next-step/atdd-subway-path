package nextstep.subway.domain;

import java.util.List;

public class Path {

	private final Sections sections;

	public Path(List<Section> sections) {
		this.sections = new Sections(sections);
	}

	public List<Station> getStations() {
		return sections.getStations();
	}

	public int getTotalDistance() {
		return sections.getTotalDistance();
	}
}
