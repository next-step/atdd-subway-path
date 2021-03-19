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

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        if(getSectionSize() == 0) {
            return stations;
        }

        Station station = getFirstStation();
        stations.add(station);
        while ((station = getNextStation(station)) != null){
            stations.add(station);
        }

        return stations;
    }

    public Station getNextStation(Station station) {
        Station nextStation = null;
        if (sections.size() == 0) {
            return nextStation;
        }

        Optional<Section> section = sections.stream()
                .filter(it -> it.getUpStation().equals(station))
                .findFirst();

        if(section.isPresent()){
            nextStation = section.get().getDownStation();
        }
        return nextStation;
    }

    public int getSectionSize() {
        return sections.size();
    }

    public Station getFirstStation() {
        return Optional.ofNullable(sections.get(0))
                .map(it -> it.getUpStation())
                .orElse(null);
    }

    public Station getLastStation() {
        return Optional.ofNullable(sections.get(getSectionSize()-1))
                .map(it -> it.getDownStation())
                .orElse(null);
    }

    public void add(Line line, Station upStaion, Station downStaion, int distance) {

        Section section = new Section(line, upStaion, downStaion, distance);

        if (getStations().size() == 0) {
            sections.add(section);
            return;
        }

        // 벨리데이션
        checkConnectableSection(section);
        validateAlreadyAdded(section);

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

    private void checkConnectableSection(Section section){
        if(!(isContainsStation(section.getUpStation()) || isContainsStation(section.getDownStation()))){
            throw new RuntimeException("상행역과 하행역 둘 중 하나도 포함되어있지 않음");
        }
    }

    private void validateAlreadyAdded(Section section){
        if(isContainsStation(section.getUpStation()) && isContainsStation(section.getDownStation())){
            throw new RuntimeException("상행역과 하행역이 이미 노선에 모두 등록됨");
        }
    }

    private boolean isContainsStation(Station station){
        return getStations().stream()
                .anyMatch(it -> it.equals(station));
    }

    private void addSectionInMiddle(int idx, Section originSection, Section newSection){
        // 구간사이 길이 계산
        int originDistance = originSection.getDistance();
        int newDistance = newSection.getDistance();
        if(originDistance <= newDistance){
            throw new RuntimeException("기존 역 사이 길이보다 크거나 같음");
        }
        // 구간에 등록될 역 정보
        Station originUpStation = originSection.getUpStation(); // == newSection.getUpStation()
        Station originDownStation = newSection.getDownStation();
        Station newUpStation = newSection.getDownStation();
        Station newDownStation = originSection.getDownStation();
        // 구간 등록
        newSection.updateSection(newUpStation, newDownStation, originDistance - newDistance);
        sections.add(idx+1, newSection);
        // 구간 업데이트
        originSection.updateSection(originUpStation, originDownStation, newDistance);
        sections.set(idx, originSection);
    }

    public void removeSection(Long stationId) {
        if (getSectionSize() <= 1) {
            throw new RuntimeException();
        }

        // 벨리데이션
        checkLastStation(stationId);

        sections.stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> sections.remove(it));
    }

    private void checkLastStation(Long stationId){
        if (getLastStation().getId() != stationId) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }
    }
}
