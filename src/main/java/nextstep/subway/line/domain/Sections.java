package nextstep.subway.line.domain;

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

    @OneToMany(mappedBy = "line", cascade = {javax.persistence.CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section){
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void remove(Section section){
        sections.remove(section);
    }

    public void remove(Station station){
        if (sections.size() <= 1) {
            throw new InvalidSectionOperationException("마지막 섹션은 삭제할 수 없습니다.");
        }

        boolean isNotValidUpStation = getStations().get(getStations().size() - 1) != station;
        if (isNotValidUpStation) {
            throw new InvalidSectionOperationException("하행 종점역만 삭제가 가능합니다.");
        }

        sections.stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst()
                .ifPresent(it -> sections.remove(it));
    }

    public Optional<Section> findSectionByUpStation(Station station){
        return sections.stream()
                .filter(it -> (it.getUpStation() == station))
                .findFirst();
    }

    public Optional<Section> findSectionByDownStation(Station station){
        return sections.stream()
                .filter(it -> (it.getDownStation() == station))
                .findFirst();
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
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
}
