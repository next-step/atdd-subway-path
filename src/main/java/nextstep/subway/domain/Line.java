package nextstep.subway.domain;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import nextstep.subway.applicaion.dto.SectionRequest;

@Entity
public class Line {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NonNull
	private String name;
	@NonNull
	private String color;

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public void addSection(Section section) {
		this.getSections().add(section);
	}

	public List<Station> getStations() {
        List<Station> stations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());
        stations.add(0, this.sections.get(0).getUpStation());
        return stations;
	}

	public void removeSection(Station station) {
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void update(String name, String color) {
		if (name != null) {
			this.name = name;
		}
		if (color != null) {
			this.color = color;
		}
	}
}
