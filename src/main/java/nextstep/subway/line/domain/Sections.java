package nextstep.subway.line.domain;

import nextstep.subway.line.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {
    private static final int FIRST_INDEX = 0;
    private static final int ONE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderColumn
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        if(sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();

        Station firstStation = sections.get(FIRST_INDEX).getUpStation();
        stations.add(firstStation);

        for(Section section : sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        addSection(new Section(line, upStation, downStation, distance));
    }

    public void addSection(Section section) {
        boolean isUpStationExists = isStationExists(section.getUpStation());
        boolean isDownStationExists = isStationExists(section.getDownStation());

        if(sections.isEmpty()) {
            sections.add(section);
            return;
        }

        if(isNewFirstSection(section)){
            sections.add(FIRST_INDEX, section);
            return;
        }

        if(isNewLastSection(section)) {
            sections.add(section);
            return;
        }

        if (!isUpStationExists && !isDownStationExists) {
            throw new BothStationNotExistsException();
        }

        if (isUpStationExists && isDownStationExists) {
            throw new BothStationExistsException();
        }

        if(isUpStationExists) {
            addSectionAtUpperSide(section);
            return;
        }

        if(isDownStationExists) {
            addSectionAtDownSide(section);
            return;
        }
    }

    public void removeSection(Long id) {
        if(sections.size() <= ONE) {
            throw new OnlyOneSectionRemainingException();
        }

        if(getFirstStation().isSameId(id)) {
            sections.remove(0);
            return;
        }

        if(getLastStation().isSameId(id)) {
            sections.remove(sections.size() - ONE);
            return;
        }

        Section targetUpperSection = findSectionToBeDeletedAtUpperSide(id);
        Section targetDownSection = findSectionToBeDeletedAtDownSide(id);

        Station newUpStation = targetUpperSection.getUpStation();
        Station newDownStation = targetDownSection.getDownStation();
        int distanceOfNewSection = targetUpperSection.getDistance() + targetDownSection.getDistance();
        int indexOfNewSection = sections.indexOf(targetUpperSection);

        Section newSection = new Section(targetUpperSection.getLine(), newUpStation, newDownStation, distanceOfNewSection);

        replaceTargetSectionsWithNewOne(targetUpperSection, targetDownSection, indexOfNewSection, newSection);
    }

    private boolean isStationExists(Station station) {
        return sections.stream().anyMatch(s -> s.getUpStation().equals(station) || s.getDownStation().equals(station));
    }

    private boolean isNewFirstSection(Section section) {
        return section.isSameWithDownStation(getFirstStation());
    }

    private boolean isNewLastSection(Section section) {
        return section.isSameWithUpStation(getLastStation());
    }

    private void addSectionAtUpperSide(Section section) {
        Section oldSection = sections.stream()
                .filter(s -> s.isSameWithUpStation(section.getUpStation()))
                .findAny()
                .orElseThrow(TargetSectionNotExistsException::new);
        Station oldDownStation = oldSection.getDownStation();
        int oldDistance = oldSection.getDistance();

        checkIfDistanceValid(section, oldDistance);

        oldSection.changeDownStationAndDistance(section.getDownStation(), section.getDistance());
        sections.add(new Section(section.getLine(), section.getDownStation(), oldDownStation,oldDistance - section.getDistance()));
    }

    private void addSectionAtDownSide(Section section) {
        Section oldSection = sections.stream()
                .filter(s -> s.isSameWithDownStation(section.getDownStation()))
                .findAny()
                .orElseThrow(TargetSectionNotExistsException::new);
        Station oldDownStation = oldSection.getDownStation();
        int oldDistance = oldSection.getDistance();

        checkIfDistanceValid(section, oldDistance);

        oldSection.changeDownStationAndDistance(section.getUpStation(), oldDistance - section.getDistance());
        sections.add(new Section(section.getLine(), section.getUpStation(), oldDownStation, section.getDistance()));
    }

    private Section findSectionToBeDeletedAtDownSide(Long id) {
        return sections.stream()
                .filter(section -> section.getUpStation().getId().equals(id))
                .findAny()
                .orElseThrow(NoStationToRemoveException::new);
    }

    private Section findSectionToBeDeletedAtUpperSide(Long id) {
        return sections.stream()
                .filter(section -> section.getDownStation().getId().equals(id))
                .findAny()
                .orElseThrow(NoStationToRemoveException::new);
    }

    private void replaceTargetSectionsWithNewOne(Section targetUpperSection, Section targetDownSection, int indexOfNewSection, Section newSection) {
        sections.remove(targetUpperSection);
        sections.remove(targetDownSection);
        sections.add(indexOfNewSection, newSection);
    }

    private Station getFirstStation() {
        return sections.get(FIRST_INDEX).getUpStation();
    }

    private Station getLastStation() {
        return sections.get(sections.size() - ONE).getDownStation();
    }

    private void checkIfDistanceValid(Section section, int oldDistance) {
        if (oldDistance <= section.getDistance()) {
            throw new InvalidDistanceException();
        }
    }
}
