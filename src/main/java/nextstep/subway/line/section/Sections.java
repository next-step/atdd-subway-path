package nextstep.subway.line.section;


import nextstep.subway.Exception.ErrorCode;
import nextstep.subway.Exception.LineException;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> get() {
        return sections;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        if (!isLastDownStation(section.getUpStation())) {
            throw new LineException(ErrorCode.CANNOT_ADD_SECTION, "노선 등록 시 상행역은 현재 하행 종점역이어야 합니다.");
        }

        if (isDuplicatedStation(section)) {
            throw new LineException(ErrorCode.CANNOT_ADD_SECTION, "이미 구간에 등록된 역입니다.");
        }

        sections.add(section);
    }

    private boolean isDuplicatedStation(Section section) {
        return allStations().contains(section.getDownStation());
    }

    public List<Station> allStations() {
        return sections.stream().flatMap(section -> section.stations().stream()).distinct().collect(Collectors.toList());
    }

    private Station lastDownStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    private boolean isLastDownStation(Long stationId) {
        return lastDownStation().match(stationId);
    }

    private boolean isLastDownStation(Station upStation) {
        return lastDownStation().equals(upStation);
    }

    public void deleteSection(Long stationId) {
        if (sections.size() == 1)
            throw new LineException(ErrorCode.CANNOT_DELETE_SECTION, "구간이 한개인 경우 삭제할 수 없습니다.");
        if (!isLastDownStation(stationId)) {
            throw new LineException(ErrorCode.CANNOT_DELETE_SECTION, "삭제 역이 하행 종점역이 아닙니다.");
        }
        sections.remove(sections.size() - 1);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}
