package nextstep.subway.domain.sections;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.exception.CannotDeleteSectionException;
import nextstep.subway.domain.sections.strategy.ChangeableSections;
import nextstep.subway.domain.sections.strategy.SectionAddStrategies;

@Embeddable
public class Sections {
    private static final String BOTH_STATIONS_REGISTERED_EXCEPTION_MESSAGE = "상/하행역이 이미 노선에 모두 등록되어 있습니다.";
    private static final String NONE_OF_STATIONS_EXIST_IN_LINE_EXCEPTION_MESSAGE = "상/하행역 모두 노선에 존재하지 않습니다.";
    private static final String MINIMUM_SECTIONS_REQUIRED_EXCEPTION_MESSAGE = "2개 이상의 구간이 존재할 경우에 한해서 삭제가 가능합니다.";
    private static final String NOT_EXISTING_DOWN_STATION_EXCEPTION_MESSAGE = "해당 역이 하행역으로 존재하는 구간이 존재하지 않습니다.";
    private static final SectionAddStrategies sectionAddStrategies = new SectionAddStrategies();

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section section, Line line) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateAddSection(section);
        ChangeableSections changeableSections = sectionAddStrategies.findChangeableSections(this, section, line);
        sections.add(section);

        changeableSections.getDeprecatedSections()
            .forEach(sections::remove);
        sections.addAll(changeableSections.getAdditionalSections());
    }

    public void deleteSection(Long stationId) {
        validateDeleteSection(stationId);

        sections.removeIf(section -> section.isSameDownStation(stationId));
    }

    private void validateDeleteSection(Long stationId) {
        if (sections.size() <= 1) {
            throw new CannotDeleteSectionException(MINIMUM_SECTIONS_REQUIRED_EXCEPTION_MESSAGE);
        }

        if (sections.stream().noneMatch(section -> section.isSameDownStation(stationId))) {
            throw new CannotDeleteSectionException(NOT_EXISTING_DOWN_STATION_EXCEPTION_MESSAGE);
        }
    }

    private boolean isDownMostStation(Long stationId) {
        Section lastSection = getValue().get(sections.size() - 1);
        return lastSection.isSameDownStation(stationId);
    }

    private void validateAddSection(Section section) {
        if (hasStation(section.getUpStation()) && hasStation(section.getDownStation())) {
            throw new CannotAddSectionException(BOTH_STATIONS_REGISTERED_EXCEPTION_MESSAGE);
        }

        if (!hasStation(section.getUpStation()) && !hasStation(section.getDownStation())) {
            throw new CannotAddSectionException(NONE_OF_STATIONS_EXIST_IN_LINE_EXCEPTION_MESSAGE);
        }
    }

    private boolean hasStation(Station station) {
        return getStations().stream()
            .anyMatch(st -> st.equals(station));
    }

    public List<Station> getStations() {
        List<Station> stations = getAllStations();

        if (stations.isEmpty()) {
            return List.of();
        }

        Station upmostStation = findUpmostStation(stations);

        var ret = new ArrayList<>(Collections.singletonList(upmostStation));

        Station upStation = upmostStation;
        while ((upStation = findNextStation(upStation)) != null) {
            ret.add(upStation);
        }

        return ret;
    }

    public List<Section> getValue() {
        List<Station> stations = getStations();

        List<Section> sortedSections = new ArrayList<>();
        for (int i = 0; i < stations.size() - 1; ++i) {
            Station station = stations.get(i);
            Section section = sections.stream()
                .filter(s -> s.isSameUpStation(station.getId()))
                .findFirst().orElseThrow();

            sortedSections.add(section);
        }

        return Collections.unmodifiableList(sortedSections);
    }

    private List<Station> getAllStations() {
        return sections.stream()
            .map(section -> List.of(section.getUpStation(), section.getDownStation()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(toList());
    }

    private Station findUpmostStation(List<Station> stations) {
        Map<Station, Boolean> inDegreeMap = new HashMap<>();
        sections.forEach(section -> {
            Station downStation = section.getDownStation();
            inDegreeMap.put(downStation, true);
        });

        Set<Station> stationsWithInDegree = inDegreeMap.keySet();

        return stations.stream()
            .filter(station -> !stationsWithInDegree.contains(station))
            .findFirst()
            .orElseThrow();
    }

    private Station findNextStation(Station station) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .map(Section::getDownStation)
            .findFirst()
            .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sections sections1 = (Sections)o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
