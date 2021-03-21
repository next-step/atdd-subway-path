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
        if(addFirst(section)){
            return;
        }

        // 마지막
        if(addLast(section)){
            return;
        }

        // 중간에 추가
        addMiddle(section);
    }

    private boolean addFirst(Section section){
        if(getFirstStation().equals(section.getDownStation())){
            sections.add(0, section);
            return true;
        }
        return false;
    }

    private boolean addLast(Section section){
        if(getLastStation().equals(section.getUpStation())){
            sections.add(section);
            return true;
        }
        return false;
    }

    private void addMiddle(Section section){
        sections.stream()
                .filter(it -> it.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(it -> arrangeAddSection(sections.indexOf(it), it, section));
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

    private void arrangeAddSection(int idx, Section frontSection, Section backSection){
        // 구간사이 길이 계산
        int frontDistance = frontSection.getDistance();
        int backDistance = backSection.getDistance();
        if(frontDistance <= backDistance){
            throw new RuntimeException("기존 역 사이 길이보다 크거나 같음");
        }
        // 구간에 등록될 역 정보
        Station frontUpStation = frontSection.getUpStation();
        Station frontDownStation = backSection.getDownStation();
        Station backUpStation = backSection.getDownStation();
        Station backDownStation = frontSection.getDownStation();

        // 상행 구간
        frontSection.updateSection(frontUpStation, frontDownStation, backDistance);
        sections.set(idx, frontSection);

        // 하행 구간
        backSection.updateSection(backUpStation, backDownStation, frontDistance - backDistance);
        sections.add(idx+1, backSection);

    }

    public void removeSection(Long stationId) {

        // 벨리데이션
        Station station = getStationById(stationId);
        checkMoreThanTwoSection();
        checkExistsStation(station);

        // 처음
        if(removeFirst(station)){
            return;
        }

        // 마지막
        if(removeLast(station)){
            return;
        }

        // 중간
        removeMiddle(station);

    }

    private boolean removeFirst(Station station){
        if(getFirstStation().equals(station)){
            sections.remove(0);
            return true;
        }
        return false;
    }

    private boolean removeLast(Station station){
        if(getLastStation().equals(station)){
            sections.remove(getSectionSize()-1);
            return true;
        }
        return false;
    }

    private void removeMiddle(Station station){
        sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findFirst()
                .ifPresent(it -> {
                    int idx = sections.indexOf(it);
                    arrangeRemoveSection(idx, it, sections.get(idx+1));
                });
    }

    private void arrangeRemoveSection(int idx, Section frontSection, Section backSection){
        // 구간사이 길이 계산
        int frontDistance = frontSection.getDistance();
        int backDistance = backSection.getDistance();
        // 구간에 등록될 역 정보
        Station frontUpStation = frontSection.getUpStation();
        Station frontDownStation = backSection.getDownStation();

        // 상행 구간
        frontSection.updateSection(frontUpStation, frontDownStation, frontDistance + backDistance);
        sections.set(idx, frontSection);

        // 하행 구간
        sections.remove(idx+1);
    }

    private Station getStationById(Long stationId){
        return getStations().stream()
                        .filter(it -> it.getId() == stationId)
                        .findFirst()
                        .orElse(null);
    }

    private void checkMoreThanTwoSection(){
        if (getSectionSize() < 2) {
            throw new RuntimeException("두개 이상의 구간부터 삭제가 가능합니다.");
        }
    }

    private void checkExistsStation(Station station){
        if(station == null){
            throw new RuntimeException("존재하지 않는 역입니다.");
        }
    }


}
