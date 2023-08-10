package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "start_station_id")
    private Station startStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "end_station_id")
    private Station endStation;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
            CascadeType.MERGE}, orphanRemoval = true)
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

    public List<Station> getStations() {
        if (this.getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station nowStation = startStation;
        stations.add(nowStation);
        while (!nowStation.equals(endStation)) {
            for (Section section : sections) {
                if (section.getUpStation().equals(nowStation)) {
                    nowStation = section.getDownStation();
                    stations.add(nowStation);
                    break;
                }
            }
        }
        return stations;
    }

    public void addSections(Section section) {
        if (this.sections.isEmpty()) {
            this.startStation = section.getUpStation();
            this.endStation = section.getDownStation();
        }

        if (this.endStation.equals(section.getUpStation())) {
            this.endStation = section.getDownStation();
        }
        if (this.startStation.equals(section.getDownStation())) {
            this.startStation = section.getUpStation();
        }
        sections.add(section);
    }

    public Station getStartStation() {
        return this.startStation;
    }

    public Station getEndStation() {
        return this.endStation;
    }

    public void removeSection(Station station) {
        for (Section section : sections) {
            if (section.getDownStation().equals(station)) {
                endStation = section.getUpStation();
                this.sections.remove(section);
                break;
            }
        }
    }

}
