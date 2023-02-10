package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

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
        if (sections.isEmpty()) {
            sections.addLast(section);
            return ;
        }

        // 상행, 하행 둘다 노선에 있을 때 예외 처리
        if (sections.containsStations(List.of(section.getUpStation(), section.getDownStation()))) {
            throw new SectionAlreadyCreateStationException();
        }
        if (!sections.checkExistStation(section.getUpStation()) && !sections.checkExistStation(section.getDownStation())) {
            throw new SectionDoesNotHaveAlreadyCreateStationException();
        }

        // 노선 상행역, 하행역에 앞, 뒤에 추가
        if (isUpStationId(section.getDownStation().getId()) || isDownStaionId(section.getUpStation().getId())) {
            sections.addLast(section);
            return ;
        }
        addSectionInMiddle(section);
    }

    private void addSectionInMiddle(Section section) {
        if (sections.addSectionSameUpStation(section)) {
            return ;
        }
        sections.addSectionSameDownStation(section);
    }

    private boolean isDownStaionId(Long id) {
        return sections.getLastSection().getDownStation().getId().equals(id);
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
