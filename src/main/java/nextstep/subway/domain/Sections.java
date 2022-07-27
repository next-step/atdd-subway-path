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

        // 이미 모두 등록되어 있음
        if (getStations().contains(newUpStation) && getStations().contains(newDownStation)) {
            throw new BusinessException("이미 모두 등록되어 있는 역들 입니다. : " + newUpStation.getName() + ", " + newDownStation.getName(), HttpStatus.BAD_REQUEST);
        }

        // 둘다 포함되어 있지 않음
        if (!getStations().contains(newUpStation) && !getStations().contains(newDownStation)) {
            throw new BusinessException("모두 등록되어 있지 않은 역들 입니다. : " + newUpStation.getName() + ", " + newDownStation.getName(), HttpStatus.BAD_REQUEST);
        }

        for (Section section : this.sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            int distance = section.getDistance();

            // 사이에 있음
            if (upStation.equals(newUpStation) && !downStation.equals(newDownStation) && newDistance < distance) {
                this.sections.remove(section);
                this.sections.add(new Section(section.getLine(), newUpStation, newDownStation, newDistance));
                this.sections.add(new Section(section.getLine(), newDownStation, downStation, distance - newDistance));
                return;
            }

            // 사이에 있음
            if (downStation.equals(newDownStation) && !upStation.equals(newUpStation) && newDistance < distance) {
                this.sections.remove(section);
                this.sections.add(new Section(section.getLine(), upStation, newUpStation, distance - newDistance));
                this.sections.add(new Section(section.getLine(), newUpStation, newDownStation, newDistance));
                return;
            }

            // 새로운 역이 상행 종점
            if (newDownStation.equals(this.findFirstStation()) && !getStations().contains(newUpStation)) {
                this.sections.add(newSection);
                return;
            }

            // 새로운 역이 하행 종점
            if (newUpStation.equals(this.findLastStation()) && !getStations().contains(newDownStation)) {
                this.sections.add(newSection);
                return;
            }
        }

        throw new BusinessException("해당 구간을 등록할 수 없습니다. : " + newUpStation.getName() + ", " + newDownStation.getName(), HttpStatus.BAD_REQUEST);
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
        if (!this.getLastSection()
                .getDownStation()
                .equals(station)) {
            throw new IllegalArgumentException();
        }

        this.sections.remove(getLastSection());
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
