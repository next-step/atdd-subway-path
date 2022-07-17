package nextstep.subway.domain;

import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.NotFountSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if (!isEmpty()) {
            addSection(newSection);
            return;
        }

        this.sections.add(newSection);
    }

    public void remove() {
        this.sections.remove(size() - 1);
    }

    public boolean isLastDownStation(Station station) {
        return getLastDownStation().equals(station);
    }

    public List<Station> getStations() {
        return mapStations(getFirstSection(), new ArrayList<>());
    }

    public Station getLastDownStation() {
        return this.sections.get(size() - 1).getDownStation();
    }

    public int size() {
        return this.sections.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    private List<Station> mapStations(Section section, List<Station> stations) {
        stations.add(section.getUpStation());

        Section nextSection = findSection(section);
        if (nextSection == null) {
            stations.add(section.getDownStation());
            return stations;
        }

        return mapStations(nextSection, stations);
    }

    private Section findSection(Section currentSection) {
        return this.sections.stream()
                .filter(section -> section.isSameAsUpStation(currentSection.getDownStation()))
                .findFirst()
                .orElse(null);
    }

    private Section getFirstSection() {
        return this.sections.stream()
                .filter(section -> this.sections.stream()
                        .noneMatch(other -> section.isSameAsUpStation(other.getDownStation())))
                .findFirst()
                .orElseThrow(NotFountSectionException::new);
    }

    private void addSection(Section newSection) {
        validateNotFountStations(newSection);

        if (isDuplicateSection(newSection)) {
            throw new DuplicateSectionException();
        }

        if (addSectionTemplate(section -> section.isAddBetween(newSection),
                section -> addBetweenSection(newSection, section))) {
            return;
        }

        if (addSectionTemplate(section -> section.isSameAsUpStation(newSection.getDownStation()),
                section -> addUpSection(newSection, section))) {
            return;
        }

        if (addSectionTemplate(section -> section.isSameAsDownStation(newSection.getUpStation()),
                section -> this.sections.add(newSection))) {
            return;
        }
    }

    private boolean addSectionTemplate(Predicate<Section> filterAction, Consumer<Section> addAction) {
        Optional<Section> section = getSectionForFilter(filterAction);
        if (section.isPresent()) {
            addAction.accept(section.get());
            return true;
        }
        return false;
    }

    private boolean isDuplicateSection(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.isSameStations(newSection));
    }

    private Optional<Section> getSectionForFilter(Predicate<Section> filterAction) {
        return sections.stream()
                .filter(filterAction)
                .findFirst();
    }

    private void addUpSection(Section newSection, Section section) {
        int index = this.sections.indexOf(section);
        this.sections.add(index, newSection);
    }

    private void addBetweenSection(Section newSection, Section section) {
        if (!section.isEnoughDistance(newSection.getDistance())) {
            throw new InvalidDistanceException(section.getDistance());
        }

        int index = this.sections.indexOf(section);

        if (section.isSameAsUpStation(newSection.getUpStation())) {
            this.sections.add(index, newSection);
            section.changeDownSection(newSection);
            return;
        }

        this.sections.add(index + 1, newSection);
        section.changeUpSection(newSection);
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

}
