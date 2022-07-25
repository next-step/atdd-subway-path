package nextstep.subway.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Line {

    private static final int MIN_SECTION_SIZE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public Section getLastSection() {
        return this.sections.get(getSectionLastIndex());
    }

    private int getSectionLastIndex() {
        return this.getSections().size() - 1;
    }

    public List<Station> getAllStation() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        if (isOneSectionSize()) {
            return Arrays.asList(getFirstSection().getUpStation(), getFirstSection().getDownStation());
        }

        return Stream.concat(
            Stream.of(getFirstSection().getUpStation()),
            this.sections.stream().map(Section::getDownStation)
        )
            .collect(toList());
    }

    public Section getFirstSection() {
        return sections.get(0);
    }

    private boolean isOneSectionSize() {
        return this.sections.size() == MIN_SECTION_SIZE;
    }

    public void removeLastSection() {
        this.sections.remove(getLastSection());
    }
}
