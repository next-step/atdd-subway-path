package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.AlreadyExistDownStationException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.NotMatchedUpStationException;
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

		validateAddSection(section);
		sections.add(section);
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
		boolean isNotValidUpStation = findLastStation() != section.getUpStation();
		if (isNotValidUpStation) {
			throw new NotMatchedUpStationException();
		}

		boolean isDownStationExisted = getStations().contains(section.getDownStation());
		if (isDownStationExisted) {
			throw new AlreadyExistDownStationException();
		}
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
