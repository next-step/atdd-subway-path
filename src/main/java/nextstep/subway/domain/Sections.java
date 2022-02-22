package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Embeddable
public class Sections {
    private static final String ALREADY_EXIST_SECTION_MESSAGE = "이미 존재하는 구간입니다.";
    private static final String NO_FIRST_STATION_MESSAGE = "상행 종점역이 존재하지 않습니다.";
    private static final String NO_LAST_STATION_MESSAGE = "하행 종점역이 존재하지 않습니다.";
    private static final String NO_SAME_UP_STATION_SECTION_EXIST_MESSAGE = "해당 역이 상행역인 구간이 존재하지 않습니다.";
    private static final String NO_SAME_DOWN_STATION_SECTION_EXIST_MESSAGE = "해당 역이 하행역인 구간이 존재하지 않습니다.";
    private static final String CANNOT_DELETE_MIN_SIZE_SECTION_MESSAGE = "구간이 1개일 때는 제거할 수 없습니다.";
    private static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sectionList = sections;
    }

    public void addSection(Section section) {
        if (isAlreadyExistSection(section) || nonExistStation(section)) {
            throw new IllegalArgumentException(ALREADY_EXIST_SECTION_MESSAGE);
        }

        if (isMiddleSection(section)) {
            addMiddleSection(section);
            return;
        }

        sectionList.add(section);
    }

    private boolean isAlreadyExistSection(Section section) {
        return anyMatch(section::equalsUpAndDownStation);
    }

    private boolean nonExistStation(Section section) {
        return !sectionList.isEmpty() && noneMatch(otherSection ->
                section.getUpStation().equals(otherSection.getUpStation())
                        || section.getUpStation().equals(otherSection.getDownStation())
                        || section.getDownStation().equals(otherSection.getUpStation())
                        || section.getDownStation().equals(otherSection.getDownStation())
        );
    }

    private boolean isMiddleSection(Section section) {
        return anyMatch(otherSection ->
                section.getUpStation().equals(otherSection.getUpStation())
                        || section.getDownStation().equals(otherSection.getDownStation()));
    }

    private void addMiddleSection(Section section) {
        findSectionBy(section::hasSameDownStation)
                .ifPresent(foundSection -> {
                    Section changedSection = foundSection.changeDownStation(section);
                    replaceSection(foundSection, changedSection);
                    sectionList.add(section);
                });

        findSectionBy(section::hasSameUpStation)
                .ifPresent(foundSection -> {
                    Section changedSection = foundSection.changeUpStation(section);
                    replaceSection(foundSection, changedSection);
                    sectionList.add(section);
                });
    }

    private void replaceSection(Section beforeSection, Section changedSection) {
        int index = sectionList.indexOf(beforeSection);
        sectionList.set(index, changedSection);
    }

    private Optional<Section> findSectionBy(Predicate<Section> sectionPredicate) {
        return sectionList.stream()
                .filter(sectionPredicate)
                .findFirst();
    }

    private Section findSectionBy(Predicate<Section> sectionPredicate, String message) {
        return findSectionBy(sectionPredicate)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }

    private void addNextSection(Section section, List<Station> stations) {
        stations.add(section.getDownStation());

        findSectionBy(section::isPreviousSection)
                .ifPresent(nextSection -> addNextSection(nextSection, stations));
    }

    public void deleteSection(Station station) {
        if (sectionList.size() == MIN_SECTION_SIZE) {
            throw new IllegalArgumentException(CANNOT_DELETE_MIN_SIZE_SECTION_MESSAGE);
        }

        if (nonExistStation(station)) {
            return;
        }

        if (isFirstStation(station)) {
            sectionList.remove(findFirstSection());
            return;
        }

        if (isLastStation(station)) {
            sectionList.remove(findLastSection());
            return;
        }

        removeMiddleSection(station);
    }

    private boolean nonExistStation(Station station) {
        return noneMatch(section ->
                station.equals(section.getUpStation())
                        || station.equals(section.getDownStation())
        );
    }

    private Section findFirstSection() {
        return findSectionBy(this::firstSectionCondition, NO_FIRST_STATION_MESSAGE);
    }

    private boolean firstSectionCondition(Section section) {
        return noneMatch(section::isNextSection);
    }

    private Section findLastSection() {
        return findSectionBy(this::lastSectionCondition, NO_LAST_STATION_MESSAGE);
    }

    private boolean lastSectionCondition(Section section) {
        return noneMatch(section::isPreviousSection);
    }

    private void removeMiddleSection(Station station) {
        Section upSection = findSectionBy(section -> section.getDownStation().equals(station),
                NO_SAME_DOWN_STATION_SECTION_EXIST_MESSAGE);
        Section downSection = findSectionBy(section -> section.getUpStation().equals(station),
                NO_SAME_UP_STATION_SECTION_EXIST_MESSAGE);

        combineSections(upSection, downSection);
    }

    private void combineSections(Section upSection, Section downSection) {
        int index = sectionList.indexOf(upSection);
        Section combinedUpSection = upSection.combine(downSection);
        sectionList.set(index, combinedUpSection);
        sectionList.remove(downSection);
    }

    private boolean anyMatch(Predicate<Section> sectionPredicate) {
        return sectionList.stream().anyMatch(sectionPredicate);
    }

    private boolean noneMatch(Predicate<Section> sectionPredicate) {
        return sectionList.stream().noneMatch(sectionPredicate);
    }

    private boolean isFirstStation(Station station) {
        return findFirstSection().getUpStation().equals(station);
    }

    private boolean isLastStation(Station station) {
        return findLastSection().getDownStation().equals(station);
    }

    public List<Section> getSectionList() {
        return Collections.unmodifiableList(sectionList);
    }

    public List<Station> getStationList() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        Section firstSection = findFirstSection();
        List<Station> stations = makeStationList(firstSection);

        return Collections.unmodifiableList(stations);
    }

    private List<Station> makeStationList(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());

        addNextSection(section, stations);
        return stations;
    }

    public int totalDistance() {
        return 0;
    }
}
