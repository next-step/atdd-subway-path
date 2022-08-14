package nextstep.subway.domain;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import nextstep.subway.ui.SubwayErrorCode;
import nextstep.subway.ui.SubwayException;

public enum SectionRemovePosition {
	FIRST(isFirst(), removeFirst()),
	LAST(isLast(), removeLast()),
	MIDDLE(isMiddle(), removeMiddle());

	private final BiFunction<Sections, Station, Boolean> position;
	private final BiConsumer<Sections, Station> action;

	SectionRemovePosition(BiFunction<Sections, Station, Boolean> position, BiConsumer<Sections, Station> action) {
		this.position = position;
		this.action = action;
	}

	public static SectionRemovePosition from(Sections sections, Station station) {
		validate(sections, station);

		return Arrays.stream(values())
			.filter(position -> position.find(sections, station))
			.findFirst()
			.orElse(MIDDLE);
	}

	private static void validate(Sections sections, Station station) {
		if (sections.size() <= 1) {
			throw new SubwayException(SubwayErrorCode.NO_SECTIONS_LEFT);
		}

		if (!sections.contains(station)) {
			throw new SubwayException(SubwayErrorCode.NOT_EXIST_STATION);
		}
	}

	public void remove(Sections sections, Station station) {
		this.action.accept(sections, station);
	}

	private boolean find(Sections sections, Station station) {
		return this.position.apply(sections, station);
	}

	private static BiFunction<Sections, Station, Boolean> isFirst() {
		return (Sections sections, Station station) -> sections.firstStation() == station;
	}

	private static BiConsumer<Sections, Station> removeFirst() {
		return (Sections sections, Station station) -> sections.removeById(0);
	}

	private static BiFunction<Sections, Station, Boolean> isLast() {
		return (Sections sections, Station station) -> sections.lastStation() == station;
	}

	private static BiConsumer<Sections, Station> removeLast() {
		return (Sections sections, Station station) -> sections.removeById(sections.size() - 1);
	}

	private static BiFunction<Sections, Station, Boolean> isMiddle() {
		return Sections::contains;
	}

	private static BiConsumer<Sections, Station> removeMiddle() {
		return (Sections sections, Station station) -> {
			int index = IntStream.range(0, sections.size())
				.filter(i -> sections.getUpStationById(i).equals(station))
				.findFirst()
				.orElse(sections.size() - 1);

			// 양쪽 구간 합치기
			Section upSection = sections.getSectionById(index - 1);
			Section downSection = sections.getSectionById(index);
			Section newSection = new Section(upSection.getLine(), upSection.getUpStation(),
				downSection.getDownStation(),
				upSection.getDistance() + downSection.getDistance());

			// 기존 구간 제거
			sections.removeById(index - 1);
			sections.removeById(index - 1);

			// 합친 구간 추가
			sections.add(newSection);
		};
	}
}
