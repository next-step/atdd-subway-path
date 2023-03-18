package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.constants.ErrorConstant.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getSortStation() {
        List<Station> stations = new ArrayList<>();
        Station findStation = findFirstStation();
        while (!stations.contains(findLastStation())) {
            for (Section section : sections) {
                if (section.getUpStation() == findStation) {
                    stations.add(section.getUpStation());
                    stations.add(section.getDownStation());
                    findStation = section.getDownStation();
                }
            }
        }
        return stations.stream().distinct().collect(Collectors.toList());
    }

    public Station getLastStation() {
        return sections.get(sections.size()-1).getDownStation();
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        if (sections.size() >= 2) {
            return getSortStation();
        }
        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        stations.add(sections.get(0).getDownStation());
        return stations;
    }


    public void addSection(Section addSection) {
        if (getStations().contains(addSection.getUpStation()) && getStations().contains(addSection.getDownStation())) {
            throw new IllegalArgumentException(ALREADY_ENROLL_UP_AND_DOWN_STATION);
        }

        if (!sections.isEmpty()) {
            if (!getStations().contains(addSection.getUpStation()) && !getStations().contains(addSection.getDownStation())) {
                throw new IllegalArgumentException(NOT_ENROLL_UP_AND_DOWN_STATION);
            }
        }

        for (Section section : sections) {
            if (section.getUpStation() == addSection.getUpStation()) {
                if (section.getDistance() >= addSection.getDistance()) {
                    throw new IllegalArgumentException(NOT_SAME_SMALL_DISTANCE);
                }
                section.update(addSection.getDownStation(), addSection.getDistance());
            }
        }
        sections.add(addSection);
    }



    public Station findFirstStation() {
        Station upStation = null;
        List<Station> stations = sections.stream().map(Section::getDownStation).collect(Collectors.toList());
        for (Section section : sections) {
            if(!stations.contains(section.getUpStation())) {
                upStation = section.getUpStation();
            }
        }
        return upStation;
    }

    public Station findLastStation() {
        Station downStation = null;
        List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        for (Section section : sections) {
            if (!stations.contains(section.getDownStation())) {
                downStation = section.getDownStation();
            }
        }
        return downStation;
    }


    public void deleteSection(Station station) {
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException("해당 역은 존재하지 않습니다.");
        }
        if (station != getLastStation()) {
            throw new IllegalArgumentException("해당 역은 삭제할 수 없습니다");
        }
        sections.remove(sections.size()-1);
    }
}
