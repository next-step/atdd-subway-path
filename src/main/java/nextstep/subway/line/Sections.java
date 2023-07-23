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
        Optional<Section> sameUpStationSection = getSameUpStationOfSection(newSection);
        if (sameUpStationSection.isPresent()) {
            insertSection(newSection, sameUpStationSection.get(), StationType.DOWN);
            return;
        }
        Optional<Section> sameDownStationSection = getSameDownStationOfSection(newSection);
        if (sameDownStationSection.isPresent()) {
            insertSection(newSection, sameDownStationSection.get(), StationType.UP);
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

    private Optional<Section> getSameUpStationOfSection(Section newSection) {
        return getSameUpStationSection(newSection.getUpStation());
    }

    public Optional<Section> getSameUpStationSection(Station station) {
        return sectionCollection.stream()
                .filter(section -> section.isSameUpStation(station))
                .findFirst();
    }

    private Optional<Section> getSameDownStationOfSection(Section newSection) {
        return getSameDownStationSection(newSection.getDownStation());
    }

    public Optional<Section> getSameDownStationSection(Station station) {
        return sectionCollection.stream()
                .filter(section -> section.isSameDownStation(station))
                .findFirst();
    }

    public void deleteSection(Station station) {
        if (sectionCollection.size() == 1) {
            throw new SingleSectionRemovalException();
        }
        Optional<Section> sameDownStationSection = getSameDownStationSection(station);
        Optional<Section> sameUpStationSection = getSameUpStationSection(station);
        boolean sameUpStationSectionExists = sameUpStationSection.isPresent();
        boolean sameDownStationSectionExists = sameDownStationSection.isPresent();
        if (sameUpStationSectionExists && sameDownStationSectionExists) {
            deleteCenterSection(sameDownStationSection.get(), sameUpStationSection.get());
            return;
        }
        if (!sameUpStationSectionExists && sameDownStationSectionExists) {
            sectionCollection.remove(sameDownStationSection.get());
            return;
        }
        if (sameUpStationSectionExists) {
            sectionCollection.remove(sameUpStationSection.get());
            return;
        }
        throw new StationNotIncludedException();
    }

    private void deleteCenterSection(Section upStationSection, Section downStationSection) {
        sectionCollection.remove(upStationSection);
        sectionCollection.remove(downStationSection);
        sectionCollection.add(new Section(upStationSection.getUpStation(), downStationSection.getDownStation(),
                upStationSection.getDistance() + downStationSection.getDistance()));
    }
}
