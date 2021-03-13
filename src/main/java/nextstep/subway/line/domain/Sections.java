package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.NotExistStationException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.TooLowLengthSectionsException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	private static final int SECTIONS_MIN_SIZE = 1;

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public void addSection(Section section) {
		if (sections.isEmpty()) {
			sections.add(section);
			return;
		}

		Section findSection = sections.stream()
			.filter(s -> s.containsStation(section))
			.findAny()
			.orElseThrow(NotExistStationException::new);

		if (findSection.isLastSection(section)) {
			sections.add(section);
			return;
		}

		List<Section> divideSections = findSection.divideSections(section);
		sections.remove(findSection);
		sections.addAll(divideSections);
	}

	public void removeSection(Station station) {
		validateDeleteSection(station);

		final Section target = sections.stream()
			.filter(section -> section.getDownStation().equals(station))
			.findAny()
			.orElseThrow(RuntimeException::new);

		sections.remove(target);
	}

	public List<Station> getStations() {
		if (sections.isEmpty()) {
			return Collections.emptyList();
		}

		List<Station> stations = new ArrayList<>();
		Station downStation = findUpStation();
		stations.add(downStation);

		while (downStation != null) {
			Station finalDownStation = downStation;
			Optional<Section> nextLineStation = sections.stream()
				.filter(it -> it.getUpStation() == finalDownStation)
				.findFirst();
			if (!nextLineStation.isPresent()) {
				break;
			}
			downStation = nextLineStation.get().getDownStation();
			stations.add(downStation);
		}

		return stations;
	}

	private Station findUpStation() {
		Station downStation = sections.get(0).getUpStation();
		while (downStation != null) {
			Station finalDownStation = downStation;
			Optional<Section> nextLineStation = sections.stream()
				.filter(it -> it.getDownStation() == finalDownStation)
				.findFirst();
			if (!nextLineStation.isPresent()) {
				break;
			}
			downStation = nextLineStation.get().getUpStation();
		}

		return downStation;
	}

	private void validateAddSection(Section section) {

	}

	private void validateDeleteSection(Station station) {
		if (sections.size() <= SECTIONS_MIN_SIZE) {
			throw new TooLowLengthSectionsException(SECTIONS_MIN_SIZE);
		}

		if (isNotLastStation(station)) {
			throw new NotLastStationException(station.getName());
		}
	}

	private boolean isNotLastStation(Station station) {
		return !findLastStation().equals(station);
	}

	private Station findLastStation() {
		return getStations().get(getStations().size() - 1);
	}
}
