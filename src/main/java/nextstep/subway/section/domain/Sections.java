package nextstep.subway.section.domain;

import nextstep.subway.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        return new ArrayList<>(new HashSet<>(stations));
    }

    public void addSection(Section section) {
        if (this.sections.isEmpty()) {
            this.sections.add(section);
            return;
        }
        if (isNotLastStation(section.getUpStation())) {
            throw new IsNotLastStationException();
        }
        if (isExistDownStation(section)) {
            throw new AlreadyExistDownStationException();
        }
        this.sections.add(section);
    }

    public void deleteSection(Station station) {
        if (!getStations().contains(station)) {
            throw new NotFoundStationException();
        }
        if (isNotLastStation(station)) {
            throw new IsNotLastStationException();
        }
        if (size() == 1) {
            throw new DeleteSectionException();
        }

        Section lastSection = getLastSection();

        lastSection.delete();
        this.sections.remove(lastSection);
    }

    private boolean isNotLastStation(Station station) {
        return !getLastSection().isDownStation(station);
    }

    private Section getLastSection() {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return this.sections.get(size() - 1);
    }

    private boolean isExistDownStation(Section section) {
        return getStations().stream()
                .anyMatch(comparedStation ->
                        comparedStation.equals(section.getDownStation())
                );
    }

    public boolean hasSection(Section section) {
        return this.sections.contains(section);
    }

    public int size() {
        return this.sections.size();
    }

}
