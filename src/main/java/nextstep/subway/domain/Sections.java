package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collection;

import static nextstep.subway.domain.validator.SectionValidator.addSectionValidator;
import static nextstep.subway.domain.validator.SectionValidator.checkDistance;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public List<Section> getAllSections() {
		return sections;
	}

	public void addSection(Line line, Station upStation, Station downStation, int distance) {
		if (sections.isEmpty()) {
			sections.add(new Section(line, upStation, downStation, distance));
			return;
		}

		addSectionValidator(line, upStation, downStation);

		Section findedSection = sections.stream().filter(s -> s.getUpStation().equals(upStation))
				.findFirst()
				.orElse(null);

		if (findedSection != null) {
			splitSection(line, downStation, distance, findedSection);
			return;
		}

		sections.add(new Section(line, upStation, downStation, distance));
	}
	public void remove(Line line, Station oldSection) {
		if (!sections.get(sections.size() - 1).getDownStation().equals(oldSection)) {
			throw new IllegalArgumentException();
		}
		sections.remove(sections.size() - 1);
	}

	public int size() {
		return sections.size();
	}

	public boolean isEmpty() {
		return sections.isEmpty();
	}

	public List<Station> getStations() {
		return this.sections.stream()
				.map(s -> List.of(s.getUpStation(), s.getDownStation()))
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.toList());
	}

	private void splitSection(Line line, Station downStation, int distance, Section oldSection) {
		Station oldUpstation = oldSection.getUpStation();
		Station oldDownStation = oldSection.getDownStation();
		int oldDistance = oldSection.getDistance();

		checkDistance(distance, oldDistance);

		sections.remove(oldSection);

		sections.addAll(List.of(
				new Section(line, downStation, oldDownStation, oldDistance - distance),
				new Section(line, oldUpstation, downStation, distance)
		));
	}
}
