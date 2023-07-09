package subway.domain;

import lombok.*;
import subway.exception.StationLineCreateException;
import subway.exception.StationLineSectionCreateException;
import subway.exception.StationLineSectionDeleteException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "lineId")
public class StationLine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long lineId;

	@Column
	private String name;

	@Column
	private String color;

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<StationLineSection> sections = new ArrayList<>();

	@Builder
	public StationLine(String name, String color, Station upStation, Station downStation, BigDecimal distance) {
		if (upStation.equals(downStation)) {
			throw new StationLineCreateException("upStation and downStation can't be equal");
		}

		this.name = name;
		this.color = color;

		createSection(upStation, downStation, distance);
	}

	public void update(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public StationLineSection createSection(Station sectionUpStation, Station sectionDownStation, BigDecimal distance) {
		checkSectionCanAdded(sectionUpStation, sectionDownStation);

		final StationLineSection section = StationLineSection.builder()
			.upStation(sectionUpStation)
			.downStation(sectionDownStation)
			.distance(distance)
			.build();

		section.apply(this);

		sections.add(section);

		return section;
	}

	private void checkSectionCanAdded(Station sectionUpStation, Station sectionDownStation) {
		if (getSections().isEmpty()) {
			return;
		}

		if (Objects.nonNull(getLineLastDownStation()) && !getLineLastDownStation().equals(sectionUpStation)) {
			throw new StationLineSectionCreateException("section up station must be equals to line last down station");
		}

		final boolean isExistSectionDownStationToLine = getAllStations().stream()
			.anyMatch(sectionDownStation::equals);

		if (isExistSectionDownStationToLine) {
			throw new StationLineSectionCreateException("section down station must not be included to line station");
		}
	}

	public void deleteSection(Station targetStation) {
		checkSectionCanDeleted(targetStation);

		final StationLineSection targetSection = getSections().stream()
			.filter(section -> targetStation.equals(section.getDownStation()))
			.findFirst()
			.orElseThrow(() -> new StationLineSectionDeleteException("not found deleted target section"));

		getSections().remove(targetSection);
	}

	private void checkSectionCanDeleted(Station targetStation) {
		if (!targetStation.equals(getLineLastDownStation())) {
			throw new StationLineSectionDeleteException("target section must be last station of line");
		}

		if (getSections().size() < 2) {
			throw new StationLineSectionDeleteException("section must be greater or equals than 2");
		}
	}

	public List<Station> getAllStations() {
		final List<Station> allUpStation = getSections().stream()
			.map(StationLineSection::getUpStation)
			.collect(Collectors.toList());

		Optional.ofNullable(getLineLastDownStation())
			.ifPresent(allUpStation::add);

		return allUpStation;
	}

	public Station getLineFirstUpStation() {
		return getSections().stream()
			.map(StationLineSection::getUpStation)
			.findFirst()
			.orElse(null);
	}

	public Station getLineLastDownStation() {
		return Optional.ofNullable(getLastSection())
			.map(StationLineSection::getDownStation)
			.orElse(null);
	}

	public StationLineSection getLastSection() {
		if (getSections().isEmpty()) {
			return null;
		}

		final int lastIndexOfSections = getSections().size() - 1;

		return getSections().get(lastIndexOfSections);
	}
}
