package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.exception.NotEnoughSectionDeleteException;
import nextstep.subway.domain.exception.NotExistSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Sections {

    private static final int EMPTY_VALUE = 0;
    private static final int ONE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    public void add(Section section) {
        if (values.isEmpty()) {
            values.add(section);
            return;
        }
        if (this.hasNotOnlyOneStation(section)) {
            throw new IllegalArgumentException();
        }
        Section connectableSection = findConnectableSection(section);
        if (connectableSection.isConnectInSide(section)) {
            connectableSection.connectInside(section);
        }
        values.add(section);
    }

    private Section findConnectableSection(Section section) {
        return values.stream()
                .filter(section::isConnectable)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean hasNotOnlyOneStation(Section section) {
        return !hasOnlyOneStation(section);
    }

    private boolean hasOnlyOneStation(Section section) {
        boolean hasUpStation = this.hasStation(section.getUpStation());
        boolean hasDownStation = this.hasStation(section.getDownStation());

        if (hasUpStation && hasDownStation) {
            return false;
        }
        return hasUpStation || hasDownStation;
    }

    private boolean hasStation(Station station) {
        return values.stream().anyMatch(section -> section.hasStation(station));
    }

    public void delete(Station station) {
        if (this.hasZeroOrOneSection()) {
            throw new NotEnoughSectionDeleteException();
        }
        Section findSection = findSectionByDownStation(station);
        if (isNotFinalSection(findSection)) {
            Section postSection = findPostSection(findSection);
            postSection.combine(findSection);
        }
        values.remove(findSection);
    }

    private boolean hasZeroOrOneSection() {
        return values.isEmpty() || values.size() == ONE;
    }

    private Section findPostSection(Section section) {
        return values.stream()
                .filter(s -> s.isMatchUpStation(section.getDownStation()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isNotFinalSection(Section findSection) {
        return !values.get(lastIndex()).equals(findSection);
    }

    private Section findSectionByDownStation(Station station) {
        return values.stream()
                .filter(section -> section.isMatchDownStation(station))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private int lastIndex() {
        return values.size() - ONE;
    }

    public List<Station> getStations() {
        if(values.isEmpty()) {
            return Collections.emptyList();
        }
        Section firstSection = findFirstSection();
        List<Section> connectedOrderSections = new ArrayList<>();
        connectedOrderSections.add(firstSection);

        while (connectedOrderSections.size() != values.size()) {
            Section lastStation = connectedOrderSections.get(connectedOrderSections.size() - ONE);
            Section connectableSection = findConnectableSection(lastStation);
            connectedOrderSections.add(connectableSection);
        }
        return exportStations(connectedOrderSections);
    }

    private List<Station> exportStations(List<Section> sections) {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    private Section findFirstSection() {
        return values.stream()
                .filter(section -> isMissMatchDownStation(section.getUpStation()))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    private boolean isMissMatchDownStation(Station station) {
        return values.stream()
                .allMatch(section -> section.isMissMatchDownStation(station));
    }
}
