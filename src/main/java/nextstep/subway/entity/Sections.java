package nextstep.subway.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    public static final int MIN_DELETE_REQUIRED_SECTIONS_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section sectionToAdd) {
        if (hasNoSections()) {
            sections.add(sectionToAdd);
            return;
        }

        sortByConnectedSections(sections);

        if (isAddLastSection(sectionToAdd)) {
            sections.add(sectionToAdd);
            return;
        }
        insertSection(sectionToAdd);
    }

    public boolean isDeletionAllowed() {
        return sections.size() > MIN_DELETE_REQUIRED_SECTIONS_SIZE;
    }

    public boolean canDeleteSection(Station stationToDelete) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("해당 노선에 구간이 존재하지 않습니다.");
        }
        if (!isDeletionAllowed()) {
            throw new IllegalArgumentException("구간이 최소 2개 이상일 경우에만 삭제할 수 있습니다.");
        }
        if (!getAllStations().contains(stationToDelete)) {
            throw new IllegalArgumentException("해당 역이 구간에 존재하지 않습니다.");
        }
        return true;
    }

    public void deleteSection(Line line, Station stationToDelete) {
        sortByConnectedSections(sections);

        if (isFirstStation(stationToDelete)) {
            sections.remove(findFirstSection());
            return;
        }

        if (isLastStation(stationToDelete)) {
            sections.remove(findLastSection());
            return;
        }
        deleteIntermediateStation(line, stationToDelete);
    }

    private void deleteIntermediateStation(Line line, Station stationToDelete) {
        Section commonSection = findCommonSection(stationToDelete);
        replaceAndConnectSection(commonSection, findNextSection(commonSection));
    }

    private Section findNextSection(Section commonSection) {
        return sections.get(sections.indexOf(commonSection) + 1);
    }

    private void replaceAndConnectSection(Section sectionToDelete, Section nextSection) {
        sections.remove(sectionToDelete);
        sections.remove(nextSection);
        sections.add(createAndConnectNewSection(sectionToDelete, nextSection));
    }

    private Section createAndConnectNewSection(Section commonSection, Section nextSectionBasedOnCommonSection) {
        return new Section(
                commonSection.getUpStation(),
                nextSectionBasedOnCommonSection.getDownStation(),
                commonSection.getDistance() + nextSectionBasedOnCommonSection.getDistance(),
                commonSection.getLine());
    }

    private Section findCommonSection(Station station) {
        return sections.stream()
                .filter(section -> section.isAtLeastOneSameStation(station))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Section findLastSection() {
        return sections.get(sections.size() - 1);
    }

    private Section findFirstSection() {
        return sections.get(0);
    }

    private boolean isLastStation(Station stationToDelete) {
        return findLastStation().isSame(stationToDelete);
    }

    private List<Section> sortByConnectedSections(List<Section> sections) {
        LinkedList<Section> linkedSections = new LinkedList<>();
        List<Section> sectionsToRemove = new ArrayList<>();

        Section firstSection = sections.get(0);
        linkedSections.add(firstSection);
        sectionsToRemove.add(firstSection);

        while (!sections.isEmpty()) {
            for (Section section : sections) {
                if (canConnectBefore(linkedSections.getFirst(), section)) {
                    linkedSections.addFirst(section);
                    sectionsToRemove.add(section);
                    break;
                }

                if (canConnectNext(linkedSections.getLast(), section)) {
                    linkedSections.addLast(section);
                    sectionsToRemove.add(section);
                }
            }
            sections.removeAll(sectionsToRemove);
        }

        sections.clear();
        sections.addAll(linkedSections);
        return sections;
    }

    private boolean canConnectBefore(Section targetSection, Section sectionToConnect) {
        return targetSection.canPrependSection(sectionToConnect);
    }

    private boolean canConnectNext(Section targetSection, Section sectionToConnect) {
        return targetSection.canAppendSection(sectionToConnect);
    }

    public Station findLastStation() {
        if (sections.isEmpty()) {
            throw new RuntimeException();
        }
        return sections.get(sections.size() - 1).getDownStation();
    }

    public boolean hasExistingStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isAtLeastOneSameStation(station));
    }

    public boolean hasNoSections() {
        return sections.isEmpty();
    }

    private void insertSection(Section sectionToInsert) {
        Section commonSection = findSectionContainingStation(sectionToInsert);
        Station commonStation = findCommonStation(commonSection, sectionToInsert);

        if (isFirstStation(commonStation)) {
            insertBasedOnFirstStation(sectionToInsert, commonSection, commonStation);
            return;
        }
        insertBasedOnIntermediateStation(sectionToInsert, commonStation, commonSection);
    }

    private void insertBasedOnIntermediateStation(Section sectionToInsert, Station commonStation, Section commonSection) {
        if (canPrependSectionBasedOnStation(sectionToInsert, commonStation)) {
            prependSection(sectionToInsert, commonSection);
            return;
        }
        if (canAppendSectionBasedOnStation(sectionToInsert, commonStation)) {
            appendSection(sectionToInsert, findNextSection(commonSection));
        }
    }

    private void insertBasedOnFirstStation(Section sectionToInsert, Section commonSection, Station firstStation) {
        if (canPrependSectionBasedOnStation(sectionToInsert, firstStation)) {
            sections.add(sectionToInsert);
            return;
        }
        if (canAppendSectionBasedOnStation(sectionToInsert, firstStation)) {
            appendSection(sectionToInsert, commonSection);
        }
    }

    private void prependSection(Section newSectionToInsert, Section previousSectionToConnect) {
        sections.add(newSectionToInsert);
        sections.add(createPreviousSection(newSectionToInsert, previousSectionToConnect));
        sections.remove(previousSectionToConnect);
    }

    private void appendSection(Section newSectionToInsert, Section nextSection) {
        sections.add(newSectionToInsert);
        sections.add(createNextSection(newSectionToInsert, nextSection));
        sections.remove(nextSection);
    }

    private Station findCommonStation(Section commonSectionAtInsertion, Section sectionToInsert) {
        return commonSectionAtInsertion.findCommonStation(sectionToInsert);
    }

    private boolean canAppendSectionBasedOnStation(Section sectionToInsert, Station commonStation) {
        return commonStation.isSame(sectionToInsert.getUpStation());
    }

    private boolean canPrependSectionBasedOnStation(Section sectionToInsert, Station commonStation) {
        return commonStation.isSame(sectionToInsert.getDownStation());
    }

    private boolean isFirstStation(Station insertionStation) {
        return sections.get(0).getUpStation().isSame(insertionStation);
    }

    private Section createPreviousSection(Section sectionToInsert, Section sectionOfIndex) {
        return new Section(
                sectionOfIndex.getUpStation(),
                sectionToInsert.getUpStation(),
                sectionOfIndex.getDistance() - sectionToInsert.getDistance(),
                sectionToInsert.getLine());
    }

    private Section createNextSection(Section sectionToInsert, Section sectionOfIndex) {
        return new Section(
                sectionToInsert.getDownStation(),
                sectionOfIndex.getDownStation(),
                sectionOfIndex.getDistance() - sectionToInsert.getDistance(),
                sectionToInsert.getLine());
    }

    private Section findSectionContainingStation(Section sectionToInsert) {
        return sections.stream()
                .filter(section -> section.isAtLeastOneSameStation(sectionToInsert))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private boolean isAddLastSection(Section section) {
        return canAppendSectionBasedOnStation(section, findLastStation());
    }

    public List<Section> getAllSections() {
        return sortByConnectedSections(sections);
    }

    public List<Station> getAllStations() {
        Set<Station> allStations = new HashSet<>();
        sections.forEach(section -> {
            allStations.add(section.getUpStation());
            allStations.add(section.getDownStation());
        });
        return new ArrayList<>(allStations);
    }
}
