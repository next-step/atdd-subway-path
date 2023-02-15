package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Slf4j
@EqualsAndHashCode
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Sections implements Iterable<Section> {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("orderSeq ASC")
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

        SectionAddAction action = SectionAddAction.of(values, newSection);
        action.add(values, newSection);

        updateOrder();
    }

    private boolean forceAddCondition(Section newSection) {
        return values.isEmpty() || values.contains(newSection); // 메서드 대시 변수로 할당하는게 좋을까?
    }

    public void remove(Station station) {
        Long id = station.getId();
        SectionDeleteAction action = SectionDeleteAction.of(values, id);
        action.delete(values, id);

        updateOrder();
    }

    private void updateOrder() {
        for (int i = 0; i < values.size(); i++) {
            Section section = values.get(i);
            section.changeOrder(i);
        }
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

    public void merge(List<Section> list) {
        list.addAll(copy());
    }

    public List<Section> copy() {
        return values.stream().collect(Collectors.toList());
    }
}
