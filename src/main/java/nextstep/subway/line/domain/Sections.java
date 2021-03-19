package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

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
        isConnectableSection(section);
        isAlreadyAdded(section);

        // 처음
        if(getFirstStation().equals(section.getDownStation())){
            sections.add(0, section);
            return;
        }
        // 마지막
        if(getLastStation().equals(section.getUpStation())){
            sections.add(section);
            return;
        }
        // 중간에 추가
        sections.stream()
                .filter(it -> it.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(it -> addSectionInMiddle(sections.indexOf(it), it, section));
    }

    void isConnectableSection(Section section){
        if(!(isContainsStation(section.getUpStation()) || isContainsStation(section.getDownStation()))){
            throw new RuntimeException("상행역과 하행역 둘 중 하나도 포함되어있지 않음");
        }
    }

    void isAlreadyAdded(Section section){
        if(isContainsStation(section.getUpStation()) && isContainsStation(section.getDownStation())){
            throw new RuntimeException("상행역과 하행역이 이미 노선에 모두 등록됨");
        }
    }

    // 해당역 포함여부
    boolean isContainsStation(Station station){
        return getStations().stream()
                .anyMatch(it -> it.equals(station));
    }

    // 구간사이에 구간추가
    private void addSectionInMiddle(int idx, Section originSection, Section newSection){
        // 구간사이 길이 계산
        int originDist = originSection.getDistance();
        int newDist = newSection.getDistance();
        if(originDist <= newDist){
            throw new RuntimeException("기존 역 사이 길이보다 크거나 같음");
        }
        // 구간 등록+업데이트
        Section tmp = new Section(originSection.getLine(), newSection.getDownStation(), originSection.getDownStation(), originDist - newDist);
        sections.add(idx+1, tmp);
        sections.set(idx, originSection.insertStation(newSection.getDownStation(), newDist));
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
