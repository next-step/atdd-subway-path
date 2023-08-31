package nextstep.subway.domain;

import nextstep.subway.common.exception.BusinessException;

import javax.persistence.*;
import java.util.*;

@Embeddable
public class Sections {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "start_station_id")
    private Station startStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "end_station_id")
    private Station endStation;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
            CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections;


    public Sections() {
        sections = new ArrayList<>();
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
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
                }
            }
        }
        return stations;
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public void addSection(Section section) {
        if (new HashSet<>(this.getStations()).containsAll(List.of(section.getUpStation(), section.getDownStation()))) {
            throw new BusinessException();
        }

        if (this.sections.isEmpty()) {
            this.startStation = section.getUpStation();
            this.endStation = section.getDownStation();
            sections.add(section);
            return;
        }

        if (this.endStation.equals(section.getUpStation())) {
            this.endStation = section.getDownStation();
            sections.add(section);
            return;
        }
        if (this.startStation.equals(section.getDownStation())) {
            this.startStation = section.getUpStation();
            sections.add(section);
            return;
        }

        Optional<Section> beforeSection = sections.stream()
                .filter(sectionIter ->
                        sectionIter.getUpStation().equals(section.getUpStation()) || sectionIter.getDownStation().equals(section.getDownStation()
                        )).findFirst();
        beforeSection.orElseThrow(BusinessException::new).splitSection(section);
        sections.add(section);
    }

    public void removeSection(Station station) {
        sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .ifPresent(section -> {
                    endStation = section.getUpStation();
                    sections.remove(section);
                });
    }

}
