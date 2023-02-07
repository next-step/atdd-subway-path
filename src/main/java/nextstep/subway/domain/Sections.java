package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Slf4j
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
        if (forceAddCondition(newSection)) {
            values.add(newSection);
            return;
        }

        SectionAction action = SectionAction.of(this, newSection);
        action.add(this, newSection);

        updateOrder();
    }

    private void updateOrder() {
        for (int i = 0; i < values.size(); i++) {
            Section section = values.get(i);
            section.changeOrder(i);
        }
    }

    private boolean forceAddCondition(Section newSection) {
        return values.isEmpty() || values.contains(newSection); // 메서드 대시 변수로 할당하는게 좋을까?
    }

    public boolean isAddUpStation(Section newSection) {
        return values.stream()
                .anyMatch(addUpStationPredicate(newSection));
    }

    private Predicate<Section> addUpStationPredicate(Section newSection) {
        return s -> s.isSameUpStation(newSection.getDownStation());
    }

    public boolean isAddMiddleStation(Section newSection) {
        return values.stream()
                .anyMatch(addMiddlePredicate(newSection));
    }

    private Predicate<Section> addMiddlePredicate(Section newSection) {
        return s -> s.isSameUpStation(newSection.getUpStation()) && s.isDistanceGreaterThen(newSection.getDistance());
    }

    public boolean isAddDownStation(Section newSection) {
        return values.stream()
                .anyMatch(s -> s.isSameDownUpStation(newSection.getUpStation()));
    }

    public void addUpStation(Section newSection) {
        values.add(0, newSection);
    }

    public void addMiddleStation(Section newSection) {
        Section oldSection = values.stream()
                .filter(addMiddlePredicate(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("구간을 찾을 수 없습니다."));

        values.add(values.indexOf(oldSection), newSection);


        oldSection.minusDistacne(newSection.getDistance());
        oldSection.changeUpStation(newSection.getDownStation());

        values.set(values.indexOf(oldSection), oldSection);
    }

    public void addDownStation(Section newSection) {
        values.add(newSection);
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

    private List<Long> getStationIds() {
        return getStations().stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }

    public List<Station> getStations() {
        if (values.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> upStations = upStations(values);
        upStations.add(getLast().getDownStation());
        return upStations;
    }

    private List<Station> upStations(List<Section> list) {
        return list.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public Section getLast() {
        return values.get(getLastIndex());
    }

    private int getLastIndex() {
        return values.size() - 1;
    }

    public boolean contains(Section section) {
        return values.contains(section);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean hasStation(Station station) {
        return getStations().contains(station);
    }

    @PostPersist
    private void sort() {
        values.sort(Comparator.comparingInt(Section::getOrder));
        log.debug("정렬실행 {}", values);
    }
}
