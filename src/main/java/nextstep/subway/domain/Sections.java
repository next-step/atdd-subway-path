package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@EqualsAndHashCode
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Sections implements Iterable<Section> {

    public static final String IS_NOT_LAST_SECTION_DOWN_STATION = "마지막 구간의 하행역이 아닙니다.";
    public static final String LINE_SECTION_IS_ONLY_ONE = "노선에 구간이 하나 입니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    public static Sections from(Section... inputSections) {
        Sections newSections = new Sections();
        for (Section section : inputSections) {
            newSections.add(section);
        }
        return newSections;
    }

    @Override
    public Iterator<Section> iterator() {
        return values.iterator();
    }

    @Override
    public void forEach(Consumer<? super Section> action) {
        values.forEach(action);
    }

    @Override
    public Spliterator<Section> spliterator() {
        return values.spliterator();
    }

    public Section get(int index) {
        return values.get(index);
    }

    public int size() {
        return values.size();
    }

    public void add(Section newSection) {
        validateForAdd(newSection);

        Optional<Section> optionalSection = values.stream()
                .filter(s -> s.getUpStation().getId().equals(newSection.getUpStationId()))
                .findAny();

        if (optionalSection.isPresent()) {
            Section oldSection = optionalSection.get();

            int oldDis = oldSection.getDistance();
            int newDIs = newSection.getDistance();

            Station oldSectionDownStation = oldSection.getDownStation();
            Station newSectionDownStation = newSection.getDownStation();

            oldSection.changeDistance(oldDis - newDIs);
            oldSection.changeDownStation(newSectionDownStation);

            newSection.changeDownStationToUpStation();
            newSection.changeDownStation(oldSectionDownStation);

            values.remove(oldSection);
            values.add(oldSection);
            values.add(newSection);
            return;
        }

        values.add(newSection);
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {
        Section section = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();

        if (values.contains(section)) {
            return;
        }

        add(section);
        section.changeLine(line);
    }

    public void remove(Section section) {
        if (!values.contains(section)) {
            return;
        }

        validateRemove(section.getDownStation());
        values.remove(section);
        section.removeLine();
    }

    public void remove(Station station) {
        validateRemove(station);

        Section section = values.stream()
                .filter(s -> s.getDownStation().equals(station))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("삭제 할 역이 없습니다."));

        values.remove(section);
    }

    public void validateRemove(Station station) {
        if (!getLast().getDownStation().equals(station)) {
            throw new IllegalArgumentException(IS_NOT_LAST_SECTION_DOWN_STATION);
        }

        if (values.size() <= 1) {
            throw new IllegalArgumentException(LINE_SECTION_IS_ONLY_ONE);
        }
    }

    private void validateForAdd(Section newSection) {
        if (values.isEmpty() || values.contains(newSection)) {
            return;
        }

        validateSavedStation(newSection);
    }

    private void validateSavedStation(Section section) {
        List<Long> ids = getStationIds();
        if (ids.contains(section.getUpStationId()) && ids.contains(section.getDownStationId())) {
            throw new IllegalArgumentException("이미 노선에 등록된 역 입니다. id:" + section);

        }
    }

    private List<Long> getStationIds() {
        return getStations().stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }

    public List<Station> getStations() {
        if (values.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> upStations = upStations();
        upStations.add(getLast().getDownStation());
        return upStations;
    }

    private List<Station> upStations() {
        return values.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public Section getLast() {
        return values.get(values.size() - 1);
    }


    public boolean contains(Section section) {
        return values.contains(section);
    }


    public boolean isEmpty() {
        return values.isEmpty();
    }
}
