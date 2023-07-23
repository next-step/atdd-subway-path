package nextstep.subway.line;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import nextstep.subway.station.Station;

@Embeddable
public class Sections {

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "line_section", joinColumns = @JoinColumn(name = "line_id"))
    private List<Section> sectionCollection = new ArrayList<>();

    public Sections() {
    }

    public Sections(Station upStation, Station downStation, int distance) {
        this.sectionCollection = new ArrayList<>(List.of(new Section(upStation, downStation, distance)));
    }

    public List<Station> getStations() {
        List<Section> answerSection = new ArrayList<>();
        Optional<Section> nowSectionOption = getTopSection();
        while (nowSectionOption.isPresent()) {
            Section nowSection = nowSectionOption.get();
            answerSection.add(nowSection);
            nowSectionOption = sectionCollection.stream()
                    .filter(section -> section.getUpStation().equals(nowSection.getDownStation()))
                    .findFirst();
        }
        return answerSection.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    private static void validDistance(Section insertSection, Section targetSection) {
        if (insertSection.getDistance() >= targetSection.getDistance()) {
            throw new InvalidDistanceException();
        }
    }

    private Optional<Section> getTopSection() {
        return sectionCollection.stream()
                .filter(this::isTopSection)
                .findFirst();
    }

    public void addSection(Section newSection) {
        if (sectionCollection.isEmpty()) {
            sectionCollection.add(newSection);
            return;
        }
        checkSection(newSection);
        Station upStation = newSection.getUpStation();
        if (existsSameUpStationSection(upStation)) {
            insertSection(newSection, getSameUpStationSection(upStation), StationType.DOWN);
            return;
        }
        Station downStation = newSection.getDownStation();
        if (existsSameDownStationSection(downStation)) {
            insertSection(newSection, getSameDownStationSection(downStation), StationType.UP);
            return;
        }
        sectionCollection.add(newSection);
    }


    private boolean isTopSection(Section targetSection) {
        return sectionCollection.stream()
                .noneMatch(section -> section.getDownStation().equals(targetSection.getUpStation()));
    }

    private void checkSection(Section newSection) {
        List<Station> stations = getStations();
        boolean doesContainsDownStation = stations.contains(newSection.getDownStation());
        boolean doesContainsUpStation = stations.contains(newSection.getUpStation());
        if (doesContainsDownStation && doesContainsUpStation) {
            throw new AlreadyConnectedException();
        }
        if (!sectionCollection.isEmpty() && !doesContainsDownStation && !doesContainsUpStation) {
            throw new MissingStationException();
        }
    }

    private boolean existsSameUpStationSection(Station station) {
        return sectionCollection.stream()
                .anyMatch(section -> section.getUpStation().equals(station));
    }

    private void insertSection(Section insertSection, Section targetSection, StationType stationType) {
        validDistance(insertSection, targetSection);
        sectionCollection.remove(targetSection);
        sectionCollection.add(insertSection);
        if (stationType.equals(StationType.UP)) {
            sectionCollection.add(new Section(targetSection.getUpStation(), insertSection.getUpStation(),
                    targetSection.getDistance() - insertSection.getDistance()));
            return;
        }
        sectionCollection.add(new Section(insertSection.getDownStation(), targetSection.getDownStation(),
                targetSection.getDistance() - insertSection.getDistance()));
    }

    private boolean existsSameDownStationSection(Station station) {
        return sectionCollection.stream()
                .anyMatch(section -> section.getDownStation().equals(station));
    }

    public Section getSameUpStationSection(Station station) {
        return sectionCollection.stream()
                .filter(section -> section.isSameUpStation(station))
                .findFirst()
                .orElseThrow(SectionNotFoundException::new);
    }

    public Section getSameDownStationSection(Station station) {
        return sectionCollection.stream()
                .filter(section -> section.isSameDownStation(station))
                .findFirst()
                .orElseThrow(SectionNotFoundException::new);
    }

    public void deleteSection(Station station) {
        checkSingleSection();
        boolean sameUpStationSectionExists = existsSameUpStationSection(station);
        boolean sameDownStationSectionExists = existsSameDownStationSection(station);
        if (sameUpStationSectionExists && sameDownStationSectionExists) {
            deleteCenterSection(getSameUpStationSection(station), getSameDownStationSection(station));
            return;
        }
        if (sameDownStationSectionExists) {
            sectionCollection.remove(getSameDownStationSection(station));
            return;
        }
        if (sameUpStationSectionExists) {
            sectionCollection.remove(getSameUpStationSection(station));
            return;
        }
        throw new StationNotIncludedException();
    }

    private void checkSingleSection() {
        if (sectionCollection.size() == 1) {
            throw new SingleSectionRemovalException();
        }
    }

    private void deleteCenterSection(Section downSection, Section upSection) {
        sectionCollection.remove(downSection);
        sectionCollection.remove(upSection);
        Station upStation = upSection.getUpStation();
        Station downStation = downSection.getDownStation();
        int distance = upSection.getDistance() + downSection.getDistance();
        sectionCollection.add(new Section(upStation, downStation, distance));
    }
}
