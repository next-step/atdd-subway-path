package nextstep.subway.domain;

import java.util.List;
import java.util.stream.IntStream;

public enum SectionRemovePosition {
	FIRST((List<Section> sections, Station station) -> sections.remove(0)),
	LAST((List<Section> sections, Station station) -> sections.remove(sections.size() - 1)),
	MIDDLE((List<Section> sections, Station station) -> {
		int index = IntStream.range(0, sections.size())
			.filter(i -> sections.get(i).getUpStation().equals(station))
			.findFirst()
			.orElse(sections.size() - 1);

		// 양쪽 구간 합치기
		Section upSection = sections.get(index - 1);
		Section downSection = sections.get(index);
		Section newSection = new Section(upSection.getLine(), upSection.getUpStation(), downSection.getDownStation(),
			upSection.getDistance() + downSection.getDistance());

		// 기존 구간 제거
		sections.remove(index - 1);
		sections.remove(index - 1);

		// 합친 구간 추가
		sections.add(newSection);
	});

	private final SectionRemovePosition.RemoveStationFunction action;

	SectionRemovePosition(SectionRemovePosition.RemoveStationFunction action) {
		this.action = action;
	}

	public static SectionRemovePosition from(Sections sections, Station station) {
		validate(sections, station);

		if (isLastSection(sections, station)) {
			return LAST;
		}
		if (isFirstSection(sections, station)) {
			return FIRST;
		}
		return MIDDLE;
	}

	private static void validate(Sections sections, Station station) {
		// 구간이 하나인 경우 삭제할 수 없음
		if (sections.getSections().size() <= 1) {
			throw new IllegalArgumentException("There is only one section left");
		}

		// 노선에 역이 등록되어 있지 않으면 에러
		if (!sections.getStations().contains(station)) {
			throw new IllegalArgumentException("Station does not registered");
		}
	}

	private static boolean isLastSection(Sections sections, Station station) {
		return sections.lastStation() == station;
	}

	private static boolean isFirstSection(Sections sections, Station station) {
		return sections.firstStation() == station;
	}

	public void remove(List<Section> sections, Station station) {
		this.action.remove(sections, station);
	}

	@FunctionalInterface
	public interface RemoveStationFunction {
		void remove(List<Section> sections, Station station);
	}
}
