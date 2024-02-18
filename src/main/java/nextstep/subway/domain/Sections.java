package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import nextstep.subway.exception.IllegalSectionException;

@Embeddable
public class Sections {
    public static final int ONLY_ONE_SECTION = 1;
    public static final int SECTIONS_FIRST_POSITION = 0;

    @Override
    public String toString() {
        return "Sections{" +
               "sections=" + sections +
               '}';
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    @OrderColumn
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        if (isPossibleToAddLast(newSection)) {
            sections.add(newSection);
            return;
        }
        if (isPossibleToAddFirst(newSection)) {
            sections.add(SECTIONS_FIRST_POSITION, newSection);
            return;
        }
        addSectionInTheMiddle(newSection);
    }

    private boolean isPossibleToAddFirst(Section newSection) {
        Section firstSection = sections.get(0);
        if (firstSection.isSameWithUpStation(newSection.getDownStation())
            && notExistStationInSections(newSection.getUpStation())) {
            return true;
        }
        return false;
    }

    private boolean isPossibleToAddLast(Section newSection) {
        Section lastSection = sections.get(sections.size() - 1);
        if (lastSection.isSameWithDownStation(newSection.getUpStation())
            && notExistStationInSections(newSection.getDownStation())) {
            return true;
        }
        return false;
    }

    private void addSectionInTheMiddle(Section newSection) {
        int sectionPosition = findSectionPositionByUpStation(newSection.getUpStation());
        Section existingSection = sections.get(sectionPosition);
        if (existingSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalSectionException("추가하려는 구간의 길이가 길어 기존 구간 사이에 추가할 수 없습니다.");
        }
        Section middleSection = Section.createMiddleSection(existingSection, newSection);
        sections.remove(sectionPosition);
        sections.add(sectionPosition, newSection);
        sections.add(sectionPosition + 1, middleSection);
    }

    private int findSectionPositionByUpStation(Station station) {
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            if (section.isSameWithUpStation(station)) {
                return i;
            }
        }
        throw new IllegalSectionException("해당 역을 상행역으로 가진 구간이 해당 노선에 존재하지 않습니다");
    }

    private int findSectionPositionByDownStation(Station station) {
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            if (section.isSameWithDownStation(station)) {
                return i;
            }
        }
        throw new IllegalSectionException("해당 역을 하행역으로 가진 구간이 해당 노선에 존재하지 않습니다");
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void removeSection(Station station) {
        if (ONLY_ONE_SECTION == sections.size()) {
            throw new IllegalSectionException("해당 노선에 구간이 1개만 남아 있어 삭제할 수 없습니다.");
        }
        if (notExistStationInSections(station)) {
            throw new IllegalSectionException("노선에 해당 역이 존재하지 않습니다.");
        }
        if (isLastDownStation(station)) {
            sections.remove(sections.size() - 1);
            return;
        }
        if (isFirstUpStation(station)) {
            sections.remove(0);
            return;
        }
        removeSectionInTheMiddle(station);
    }

    private void removeSectionInTheMiddle(Station station) {
        int sectionPosition = findSectionPositionByDownStation(station);
        Section prevSectionToRemove = sections.get(sectionPosition);
        Section nextSectionToRemove = sections.get(sectionPosition + 1);
        Section sectionAfterRemove = Section.of(prevSectionToRemove.getUpStation(),
                                                nextSectionToRemove.getDownStation(),
                                                prevSectionToRemove.getDistance() + nextSectionToRemove.getDistance());
        sections.remove(sectionPosition); // remove prevSectionToRemove
        sections.remove(sectionPosition); // remove nextSectionToRemove
        sections.add(sectionPosition, sectionAfterRemove);
    }
    private boolean isLastDownStation(Station station) {
        Section lastSection = sections.get(sections.size() - 1);
        return lastSection.isSameWithDownStation(station);
    }

    private boolean isFirstUpStation(Station station) {
        Section firstSection = sections.get(0);
        return firstSection.isSameWithUpStation(station);
    }
    public Stations getStations() {
        Set<Station> orderedStations = sections.stream()
                                        .flatMap(section ->
                                                      Stream.of(section.getUpStation(), section.getDownStation()))
                                        .collect(Collectors.toCollection(LinkedHashSet::new));
        List<Station> stationList = new ArrayList<>(orderedStations);
        return Stations.of(stationList);
    }

    private boolean notExistStationInSections(Station station) {
        return !sections.stream()
                        .flatMap(section ->
                                     Stream.of(section.getUpStation(), section.getDownStation()))
                        .distinct()
                        .collect(Collectors.toList())
                        .contains(station);
    }
}
