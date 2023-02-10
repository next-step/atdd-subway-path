package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        isValidationSection(section);

        this.getSections().add(section);
    }

    public void addSection(String addTypeCd, Section section) {
        isValidationSection(section);

        if ("1".equals(addTypeCd)) {
            Section standardSection = this.getSections().stream().filter(a -> section.getDownStation().getName().equals(a.getUpStation().getName())).findFirst().orElseThrow(IllegalArgumentException::new);

            this.getSections().add(this.getSections().indexOf(standardSection), section);
            return;
        }

        if ("2".equals(addTypeCd)) {
            Section standardSection = this.getSections().stream().filter(a -> section.getUpStation().getName().equals(a.getUpStation().getName())).findFirst().orElseThrow(IllegalArgumentException::new);

            isValidationSectionDistance(standardSection, section);

            int standardIndex = this.getSections().indexOf(standardSection);

            this.getSections().set(standardIndex, new Section(this, standardSection.getUpStation(), section.getDownStation(), section.getDistance()));
            this.getSections().add(standardIndex+1, new Section(this, section.getDownStation(), standardSection.getDownStation(), standardSection.getDistance()-section.getDistance()));

            return;
        }
    }

    private void isValidationSectionDistance(Section standardSection, Section section) {
        if (standardSection.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException();
        }
    }

    private void isValidationSection(Section section) {
        if ((this.getSections().size() > 0) && isExistUpOrDownStation(section)){
            throw new IllegalArgumentException();
        }
    }

    private boolean isExistUpOrDownStation (Section section) {
        int existStationCnt = this.getStations().stream().filter(a -> a.getName().equals(section.getUpStation().getName())
                || a.getName().equals(section.getDownStation().getName())).collect(Collectors.toList()).size();
        return existStationCnt != 1;
    }

    public void removeSection(Station station) {
        if (!this.getSections().get(this.getSections().size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        this.getSections().remove(this.getSections().size() - 1);
    }

    public List<Station> getStations() {
        if (this.getSections().size() <= 0) {
            return new ArrayList<>();
        }

        List<Station> stations = this.getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, this.getSections().get(0).getUpStation());

        return stations;
    }
}
