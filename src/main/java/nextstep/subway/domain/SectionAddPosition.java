package nextstep.subway.domain;

import java.util.List;
import java.util.stream.IntStream;

public enum SectionAddPosition {
	FIRST((List<Section> sections, Section section) -> sections.add(0, section)),
	LAST(List::add),
	MIDDLE((List<Section> sections, Section section) -> {
		int index = IntStream.range(0, sections.size())
			.filter(i-> sections.get(i).getDownStation().equals(section.getUpStation()))
			.findFirst()
			.orElse(0);
		if(sections.get(index).getDistance() <= section.getDistance()) {
			throw new IllegalArgumentException("The distance is too large");
		}
		sections.add(index, section);
	});

	private final AddStationFunction action;

	SectionAddPosition(AddStationFunction action) {
		this.action = action;
	}

	public static SectionAddPosition from(Sections sections, Section section) {
		validate(sections, section);

		if(isLastSection(sections, section)) {
			return LAST;
		}
		if(isFirstSection(sections, section)) {
			return FIRST;
		}
		return MIDDLE;
	}

	private static void validate(Sections sections, Section section) {
		if(sections.isEmpty()){
			return;
		}

		// 상행과 하행이 모두 새로운 역이면 에러
		if(!sections.getStations().contains(section.getUpStation()) && !sections.getStations().contains(section.getDownStation())) {
			throw new IllegalArgumentException("Station does not match");
		}

		// 상행과 하행이 모두 등록되어 있으면 에러
		if(sections.getStations().contains(section.getUpStation()) && sections.getStations().contains(section.getDownStation())) {
			throw new IllegalArgumentException("This section already exists");
		}
	}

	private static boolean isLastSection(Sections sections, Section section) {
		return sections.isEmpty() || sections.lastStation() == section.getUpStation();
	}

	private static boolean isFirstSection(Sections sections, Section section) {
		return sections.firstStation() == section.getDownStation();
	}

	public void add(List<Section> sections, Section section) {
		this.action.add(sections, section);
	}

	@FunctionalInterface
	public interface AddStationFunction {
		void add(List<Section> sections, Section section);
	}
}
