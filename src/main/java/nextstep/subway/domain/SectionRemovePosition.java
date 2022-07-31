package nextstep.subway.domain;

import java.util.Arrays;
import java.util.stream.IntStream;

import nextstep.subway.ui.SubwayErrorCode;
import nextstep.subway.ui.SubwayException;

public enum SectionRemovePosition {
	FIRST((Sections sections, Station station) -> sections.firstStation() == station,
		(Sections sections, Station station) -> sections.removeById(0)),

	LAST((Sections sections, Station station) -> sections.lastStation() == station,
		(Sections sections, Station station) -> sections.removeById(sections.size() - 1)),

	MIDDLE(Sections::contains,
		(Sections sections, Station station) -> {
		int index = IntStream.range(0, sections.size())
			.filter(i -> sections.getUpStationById(i).equals(station))
			.findFirst()
			.orElse(sections.size() - 1);

		// 양쪽 구간 합치기
		Section upSection = sections.getSectionById(index - 1);
		Section downSection = sections.getSectionById(index);
		Section newSection = new Section(upSection.getLine(), upSection.getUpStation(), downSection.getDownStation(),
			upSection.getDistance() + downSection.getDistance());

		// 기존 구간 제거
		sections.removeById(index - 1);
		sections.removeById(index - 1);

		// 합친 구간 추가
		sections.add(newSection);
	});

	private final RemoveStationFunction action;
	private final FindPositionFunction position;

	SectionRemovePosition(FindPositionFunction position, RemoveStationFunction action) {
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
		this.action.remove(sections, station);
	}

	@FunctionalInterface
	public interface RemoveStationFunction {
		void remove(Sections sections, Station station);
	}

	private boolean find(Sections sections, Station station) {
		return this.position.find(sections, station);
	}

	@FunctionalInterface
	public interface FindPositionFunction {
		boolean find(Sections sections, Station station);
	}
}
