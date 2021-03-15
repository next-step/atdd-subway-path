package nextstep.subway.line.domain;

import nextstep.subway.line.exception.InvalidSectionOperationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @Transient
    private Line line;

    @OneToMany(mappedBy = "line", cascade = {javax.persistence.CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Sections(){ }

    public Sections(Line line){
        this.line = line;
    }


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

        final Optional<Section> optionalUpSection = findSectionByUpStation(station);
        final Optional<Section> optionalDownSection = findSectionByDownStation(station);
        if (optionalUpSection.isPresent() && !optionalDownSection.isPresent()) {
            sections.remove(optionalUpSection.get());
            return;
        }

        if (!optionalUpSection.isPresent() && optionalDownSection.isPresent()) {
            sections.remove(optionalDownSection.get());
            return;
        }

        final Section upSection = optionalUpSection.get();
        final Section downSection = optionalDownSection.get();
        sections.remove(optionalUpSection.get());
        sections.remove(optionalDownSection.get());
        sections.add(new Section(this.line, downSection.getUpStation(), upSection.getDownStation(), downSection.getDistance() + upSection.getDistance()));
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
}
