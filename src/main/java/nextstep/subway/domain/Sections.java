package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Arrays.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        if (alreadyConnectStation(section)) {
            throw new IllegalArgumentException("이미 등록된 역은 등록할 수 없어요.");
        }

        if (nonExistConnectStation(section)) {
            throw new IllegalArgumentException("연결할 수 있는 역이 없어요.");
        }

        if (isAddFirstOrLastSection(section)) {
            this.sections.add(section);
            return;
        }

        if (anyMatchSection(s -> s.equalsUpStation(section.getUpStation()))) {
            Section originalSection = getSectionByCondition(s -> s.equalsUpStation(section.getUpStation()));
            originalSection.updateUpStationToSectionDownStation(section);
        }

        if (anyMatchSection(s -> s.equalsDownStation(section.getDownStation()))) {
            Section originalSection = getSectionByCondition(s -> s.equalsDownStation(section.getDownStation()));
            originalSection.updateDownStationToSectionUpStation(section);
        }

        this.sections.add(section);
    }

    public Section get(int index) {
        return this.sections.get(index);
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Station> sortedStations = new LinkedHashSet<>();
        Section section = getFirstSection();

        while (section != null) {
            sortedStations.addAll(asList(section.getUpStation(), section.getDownStation()));
            section = getNextSection(section.getDownStation());
        }

        return List.copyOf(sortedStations);
    }

    public int size() {
        return this.sections.size();
    }


    public void deleteStation(Station station) {
        if (!station.equals(lastStation())) {
            throw new IllegalArgumentException();
        }

        deleteLastStation();
    }

    private Section getFirstSection() {
        return this.sections.stream()
                .filter(section -> anyNonMatchSection(s -> s.equalsDownStation(section.getUpStation())))
                .findAny()
                .orElseThrow(AssertionError::new);
    }

    private Section getNextSection(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.equalsUpStation(downStation))
                .findAny()
                .orElse(null);
    }

    private Station lastStation() {
        return this.sections.get(sectionsLastIndex()).getDownStation();
    }

    private void deleteLastStation() {
        this.sections.remove(sectionsLastIndex());
    }

    private int sectionsLastIndex() {
        return this.sections.size() - 1;
    }

    private boolean alreadyConnectStation(Section section) {
        return anyMatchSection(s -> s.equalsUpStation(section.getUpStation())) && anyMatchSection(s -> s.equalsDownStation(section.getDownStation()));
    }

    private boolean nonExistConnectStation(Section section) {
        return anyNonMatchSection(s -> s.anyEqualsStation(section.getUpStation()) || s.anyEqualsStation(section.getDownStation()));
    }

    private boolean isAddFirstOrLastSection(Section section) {
        return anyMatchSection(s -> s.equalsDownStation(section.getUpStation()) || s.equalsUpStation(section.getDownStation()));
    }

    private Section getSectionByCondition(Predicate<Section> condition) {
        return sections.stream()
                .filter(condition)
                .findAny()
                .orElseThrow(AssertionError::new);
    }


    private boolean anyNonMatchSection(Predicate<Section> condition) {
        return !anyMatchSection(condition);
    }

    private boolean anyMatchSection(Predicate<Section> condition) {
        return sections.stream()
                .anyMatch(condition);
    }

}
