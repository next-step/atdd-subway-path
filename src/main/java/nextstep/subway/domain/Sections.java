package nextstep.subway.domain;

import nextstep.subway.exception.RemoveSectionFailException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int MIN_SECTIONS_SIZE = 1;
    private static final int FIRST_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(FIRST_INDEX, getFirstStation());

        return stations;
    }

    private Station getFirstStation() {
        return sections.get(FIRST_INDEX)
                .getUpStation();
    }

    public void removeSection(Long stationId) {
        validateSectionCount();
        validateIsLastStation(stationId);
        sections.remove(getLastSection());
    }

    private Section getLastSection() {
        return sections.get(getLastIndex());
    }

    private void validateIsLastStation(Long stationId) {
        if (!isLastStation(stationId)) {
            throw new RemoveSectionFailException();
        }
    }

    private void validateSectionCount() {
        if (sections.size() == MIN_SECTIONS_SIZE) {
            throw new RemoveSectionFailException();
        }
    }

    private boolean isLastStation(Long stationId) {
        return getLastDownStation().getId()
                .equals(stationId);
    }

    private Station getLastDownStation() {
        return sections.get(getLastIndex())
                .getDownStation();
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }
}
