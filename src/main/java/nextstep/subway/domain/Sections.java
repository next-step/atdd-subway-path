package nextstep.subway.domain;

import nextstep.subway.exception.*;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public int size() {
        return sections.size();
    }

    public void add(Section newSection) {
        validateNewSection(newSection);

        if (sections.isEmpty() || shouldAddNewUpEndSection(newSection) || shouldAddNewDownEndSection(newSection)) {
            sections.add(newSection);
            return;
        }
        if (shouldAddSectionBetweenStation(newSection)) {
            addSectionBetweenStation(newSection);
            return;
        }

        throw new AnyNewSectionException();
    }

    public List<Station> getStations() {
        Station upEndStation = getUpEndStation();

        List<Station> findStations = new ArrayList<>();
        findStations.add(upEndStation);

        Stack<Station> upStationStack = new Stack<>();
        upStationStack.push(upEndStation);

        while (!upStationStack.isEmpty()) {
            Station currentStation = upStationStack.pop();
            sections.stream()
                    .filter(section -> section.isUpStation(currentStation))
                    .findAny()
                    .ifPresent(section -> {
                        Station nextStation = section.getDownStation();
                        findStations.add(nextStation);
                        upStationStack.push(nextStation);
                    });
        }

        return findStations;
    }

    public void remove(Section... section) {
        sections.removeAll(List.of(section));
    }

    public void remove(Station station) {
        validateRemoveSection(station);
        Section downSection = getDownSection(station);
        Section upSection = getUpSection(station);
        upSection.changeDownStation(downSection.getDownStation());
        upSection.addDistance(downSection.getDistance());
        sections.remove(downSection);
    }

    private void validateRemoveSection(Station station) {
        if (sections.size() < 2) {
            throw new MinimumSizeRemoveSectionException();
        }
        if (!getStationsSet().contains(station)) {
            throw new NotExistedRemoveSectionException();
        }
    }

    private Section getDownSection(Station station) {
        return sections.stream().filter(section -> section.isUpStation(station))
                .findAny().orElseThrow(SectionNotFoundException::new);
    }

    private Section getUpSection(Station station) {
        return sections.stream().filter(section -> section.isDownStation(station))
                .findAny().orElseThrow(SectionNotFoundException::new);
    }

    private Station getUpEndStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> sections.stream().noneMatch(section -> section.isDownStation(upStation)))
                .findAny()
                .orElseThrow(StationNotFoundException::new);
    }

    private Station getDownEndStation() {
        return sections.stream()
                .map(Section::getDownStation)
                .filter(downStation -> sections.stream().noneMatch(section -> section.isUpStation(downStation)))
                .findAny()
                .orElseThrow(StationNotFoundException::new);
    }

    private void addSectionBetweenStation(Section newSection) {
        Section findSection = getDownSection(newSection.getUpStation());
        findSection.changeUpStation(newSection.getDownStation());
        findSection.subtractDistance(newSection.getDistance());
        sections.add(newSection);
    }

    private boolean shouldAddSectionBetweenStation(Section newSection) {
        return sections.stream().anyMatch(section -> section.isUpStation(newSection.getUpStation()));
    }

    private boolean shouldAddNewUpEndSection(Section newSection) {
        return getUpEndStation().equals(newSection.getDownStation());
    }

    private boolean shouldAddNewDownEndSection(Section newSection) {
        return getDownEndStation().equals(newSection.getUpStation());
    }

    private void validateNewSection(Section newSection) {
        Set<Station> stationsSet = getStationsSet();
        if (stationsSet.contains(newSection.getDownStation()) && stationsSet.contains(newSection.getUpStation())) {
            throw new DuplicationNewSectionException();
        }
        if (!sections.isEmpty() && !stationsSet.contains(newSection.getDownStation())
                && !stationsSet.contains(newSection.getUpStation())) {
            throw new NotExistedNewSectionException();
        }
    }

    private Set<Station> getStationsSet() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }

    public int getTotalDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }
}
