package nextstep.subway.line.section;

import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Embeddable
public class Sections {

    public static final int FIRST_INDEX = 0;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    @OrderBy("upStation.id ASC")
    private List<Section> sectionList = new LinkedList<>();

    protected Sections() {
    }

    private Sections(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public static Sections from(List<Section> sectionList) {
        return new Sections(sectionList);
    }

    public List<Station> startStations() {
        return this.sectionList.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private Section firstSection() {
        return this.sectionList.get(0);
    }

    private Section lastSection() {
        return this.sectionList.get(lastIndex());
    }

    public Station lastStation() {
        return lastSection().getDownStation();
    }

    private int lastIndex() {
        return this.sectionList.size() - 1;
    }

    public ApplyType add(Section section) {
        if (isAlreadyAdded(section)) {
            throw new IllegalArgumentException("이미 추가된 구간입니다.");
        }

        if (canAddFirst(section)) {
            this.sectionList.add(FIRST_INDEX, section);
            return ApplyType.FIRST;
        }

        if (canAddLast(section)) {
            this.sectionList.add(section);
            return ApplyType.LAST;
        }

        addMiddle(section);
        return ApplyType.MIDDLE;
    }

    private boolean isAlreadyAdded(Section section) {
        return this.sectionList.stream()
                .anyMatch(s -> s.anyMatchUpStationAndDownStation(section));
    }

    private void addMiddle(Section section) {
        AddingPosition addingPosition = AddingPosition.of(this.sectionList, section);
        Section existing = this.sectionList.get(addingPosition.findingIndex());
        existing.changeSectionFromToInput(addingPosition, section);
        this.sectionList.add(addingPosition.addingIndex(), section);
    }

    private boolean canAddFirst(Section section) {
        return firstSection().isSameUpStationInputDownStation(section);
    }

    private boolean canAddLast(Section section) {
        return lastSection().isSameDownStationInputUpStation(section);
    }

    public ApplyDistance delete(Station station) {
        if (this.sectionList.size() == 1) {
            throw new IllegalArgumentException("구간이 하나 일 때는 삭제를 할 수 없습니다.");
        }
        return deleteTarget(station);
    }

    private ApplyDistance deleteTarget(Station station) {
        Section targetSection;

        if (canDeleteFirst(station)) {
            targetSection = firstSection();
            this.sectionList.remove(targetSection);
            return ApplyDistance.applyFirst(targetSection.distance());
        }

        if (canDeleteLast(station)) {
            targetSection = lastSection();
            this.sectionList.remove(targetSection);
            return ApplyDistance.applyLast(targetSection.distance());
        }

        return ApplyDistance.applyMiddle(deleteMiddle(station).distance());
    }

    private Section deleteMiddle(Station station) {
        int index = findMiddleIndex(station);
        Section section = this.sectionList.get(index);
        Section targetSection = this.sectionList.get(index + 1);
        section.changeDownStationFromToInputDownStation(targetSection);
        this.sectionList.remove(targetSection);
        return targetSection;
    }

    private int findMiddleIndex(Station station) {
        return IntStream.range(0, this.sectionList.size() - 1)
                .filter(i -> this.sectionList.get(i).isSameDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 역을 찾지 못했습니다."));
    }

    private boolean canDeleteFirst(Station station) {
        return firstSection().isSameUpStation(station);
    }

    private boolean canDeleteLast(Station station) {
        return lastSection().isSameDownStation(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections = (Sections) o;
        return Objects.equals(sectionList, sections.sectionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionList);
    }
}
