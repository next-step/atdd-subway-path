package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	protected Sections() {
		//
	}

	public List<Section> values() {

		return sections;
	}

	public void add(int index, Section section) {
		sections.add(index, section);
	}

	// 구간들중에서 해당 파라미터로 온 UpStation 인 구간을 찾기
	public Optional<Section> findUpStation(Station station) {

		return sections
						.stream()
						.filter(section -> section.getUpStation().equals(station))
						.findAny();
	}

	public Section findLastUpSection() {

		List<Station> downStations = sections
						.stream()
						.map(Section::getDownStation)
						.collect(Collectors.toList());

		return sections.stream()
						.filter(section -> !downStations.contains(section.getUpStation()))
						.findAny().orElseThrow(RuntimeException::new);
	}

	public Section findLastDownSection() {
		List<Station> upStations = sections
						.stream()
						.map(Section::getUpStation)
						.collect(Collectors.toList());

		return sections.stream()
						.filter(section -> !upStations.contains(section.getDownStation()))
						.findAny().orElseThrow(RuntimeException::new);
	}

	public boolean isEmpty() {

		return sections.isEmpty();
	}

	public int size() {

		return sections.size();
	}

	public int remove(Station station) {
		Section toBeRemovedSection = sections.stream()
						.filter(section -> section.getDownStation().equals(station))
						.findAny()
						.orElseThrow(RuntimeException::new);
		int index = sections.indexOf(toBeRemovedSection);
		sections.remove(toBeRemovedSection);

		return index;
	}

	public boolean isLastDownStation(Station station) {

		return findLastDownSection().getDownStation().equals(station);
	}

	public Optional<Section> findSectionWithUpStation(Station station) {

		return sections
						.stream()
						.filter(section -> section.getUpStation().equals(station))
						.findAny();
	}
}
