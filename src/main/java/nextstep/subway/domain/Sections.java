package nextstep.subway.domain;

import nextstep.subway.common.exception.SubwayException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static nextstep.subway.common.exception.ErrorCode.*;

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


    public void addSection(Section section) {

        containsStation(section);

        List<Section> betweenSection = findBetweenSection(section);

        if (!betweenSection.isEmpty()) {
            if (betweenSection.get(0).getDistance() <= section.getDistance()) {
                throw new SubwayException(NOT_SAME_SMALL_DISTANCE);
            }
            betweenSection.get(0).update(section.getDownStation(), section.getDistance());
        }
        sections.add(section);
    }


    private List<Section> findBetweenSection(Section addSection) {
        return sections.stream().filter(section1 ->
                addSection.getUpStation().equals(section1.getUpStation())).collect(Collectors.toList());
    }


    private void containsStation(Section section) {
        if (!sections.isEmpty()) {
            if (!getStations().contains(section.getUpStation()) && !getStations().contains(section.getDownStation())) {
                throw new SubwayException(NOT_ENROLL_UP_AND_DOWN_STATION);
            }
        }

        if (getStations().contains(section.getUpStation()) && getStations().contains(section.getDownStation())) {
            throw new SubwayException(ALREADY_ENROLL_UP_AND_DOWN_STATION);
        }

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
