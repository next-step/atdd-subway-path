package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();

    public void add(Line line, Station upStation, Station downStation, int distance) {
        if (isNotValidStations(upStation, downStation)) {
            throw new IllegalArgumentException();
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

    public void removeByStation(Station downStation) {
        if (isNotLastStation(downStation)) {
            throw new IllegalArgumentException();
        }
        sectionList.remove(sectionList.size() - 1);
    }

    public List<Station> getStations() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        var stations = new ArrayList<Station>();
        var firstSection = getFirstSection();
        stations.add(firstSection.getUpStation());

        var currentSection = Optional.of(firstSection);
        while (currentSection.isPresent()) {
            stations.add(currentSection.get().getDownStation());
            currentSection = getNextSection(currentSection.get());
        }

        return stations;
    }

    private boolean isNotLastStation(Station station) {
        var stations = getStations();
        return !stations.get(stations.size() - 1).equals(station);
    }

    private boolean isNotFirstStation(Station station) {
        var stations = getStations();
        return !stations.get(0).equals(station);
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
