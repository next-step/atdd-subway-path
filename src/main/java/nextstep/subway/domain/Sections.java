package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.DuplicateException;
import nextstep.subway.applicaion.exception.NotExistSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    private final String NOT_EXIST_STATION = "해당 역들은 노선 내 구간에 존재하지 않습니다.";

    protected Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.stream()
                .sorted()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Line line, Station upStation, Station downStation, Distance distance) {
        final Section section = Section.of(line, upStation, downStation, distance);

        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        boolean existsUpStation = existByStation(upStation);
        boolean existsDownStation = existByStation(downStation);

        if (existsUpStation && existsDownStation) {
            throw new DuplicateException(String.format("%s %s", upStation.getName(), downStation.getName()));
        }

        if (!existsUpStation && !existsDownStation) {
            throw new IllegalArgumentException(NOT_EXIST_STATION);
        }

        if (existsUpStation) {
            appendSection(section);
            return;
        }

        prependSection(section);
    }

    private boolean existByStation(Station station) {
        return sections.stream()
                .anyMatch(section -> station.equals(section.getUpStation()) || station.equals(section.getDownStation()));
    }

    private void appendSection(Section section) {
	    final Station upStation = section.getUpStation();

	    if (equalsLastStation(upStation)) {
		    sections.add(section);
		    return;
	    }

	    Section relatedSection = getRelatedUpStationSection(upStation)
			    .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_STATION));

	    relatedSection.upStationUpdate(section.getDownStation());
	    relatedSection.divideDistance(section.getDistance());
	    sections.add(section);
    }

	private boolean equalsLastStation(Station upStation) {
		List<Station> stations = getStations();
		Station lastStation = stations.get(stations.size() - 1);

		return lastStation.equals(upStation);
	}

	private Optional<Section> getRelatedUpStationSection(Station upStation) {
		return sections.stream()
				.filter(generatedSection -> upStation.equals(generatedSection.getUpStation()))
				.findAny();
	}

	private void prependSection(Section section) {
		final Station downStation = section.getDownStation();

		if (equalsFirstStation(downStation)) {
			sections.add(section);
			return;
		}

		Section relatedSection = getRelatedDownStationSection(downStation)
				.orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_STATION));

		relatedSection.downStationUpdate(section.getUpStation());
		relatedSection.divideDistance(section.getDistance());
		sections.add(section);
	}

	private boolean equalsFirstStation(Station downStation) {
		List<Station> stations = getStations();
		Station firstStation = stations.get(0);

		return firstStation.equals(downStation);
	}

	private Optional<Section> getRelatedDownStationSection(Station downStation) {
		return sections.stream()
				.filter(generatedSection -> downStation.equals(generatedSection.getDownStation()))
				.findFirst();
	}

	public int count() {
		return sections.size();
	}

	public void deleteSection(Station station) {

		Section relatedUpStationSection = getRelatedUpStationSection(station).orElse(null);
		Section relatedDownStationSection = getRelatedDownStationSection(station).orElse(null);

		if (relatedUpStationSection == null && relatedDownStationSection == null) {
			throw new NotExistSectionException(station.getName());
		}

		if (relatedUpStationSection != null && relatedDownStationSection != null) {
			mergeSection(relatedUpStationSection, relatedDownStationSection);
			return;
		}

		if (relatedDownStationSection != null) {
			exceptSection(relatedDownStationSection);
			return;
		}

		exceptSection(relatedUpStationSection);
	}

	private void mergeSection(Section relatedUpStationSection, Section relatedDownStationSection) {
		Station appendStation = relatedUpStationSection.getDownStation();

		relatedDownStationSection.downStationUpdate(appendStation);
		relatedDownStationSection.addDistance(relatedUpStationSection.getDistance());

		sections.remove(relatedUpStationSection);
	}

	private void exceptSection(Section relatedStationSection) {
		sections.remove(relatedStationSection);
	}
}
