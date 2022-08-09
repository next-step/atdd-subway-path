package nextstep.subway.domain;

import nextstep.subway.exception.NonExistentSectionException;
import nextstep.subway.exception.SectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int MIN_SECTIONS_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {
        checkStationsExist(upStation, downStation);
        checkSameSectionExist(upStation, downStation);

        if (sections.isEmpty()) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }

        if (isTerminalSection(upStation, downStation)) {
            addSectionAtTerminal(line, upStation, downStation, distance);
            return;
        }

        addSectionAtMiddle(line, upStation, downStation, distance);
    }

    public void remove(Station station) {
        if (sections.size() <= MIN_SECTIONS_SIZE) {
            throw new SectionException("구간이 하나 일때는 삭제할 수 없습니다.");
        }

        if (isLastDownStation(station)) {
            removeDownStation(station);
            return;
        }

        if (isFirstUpStation(station)) {
            removeUpStation(station);
            return;
        }

        if (isMiddleStation(station)) {
            removeMiddleStation(station);
            return;
        }

        throw new SectionException("존재하지 않는 지하철 역입니다.");
    }

    private void checkSameSectionExist(Station upStation, Station downStation) {
        for (Section section : sections) {
            if (section.hasSameStations(upStation, downStation)) {
                throw new SectionException("상행역과 하행역이 이미 등록되어 있습니다.");
            }
        }
    }

    public void checkStationsExist(Station upStation, Station downStation) {
        if (sections.isEmpty()) {
            return;
        }

        for (Section section : sections) {
            if (section.contains(upStation, downStation)) {
                return;
            }
        }

        throw new SectionException("지하철 노선 구간에 상행역과 하행역이 등록되어있지 않습니다.");
    }

    private boolean isTerminalSection(Station upStation, Station downStation) {
        List<Station> stations = getStations();
        return stations.get(0).equals(downStation) || stations.get(stations.size() - 1).equals(upStation);
    }

    private void addSectionAtTerminal(Line line, Station upStation, Station downStation, int distance) {
        for (Section section : sections) {
            if (section.isDownStation(upStation) || section.isUpStation(downStation)) {
                sections.add(new Section(line, upStation, downStation, distance));
                return;
            }
        }
    }

    private void addSectionAtMiddle(Line line, Station upStation, Station downStation, int distance) {
        if (isAddUpStation(upStation)) {
            addSectionBasedUpStation(line, upStation, downStation, distance);
            return;
        }

        addSectionBasedDownStation(line, upStation, downStation, distance);
    }

    private boolean isAddUpStation(Station upStation) {
        return sections.stream()
                .anyMatch(it -> it.isUpStation(upStation));
    }

    private void addSectionBasedUpStation(Line line, Station upStation, Station downStation, int distance) {
        Section section = sections.stream()
                .filter(it -> it.isUpStation(upStation))
                .findAny()
                .orElseThrow(NonExistentSectionException::new);

        checkAvailableDistance(distance, section);
        sections.add(new Section(line, upStation, downStation, distance));
        sections.add(new Section(line, downStation, section.getDownStation(), section.getDistance() - distance));
        sections.remove(section);
    }

    private void addSectionBasedDownStation(Line line, Station upStation, Station downStation, int distance) {
        Section section = sections.stream()
                .filter(it -> it.isDownStation(upStation))
                .findAny()
                .orElseThrow();

        checkAvailableDistance(distance, section);
        sections.add(new Section(line, upStation, downStation, distance));
        sections.add(new Section(line, section.getUpStation(), upStation, section.getDistance() - distance));
        sections.remove(section);
    }

    private void checkAvailableDistance(int distance, Section section) {
        if (section.getDistance() <= distance) {
            throw new SectionException("새로운 구간의 길이가 기존 구간의 길이보다 큽니다.");
        }
    }

    public List<Station> getStations() {
        List<Station> existStations = existStations();
        if (existStations.isEmpty()) {
            return Collections.emptyList();
        }

        Station upStation = getFirstStation(existStations);
        List<Station> stations = new ArrayList<>(List.of(upStation));

        while (isNotLastStation(upStation)) {
            upStation = findNextStation(upStation, stations);
            stations.add(upStation);
        }

        return stations;
    }

    private Station findNextStation(Station upStation, List<Station> stations) {
        for (Section section : sections) {
            if (section.isUpStation(upStation)) {
                upStation = section.getDownStation();
                break;
            }
        }
        return upStation;
    }

    private boolean isNotLastStation(Station firstStation) {
        for (Section section : sections) {
            if (section.isUpStation(firstStation)) {
                return true;
            }
        }
        return false;
    }

    private List<Station> existStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private Station getFirstStation(List<Station> stations) {
        for (Station station : stations) {
            if (isFirstStation(station)) {
                return station;
            }
        }
        throw new SectionException("구간의 구성이 잘못되었습니다.");
    }

    private boolean isFirstStation(Station station) {
        for (Section section : sections) {
            if (section.isDownStation(station)) {
                return false;
            }
        }
        return true;
    }

    private void removeMiddleStation(Station station) {
        Line line = sections.get(0).getLine();
        Section upSection = new Section();
        Section downSection = new Section();
        Station upStation = new Station();
        Station downStation = new Station();

        int upSectionDistance = 0;
        int downSectionDistance = 0;

        for (Section section : sections) {
            if (section.isUpStation(station)) {
                downStation = section.getDownStation();
                upSectionDistance = section.getDistance();
                upSection = section;
            }

            if (section.isDownStation(station)) {
                upStation = section.getUpStation();
                downSectionDistance = section.getDistance();
                downSection = section;
            }
        }

        sections.remove(upSection);
        sections.remove(downSection);
        sections.add(new Section(line, upStation, downStation, upSectionDistance + downSectionDistance));
    }

    private void removeUpStation(Station station) {
        for (Section section : sections) {
            if (section.isUpStation(station)) {
                sections.remove(section);
                break;
            }
        }
    }

    private void removeDownStation(Station station) {
        for (Section section : sections) {
            if (section.isDownStation(station)) {
                sections.remove(section);
                break;
            }
        }
    }

    private boolean isMiddleStation(Station station) {
        List<Station> stations = getStations();
        return stations.contains(station) && stations.indexOf(station) != 0 && stations.indexOf(station) != stations.size() - 1;
    }

    private boolean isFirstUpStation(Station station) {
        List<Station> stations = getStations();
        return stations.indexOf(station) == 0;
    }

    private boolean isLastDownStation(Station station) {
        List<Station> stations = getStations();
        return stations.indexOf(station) == stations.size() - 1;
    }
}
