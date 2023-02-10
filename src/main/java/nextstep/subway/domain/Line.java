package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

import nextstep.subway.exception.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            // 상행, 하행 둘다 노선에 있을 때 예외 처리
            if (sections.containsStations(List.of(section.getUpStation(), section.getDownStation()))) {
                throw new SectionAlreadyCreateStationException();
            }
            if (!sections.checkExistStation(section.getUpStation()) && !sections.checkExistStation(section.getDownStation())) {
                throw new SectionDoesNotHaveAlreadyCreateStationException();
            }

            // 추가하행역이 노선의 상행역인 경우
            if (isUpStationId(section.getDownStation().getId())) {
                // 노선의 맨 앞에 구간 추가
                sections.addFirst(section);
            } else {
                sections.addLast(section);
            }
        } else {
            sections.addLast(section);
        }
    }

    private boolean doesNotContainStationId(Long id) {
        List<Station> stations = getStations();
        return stations.stream()
                .map(Station::getId)
                .anyMatch((stationId) -> stationId.equals(id));
    }

    private boolean isUpStationId(long id) {
        return sections.getFirstSection().getUpStation().getId().equals(id);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Station station) {
        if (!sections.getLastSection().getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.removeLastSection();
    }

    public void changeNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public boolean isEmptySection() {
        return this.sections.isEmpty();
    }

    public boolean checkExistStation(Station station) {
        return sections.checkExistStation(station);
    }
}
