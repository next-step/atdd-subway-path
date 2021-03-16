package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        sections = sections;
    }

    /* 도메인 기능 메서드 */

    // 노선의 역 조회
    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        if(getSectionSize() > 0){
            Station downStation = getFirstStation();
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
        }
        return stations;
    }

    // 구간개수
    public int getSectionSize() {
        return sections.size();
    }

    // 상행역 종점 조회
    public Station getFirstStation() {
        return Optional.ofNullable(sections.get(0))
                .map(it -> it.getUpStation())
                .orElse(null);
    }

    // 하행역 종점 조회
    public Station getLastStation() {
        return Optional.ofNullable(sections.get(getSectionSize()-1))
                .map(it -> it.getDownStation())
                .orElse(null);
    }

    // 구간 추가
    public void add(Section section) {
        if (getStations().size() == 0) {
            sections.add(section);
            return;
        }

        // 벨리데이션
        isNotValidUpStation(section.getUpStation());
        isDownStationExisted(section.getDownStation());

        sections.add(section);
    }

    // 구간 추가 벨리데이션
    private void isNotValidUpStation(Station upStation){
        if (getLastStation() != upStation) {
            throw new RuntimeException("상행역은 하행 종점역이어야 합니다.");
        }
    }

    // 구간 추가 벨리데이션
    private void isDownStationExisted(Station downStation){
        if (getStations().stream().anyMatch(it -> it == downStation)) {
            throw new RuntimeException("하행역이 이미 등록되어 있습니다.");
        }
    }

    // 구간 삭제
    public void removeSection(Long stationId) {
        if (getSectionSize() <= 1) {
            throw new RuntimeException();
        }

        // 벨리데이션
        isNotValidUpStation(stationId);

        sections.stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> sections.remove(it));
    }

    // 구간 삭제 벨리데이션
    private void isNotValidUpStation(Long stationId){
        if (getLastStation().getId() != stationId) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }
    }
}
