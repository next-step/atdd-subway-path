package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
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
        if(sections.isEmpty()) {
            sections.add(new Section(this, upStation, downStation, distance));
            return;
        }

        int index = IntStream.range(0, sections.size())
            .filter(i -> sections.get(i).getUpStation() == upStation || sections.get(i).getDownStation() == downStation)
            .findFirst()
            .orElse(-1);

        if(index != -1) {
            Section section = sections.get(index);

            if(section.getUpStation() == upStation) {
                Section res = new Section(section.getLine(), downStation, section.getDownStation(), section.getDistance() - distance);
                sections.remove(index);

                sections.add(index, res);
            }

            if(section.getDownStation() == downStation) {
                Section res = new Section(section.getLine(), section.getUpStation(), upStation, section.getDistance() - distance);
                sections.remove(index);

                sections.add(index, res);
            }
        }

        sections.add(new Section(this, upStation, downStation, distance));
    }

    // private int getIndexContainStation(Station upStation, Station downStation) {
    //     return IntStream.range(0, sections.size())
    //         .filter(i -> sections.get(i).getUpStation() == upStation || sections.get(i).getDownStation() == downStation)
    //         .findFirst()
    //         .orElse(-1);
    // }
    //
    // private void modifySection
}
