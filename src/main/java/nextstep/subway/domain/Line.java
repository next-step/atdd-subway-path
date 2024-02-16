package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Column(nullable = false)
    private int distance;

    private Long upStationId;

    private Long downStationId;

    // TODO 일급 컬렉션 작성
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void updateLine(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(final Section section) {
        // 새 라인의 경우 section 검증 제외
        if (!isNewLine()) {
            LineValidator.checkSectionForAddition(this, section);
        }

        this.downStationId = section.getDownStation().getId();
        this.distance += section.getDistance();
        this.sections.add(section);
//        section.registerLine(this);
    }

    public void removeSection(final Section section) {
        LineValidator.checkSectionForRemove(this, section);

        this.downStationId = section.getUpStation().getId();
        this.distance -= section.getDistance();
        this.sections.remove(section);
    }

    private boolean isNewLine() {
        return this.getSections().isEmpty();
    }

    protected Line() {
    }

    public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public List<Section> getSections() {
        return sections;
    }
}
