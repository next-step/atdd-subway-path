package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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

	public List<Station> getStations(Long finalUpStationId, Long finalDownStationId) {
		Set<Station> stations = new LinkedHashSet<>();

		Long findUpStationId = finalUpStationId;

		Map<Long, Section> upStationIdAndSectionMap = this.sections.stream()
			.collect(Collectors.toMap(
					Section::getUpStationId,
					Function.identity()
				)
			);

		while (!findUpStationId.equals(finalDownStationId)) {
			Section section = upStationIdAndSectionMap.get(findUpStationId);
			stations.add(section.getUpStation());
			stations.add(section.getDownStation());
			findUpStationId = section.getDownStationId();
		}

		return List.copyOf(stations);
	}

	public void remove(Line line, Station station) {
		if (!existingStations().contains(station)) {
			throw new SectionRemoveException(SectionErrorCode.NOT_INCLUDE_STATION);
		}

		if (this.sections.size() == SINGLE_SECTION_COUNT) {
			throw new SectionRemoveException(SectionErrorCode.SINGLE_SECTION);
		}

		Optional<Section> inFrontSection = this.sections.stream()
			.filter(it -> it.equalDownStation(station))
			.findFirst();

		Optional<Section> afterSection = this.sections.stream()
			.filter(it -> it.equalUpStation(station))
			.findFirst();

		if (inFrontSection.isPresent() && afterSection.isPresent()) {
			removeMiddleStation(line, inFrontSection.get(), afterSection.get(), station);
			return;
		}

		inFrontSection.ifPresent(section -> removeFinalDownStation(line, section));
		afterSection.ifPresent(section -> removeFinalUpStation(line, section));
	}

	public void createInitialLineSection(Station upStation, Station downStation, int distance, Line line) {
		if (distance == INVALID_SECTION_DISTANCE) {
			throw new SubwayBadRequestException(LineErrorCode.INVALID_SECTION_DISTANCE);
		}

		this.sections.add(new Section(line, upStation, downStation, distance));
	}

	private void removeFinalUpStation(Line line, Section infrontSection) {
		this.sections.remove(infrontSection);
		line.updateFinalUpStation(infrontSection.getDownStation());
	}

	private void removeFinalDownStation(Line line, Section afterSection) {
		this.sections.remove(afterSection);
		line.updateFinalDownStation(afterSection.getUpStation());
	}

	private void removeMiddleStation(Line line, Section inFrontSection, Section afterSection, Station station) {
		this.sections.remove(inFrontSection);
		this.sections.remove(afterSection);
		this.sections.add(
			new Section(
				line,
				inFrontSection.getUpStation(),
				afterSection.getDownStation(),
				inFrontSection.getDistance() + afterSection.getDistance()
			)
		);
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
		List<Station> stations = existingStations();
		return stations.contains(upStation) && stations.contains(downStation);
	}

	private List<Station> existingStations() {
		Set<Station> stations = new HashSet<>();
		stations.addAll(extractUpStations());
		stations.addAll(extractDownStations());

		return List.copyOf(stations);
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
			addSectionIfEqualUpStation(line, upStation, downStation, distance, includedSection);
			return;
		}

		addSectionIfEqualDownStation(line, upStation, downStation, distance, includedSection);
	}

	private void addSectionIfEqualUpStation(
		Line line,
		Station upStation,
		Station downStation,
		int distance,
		Section includedSection) {
		this.sections.remove(includedSection);
		this.sections.add(new Section(line, upStation, downStation, distance));
		this.sections.add(
			new Section(
				line,
				downStation,
				includedSection.getDownStation(),
				includedSection.getDistance() - distance
			)
		);
	}

	private void addSectionIfEqualDownStation(Line line,
		Station upStation,
		Station downStation,
		int distance,
		Section includedSection) {

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
