package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.CommonCode;
import nextstep.subway.exception.code.SectionCode;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        Station upEndStation = getUpEndStation();
        Station downEndStation = getDownEndStation();
        if (upEndStation.equals(section.getDownStation()) || downEndStation.equals(section.getUpStation())) {
            sections.add(section);
            return;
        }

        Optional<Section> sectionMatchUpStation = getSectionSameUpStation(section.getUpStation());
        Optional<Section> sectionMatchDownStation = getSectionSameDownStation(section.getDownStation());

        checkContainAllStationInLine(sectionMatchUpStation, sectionMatchDownStation);
        checkNotContainAllStationInLine(sectionMatchUpStation, sectionMatchDownStation);

        sectionMatchUpStation.ifPresent(originSection -> originSection.minus(section));
        sectionMatchDownStation.ifPresent(originSection -> originSection.minus(section));
        sections.add(section);
    }

    private void checkNotContainAllStationInLine(final Optional<Section> sectionMatchUpStation, final Optional<Section> sectionMatchDownStation) {
        if(sectionMatchUpStation.isEmpty() && sectionMatchDownStation.isEmpty()){
            throw new CustomException(CommonCode.PARAM_INVALID);
        }
    }

    private void checkContainAllStationInLine(final Optional<Section> sectionMatchUpStation, final Optional<Section> sectionMatchDownStation) {
        if(sectionMatchUpStation.isPresent() && sectionMatchDownStation.isPresent()){
            throw new CustomException(CommonCode.PARAM_INVALID);
        }
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

    public Station getDownEndStation() {
        Set<Station> stations = new HashSet<>(getStations());
        for (Section section : getSections()) {
            stations.remove(section.getUpStation());
        }
        return new ArrayList<>(stations).get(0);
    }

    public Station getUpEndStation() {
        Set<Station> stations = new HashSet<>(getStations());
        for (Section section : getSections()) {
            stations.remove(section.getDownStation());
        }
        return new ArrayList<>(stations).get(0);
    }

    public List<Station> getStations() {
        return getSections().stream()
                                  .map(Section::getAllStation)
                                  .flatMap(List::stream)
                                  .distinct()
                                  .collect(Collectors.toUnmodifiableList());
    }


    public List<Station> getStationsSorted() {
        return getSectionsSorted().stream()
                                  .map(Section::getAllStation)
                                  .flatMap(List::stream)
                                  .distinct()
                                  .collect(Collectors.toUnmodifiableList());
    }

    public List<Section> getSectionsSorted() {
        List<Section> result = new ArrayList<>();
        Optional<Section> section = getSectionSameUpStation(getUpEndStation());
        while (section.isPresent()) {
            result.add(section.get());
            section = getSectionSameUpStation(section.get().getDownStation());
        }
        return result;
    }

    public List<String> getStationNames() {
        return getStationsSorted().stream()
                                  .map(Station::getName)
                                  .collect(Collectors.toList());
    }

    private Optional<Section> getSectionSameUpStation(final Station station) {
        return sections.stream()
                       .filter(section -> section.getUpStation().equals(station))
                       .findFirst();
    }

    private Optional<Section> getSectionSameDownStation(final Station station) {
        return sections.stream()
                       .filter(section -> section.getDownStation().equals(station))
                       .findFirst();
    }
}
