package nextstep.subway.domain;

import nextstep.subway.exception.AddSectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (isEmptySections()) {
            this.sections.add(new Section(this, upStation, downStation, distance));
            return;
        }

        for (int i = 0; i < this.sections.size(); i++) {
            validateSameUpDownStation(upStation, downStation, sections.get(i));

            if (Objects.equals(sections.get(i).getUpStation().getId(), upStation.getId())) {
                Station currentUpStation = sections.get(i).getUpStation();
                Station currentDownStation = sections.get(i).getDownStation();
                int newSectionDistance = getNewSectionDistance(distance, sections.get(i));
                sections.get(i).modify(this, currentUpStation, downStation, distance);
                this.sections.add(i + 1, new Section(this, downStation, currentDownStation, newSectionDistance));
                return;
            }
        }
    }

    private boolean isEmptySections() {
        return getSections() == null || getSections().isEmpty();
    }

    private int getNewSectionDistance(int distance, Section section) {
        validateDistance(distance, section);
        return section.getDistance() - distance;
    }

    private void validateDistance(int distance, Section section) {
        if (section.getDistance() <= distance) {
            throw new AddSectionException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    private void validateSameUpDownStation(Station upStation, Station downStation, Section section) {
        if (Objects.equals(section.getUpStation().getId(), upStation.getId()) && Objects.equals(section.getDownStation().getId(), downStation.getId())) {
            throw new AddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }
}
