package nextstep.subway.domain;

import nextstep.subway.exception.BusinessException;
import org.springframework.http.HttpStatus;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return this.sections;
    }

    public Section getLastSection() {
        if (this.sections.isEmpty()) {
            return null;
        }

        return this.sections.get(this.sections.size() - 1);
    }

    public void addSection(Line line, Station newUpStation, Station newDownStation, int newDistance) {
        Section newSection = new Section(line, newUpStation, newDownStation, newDistance);
        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        validateExceptionCase(newUpStation, newDownStation);

        for (Section section : this.sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            int distance = section.getDistance();

            if (isNewStationInBetween(newUpStation, newDownStation, upStation, downStation, newDistance, distance)) {
                this.sections.remove(section);
                this.sections.add(new Section(section.getLine(), newUpStation, newDownStation, newDistance));
                this.sections.add(new Section(section.getLine(), newDownStation, downStation, distance - newDistance));
                return;
            }

            if (isNewUpStation(newUpStation, newDownStation) || isNewDownStation(newUpStation, newDownStation)) {
                this.sections.add(newSection);
                return;
            }
        }

        throw new BusinessException("해당 구간을 등록할 수 없습니다. : " + newUpStation.getName() + ", " + newDownStation.getName(), HttpStatus.BAD_REQUEST);
    }

    private boolean isNewDownStation(Station newUpStation, Station newDownStation) {
        return newUpStation.equals(this.findLastStation()) && !getStations().contains(newDownStation);
    }

    private boolean isNewUpStation(Station newUpStation, Station newDownStation) {
        return newDownStation.equals(this.findFirstStation()) && !getStations().contains(newUpStation);
    }

    private boolean isNewStationInBetween(Station newUpStation, Station newDownStation, Station upStation, Station downStation, int newDistance, int distance) {
        return (upStation.equals(newUpStation) && !downStation.equals(newDownStation) && newDistance < distance) || (downStation.equals(newDownStation) && !upStation.equals(newUpStation) && newDistance < distance);
    }

    private void validateExceptionCase(Station newUpStation, Station newDownStation) {
        if (isAllAlreadyIncluded(newUpStation, newDownStation)) {
            throw new BusinessException("이미 모두 등록되어 있는 역들 입니다. : " + newUpStation.getName() + ", " + newDownStation.getName(), HttpStatus.BAD_REQUEST);
        }

        if (isAllNotIncluded(newUpStation, newDownStation)) {
            throw new BusinessException("모두 등록되어 있지 않은 역들 입니다. : " + newUpStation.getName() + ", " + newDownStation.getName(), HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isAllNotIncluded(Station newUpStation, Station newDownStation) {
        return !getStations().contains(newUpStation) && !getStations().contains(newDownStation);
    }

    private boolean isAllAlreadyIncluded(Station newUpStation, Station newDownStation) {
        return getStations().contains(newUpStation) && getStations().contains(newDownStation);
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : this.sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    public List<Station> getStationsInOrder() {
        Station firstStation = findFirstStation();
        Station lastStation = findLastStation();

        List<Station> list = new ArrayList<>();
        list.add(firstStation);
        Station nextStation = firstStation;
        while (!nextStation.equals(lastStation)) {
            for (Section section : this.sections) {
                if (section.getUpStation()
                        .equals(nextStation)) {
                    nextStation = section.getDownStation();
                    list.add(nextStation);
                }
            }
        }
        return list;
    }

    public void deleteStation(Station station) {
        if (this.sections.size() <= 1) {
            throw new BusinessException("해당 지하철 노선에 지하철 구간이 하나밖에 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        if (isNotIncludedStation(station)) {
            throw new BusinessException("해당 지하철 노선에 등록되지 않은 역입니다.", HttpStatus.BAD_REQUEST);
        }

        if (isFirstStation(station)) {
            removeUpStation(station);
            return;
        }

        if (isLastStation(station)) {
            removeDownStation(station);
            return;
        }

        if (isMiddleStation(station)) {
            removeMiddleStation(station);
            return;
        }

        throw new BusinessException("해당 지하철 노선에서 지하철역을 삭제할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean isNotIncludedStation(Station station) {
        return !this.getStations()
                .contains(station);
    }

    private void removeMiddleStation(Station station) {
        Line line = this.sections.get(0)
                .getLine();
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

        this.sections.remove(upSection);
        this.sections.remove(downSection);
        this.sections.add(new Section(line, upStation, downStation, upSectionDistance + downSectionDistance));
    }

    private void removeUpStation(Station station) {
        for (Section section : this.sections) {
            if (section.isUpStation(station)) {
                this.sections.remove(section);
                return;
            }
        }
    }

    private void removeDownStation(Station station) {
        for (Section section : sections) {
            if (section.isDownStation(station)) {
                sections.remove(section);
                return;
            }
        }
    }

    private Station findFirstStation() {
        List<Station> stations = this.getStations();
        for (Station station : stations) {
            if (isFirstStation(station)) {
                return station;
            }
        }
        throw new IllegalStateException();
    }

    private boolean isFirstStation(Station station) {
        for (Section section : this.sections) {
            if (section.isDownStation(station)) {
                return false;
            }
        }
        return true;
    }

    private Station findLastStation() {
        List<Station> stations = this.getStations();
        for (Station station : stations) {
            if (isLastStation(station)) {
                return station;
            }
        }
        throw new IllegalStateException();
    }

    private boolean isLastStation(Station station) {
        for (Section section : this.sections) {
            if (section.isUpStation(station)) {
                return false;
            }
        }
        return true;
    }

    private boolean isMiddleStation(Station station) {
        return this.getStations()
                .contains(station) && !isFirstStation(station) && !isLastStation(station);
    }

    public int getDistance() {
        return this.sections.stream()
                .mapToInt(section -> section.getDistance())
                .sum();
    }

    private Station findFirstStation() {
        List<Station> stations = this.getStations();
        for (Station station : stations) {
            if (isFirstStation(station)) {
                return station;
            }
        }
        throw new IllegalStateException();
    }

    private boolean isFirstStation(Station station) {
        for (Section section : this.sections) {
            if (section.isDownStation(station)) {
                return false;
            }
        }
        return true;
    }

    private Station findLastStation() {
        List<Station> stations = this.getStations();
        for (Station station : stations) {
            if (isLastStation(station)) {
                return station;
            }
        }
        throw new IllegalStateException();
    }

    private boolean isLastStation(Station station) {
        for (Section section : this.sections) {
            if (section.isFirstStation(station)) {
                return false;
            }
        }
        return true;
    }

    public int getDistance() {
        return this.sections.stream()
                .mapToInt(section -> section.getDistance())
                .sum();
    }
}
