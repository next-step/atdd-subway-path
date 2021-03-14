package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public int size() {
        return getSections().size();
    }

    public void addSection(Section section) {

        List<Station> stations = this.getStations();

        sameStationValidate(section);

        if (stations.isEmpty()) {
            this.sections.add(section);
            return;
        }

        lastStationMatchValidate(section);
        upAndDownExistsValidate(stations, section);
        downExistsValidate(stations, section);

        this.sections.add(section);
    }

    public void removeSection(Station station) {
        sizeValidate();
        removeLastStationValidate(station);
        sections.remove(sections.size() - 1);
    }


    private void lastStationMatchValidate(Section section) {
        if (!findLastStation().equals(section.getUpStation())) {
            throw new RuntimeException("추가하는 구간의 상행역이 잘못되었습니다.");
        }
    }

    private void upAndDownExistsValidate(List<Station> stations, Section section) {
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new RuntimeException("상행역과 하행역이 이미 존재합니다.");
        }
    }

    private void downExistsValidate(List<Station> stations, Section section) {
        if (stations.contains(section.getDownStation())) {
            throw new RuntimeException("하행역이 이미 노선에 포함되어 있습니다.");
        }
    }

    private void sameStationValidate(Section section) {
        if (section.getUpStation().equals(section.getDownStation())) {
            throw new RuntimeException("구간의 두 역이 동일합니다.");
        }
    }

    private void sizeValidate() {
        if (this.sections.size() <= 1) {
            throw new RuntimeException("해당 라인에 구간이 1개 밖에 없습니다.");
        }
    }

    private void removeLastStationValidate(Station station) {
        if (!findLastStation().equals(station)) {
            throw new RuntimeException("해당 구간의 마지막 역이 아닙니다.");
        }
    }

    private Station findLastStation() {
        Section lastSection = this.getSections().get(sections.size() - 1);
        return lastSection.getDownStation();
    }
}