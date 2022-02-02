package nextstep.subway.domain;

import nextstep.subway.exception.DeleteSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        Optional<Section> sameUpStation = findSectionEqualsUpStation(upStation);

        if (sameUpStation.isPresent()) {
            Section existSection = sameUpStation.get();
            Station existDownStation = existSection.getDownStation();
            int existSectionDistance = existSection.getDistance();
            int existSectionIndex = sections.indexOf(existSection);

            existSection.changeDownStationDistance(downStation, section.getDistance());
            section.changeStationDistance(downStation, existDownStation, existSectionDistance - section.getDistance());

            this.sections.add(existSectionIndex + 1, section);
            return;
        }

        this.sections.add(section);
    }

    private Optional<Section> findSectionEqualsUpStation(Station upStation) {
        return sections
            .stream()
            .filter(it -> it.getUpStation().equals(upStation))
            .findFirst();
    }

    public List<Section> getAllSections() {
        return sections;
    }

    public Station getLastDownStation() {
        return sections
            .stream()
            .map(Section::getDownStation)
            .reduce((a, b) -> b)
            .orElseThrow(() -> new NoSuchElementException("하행 종점역이 없습니다."));
    }

    public List<Station> getAllStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> downStations = getAllDownStations();
        Section section = findFirstSection(downStations);

        List<Station> allStations = new ArrayList<>();
        allStations.add(section.getUpStation());

        while (true) {
            Station downStation = section.getDownStation();
            Optional<Section> optionalFindSection = sections
                .stream()
                .filter(it -> it.getUpStation().equals(downStation))
                .findFirst();

            boolean findResult = optionalFindSection.isPresent();

            if (findResult) {
                section = optionalFindSection.get();
                Station upStation = section.getUpStation();

                allStations.add(upStation);
                continue;
            }

            allStations.add(section.getDownStation());

            break;
        }

        return allStations;
    }

    private Section findFirstSection(List<Station> downStations) {
        return sections
            .stream()
            .filter(it -> !downStations.contains(it.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("상행 종점이 상행역인 구간이 없습니다."));
    }

    private List<Station> getAllDownStations() {
        return sections.
            stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    public boolean isAvailableDelete() {
        return sections.size() > 1;
    }

    public void removeSection(Station station) {
        Station lastDownStation = getLastDownStation();

        if (!isAvailableDelete()) {
            throw new DeleteSectionException("구간이 1개 이하인 경우 역을 삭제할 수 없습니다.");
        }

        if (!lastDownStation.equals(station)) {
            throw new DeleteSectionException("구간에 일치하는 하행 종점역이 없습니다.");
        }

        Section delete = getByDownStation(lastDownStation);

        sections.remove(delete);
    }

    public Section getByDownStation(Station station) {
        return sections.stream()
            .filter(section -> section.getDownStation() == station)
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("일치하는 구간을 찾을 수 없습니다."));
    }
}
