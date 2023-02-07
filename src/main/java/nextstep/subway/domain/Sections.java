package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.domain.exception.LineErrorCode;
import nextstep.subway.domain.exception.SectionErrorCode;
import nextstep.subway.domain.exception.SectionRemoveException;
import nextstep.subway.domain.exception.SubwayBadRequestException;

@Embeddable
public class Sections {

	private static final int INVALID_SECTION_DISTANCE = 0;

	private static final int SINGLE_SECTION_COUNT = 1;

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public void addSection(Line line, Station upStation, Station downStation, int distance) {
		this.sections.add(new Section(line, upStation, downStation, distance));
	}

	public List<Section> getList() {
		return Collections.unmodifiableList(this.sections);
	}

	public List<Station> getStations() {
		Set<Station> stations = new HashSet<>();
		stations.addAll(extractUpStations());
		stations.addAll(extractDownStations());

		return List.copyOf(stations);
	}

	private Set<Station> extractUpStations() {
		return this.sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toUnmodifiableSet());
	}

	private Set<Station> extractDownStations() {
		return this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toUnmodifiableSet());
	}

	public void createInitialLineSection(Station upStation, Station downStation, int distance, Line line) {
		if (distance == INVALID_SECTION_DISTANCE) {
			throw new SubwayBadRequestException(LineErrorCode.INVALID_SECTION_DISTANCE);
		}

		this.sections.add(new Section(line, upStation, downStation, distance));
	}

	public Station remove(Station station, Long finalDownStationId) {
		if (this.sections.size() == SINGLE_SECTION_COUNT) {
			throw new SectionRemoveException(SectionErrorCode.SINGLE_SECTION);
		}

		Section section = this.sections.stream()
			.filter(it -> it.equalDownStation(station))
			.findFirst()
			.orElseThrow(() -> new SectionRemoveException(SectionErrorCode.NOT_INCLUDE_STATION));

		if (section.hasFinalDownStation(finalDownStationId)) {
			sections.remove(section);
			return section.getUpStation();
		}

		throw new SectionRemoveException(SectionErrorCode.INVALID_REMOVE_STATION);
	}
}
