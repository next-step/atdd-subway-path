package nextstep.subway.domain;

import nextstep.subway.common.exception.SubwayException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

    public Station getLastStationBySort() {
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
            betweenSection.get(0).addUpdate(section.getDownStation(), section.getDistance());
        }
        sections.add(section);
    }


    private List<Section> findBetweenSection(Section addSection) {
        return sections.stream().filter(section ->
                addSection.getUpStation().equals(section.getUpStation())).collect(Collectors.toList());
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
            throw new SubwayException(NOT_EXIST_STATION);
        }

        if (getStations().size() == 2) {
            throw new SubwayException(NOT_DELETE_LAST_SECTION);
        }

        if (isDownStation(station)) {
            sections.removeIf(section -> section.getDownStation() == station);
        }

        if (isUpStation(station)) {
            sections.removeIf(section -> section.getUpStation() == station);
        }

        if (isBetweenStation(station)) {
            deleteBetweenStation(station);
        }
    }

    private boolean isDownStation(Station station) {
        return findFirstStation() != station && findLastStation() == station;
    }

    private boolean isUpStation(Station station) {
        return findFirstStation() == station && findLastStation() != station;
    }

    private boolean isBetweenStation(Station station) {
        return findFirstStation() != station && findLastStation() != station;
    }


    private void deleteBetweenStation(Station station) {
        Station upStation = null;
        int distance = 0;
        Iterator<Section> iterator = sections.iterator();
        while (iterator.hasNext()) {
            Section section = iterator.next();
            if (section.getDownStation().equals(station)) {
                upStation = section.getUpStation();
                distance = section.getDistance();
                iterator.remove();
            }
            if (section.getUpStation().equals(station)) {
                section.deleteUpdate(upStation, distance);
            }
        }


    }
}
