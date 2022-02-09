package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.DuplicateException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public void add(Line line, Station upStation, Station downStation, int distance) {
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

        Section relatedSection = getRelatedUpStationSection(upStation);

        relatedSection.upStationUpdate(section.getDownStation(), section.getDistance());
        sections.add(section);
    }

    private boolean equalsLastStation(Station upStation) {
        List<Station> stations = getStations();
        Station lastStation = stations.get(stations.size() - 1);

        return lastStation.equals(upStation);
    }

    private Section getRelatedUpStationSection(Station upStation) {
        return sections.stream()
                .filter(generatedSection -> upStation.equals(generatedSection.getUpStation()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_STATION));
    }

    private void prependSection(Section section) {
        final Station downStation = section.getDownStation();

        if (equalsFirstStation(downStation)) {
            sections.add(section);
            return;
        }

        Section relatedSection = getRelatedDownStationSection(downStation);

        relatedSection.downStationUpdate(section.getUpStation(), section.getDistance());
        sections.add(section);
    }

    private boolean equalsFirstStation(Station downStation) {
        List<Station> stations = getStations();
        Station firstStation = stations.get(0);

        return firstStation.equals(downStation);
    }

    private Section getRelatedDownStationSection(Station downStation) {
        return sections.stream()
                .filter(generatedSection -> downStation.equals(generatedSection.getDownStation()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_STATION));
    }

    public Station getLastDownStation() {
        int lastIndex = sections.size() - 1;

        return sections.get(lastIndex).getDownStation();
    }

    public void deleteLastSection() {
        sections.remove(sections.size() - 1);
    }

    public int count() {
        return sections.size();
    }
}
