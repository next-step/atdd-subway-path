package nextstep.subway.domain;

import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.exception.CannotDeleteSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {

        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        boolean hasUpStation = hasUpStation(section.getUpStation());
        boolean hasDownStation = hasDownStation(section.getDownStation());

        if (hasUpStation && hasDownStation) {
            throw new CannotAddSectionException(section.upStationName(), section.downStationName());
        }

        boolean existsSection = sections.stream()
                .anyMatch(section1 -> section1.containsStations(section));
        if (!existsSection) {
            throw new CannotAddSectionException();
        }

        if (isAddFirstUpStation(section)) {
            sections.add(section);
            return;
        }
        if (isAddLastDownStation(section)) {
            sections.add(section);
            return;
        }

        Section targetSection = findCombineSection(section);
        if (section.isGreaterThanDistance(targetSection.getDistance())) {
            throw new CannotAddSectionException(targetSection.getDistance(), section.getDistance());
        }

        Section dividedSection = targetSection.divideSection(section);
        sections.remove(targetSection);
        sections.add(dividedSection);
        sections.add(section);
    }

    private boolean isAddFirstUpStation(Section section) {
        boolean addableFirstStation = findFirstUpStation().equals(section.getDownStation());
        boolean hasStation = hasStation(section.getUpStation());

        return addableFirstStation && !hasStation;
    }

    private boolean isAddLastDownStation(Section section) {
        boolean addableLastStation = findLastDownStation().equals(section.getUpStation());
        boolean hasStation = hasStation(section.getDownStation());

        return addableLastStation && !hasStation;
    }

    private Section findCombineSection(Section section) {
        return sections.stream()
                .filter(section1 -> section1.hasSameUpStation(section.getUpStation())
                        || section1.hasSameDownStation(section.getDownStation()))
                .findFirst()
                .orElseThrow(CannotAddSectionException::new);
    }

    private boolean hasStation(Station station) {
        return sections.stream()
                .anyMatch(section1 -> section1.containsStation(station));
    }

    private boolean hasUpStation(Station upStation) {
        return sections.stream()
                .anyMatch(section -> section.hasSameUpStation(upStation));
    }

    private boolean hasDownStation(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.hasSameDownStation(downStation));
    }

    private Station findFirstUpStation() {

        Station station = sections.get(0).getUpStation();
        return findFirstUpStation(station);
    }

    private Station findFirstUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(upStation))
                .findFirst()
                .map(Section::getUpStation)
                .map(this::findFirstUpStation)
                .orElse(upStation);
    }

    private Station findLastDownStation() {
        Station station = sections.get(0).getDownStation();
        return findLastDownStation(station);
    }

    private Station findLastDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findFirst()
                .map(Section::getDownStation)
                .map(this::findLastDownStation)
                .orElse(downStation);
    }

    public List<Station> stations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        Station findUpStation = findFirstUpStation();
        stations.add(findUpStation);

        addInOrderOfStations(stations, findUpStation);

        return Collections.unmodifiableList(stations);
    }

    private void addInOrderOfStations(List<Station> stations, Station findUpStation) {
        for (int i = 0; i < sections.size(); i++) {
            Station upStation = findUpStation;
            Station station = sections.stream()
                    .filter(section -> section.hasSameUpStation(upStation))
                    .findFirst()
                    .map(Section::getDownStation)
                    .orElseThrow(IllegalArgumentException::new);
            stations.add(station);
            findUpStation = station;
        }
    }

    public List<Integer> distances() {
        List<Integer> distances = new ArrayList<>();
        List<Station> stations = stations();
        for (int i = 0; i < stations.size() - 1; i++) {
            Station station = stations.get(i);
            Integer distance = sections.stream()
                    .filter(section -> section.hasSameUpStation(station))
                    .findFirst()
                    .map(Section::getDistance)
                    .orElseThrow(EntityNotFoundException::new);
            distances.add(distance);
        }
        return distances;
    }

    public void deleteSection(Station station) {
        if (!hasStation(station)) {
            throw new CannotDeleteSectionException(station.getName());
        }

        if (sections.size() == 1) {
            throw new CannotDeleteSectionException();
        }

        Section lastDownSection = findLastDownSection();
        if (lastDownSection.hasSameDownStation(station)) {
            sections.remove(lastDownSection);
            return;
        }

        Section firstUpSection = findFirstUpSection();
        if (firstUpSection.hasSameUpStation(station)) {
            sections.remove(firstUpSection);
            return;
        }

        Section containsUpStation = findSectionContainsUpStation(station);
        Section containsDownStation = findSectionContainsDownStation(station);
        containsDownStation.combineSection(containsUpStation);

        sections.remove(containsUpStation);
    }

    private Section findLastDownSection() {
        Station lastDownStation = findLastDownStation();
        return findSectionContainsDownStation(lastDownStation);
    }

    private Section findFirstUpSection() {
        Station firstUpStation = findFirstUpStation();
        return findSectionContainsUpStation(firstUpStation);
    }

    private Section findSectionContainsUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(station))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
    }

    private Section findSectionContainsDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasSameDownStation(station))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
    }
}
