package nextstep.subway.line.domain;

import nextstep.subway.line.exception.InvalidSectionOperationException;
import nextstep.subway.line.exception.InvalidStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {javax.persistence.CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Sections(){ }

    public void add(Section newSection){
        if (sections.size() == 0) {
            sections.add(newSection);
            return;
        }

        validateStations(newSection.getUpStation(), newSection.getDownStation());
        Optional<Section> optionalUpStationSection = findSectionByUpStation(newSection.getUpStation());
        if (optionalUpStationSection.isPresent()){
            addUpfrontSection(optionalUpStationSection.get(), newSection);
            return;
        }

        Optional<Section> optionalDownStationSection = findSectionByDownStation(newSection.getDownStation());
        if (optionalDownStationSection.isPresent()){
            addDownBehindSection(optionalDownStationSection.get(), newSection);
            return;
        }
        sections.add(newSection);
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {
        this.add(new Section(line, upStation, downStation, distance));
    }

    private void addUpfrontSection(Section oldSection, Section newSection) {
        sections.remove(oldSection);
        sections.add(newSection);
        sections.add(new Section(newSection.getLine(), newSection.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - newSection.getDistance()));
    }

    private void addDownBehindSection(Section oldSection, Section newSection) {
        sections.remove(oldSection);
        sections.add(newSection);
        sections.add(new Section(oldSection.getLine(), oldSection.getUpStation(), newSection.getUpStation(), oldSection.getDistance() - newSection.getDistance()));
    }

    private void validateStations(Station upStation, Station downStation) {
        final boolean existUpStation = isInStation(upStation);
        final boolean existDownStation = isInStation(downStation);
        if (existUpStation && existDownStation) {
            throw new nextstep.subway.line.exception.InvalidStationException("이미 두 역은 등록되어 있습니다.");
        }
        if (!existUpStation && !existDownStation) {
            throw new InvalidStationException("이미 두 역 중 한 역은 등록되어 있어야 합니다.");
        }
    }

    private boolean isInStation(Station station) {
        return getStations().stream().anyMatch(it -> it == station);
    }

    public int  getSectionSize() {
        return sections.size();
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
        sections.add(new Section(downSection.getLine(), downSection.getUpStation(), upSection.getDownStation(), downSection.getDistance() + upSection.getDistance()));
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
