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
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Station upStation, Station downStation, int distance) {
        this.sections = new ArrayList<>(List.of(new Section(upStation, downStation, distance)));
    }

    public List<Station> getStations() {
        List<Section> answerSection = new ArrayList<>();
        Optional<Section> nowSectionOption = getTopSection();
        while (nowSectionOption.isPresent()) {
            Section nowSection = nowSectionOption.get();
            answerSection.add(nowSection);
            nowSectionOption = sections.stream()
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

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
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
        sections.add(newSection);
    }

    public void deleteSection(Station station) {
        if (sections.size() == 1) {
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
            sections.remove(sameDownStationSection.get());
            return;
        }
        if (sameUpStationSectionExists) {
            sections.remove(sameUpStationSection.get());
            return;
        }
        throw new StationNotIncludedException();
    }

    private void deleteCenterSection(Section upStationSection, Section downStationSection) {
        sections.remove(upStationSection);
        sections.remove(downStationSection);
        sections.add(new Section(upStationSection.getUpStation(), downStationSection.getDownStation(),
                upStationSection.getDistance() + downStationSection.getDistance()));
    }

    private Optional<Section> getSameUpStationOfSection(Section newSection) {
        return getSameUpStationSection(newSection.getUpStation());
    }

    private Optional<Section> getSameDownStationOfSection(Section newSection) {
        return getSameDownStationSection(newSection.getDownStation());
    }

    public Optional<Section> getSameUpStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findFirst();
    }

    public Optional<Section> getSameDownStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findFirst();
    }

    private void insertSection(Section insertSection, Section targetSection, StationType stationType) {
        validDistance(insertSection, targetSection);
        sections.remove(targetSection);
        sections.add(insertSection);
        if (stationType.equals(StationType.UP)) {
            sections.add(new Section(targetSection.getUpStation(), insertSection.getUpStation(),
                    targetSection.getDistance() - insertSection.getDistance()));
            return;
        }
        sections.add(new Section(insertSection.getDownStation(), targetSection.getDownStation(),
                targetSection.getDistance() - insertSection.getDistance()));
    }

    private void checkSection(Section newSection) {
        List<Station> stations = getStations();
        boolean doesContainsDownStation = stations.contains(newSection.getDownStation());
        boolean doesContainsUpStation = stations.contains(newSection.getUpStation());
        if (doesContainsDownStation && doesContainsUpStation) {
            throw new AlreadyConnectedException();
        }
        if (!sections.isEmpty() && !doesContainsDownStation && !doesContainsUpStation) {
            throw new MissingStationException();
        }
    }

    private Optional<Section> getTopSection() {
        return sections.stream()
                .filter(this::isTopSection)
                .findFirst();
    }

    private boolean isTopSection(Section targetSection) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().equals(targetSection.getUpStation()));
    }
}
