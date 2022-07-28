package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.SectionCode;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {
    public static final int INVALID_REMOVE_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void add(final Section section) {
        sections.add(section);
    }

    public void removeSection(final Long stationId) {
        checkInvalidRemoveSize();
        checkIsDownEndStation(stationId);
        sections.remove(size() - 1);
    }

    private void checkInvalidRemoveSize() {
        if (size() <= INVALID_REMOVE_SIZE) {
            throw new CustomException(SectionCode.SECTION_REMOVE_INVALID);
        }
    }

    private void checkIsDownEndStation(final Long stationId) {
        if (!getDownEndStation().getId().equals(stationId)) {
            throw new IllegalArgumentException();
        }
    }

    public int size() {
        return sections.size();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> getStations() {
        return sections.stream()
                       .map(Section::getAllStation)
                       .flatMap(List::stream)
                       .distinct()
                       .collect(Collectors.toUnmodifiableList());
    }

    public List<String> getStationNames() {
        return getStations().stream()
                            .map(Station::getName)
                            .collect(Collectors.toList());
    }

    public Station getDownEndStation() {
        Set<Station> stations = new HashSet<>(getStations());
        for(Section section : getSections()){
            stations.remove(section.getUpStation());
        }
        return new ArrayList<>(stations).get(0);
    }

    public Station getUpEndStation() {
        Set<Station> stations = new HashSet<>(getStations());
        for(Section section : getSections()){
            stations.remove(section.getDownStation());
        }
        return new ArrayList<>(stations).get(0);
    }
}
