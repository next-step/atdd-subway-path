package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exception.BothStationAlreadyEnrolledException;
import nextstep.subway.line.domain.exception.InvalidDistanceException;
import nextstep.subway.line.domain.exception.NoneOfStationEnrolledException;
import nextstep.subway.line.domain.exception.OnlyOneSectionRemainException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    protected void addSection(Section section) {
        if (getStations().isEmpty()) {
            sections.add(section);
            return;
        }

        //upstation과 매칭 됐을 때

        boolean isUpStationExist = isStationExistInStations(section.getUpStation());
        boolean isDownStationExist = isStationExistInStations(section.getDownStation());

        if (isDownStationExist && isUpStationExist) {
            throw new BothStationAlreadyEnrolledException("추가하고자 하는 두 역 모두 등록 되어있습니다.");
        } else if (!isUpStationExist && !isDownStationExist) {
            throw new NoneOfStationEnrolledException("추가하고자 하는 두 역 모두 등록이 안되어있습니다.");
        } else if (isUpStationExist) {
            insertSectionWhenUpStationMatch(section);
        } else if (isDownStationExist) {
            insertSectionWhenDownStationMatch(section);
        }
    }

    private boolean isStationExistInStations(Station station){
        return getStations()
                .stream()
                .anyMatch(st -> st == station);
    }

    protected List<Station> getStations() {
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getUpStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    private Station findUpStation() {
        Station downStation = sections.get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    public void insertSectionWhenUpStationMatch(Section insertedSection) {
        int index = getStations().indexOf(insertedSection.getUpStation());
        //뒤의 요소 변경
        if (index < sections.size()) {
            Section changedSection = sections.get(index);
            int changedDistance = changedSection.getDistance() - insertedSection.getDistance();
            validateDistance(changedDistance);
            changedSection.changeUpStation(insertedSection.getDownStation());
            changedSection.changeDistance(changedDistance);
        }
        sections.add(index, insertedSection);
    }

    public void insertSectionWhenDownStationMatch(Section insertedSection) {
        int index = getStations().indexOf(insertedSection.getDownStation());
        //앞의 요소 변경
        if (index > 0) {
            Section changedSection = sections.get(index - 1);
            int changedDistance = changedSection.getDistance() - insertedSection.getDistance();
            validateDistance(changedDistance);
            changedSection.changeDownStation(insertedSection.getUpStation());
            changedSection.changeDistance(changedDistance);
        }
        sections.add(index, insertedSection);
    }

    public void removeSection(Station station) {
        if (sections.size() <= 1) {
            throw new OnlyOneSectionRemainException("이 노선에 남아있는 구간이 1개라 더 이상 역을 삭제할 수 없습니다");
        }
        int indexOfStation = getStations().indexOf(station);
        boolean isStationExistInLine = indexOfStation==-1;

        if(isStationExistInLine){
            throw new NoneOfStationEnrolledException("삭제하려는 역이 노선에 등록되어 있지 않습니다");
        }

        if(indexOfStation==0){
            sections.remove(0);
            return;
        }
        if(indexOfStation==getStations().size()-1){
            sections.remove(sections.size()-1);
            return;
        }
        int changedDistance = sections.get(indexOfStation-1).getDistance()
                + sections.get(indexOfStation).getDistance();

        sections.get(indexOfStation-1).changeDownStation(getStations().get(indexOfStation+1));
        sections.get(indexOfStation-1).changeDistance(changedDistance);
        sections.remove(indexOfStation);
    }

    private void validateDistance(int changedDistance){
        if (changedDistance <= 0) throw new InvalidDistanceException("거리가 맞지 않습니다");
    }

    public List<Section> getSectionList() {
        return sections;
    }
}
