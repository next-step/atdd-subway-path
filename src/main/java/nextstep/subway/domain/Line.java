package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @ManyToOne
    private Station firstStation;

    @ManyToOne
    private Station lastStation;

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

    public boolean hasSections() {
        return sections.size() > 0;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            firstStation = section.getUpStation();
            lastStation = section.getDownStation();
            return;
        }

        // 상행 종점
        if (isFirstSection(section)) {
            sections.add(0, section);
            firstStation = section.getUpStation();
            return;
        }

        // 하행 종점
        if (isLastSection(section)) {
            sections.add(section);
            lastStation = section.getDownStation();
            return;
        }

        // 사이
        sections.add(section);
    }

    public int getSectionsCount() {
        return sections.size();
    }

    public Section getFirstSection() {
        if (sections.isEmpty()) {
            throw new NoSuchElementException();
        }

        return sections.stream().filter(sec -> isFirstStation(sec.getUpStation())).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();

        Section currSection = getFirstSection();
        while (currSection != null) {
            stations.add(currSection.getUpStation());
            stations.add(currSection.getDownStation());
            currSection = nextSection(currSection);
        }

        return new ArrayList<>(stations);
    }

    private Section nextSection(Section currSection) {
        // 다음 구간 : 현재 구간의 하행역이 상행역인 구간
        return sections.stream().filter(sec -> sec.getUpStation().equals(currSection.getDownStation())).findFirst().orElse(null);
    }

    public void removeSection(Station station) {
        if (!isLastStation(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public int getLength() {
        if (sections.isEmpty()) {
            return 0;
        }

        return sections.stream().map(Section::getDistance).reduce(0, Integer::sum);
    }

    public Station getFirstStation() {
        return firstStation;
    }

    public Station getLastStation() {
        return lastStation;
    }

    private boolean isFirstStation(Station station) {
        return firstStation.equals(station);
    }

    private boolean isLastStation(Station station) {
        return sections.get(sections.size() - 1).getDownStation().equals(station);
    }

    private boolean isFirstSection(Section section) {
        return isFirstStation(section.getDownStation());
    }

    private boolean isLastSection(Section section) {
        return isLastStation(section.getUpStation());
    }

}
