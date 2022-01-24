package nextstep.subway.line.domain;

import nextstep.subway.line.exception.EmptyLineException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.SectionAlreadyRegisteredException;
import nextstep.subway.line.exception.SectionNotSearchedException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    private static final int FIRST_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Sections() {}

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public int size() {
        return sections.size();
    }

    public List<Section> toList() {
        return new ArrayList<>(sections);
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        validateAddSection(section);

        Section searchedSection = searchSection(section);
        if (searchedSection.isSplittable(section)) {
            sections.remove(searchedSection);
            sections.addAll(searchedSection.split(section));
        } else if (isLastSection(searchedSection)) {
            sections.add(section);
        } else if (isFirstSection(searchedSection)) {
            sections.add(FIRST_INDEX, section);
        } else {
            throw new SectionNotSearchedException();
        }
    }

    public void removeSection(Station station) {
        validateRemoveSections(station);
        sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findFirst()
                .ifPresent(it -> sections.remove(it));
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getUpStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    private Section searchSection(Section section) {
        return sections.stream()
                .filter(section::search)
                .findAny()
                .orElseThrow(SectionNotSearchedException::new);
    }

    private void validateAddSection(Section section) {
        boolean alreadyRegistered = sections.stream()
                .anyMatch(section::isRegistered);
        if (alreadyRegistered) {
            throw new SectionAlreadyRegisteredException();
        }
    }

    private void validateRemoveSections(Station station) {
        if (sections.size() <= 1) {
            throw new EmptyLineException();
        }
        validateUpStation(station);
    }

    private void validateUpStation(Station station) {
        if (!getLastDownStation().equals(station)) {
            throw new NotLastStationException();
        }
    }

    private Station getLastDownStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    private Station findUpStation() {
        Station downStation = sections.get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    private boolean isFirstSection(Section section) {
        return sections.get(FIRST_INDEX).equals(section);
    }

    private boolean isLastSection(Section section) {
        int lastIndex = sections.size() - 1;
        return sections.get(lastIndex).equals(section);
    }
}
