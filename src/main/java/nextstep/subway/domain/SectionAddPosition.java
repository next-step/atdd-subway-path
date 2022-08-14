package nextstep.subway.domain;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import nextstep.subway.ui.SubwayErrorCode;
import nextstep.subway.ui.SubwayException;

public enum SectionAddPosition {
	FIRST(isFirst(), addFirst()),
	LAST(isLast(), addLast()),
	MIDDLE(isMiddle(), addMiddle());

	private final BiFunction<Sections, Section, Boolean> position;
	private final BiConsumer<Sections, Section> action;

	SectionAddPosition(BiFunction<Sections, Section, Boolean> position, BiConsumer<Sections, Section> action) {
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
		this.action.accept(sections, section);
	}

	private boolean find(Sections sections, Section section) {
		return this.position.apply(sections, section);
	}

	private static BiFunction<Sections, Section, Boolean> isFirst() {
		return (Sections sections, Section section) -> !sections.isEmpty()
			&& sections.firstStation() == section.getDownStation();
	}

	private static BiConsumer<Sections, Section> addFirst() {
		return (Sections sections, Section section) -> sections.addById(0, section);
	}

	private static BiFunction<Sections, Section, Boolean> isLast() {
		return (Sections sections, Section section) -> sections.isEmpty()
			|| sections.lastStation() == section.getUpStation();
	}

	private static BiConsumer<Sections, Section> addLast() {
		return Sections::addToLast;
	}

	private static BiFunction<Sections, Section, Boolean> isMiddle() {
		return (Sections sections, Section section) -> sections.contains(section.getUpStation()) || sections.contains(
			section.getDownStation());
	}

	private static BiConsumer<Sections, Section> addMiddle() {
		return (Sections sections, Section section) -> {
			int index = IntStream.range(0, sections.size())
				.filter(i -> sections.getDownStationById(i).equals(section.getUpStation()))
				.findFirst()
				.orElse(0);
			if (sections.getDistanceById(index) <= section.getDistance()) {
				throw new SubwayException(SubwayErrorCode.INVALID_DISTANCE);
			}
			sections.getSections().add(index, section);
		};
	}
}
