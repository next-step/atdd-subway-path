package nextstep.subway.domain;

import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.NotFoundSectionException;

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

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if (isEmpty()) {
            this.sections.add(newSection);
            return;
        }
        addSection(newSection);
    }

    public void remove() {
        this.sections.remove(size() - 1);
    }

    public boolean isLastDownStation(Station station) {
        return getLastDownStation().equals(station);
    }

    public List<Station> getStations() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        return mapStations(getFirstSection(), new ArrayList<>());
    }

    public Station getLastDownStation() {
        return this.sections.stream()
                .filter(section -> this.sections.stream()
                        .noneMatch(other -> other.matchUpStationForDown(section)))
                .findFirst()
                .map(Section::getDownStation)
                .orElseThrow(NotFoundSectionException::new);
    }

    private int size() {
        return this.sections.size();
    }

    private List<Station> mapStations(Section section, List<Station> stations) {
        stations.add(section.getUpStation());

        Section nextSection = findNextSection(section);
        if (nextSection == null) {
            stations.add(section.getDownStation());
            return stations;
        }

        return mapStations(nextSection, stations);
    }

    private Section findNextSection(Section currentSection) {
        return this.sections.stream()
                .filter(section -> section.matchUpStationForDown(currentSection))
                .findFirst()
                .orElse(null);
    }

    private Section getFirstSection() {
        return this.sections.stream()
                .filter(section -> this.sections.stream()
                        .noneMatch(other -> section.matchUpStationForDown(other)))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
    }

    private void addSection(Section newSection) {
        validateNotFountStations(newSection);

        if (isDuplicateSection(newSection)) {
            throw new DuplicateSectionException();
        }

        Optional<Section> between = getSectionForFilter(section -> section.isBetweenSection(newSection));
        if (between.isPresent()) {
            addBetweenSection(newSection, between.get());
            return;
        }

        getSectionForFilter(section -> section.isLeafSection(newSection))
                .ifPresent(leaf -> this.sections.add(newSection));
    }

    private boolean isDuplicateSection(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.matchStations(newSection));
    }

    private Optional<Section> getSectionForFilter(Predicate<Section> filterAction) {
        return sections.stream()
                .filter(filterAction)
                .findFirst();
    }

    private void addBetweenSection(Section newSection, Section section) {
        if (!section.isEnoughDistance(newSection.getDistance())) {
            throw new InvalidDistanceException(section.getDistance());
        }

        this.sections.add(newSection);
        if (section.matchUpStation(newSection)) {
            section.changeUpSection(newSection);
            return;
        }
        section.changeDownSection(newSection);
    }

    private void validateNotFountStations(Section newSection) {
        if (isNotFoundStations(newSection)) {
            throw new NotFoundStationException();
        }
    }

    private boolean isNotFoundStations(Section newSection) {
        return !containStation(newSection.getUpStation())
                && !containStation(newSection.getDownStation());
    }

    private boolean containStation(Station newSection) {
        return this.getStations().contains(newSection);
    }

    private boolean isEmpty() {
        return size() == 0;
    }

}
