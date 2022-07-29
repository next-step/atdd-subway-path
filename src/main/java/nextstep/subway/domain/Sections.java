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

        if (isNewUpStation(newUpStation, newDownStation) || isNewDownStation(newUpStation, newDownStation)) {
            this.sections.add(newSection);
            return;
        }

        for (Section section : this.sections) {
            if (isNewStationInBetween(newUpStation, newDownStation, newDistance, section)) {
                this.sections.remove(section);
                this.sections.add(new Section(section.getLine(), newUpStation, newDownStation, newDistance));
                this.sections.add(new Section(section.getLine(), newDownStation, section.getDownStation(), section.getDistance() - newDistance));
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

    private boolean isNewStationInBetween(Station newUpStation, Station newDownStation, int newDistance, Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();
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

        List<Station> stationsInOrder = new ArrayList<>();
        stationsInOrder.add(firstStation);
        Station nextStation = firstStation;
        while (!nextStation.equals(lastStation)) {
            nextStation = findAndAddNextStation(stationsInOrder, nextStation);
        }
        return stationsInOrder;
    }

    private Station findAndAddNextStation(List<Station> list, Station nextStation) {
        for (Section section : this.sections) {
            if (section.getUpStation()
                    .equals(nextStation)) {
                nextStation = section.getDownStation();
                list.add(nextStation);
                return nextStation;
            }
        }
        throw new BusinessException("NextStation 을 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public void deleteStation(Station station) {
        validateDeleteStationCondition(station);

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

    private void validateDeleteStationCondition(Station station) {
        if (this.sections.size() <= 1) {
            throw new BusinessException("해당 지하철 노선에 지하철 구간이 하나밖에 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        if (isNotIncludedStation(station)) {
            throw new BusinessException("해당 지하철 노선에 등록되지 않은 역입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isNotIncludedStation(Station station) {
        return !this.getStations()
                .contains(station);
    }

    private void removeMiddleStation(Station station) {
        Section upSection = removeUpSectionNearStation(station);
        Section downSection = removeDownSectionNearStation(station);

        Line line = upSection.getLine();
        Station upStation = upSection.getUpStation();
        Station downStation = downSection.getDownStation();
        int newDistance = upSection.getDistance() + downSection.getDistance();
        this.sections.add(new Section(line, upStation, downStation, newDistance));
    }

    private Section removeUpSectionNearStation(Station station) {
        Section upSection = findUpSection(station);
        this.sections.remove(upSection);
        return upSection;
    }

    private Section removeDownSectionNearStation(Station station) {
        Section downSection = findDownSection(station);
        this.sections.remove(downSection);
        return downSection;
    }

    private void removeUpStation(Station firstStation) {
        Section firstSection = findDownSection(firstStation);
        sections.remove(firstSection);
    }

    private Section findDownSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.isUpStation(station))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    private void removeDownStation(Station lastStation) {
        Section lastSection = findUpSection(lastStation);
        sections.remove(lastSection);
    }

    private Section findUpSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.isDownStation(station))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    private Station findFirstStation() {
        return this.getStations()
                .stream()
                .filter(station -> isFirstStation(station))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    private boolean isFirstStation(Station station) {
        return this.sections.stream()
                .filter(section -> section.isDownStation(station))
                .count() == 0;
    }

    private Station findLastStation() {
        return this.getStations()
                .stream()
                .filter(station -> isLastStation(station))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    private boolean isLastStation(Station station) {
        return this.sections.stream()
                .filter(section -> section.isUpStation(station))
                .count() == 0;
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
}
