package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;


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

    public void add(Section section) {
        validateForAdd(section);
        values.add(section);
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

        validateRemove(section.getDownStationId());
        values.remove(section);
        section.removeLine();
    }

    public void validateRemove(Long downStationId) {
        if (!last().getDownStationId().equals(downStationId)) {
            throw new IllegalArgumentException(IS_NOT_LAST_SECTION_DOWN_STATION);
        }

        if (values.size() <= 1) {
            throw new IllegalArgumentException(LINE_SECTION_IS_ONLY_ONE);
        }
    }

    public void removeByStationId(Long downStationId) {
        validateRemove(downStationId);

        values.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findAny()
                .ifPresent(this::remove);
    }

    private void validateForAdd(Section newSection) {
        if (values.isEmpty() || values.contains(newSection)) {
            return;
        }

        Long lastSectionDownStationId = this.last().getDownStationId();
        Long newSectionUpStationId = newSection.getUpStationId();
        Long newSectionDownStationId = newSection.getDownStationId();

        if (!lastSectionDownStationId.equals(newSectionUpStationId)) {
            throw new IllegalArgumentException(String.format("마지막 구간의 하행역과 추가하려는 구간의 상행역이 다릅니다. 하행역 id:%d, 상행역 id:%d"
                    , lastSectionDownStationId
                    , newSectionUpStationId));
        }

        boolean isSavedSectionStation = allStations()
                .stream()
                .anyMatch(station -> station.getId().equals(newSectionDownStationId));

        if (isSavedSectionStation) {
            throw new IllegalArgumentException("이미 노선에 등록된 역 입니다. id:" + newSectionDownStationId);
        }
    }

    public List<Station> allStations() {
        if (values.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> upStations = upStations();
        upStations.add(last().getDownStation());
        return upStations;
    }

    private List<Station> upStations() {
        return values.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public Section last() {
        return values.get(values.size() - 1);
    }


    public boolean contains(Section section) {
        return values.contains(section);
    }


    public boolean isEmpty() {
        return values.isEmpty();
    }
}
