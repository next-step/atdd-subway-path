package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.domain.exception.NotValidDeleteTargetStationException;
import nextstep.subway.domain.exception.NotValidSectionStationsException;
import nextstep.subway.domain.exception.StationNotFoundException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();

    public void add(Line line, Station upStation, Station downStation, int distance) {
        if (isNotValidStations(upStation, downStation)) {
            throw new NotValidSectionStationsException();
        }

        if (isExistStation(upStation) && isNotLastStation(upStation)) {
            var section = getSectionByUpStation(upStation);
            section.setNewUpStation(downStation, section.getDistance() - distance);
        }

        if (isExistStation(downStation) && isNotFirstStation(downStation)) {
            var section = getSectionByDownStation(downStation);
            section.setNewDownStation(upStation, section.getDistance() - distance);
        }

        sectionList.add(new Section(line, upStation, downStation, distance));
    }

    public void removeByStation(Station station) {
        if (sectionList.size() == 1) {
            throw new NotValidDeleteTargetStationException();
        }

        if (!isExistStation(station)) {
            throw new StationNotFoundException();
        }

        var firstSection = getFirstSection();
        if (firstSection.getUpStation().equals(station)) {
            sectionList.remove(0);
        } else {
            var targetSection = getSectionByDownStation(station);
            var nextSection = getNextSection(targetSection);

            nextSection.ifPresent(section -> section.setNewUpStation(
                    targetSection.getUpStation(),
                    targetSection.getDistance() + section.getDistance()
            ));

            sectionList.remove(targetSection);
        }
    }

    public List<Section> getOrderedSections() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        var orderedSections = new ArrayList<Section>();
        var currentSection = Optional.of(getFirstSection());

        while (currentSection.isPresent()) {
            orderedSections.add(currentSection.get());
            currentSection = getNextSection(currentSection.get());
        }

        return orderedSections;
    }

    public List<Station> getStations() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        var sections = getOrderedSections();
        var stations = new ArrayList<Station>();
        stations.add(sections.get(0).getUpStation());
        sections.forEach(section -> stations.add(section.getDownStation()));

        return stations;
    }

    private boolean isNotLastStation(Station station) {
        var sections = getOrderedSections();
        var lastStation = sections.get(sections.size() - 1).getDownStation();
        return !lastStation.equals(station);
    }

    private boolean isNotFirstStation(Station station) {
        var firstStation = getFirstSection().getUpStation();
        return !firstStation.equals(station);
    }

    private Section getFirstSection() {
        if (sectionList.isEmpty()) {
            throw new IllegalStateException();
        }

        var currSection = sectionList.get(0);
        var prevSection = getPrevSection(currSection);

        while (prevSection.isPresent()) {
            currSection = prevSection.get();
            prevSection = getPrevSection(currSection);
        }

        return currSection;
    }

    private Optional<Section> getPrevSection(Section currentSection) {
        return sectionList.stream()
                .filter(section -> currentSection.getUpStation().equals(section.getDownStation()))
                .findFirst();
    }

    private Optional<Section> getNextSection(Section currentSection) {
        return sectionList.stream()
                .filter(section -> currentSection.getDownStation().equals(section.getUpStation()))
                .findFirst();
    }

    private Section getSectionByUpStation(Station upStation) {
        return sectionList.stream()
                .filter(section -> upStation.equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Section getSectionByDownStation(Station downStation) {
        return sectionList.stream()
                .filter(section -> downStation.equals(section.getDownStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isNotValidStations(Station upStation, Station downStation) {
        return !sectionList.isEmpty() && (
                (isExistStation(upStation) && isExistStation(downStation)) ||
                (!isExistStation(upStation) && !isExistStation(downStation))
        );
    }

    private boolean isExistStation(Station station) {
        return getStations().contains(station);
    }
}
