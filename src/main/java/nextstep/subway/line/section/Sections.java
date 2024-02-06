package nextstep.subway.line.section;

import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

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

    private Section lastSection() {
        return this.sectionList.get(sectionList.size() - 1);
    }

    public Station lastStation() {
        return lastSection().getDownStation();
    }

    public void add(Section section) {
        this.sectionList.add(0, section);
    }

    public boolean isSameLastStationAndStartStation(Section station) {
        return station.isSameUpStation(lastStation());
    }

    public boolean anyMatchStation(Section section) {
        return this.sectionList.stream()
                .anyMatch(s -> s.anyMatchUpStationOrDownStation(section));
    }

    public Section delete(Station station) {
        if (this.sectionList.size() == 1) {
            throw new IllegalArgumentException("구간이 하나 일 때는 삭제를 할 수 없습니다.");
        }

        Section findSection = this.sectionList.stream()
                .filter(section -> section.isSameDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("삭제할 역을 찾지 못하였습니다."));

        if (!lastSection().equals(findSection)) {
            throw new IllegalArgumentException("마지막 구간의 역이 아닙니다.");
        }

        this.sectionList.remove(findSection);
        return findSection;
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
