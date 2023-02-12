package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

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
        return sections.getSections();
    }

    public void addSection(Section newSection) {
        boolean addInUpSection = sections.exist(newSection.getUpStation());
        boolean addInDownSection = sections.exist(newSection.getDownStation());
        addable(addInUpSection, addInDownSection);
        if (addInUpSection) {
            sections.addInUp(newSection);
        }
        if (addInDownSection) {
            sections.addInDown(newSection);
        }
        this.sections.addNew(this, newSection);
    }

    public void addable(boolean existInUpSection, boolean existInDownSection) {
        if (isExistBothSection(existInUpSection, existInDownSection)) {
            throw new IllegalArgumentException("상행, 하행이 중복된 구간을 등록할 수 없습니다.");
        }
        if (isNotFoundSection(existInUpSection, existInDownSection)) {
            throw new IllegalArgumentException("노선에 존재하지 않는 구간은 추가할 수 없습니다.");
        }
    }

    private boolean isNotFoundSection(boolean existInUpSection, boolean existInDownSection) {
        return !sections.isEmpty() && isExistBothSection(!existInUpSection, !existInDownSection);
    }

    private static boolean isExistBothSection(boolean existInUpSection, boolean existInDownSection) {
        return existInUpSection && existInDownSection;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public List<Integer> getSectionDistances() {
        return sections.getSectionDistances();
    }

    public void removeSection(Station station) {
        Optional<Section> upSection = sections.findUpSection(station);
        Optional<Section> downSection = sections.findDownSection(station);

        removable(upSection.isPresent(), downSection.isPresent());
        if (upSection.isPresent() && downSection.isPresent()) {
            Section section = upSection.get();
            sections.removeInUp(section);
            sections.removeSection(section);
        } else {
            upSection.ifPresent(sections::removeInUp);
            downSection.ifPresent(sections::removeInDown);
        }
    }

    private void removable(boolean existInUpSection, boolean existInDownSection) {
        if (isNotFoundSection(existInUpSection, existInDownSection)) {
            throw new IllegalArgumentException("존재하지 않는 구간은 삭제할 수 없습니다.");
        }
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
}
