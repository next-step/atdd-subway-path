package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if(sections.isEmpty()) {
            sections.add(section);
            return;
        }

        boolean isExistUpStation = isExistStation(section.getUpStation());
        boolean isExistDownStation = isExistStation(section.getDownStation());

        if(isExistUpStation) {
            addSectionBasedOnUpStation(section);
        }

        if(isExistDownStation) {
            addSectionBasedOnDownStation(section);
        }
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElse(null);
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElse(null);
    }

    private void addSectionBasedOnUpStation(Section section) {
        Section findSection = findSectionByUpStation(section.getUpStation());
        if(Objects.isNull(findSection)) {
            sections.add(section);
            return;
        }

        int index = sections.indexOf(findSection);
        Line line = findSection.getLine();

        int distance = findSection.getDistance() - section.getDistance();
        Section nextSection = Section.builder()
                .line(line)
                .upStation(section.getDownStation())
                .downStation(findSection.getDownStation())
                .distance(distance)
                .build();

        // sections.remove(findSection);
        // sections.add(section);
        sections.set(index, section);
        sections.add(index + 1, nextSection);
    }

    private void addSectionBasedOnDownStation(Section section) {
        Section findSection = findSectionByDownStation(section.getDownStation());
        if(Objects.isNull(findSection)) {
            sections.add(section);
            return;
        }

        int index = sections.indexOf(findSection);
        Line line = findSection.getLine();

        int distance = findSection.getDistance() - section.getDistance();
        Section prevSection = Section.builder()
                .line(line)
                .upStation(findSection.getUpStation())
                .downStation(section.getUpStation())
                .distance(distance)
                .build();

       // sections.remove(findSection);
        //sections.add(section);
        sections.set(index, prevSection);
        sections.add(index + 1, section);
    }

    private boolean isExistStation(Station station) {
        return getAllStation().contains(station);
    }

    public List<Station> getAllStation() {
        if(sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> stations = new ArrayList<>();
        Section firstSection = sections.stream()
                .filter(section -> Objects.isNull(findSectionByDownStation(section.getUpStation())))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        stations.add(firstSection.getUpStation());
        addStationInOrder(stations, firstSection.getDownStation());
        return stations;
    }

    private void addStationInOrder(List<Station> stations, Station downStation) {
        stations.add(downStation);
        Section section = findSectionByUpStation(downStation);

        if(Objects.nonNull(section)) {
            addStationInOrder(stations, section.getDownStation());
        }
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    private List<Long> getStationIds() {
        return getAllStation().stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }

    public Station getDownStation() {
        return getAllStation().get(getAllStation().size() - 1);
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public boolean isRegisteredStation(Station station) {
        return getAllStation().contains(station);
    }

    public boolean canDelete() {
        return sections.size() > 1;
    }

    public boolean isDownStation(Station station) {
        return getDownStation().equals(station);
    }
}
