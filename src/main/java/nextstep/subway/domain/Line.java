package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.getSection();
    }

    public void addSection(Section newSection) {
        boolean addInUpSection = sections.exist(newSection.getUpStation());
        boolean addInDownSection = sections.exist(newSection.getDownStation());
        validate(addInUpSection, addInDownSection);
        if (addInUpSection) {
            sections.addInUp(newSection);
        }
        if (addInDownSection) {
            sections.addInDown(newSection);
        }
        this.sections.addNew(this, newSection);
    }

    public void validate(boolean addInUpSection, boolean addInDownSection) {
        if (addInUpSection && addInDownSection) {
            throw new IllegalArgumentException("상행, 하행이 중복된 구간을 등록할 수 없습니다.");
        }
        if (!sections.isEmpty() && !addInUpSection && !addInDownSection) {
            throw new IllegalArgumentException("노선에 존재하지 않는 구간은 추가할 수 없습니다.");
        }
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public List<Integer> getSectionDistances() {
        return sections.getSectionDistances();
    }

    public void removeSection(Station station) {
        this.sections.removeSection(station);
    }

    public void removeSection(String stationName) {
        this.sections.removeSection(stationName);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
}
