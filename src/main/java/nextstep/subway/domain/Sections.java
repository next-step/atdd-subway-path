package nextstep.subway.domain;

import nextstep.subway.exception.*;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

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

    public void remove(Station station) {
        if (isEmpty() || size() == 1) {
            throw new InvalidRemoveSectionException();
        }

        if (!containStation(station)) {
            throw new NotFoundStationException();
        }

        removeSection(station);
    }

    public List<Station> getStations() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        Section firstSection = getFirstSection();
        return mapStations(firstSection, new ArrayList<>());
    }

    public Station getLastDownStation() {
        return getLastSection().getDownStation();
    }

    public void addSectionsToGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.sections.forEach(section ->
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private int size() {
        return this.sections.size();
    }

    private List<Station> mapStations(Section section, List<Station> stations) {
        stations.add(section.getUpStation());

        try {
            Section nextSection = findNextSection(section.getDownStation());
            return mapStations(nextSection, stations);
        } catch (NotFoundSectionException e) {
            stations.add(section.getDownStation());
            return stations;
        }
    }

    private void removeSection(Station station) {
        Section firstSection = getFirstSection();
        if (firstSection.matchUpStation(station)) {
            this.sections.remove(firstSection);
            return;
        }

        Section lastSection = getLastSection();
        if (lastSection.matchDownStation(station)) {
            this.sections.remove(lastSection);
            return;
        }

        removeBetweenSection(station);
    }

    private void removeBetweenSection(Station station) {
        Section beforeSection = findBeforeSection(station);
        Section nextSection = findNextSection(station);
        beforeSection.combine(nextSection);
        this.sections.remove(nextSection);
    }

    private Section findNextSection(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.matchUpStation(downStation))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
    }

    private Section findBeforeSection(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.matchDownStation(upStation))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
    }

    private Section getFirstSection() {
        return this.sections.stream()
                .filter(section -> this.sections.stream()
                        .noneMatch(other -> section.matchUpStation(other.getDownStation())))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
    }

    private Section getLastSection() {
        return this.sections.stream()
                .filter(section -> this.sections.stream()
                        .noneMatch(other -> section.matchDownStation(other.getUpStation())))
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
        if (section.matchUpStation(newSection.getUpStation())) {
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
