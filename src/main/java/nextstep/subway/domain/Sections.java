package nextstep.subway.domain;

import nextstep.subway.exception.LineMinimumSectionException;
import nextstep.subway.exception.SectionAlreadyCreateStationException;
import nextstep.subway.exception.SectionDoesNotHaveAlreadyCreateStationException;
import nextstep.subway.exception.SectionInsertDistanceTooLargeException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return ;
        }

        // 상행, 하행 둘다 노선에 있을 때 예외 처리
        if (containsStations(List.of(section.getUpStation(), section.getDownStation()))) {
            throw new SectionAlreadyCreateStationException();
        }
        if (!checkExistStation(section.getUpStation()) && !checkExistStation(section.getDownStation())) {
            throw new SectionDoesNotHaveAlreadyCreateStationException();
        }

        // 노선 상행역, 하행역에 앞, 뒤에 추가
        if (isUpStationId(section.getDownStation().getId()) || isDownStaionId(section.getUpStation().getId())) {
            sections.add(section);
            return ;
        }
        addSectionInMiddle(section);
    }

    public List<Section> getSections() {
        if (sections.isEmpty()) {
            return sections;
        }
        List<Section> sectionResults = new ArrayList<>();
        Section currSection = getFirstSection();
        sectionResults.add(currSection);
        while (true) {
            Optional<Section> nextStationOpt = getNextSection(currSection);
            if (nextStationOpt.isEmpty()) {
                break;
            }
            currSection = nextStationOpt.get();
            sectionResults.add(currSection);
        }
        return sectionResults;
    }

    private Section getLastSection() {
        return getSections().get(getLastIndex());
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }
        List<Section> currSections = getSections();
        List<Station> stations = new ArrayList<>();

        stations.add(currSections.get(0).getUpStation());
        for (Section currSection : currSections) {
            stations.add(currSection.getDownStation());
        }

        return stations;
    }

    private Optional<Section> getNextSection(Section currSection) {
        Station downStation = currSection.getDownStation();
        return sections.stream()
                .filter(section -> {
                    Station upStation = section.getUpStation();
                    return downStation.equals(upStation);
                })
                .findFirst();
    }

    private Section getFirstSection() {
        return sections.stream()
                .filter(section -> {
                    Station upStation = section.getUpStation();
                    return sections.stream()
                            .map(Section::getDownStation)
                            .noneMatch(upStation::equals);
                }).findFirst().get();
    }

    public void removeSection(Station station) {
        validateMinimumSectionSize();
        removeAndCombineSection(station);
    }

    private void removeAndCombineSection(Station station) {
        List<Section> findSections = findSectionsWithStation(station);
        sections.removeAll(findSections);
        if (isSizeTwo(findSections)) {
            Section upSection = findSections.get(0);
            Section downSection = findSections.get(1);
            sections.add(new Section(upSection.getLine(), upSection.getUpStation(), downSection.getDownStation(),
                    upSection.getDistance() + downSection.getDistance()));
        }
    }

    public boolean checkExistStation(Station station) {
        return getStations().stream()
                .anyMatch(s -> s.equals(station));
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    private boolean containsStations(List<Station> stations) {
        List<Station> existStations = getStations();
        return new HashSet<>(existStations).containsAll(stations);
    }

    private void addSectionInMiddle(Section section) {
        if (addSectionSameUpStation(section)) {
            return ;
        }
        addSectionSameDownStation(section);
    }

    private static void validateInsertSectionSize(Section section, Section existSection) {
        if (existSection.getDistance() <= section.getDistance()) {
            throw new SectionInsertDistanceTooLargeException();
        }
    }

    private boolean addSectionSameUpStation(Section section) {
        Optional<Section> optionalSectionUp = getSectionHasSameUpStation(section.getUpStation());
        if (optionalSectionUp.isPresent()) {
            Section existSection = optionalSectionUp.get();
            validateInsertSectionSize(section, existSection);
            sections.remove(existSection);
            sections.add(section);
            sections.add(new Section(section.getLine(), section.getDownStation(), existSection.getDownStation(),
                    existSection.getDistance() - section.getDistance()));
            return true;
        }
        return false;
    }

    private boolean addSectionSameDownStation(Section section) {
        Optional<Section> optionalSectionDown = getSectionHasSameDownStation(section.getDownStation());
        if (optionalSectionDown.isPresent()) {
            Section existSection = optionalSectionDown.get();
            validateInsertSectionSize(section, existSection);
            sections.remove(existSection);
            sections.add(section);
            sections.add(new Section(section.getLine(), existSection.getUpStation(), section.getUpStation(),
                    existSection.getDistance() - section.getDistance()));
            return true;
        }
        return false;
    }

    private Optional<Section> getSectionHasSameDownStation(Station downStation) {
        return getSections().stream()
                .filter(s -> s.getDownStation().equals(downStation))
                .findFirst();
    }

    private Optional<Section> getSectionHasSameUpStation(Station upStation) {
        return getSections().stream()
                .filter(s -> s.getUpStation().equals(upStation))
                .findFirst();
    }

    private boolean isDownStaionId(long id) {
        return getLastSection().getDownStation().getId().equals(id);
    }

    private boolean isUpStationId(long id) {
        return getFirstSection().getUpStation().getId().equals(id);
    }

    private void validateMinimumSectionSize() {
        if (sections.size() == 1) {
            throw new LineMinimumSectionException();
        }
    }

    private boolean isSizeTwo(List<Section> findSections) {
        return findSections.size() == 2;
    }

    private List<Section> findSectionsWithStation(Station station) {
        return getSections().stream()
                .filter(section -> {
                    Station upStation = section.getUpStation();
                    Station downStation = section.getDownStation();
                    return downStation.equals(station) || upStation.equals(station);
                }).collect(Collectors.toList());
    }
}
