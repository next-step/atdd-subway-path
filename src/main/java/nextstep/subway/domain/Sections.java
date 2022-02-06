package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.domain.exception.ExceptionMessage;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public void addSection(Line line, Station upStation, Station downStation, int distance) {
		if(sections.isEmpty()) {
			sections.add(new Section(line, upStation, downStation, distance));
			return;
		}

		validDuplicationSection(upStation, downStation);
		validIncludedAnyStation(upStation, downStation);

		updateMiddleSection(line, upStation, downStation, distance);

		sections.add(new Section(line, upStation, downStation, distance));
	}

	public List<Section> getSections() {
		return Collections.unmodifiableList(sections);
	}

	private void validDuplicationSection(Station upStation, Station downStation) {
		sections.stream()
			.filter(it -> it.isDuplicateStation(upStation, downStation))
			.findFirst()
			.ifPresent(it -> { throw new IllegalArgumentException(ExceptionMessage.DUPLICATE_SECTION.getMessage()); });
	}

	private void validIncludedAnyStation(Station upStation, Station downStation) {
		sections.stream()
			.filter(it -> it.isEqualUpStation(upStation) || it.isEqualDownStation(upStation)
				|| it.isEqualUpStation(downStation) || it.isEqualDownStation(downStation))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.DO_NOT_ADD_SECTION.getMessage()));
	}

	private void updateMiddleSection(Line line, Station upStation, Station downStation, int distance) {
		int index = IntStream.range(0, sections.size())
			.filter(i -> sections.get(i).isEqualUpStation(upStation)  || sections.get(i).isEqualDownStation(downStation))
			.findFirst()
			.orElse(-1);

		if(index != -1) {
			Section section = sections.get(index);
			Section newSection = null;

			if(!section.isGraterOrEqualThanDistance(distance)) {
				throw new IllegalArgumentException(ExceptionMessage.DO_NOT_ADD_SECTION.getMessage());
			}

			if(section.isEqualUpStation(upStation)) {
				newSection = new Section(line, downStation, section.getDownStation(), section.getDistance() - distance);
			}

			if(section.isEqualDownStation(downStation)) {
				newSection = new Section(line, section.getUpStation(), upStation, section.getDistance() - distance);
			}

			sections.remove(index);
			sections.add(index, newSection);
		}
	}
}
