package nextstep.subway.domain;

import java.util.Arrays;
import java.util.stream.IntStream;

import nextstep.subway.ui.SubwayErrorCode;
import nextstep.subway.ui.SubwayException;

public enum SectionAddPosition {
	FIRST((Sections sections, Section section) -> !sections.isEmpty() && sections.firstStation() == section.getDownStation(),
		(Sections sections, Section section) -> sections.addById(0, section)),

	LAST((Sections sections, Section section) -> sections.isEmpty() || sections.lastStation() == section.getUpStation(),
		Sections::addToLast),

	MIDDLE((Sections sections, Section section) -> sections.contains(section.getUpStation()) || sections.contains(section.getDownStation()),
		(Sections sections, Section section) -> {
		int index = IntStream.range(0, sections.size())
			.filter(i -> sections.getDownStationById(i).equals(section.getUpStation()))
			.findFirst()
			.orElse(0);
		if (sections.getDistanceById(index) <= section.getDistance()) {
			throw new SubwayException(SubwayErrorCode.INVALID_DISTANCE);
		}
		sections.getSections().add(index, section);
	});

	private final AddStationFunction action;
	private final FindPositionFunction position;

	SectionAddPosition(FindPositionFunction position, AddStationFunction action) {
		this.action = action;
		this.position = position;
	}

	public static SectionAddPosition from(Sections sections, Section section) {
		validate(sections, section);

		return Arrays.stream(values())
			.filter(position -> position.find(sections, section))
			.findFirst()
			.orElse(MIDDLE);
	}

	private static void validate(Sections sections, Section section) {
		if (sections.isEmpty()) {
			return;
		}

		if (allNewStation(sections, section)) {
			throw new SubwayException(SubwayErrorCode.INVALID_STATION);
		}

		if (allRegistered(sections, section)) {
			throw new SubwayException(SubwayErrorCode.EXIST_STATION);
		}
	}

	private static boolean allRegistered(Sections sections, Section section) {
		return sections.contains(section.getUpStation()) && sections.contains(section.getDownStation());
	}

	private static boolean allNewStation(Sections sections, Section section) {
		return !sections.contains(section.getUpStation()) && !sections.contains(section.getDownStation());
	}

	public void add(Sections sections, Section section) {
		this.action.add(sections, section);
	}

	@FunctionalInterface
	public interface AddStationFunction {
		void add(Sections sections, Section section);
	}

	private boolean find(Sections sections, Section section) {
		return this.position.find(sections, section);
	}

	@FunctionalInterface
	public interface FindPositionFunction {
		boolean find(Sections sections, Section section);
	}
}
