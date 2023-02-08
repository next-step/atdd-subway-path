package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.domain.exception.LineErrorCode;
import nextstep.subway.domain.exception.SectionAddException;
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
		if (sections.isEmpty()) {
			this.sections.add(new Section(line, upStation, downStation, distance));
			line.updateFinalUpStation(upStation);
			line.updateFinalDownStation(downStation);
			return;
		}

		if (haveStations(upStation, downStation)) {
			throw new SectionAddException(SectionErrorCode.HAVE_STATIONS);
		}

		if (line.equalFinalDownStation(upStation)) {
			addAfterSection(line, upStation, downStation, distance);
			return;
		}

		if (line.equalFinalUpStation(downStation)) {
			addInFrontSection(line, upStation, downStation, distance);
			return;
		}

		addSectionBetweenExistingSection(line, upStation, downStation, distance);
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

	private void addInFrontSection(Line line, Station upStation, Station downStation, int distance) {
		this.sections.add(new Section(line, upStation, downStation, distance));
		line.updateFinalUpStation(upStation);
	}

	private void addAfterSection(Line line, Station upStation, Station downStation, int distance) {
		this.sections.add(new Section(line, upStation, downStation, distance));
		line.updateFinalDownStation(downStation);
	}

	private void addSectionBetweenExistingSection(Line line, Station upStation, Station downStation, int distance) {
		Optional<Section> includedUpStationSection = this.sections.stream()
			.filter(it -> it.equalUpStation(upStation))
			.findFirst();

		if (includedUpStationSection.isPresent()) {
			addSectionBetweenExistingSection(
				line,
				upStation,
				downStation,
				distance,
				includedUpStationSection.get()
			);
			return;
		}

		Optional<Section> includedDownStationSection = this.sections.stream()
			.filter(it -> it.equalDownStation(downStation))
			.findFirst();

		if (includedDownStationSection.isPresent()) {
			addSectionBetweenExistingSection(
				line,
				upStation,
				downStation,
				distance,
				includedDownStationSection.get()
			);
			return;
		}

		throw new SectionAddException(SectionErrorCode.NOT_FOUND_EXISTING_STATION);
	}

	private boolean haveStations(Station upStation, Station downStation) {
		List<Station> stations = getStations();

		return stations.contains(upStation) && stations.contains(downStation);
	}

	private void addSectionBetweenExistingSection(
		Line line,
		Station upStation,
		Station downStation,
		int distance,
		Section includedSection) {
		if (!includedSection.isLonger(distance)) {
			throw new SectionAddException(SectionErrorCode.MORE_LONGER_LENGTH);
		}

		if (includedSection.equalUpStation(upStation)) {
			this.sections.remove(includedSection);
			this.sections.add(new Section(line, upStation, downStation, distance));
			this.sections.add(
				new Section(
					line,
					includedSection.getDownStation(),
					downStation,
					includedSection.getDistance() - distance
				)
			);
			return;
		}

		this.sections.remove(includedSection);
		this.sections.add(
			new Section(
				line,
				includedSection.getUpStation(),
				upStation,
				includedSection.getDistance() - distance
			)
		);
		this.sections.add(new Section(line, upStation, downStation, distance));
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
}
