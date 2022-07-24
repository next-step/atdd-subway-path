package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		validate(section);

		if(isLastSection(section)) {
			this.sections.add(section);
			return;
		}

		if(isFirstSection(section)) {
			this.sections.add(0, section);
			return;
		}

		if(isMiddleSection(section)) {
			int index = IntStream.range(0, sections.size())
					.filter(i-> sections.get(i).getDownStation().equals(section.getUpStation()))
					.findFirst()
					.orElse(0);
			if(this.sections.get(index).getDistance() <= section.getDistance()) {
				throw new IllegalArgumentException();
			}
			this.sections.add(index, section);
		}
	}

	private void validate(Section section) {
		if(this.sections.isEmpty()){
			return;
		}

		// 상행과 하행이 모두 새로운 역이면 에러
		if(!getStations().contains(section.getUpStation()) && !getStations().contains(section.getDownStation())) {
			throw new IllegalArgumentException();
		}

		// 상행과 하행이 모두 등록되어 있으면 에러
		if(getStations().contains(section.getUpStation()) && getStations().contains(section.getDownStation())) {
			throw new IllegalArgumentException();
		}
	}

	private boolean isLastSection(Section section) {
		return this.sections.isEmpty() || lastStation() == section.getUpStation();
	}

	private boolean isFirstSection(Section section) {
		return firstStation() == section.getDownStation();
	}

	private boolean isMiddleSection(Section section) {
		return getStations().contains(section.getUpStation());
	}

	public List<Station> getStations() {
		List<Station> stations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());
		stations.add(0, this.sections.get(0).getUpStation());
		return stations;
	}

	public void remove(Station station) {
		int count = this.sections.size();
		if(count <= 1) {
			throw new IllegalArgumentException();
		}

		Section lastSection = this.sections.get(count - 1);
		if (!lastSection.getDownStation().equals(station)) {
			throw new IllegalArgumentException();
		}
		this.sections.remove(lastSection);
	}

	public Boolean isEmpty() {
		return this.sections.isEmpty();
	}

	private Station firstStation() {
		return this.sections.get(0).getUpStation();
	}

	private Station lastStation() {
		return this.sections.get(this.sections.size() - 1).getDownStation();
	}
}
