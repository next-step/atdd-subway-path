package nextstep.subway.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());

        stations.add(getEndSection().getDownStation());

        return stations;
    }

    public void addSection(Section section) {
        if(!sections.isEmpty()) {
            verifyUpStation(section.getUpStation());
            verifyDuplicateDownStation(section.getDownStation());
        }

        this.sections.add(section);
    }

    private void verifyUpStation(Station upStation) {
        Station downStationOfLine = getEndSection().getDownStation();

        boolean isSameDownStationOfLine = downStationOfLine.equals(upStation);
        if(!isSameDownStationOfLine) {
            throw new IllegalArgumentException("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다");
        }
    }

    private void verifyDuplicateDownStation(Station downStation) {
        List<Station> stationsOfLine = sections.stream()
            .flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation()))
            .collect(Collectors.toList());

        boolean isDuplicateDownStation = stationsOfLine.contains(downStation);
        if(isDuplicateDownStation) {
            throw new IllegalArgumentException("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다");
        }
    }

    public void removeSection(Station station) {
        verifyStartSection(station);
        Section endSection = verifyEndSection(station);
        this.sections.remove(endSection);
    }

    private Section verifyEndSection(Station station) {
        Section endSection = getEndSection();

        boolean isEndSection = endSection.getDownStation().equals(station);
        if(!isEndSection) {
            throw new IllegalArgumentException("지하철 노선에 등록된 마지막 구간만 제거할 수 있다");
        }

        return endSection;
    }

    private void verifyStartSection(Station station) {
        boolean isStartSection = sections.get(0).getDownStation().equals(station);
        if(isStartSection) {
            throw new IllegalArgumentException("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다");
        }
    }

    private Section getEndSection() {
        return sections.get(sections.size()-1);
    }
}
