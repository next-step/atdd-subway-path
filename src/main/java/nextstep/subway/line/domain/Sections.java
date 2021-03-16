package nextstep.subway.line.domain;

import nextstep.subway.line.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    private static final int FIRST_INDEX = 0;
    private static final int ONE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderColumn
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Station firstStation = sections.get(FIRST_INDEX).getUpStation();
        stations.add(firstStation);

        sections.stream().forEach(section -> {
            stations.add(section.getDownStation());
        });

        return stations;
    }

    public List<Section> getSections() {
        return sections;
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
            sections.add(sections.size(), section);
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

        Section targetSection = sections.stream()
                .filter(section -> section.getDownStation().getId().equals(id))
                .findAny()
                .orElseThrow(NoStationToRemoveException::new);

        if(!targetSection.getDownStation().equals(getLastStation())) {
            throw new OnlyFianlCanBeDeletedException();
        }

        sections.remove(targetSection);
    }

    private boolean isStationExists(Station station) {
        return sections.stream().anyMatch(s -> s.getUpStation().equals(station) || s.getDownStation().equals(station));
    }

    private boolean isNewFirstSection(Section section) {
        return section.getDownStation().equals(getFirstStation());
    }

    private boolean isNewLastSection(Section section) {
        return section.getUpStation().equals(getLastStation());
    }

    private void addSectionAtUpperSide(Section section) {
        Section oldSection = sections.stream()
                .filter(s -> s.getUpStation().equals(section.getUpStation()))
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
                .filter(s -> s.getDownStation().equals(section.getDownStation()))
                .findAny()
                .orElseThrow(TargetSectionNotExistsException::new);
        Station oldDownStation = oldSection.getDownStation();
        int oldDistance = oldSection.getDistance();

        checkIfDistanceValid(section, oldDistance);

        oldSection.changeDownStationAndDistance(section.getUpStation(), oldDistance - section.getDistance());
        sections.add(new Section(section.getLine(), section.getUpStation(), oldDownStation, section.getDistance()));
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
