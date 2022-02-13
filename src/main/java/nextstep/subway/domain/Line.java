package nextstep.subway.domain;

import lombok.Builder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

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

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.stream()
                .sorted(Comparator.comparing(Section::getId))
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void deleteSection(Long stationId) {
        validateDeletion(stationId);
        int lastSectionIdx = this.sections.size() - 1;
        this.sections.remove(lastSectionIdx);
    }

    private void validateDeletion(Long stationId) {
        if (!isLastSection(stationId)) {
            throw new IllegalArgumentException("마지막 구간만 삭제할 수 있습니다. stationId를 확인해주세요.");
        }
    }

    private boolean isLastSection(Long stationId) {
        return getLastSection().getDownStation().hasSameId(stationId);
    }

    private Section getLastSection() {
        return getSections().get(sections.size() - 1);
    }

    public void update(Line line) {
        if (isNameValid()) {
            name = line.getName();
        }
        if (isColorValid()) {
            color = line.getColor();
        }
    }

    private boolean isNameValid() {
        return this.name != null;
    }

    private boolean isColorValid() {
        return this.color != null;
    }
}
