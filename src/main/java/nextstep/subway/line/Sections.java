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

    private static boolean sameDownStationOfTopStation(Section newSection, Section topSection) {
        return topSection.getUpStation().equals(newSection.getDownStation());
    }

    public void addAll(List<Section> sections) {
        this.sections.addAll(sections);
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

    public List<Section> getSections() {
        return sections;
    }

    public void deleteSection(Station bottomStation) {
        if (sections.size() == 1) {
            throw new SingleSectionRemovalException();
        }
        int lastIndex = sections.size() - 1;
        Section section = sections.get(lastIndex);
        if (section.isSameDownStation(bottomStation)) {
            sections.remove(section);
            return;
        }
        throw new NonDownstreamTerminusException();
    }

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        checkSection(newSection);
        Optional<Section> sameUpStationSection = getSameUpStationOfSection(newSection);
        if (sameUpStationSection.isPresent()) {
            insertByUpStation(newSection, sameUpStationSection.get());
            return;
        }
        Optional<Section> sameDownStationSection = getSameDownStationOfSection(newSection);
        if (sameDownStationSection.isPresent()) {
            insertByDownStation(newSection, sameDownStationSection.get());
            return;
        }
        Optional<Section> topSection = getTopSection();
        if (topSection.isPresent() && sameDownStationOfTopStation(newSection, topSection.get())) {
            insertTop(newSection);
            return;
        }
        insertBottom(newSection);
    }

    private Optional<Section> getSameUpStationOfSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(newSection.getUpStation()))
                .findFirst();
    }

    private void insertByUpStation(Section insertSection, Section targetSection) {
        if (insertSection.getDistance() >= targetSection.getDistance()) {
            throw new InvalidDistanceException();
        }
        sections.remove(targetSection);
        Station targetDownStation = targetSection.getDownStation();
        sections.add(insertSection);
        sections.add(new Section(insertSection.getDownStation(), targetDownStation,
                targetSection.getDistance() - insertSection.getDistance()));
    }

    private Optional<Section> getSameDownStationOfSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(newSection.getDownStation()))
                .findFirst();
    }

    private void insertByDownStation(Section inserSection, Section targetSection) {
        int targetSectionDistance = targetSection.getDistance();
        int insertSectionDistance = inserSection.getDistance();
        if (targetSectionDistance <= insertSectionDistance) {
            throw new InvalidDistanceException();
        }
        sections.remove(targetSection);
        Station targetUpStation = targetSection.getUpStation();
        sections.add(new Section(targetUpStation, inserSection.getUpStation(),
                targetSectionDistance - insertSectionDistance));
        sections.add(inserSection);
    }

    private void insertTop(Section section) {
        List<Section> targetSections = new ArrayList<>();
        targetSections.add(section);
        targetSections.addAll(sections);
        sections = targetSections;
    }

    private void insertBottom(Section newSection) {
        sections.add(newSection);
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
        return !sections.isEmpty() ? Optional.of(sections.get(0)) : Optional.empty();
    }
}
