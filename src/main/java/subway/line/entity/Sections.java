package subway.line.entity;

import subway.line.exception.LineException;
import subway.section.Section;
import subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        stations.add(getDownFinalStation());
        return stations;
    }

    public Station getDownFinalStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    public void addSection(Section section) {
        if (sections.size() > 0) {
            verifyDownStation(section);

            verifyAlreadyStation(section);
        }
        sections.add(section);
    }

    private void verifyDownStation(Section section) {
        boolean isNotLineDownStation = !getDownFinalStation().getId().equals(section.getUpStation().getId());

        if (isNotLineDownStation) {
            throw new LineException("등록할 구간의 상행역이 노선에 등록되어있는 하행종점역이 아닌 경우 구간 등록이 불가능합니다.");
        }
    }

    private void verifyAlreadyStation(Section section) {
        boolean isAlreadyStation = sections.stream().anyMatch(s ->
                s.getUpStation().getId().equals(section.getDownStation().getId()) ||
                        s.getDownStation().getId().equals(section.getDownStation().getId()));

        if (isAlreadyStation) {
            throw new LineException("이미 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다.");
        }
    }

    public void removeSection(Long stationId) {
        verifySectionCount();

        Section deleteSection = verifyDeleteDownStation(stationId);

        sections.remove(deleteSection);
    }

    private void verifySectionCount() {
        if (sections.size() <= 1) {
            throw new LineException("구간이 1개인 노선의 구간은 삭제할 수 없습니다.");
        }
    }

    private Section verifyDeleteDownStation(Long stationId) {
        Section deleteSection = sections.stream().filter(s -> s.getDownStation().getId().equals(stationId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지않는 구간입니다."));

        if (!deleteSection.getDownStation().getId().equals(getDownFinalStation().getId())) {
            throw new LineException("노선의 하행종점역만 제거할 수 있습니다.");
        }
        return deleteSection;
    }
}
