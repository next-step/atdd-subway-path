package nextstep.subway.line.domain;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.AlreadyIncludedAllStationException;
import nextstep.subway.line.exception.NotExistStationException;
import nextstep.subway.line.exception.TooLowLengthSectionsException;
import nextstep.subway.line.exception.TooManyFindSectionsException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	private static final int SECTIONS_MIN_SIZE = 1;
	private static final int ZERO = 0;
	private static final int ONE = 1;
	private static final int FIND_MAX_SIZE = 2;

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
		Section findSection = findInsertLocation(section);

		if (findSection.isLastSection(section)) {
			sections.add(section);
			return;
		}

		List<Section> divideSections = findSection.divideSections(section);
		sections.remove(findSection);
		sections.addAll(divideSections);
	}

	public void removeSection(Station station) {
		List<Section> targets = filterTargets(
			sec -> sec.stream()
				.filter(s -> s.containsStation(station))
				.collect(toList())
		);

		if (isLastSection(targets)) {
			sections.remove(targets.get(ZERO));
			return;
		}

		sections.removeAll(targets);
		sections.add(concatSection(targets));
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

	private List<Section> filterTargets(Function<List<Section>, List<Section>> func) {
		validateFilterBefore();

		List<Section> targets = func.apply(this.sections);

		validateFilterAfter(targets);

		return targets;
	}

	private boolean isLastSection(List<Section> targets) {
		return targets.size() == ONE;
	}

	private void validateFilterAfter(List<Section> targets) {
		if (targets.size() == ZERO) {
			throw new NotExistStationException();
		}

		if (targets.size() > FIND_MAX_SIZE) {
			throw new TooManyFindSectionsException(FIND_MAX_SIZE);
		}
	}

	private void validateFilterBefore() {
		if (sections.size() <= SECTIONS_MIN_SIZE) {
			throw new TooLowLengthSectionsException(SECTIONS_MIN_SIZE);
		}
	}

	private Section concatSection(List<Section> targets) {
		Section firstTarget = targets.get(ZERO);
		Section secondTarget = targets.get(ONE);

		if (secondTarget.getUpStation().equals(firstTarget.getDownStation())) {
			return Section.of(firstTarget.getLine(), firstTarget.getUpStation(),
				secondTarget.getDownStation(), addDistance(firstTarget, secondTarget));
		}

		return Section.of(firstTarget.getLine(), secondTarget.getUpStation(),
			firstTarget.getDownStation(), addDistance(firstTarget, secondTarget));
	}

	private int addDistance(Section firstTarget, Section secondTarget) {
		return firstTarget.getDistance() + secondTarget.getDistance();
	}

	private void validateAddSection(Section section) {
		if (isAllContainsStation(section)) {
			throw new AlreadyIncludedAllStationException();
		}
	}

	private boolean isAllContainsStation(Section section) {
		return getStations().contains(section.getUpStation())
			&& getStations().contains(section.getDownStation());
	}

	private Section findInsertLocation(Section section) {
		return sections.stream()
			.filter(s -> s.containsStation(section))
			.findAny()
			.orElseThrow(NotExistStationException::new);
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
}
