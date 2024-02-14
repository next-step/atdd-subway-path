package nextstep.subway.section.domain;

import nextstep.subway.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {

    private final static int FIRST = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.add(section.getUpStation());
        }

        stations.add(getLastSection().getDownStation());

        return stations;
    }

    public void addSection(Section newSection) {
        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        validateAddSection(newSection);

        if (isFirstStation(newSection.getDownStation())) {
            addFirstSection(newSection);
            return;
        }
        if (isLastStation(newSection.getUpStation())) {
            addLastSection(newSection);
            return;
        }
        addMiddleSection(newSection);
    }

    private void validateAddSection(Section newSection) {
        List<Station> stations = getStations();

        if (this.sections.contains(newSection)) {
            throw new AlreadyExistSectionException();
        }
        if (!stations.contains(newSection.getUpStation()) && !stations.contains(newSection.getDownStation())) {
            throw new NotFoundUpStationOrDownStation();
        }
    }

    private void addFirstSection(Section newSection) {
        this.sections.get(FIRST).updateUpStation(newSection.getDownStation());
        this.sections.add(FIRST, newSection);
    }

    private void addLastSection(Section newSection) {
        this.sections.add(newSection);
    }

    private void addMiddleSection(Section newSection) {
        int index = getStations().indexOf(newSection.getUpStation());
        this.sections.get(index).updateUpStation(newSection.getDownStation());
        this.sections.add(index, newSection);
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

    private boolean isFirstStation(Station station) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return sections.get(FIRST).isUpStation(station);
    }

    private boolean isLastStation(Station station) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return getLastSection().isDownStation(station);
    }

    private boolean isNotLastStation(Station station) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
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

    public List<Section> getSections() {
        return this.sections;
    }

}
