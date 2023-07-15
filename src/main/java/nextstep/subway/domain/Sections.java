package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.SectionDuplicationStationException;
import nextstep.subway.exception.SectionNotConnectingStationException;
import nextstep.subway.exception.SectionRemoveLastStationException;
import nextstep.subway.exception.SectionRemoveSizeException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            validateConnectingStation(section.getUpStation());
            validateDuplicationStation(section.getDownStation());
        }

        this.sections.add(section);
    }

    private void validateConnectingStation(Station station) {
        if (getLastSection().isNotSameDownStation(station.getId())) {
            throw new SectionNotConnectingStationException();
        }
    }

    private void validateDuplicationStation(Station station) {
        if (sections.stream()
            .anyMatch(section -> section.isSameUpStation(station.getId()))) {
            throw new SectionDuplicationStationException();
        }
    }

    private void addLastStation(List<Station> stations) {
        if (sections.size() > 0) {
            Section lastSection = getLastSection();
            stations.add(lastSection.getDownStation());
        }
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public void removeSection(Long stationId) {
        validateSectionSize();
        validateLastSection(stationId);
        sections.remove(getLastSection());
    }

    private void validateSectionSize() {
        if (sections.size() < 2) {
            throw new SectionRemoveSizeException();
        }
    }

    private void validateLastSection(Long stationId) {
        if (getLastSection().isNotSameDownStation(stationId)) {
            throw new SectionRemoveLastStationException();
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        sections.forEach(section -> stations.add(section.getUpStation()));
        addLastStation(stations);
        return stations;
    }

    public int size() {
        return sections.size();
    }
}
