package nextstep.subway.api.domain.model.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.api.domain.dto.inport.LineCreateCommand;
import nextstep.subway.common.mapper.ModelMapperBasedObjectMapper;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Line {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(length = 20, nullable = false)
	private String name;

	@Column(length = 20, nullable = false)
	private String color;

	@Embedded
	private Sections sectionCollection = new Sections();

	public static Line from(LineCreateCommand createCommand) {
		return ModelMapperBasedObjectMapper.convert(createCommand, Line.class);
	}

	public Line updateName(String name) {
		this.name = name;
		return this;
	}

	public Line updateColor(String color) {
		this.color = color;
		return this;
	}

	public boolean isContainsAnyStation(Long stationId) {
		return sectionCollection.isContainsAnyStation(stationId);
	}

	public boolean isContainsAnyStation(Long... stationIds) {
		for (Long stationId : stationIds) {
			if (sectionCollection.isContainsAnyStation(stationId)) {
				return true;
			}
		}
		return false;
	}

	public void addSection(Section section) {
		this.sectionCollection.addSection(section);
	}

	public boolean isSectionCountBelowThreshold(long size) {
		return sectionCollection.isSizeBelow(size);
	}

	public void removeLastSection() {
		sectionCollection.removeLastSection();
	}

	public boolean isNotDownEndStation(Long stationId) {
		return !isDownEndStation(stationId);
	}

	public boolean isDownEndStation(Long stationId) {
		return sectionCollection.isDownEndStation(stationId);
	}

	public List<Station> parseStations() {
		return this.sectionCollection.parseStations();
	}

	public List<Section> parseSections() {
		return this.sectionCollection.parseSections();
	}

	public void insertSection(Section newSection) {
		sectionCollection.insertSection(newSection);
	}

	public void removeStation(Long stationId) {
		sectionCollection.removeStation(stationId);
	}
}
