package nextstep.subway.domain;

import java.util.stream.Collectors;
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
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
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

    public boolean isEmptySections(){
        return sections.isEmpty();
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void changeNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Station> getStations(){
        return sections.getStations()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean isEqualLastSectionDownStation(Station station){
        return sections.isEqualLastSectionDownStation(station);
    }

    public void removeSection(){
        sections.remove();
    }
}
