package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    private Sections sections;

    protected Line() {/*no-op*/}

    public Line(Long id, String name, String color, Section section) {
        if (name == null || name.isBlank() || name.length() < 2) {
            throw new IllegalArgumentException("이름은 공백이 될 수 없습니다.");
        }

        if (color == null || color.isBlank() || color.length() < 2) {
            throw new IllegalArgumentException("색은 공백이 될 수 없습니다.");
        }

        if (section == null) {
            throw new IllegalArgumentException("구간이 존재해야 합니다.");
        }

        this.id = id;
        this.name = name;
        this.color = color;
        sections = new Sections(new Section(null, this, section.getUpStation(), section.getDownStation(), section.getDistance()));
    }

    public Line(String name, String color, Section section) {
        this(null, name, color, section);
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

    public Sections getSections() {
        return sections;
    }
}
