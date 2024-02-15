package nextstep.subway.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {

    public static final int MIN_DELETE_REQUIRED_SECTIONS_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section sectionToAdd) {
        if (isAddFirstSection()) {
            sections.add(sectionToAdd);
            return;
        }
        if (isAddLastSection(sectionToAdd)) {
            sections.add(sectionToAdd);
            return;
        }
        insertSection(sectionToAdd);
    }

    public boolean isDeletionAllowed() {
        return sections.size() > MIN_DELETE_REQUIRED_SECTIONS_SIZE;
    }

    public boolean canSectionDelete(Station stationToDelete) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("해당 노선에 구간이 존재하지 않습니다.");
        }
        if (!isDeletionAllowed()) {
            throw new IllegalArgumentException("해당 노선에 구간이 최소 2개 이상일 경우 삭제가 가능합니다.");
        }
        if (!findLastStation().isSame(stationToDelete)) {
            throw new IllegalArgumentException("마지막 구간의 하행역과 동일하지 않습니다.");
        }
        return true;
    }

    public void deleteLastSection() {
        sections.remove(sections.size() - 1);
    }

    public Station findLastStation() {
        if (sections.isEmpty()) {
            throw new RuntimeException();
        }
        return sections.get(sections.size() - 1).getDownStation();
    }

    public List<Station> getAllStations() {
        Set<Station> allStations = new HashSet<>();
        sections.forEach(section -> {
            allStations.add(section.getUpStation());
            allStations.add(section.getDownStation());
        });
        return new ArrayList<>(allStations);
    }

    public boolean hasExistingStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isAtLeastOneSameStation(station));
    }

    public boolean hasNoSections() {
        return sections.isEmpty();
    }

    private boolean isAddFirstSection() {
        return sections.isEmpty();
    }

    private void insertSection(Section sectionToAdd) {
        Section insertionPoint = findInsertionPointSection(sectionToAdd);
        Station commonStationAtInsertion = findCommonStation(insertionPoint, sectionToAdd);

        if (isFirstStation(commonStationAtInsertion)) {
            handleInsertForFirstStation(sectionToAdd, commonStationAtInsertion, insertionPoint);
            return;
        }
        handleInsertForIntermediateStation(sectionToAdd, commonStationAtInsertion, insertionPoint);
    }

    private void handleInsertForIntermediateStation(Section sectionToAdd, Station commonStationAtInsertion, Section insertionPoint) {
        if (isNextInsertion(sectionToAdd, commonStationAtInsertion)) {
            Section nextSection = sections.get(sections.indexOf(insertionPoint) + 1);
            insertSection(sectionToAdd, createNextSection(sectionToAdd, nextSection), nextSection);
            return;
        }

        if (isPreviousInsertion(sectionToAdd, commonStationAtInsertion)) {
            insertSection(sectionToAdd, createPreviousSection(sectionToAdd, insertionPoint), insertionPoint);
        }
    }

    private void handleInsertForFirstStation(Section sectionToAdd, Station commonStationAtInsertion, Section insertionPoint) {
        if (isPreviousInsertion(sectionToAdd, commonStationAtInsertion)) {
            sections.add(sectionToAdd);
            return;
        }
        if (isNextInsertion(sectionToAdd, commonStationAtInsertion)) {
            insertSection(sectionToAdd, createNextSection(sectionToAdd, insertionPoint), insertionPoint);
        }
    }

    private void insertSection(Section sectionToAdd, Section connectSection, Section sectionToRemove) {
        sections.add(sectionToAdd);
        sections.add(connectSection);
        sections.remove(sectionToRemove);
    }

    private Station findCommonStation(Section insertionPoint, Section sectionToAdd) {
        return insertionPoint.findCommonStation(sectionToAdd);
    }

    private boolean isNextInsertion(Section sectionToAdd, Station insertionPointStation) {
        return insertionPointStation.isSame(sectionToAdd.getUpStation());
    }

    private boolean isPreviousInsertion(Section sectionToAdd, Station insertionPointStation) {
        return insertionPointStation.isSame(sectionToAdd.getDownStation());
    }

    private boolean isFirstStation(Station insertionStation) {
        return sections.get(0).getUpStation().isSame(insertionStation);
    }

    private Section createPreviousSection(Section sectionToAdd, Section sectionOfIndex) {
        return new Section(
                sectionOfIndex.getUpStation(),
                sectionToAdd.getUpStation(),
                sectionOfIndex.getDistance() - sectionToAdd.getDistance(),
                sectionToAdd.getLine());
    }


    private Section createNextSection(Section sectionToAdd, Section sectionOfIndex) {
        return new Section(
                sectionToAdd.getDownStation(),
                sectionOfIndex.getDownStation(),
                sectionOfIndex.getDistance() - sectionToAdd.getDistance(),
                sectionToAdd.getLine());
    }

    private Section findInsertionPointSection(Section sectionToAdd) {
        return sections.stream()
                .filter(section -> section.isAtLeastOneSameStation(sectionToAdd))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private boolean isAddLastSection(Section section) {
        return isNextInsertion(section, findLastStation());
    }

    public List<Section> getAllSections() {
        return sections;
    }
}
