package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color, Station upStation, Station downStation, Distance distance) {
        Line line = new Line(name, color);

        line.addSection(upStation, downStation, distance);

        return line;
    }

    public void addSection(Station upStation, Station downStation, Distance distance) {
        sections.add(this, upStation, downStation, distance);
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

    public void deleteSection(Station station) {
        if (haveOnlySection()) {
            throw new IllegalArgumentException("구간이 1개인 경우 삭제가 불가합니다.");
        }

        sections.deleteSection(station);
    }

    private boolean haveOnlySection() {
        return sections.count() == 1;
    }

}
