package nextstep.subway.line.domain;

import nextstep.subway.exception.DuplicateStationException;
import nextstep.subway.exception.InvaliadDistanceException;
import nextstep.subway.exception.NotExistedStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<Section>();

    public Sections() {
    }

    private int size() {
        return sections.size();
    }

    private boolean isValidAddingLastSection(Section section) {
        Station newUpStation = section.getUpStation();
        Station currentLastStation = getLastSection().getDownStation();
        boolean isLastSame = newUpStation.equals(currentLastStation);

        if (isLastSame && validExistedStation(section.getDownStation())) {
            throw new DuplicateStationException();
        }
        if (isLastSame) return true;
        return false;
    }

    private boolean isValidAddingFirstSection(Section section) {
        Station newDownStation = section.getDownStation();
        Station currentFirstStation = sections.get(0).getUpStation();
        boolean isFirstSame = newDownStation.equals(currentFirstStation);
        if (isFirstSame && validExistedStation(section.getUpStation())) {
            throw new DuplicateStationException();
        }
        if (isFirstSame) return true;
        return false;
    }

    private boolean validExistedStation(Station station) {
        return getAllStations().stream().anyMatch(i -> i.equals(station));
    }

    public void addSection(Section section) {
        /** 구간 등록하는 비즈니스 로직 작성 */
        //최초등록 CASE
        List<Station> stations = getAllStations();
        if (stations.size() == 0) {
            sections.add(section);
            return;
        }

        //하행역 등록 CASE (기존)
        if (isValidAddingLastSection(section)) {
            sections.add(section);
            return;
        }

        //상행역 등록 CASE
        if (isValidAddingFirstSection(section)) {
            sections.add(0, section);
            return;
        }

        boolean isExistedUpStation = sections.stream().anyMatch(i -> i.getUpStation().equals(section.getUpStation()));
        boolean isExistedDownStation = sections.stream().anyMatch(i -> i.getDownStation().equals(section.getDownStation()));
        validExistedStation(isExistedUpStation, isExistedDownStation);

        if (isExistedUpStation) {
            addPrevSection(section);
        }

        if (isExistedDownStation) {
            addNextSection(section);
        }

    }

    private void validExistedStation(boolean isExistedUpStation, boolean isExistedDownStation) {
        if (isExistedUpStation && isExistedDownStation) {
            throw new DuplicateStationException();
        }

        if (!isExistedUpStation && !isExistedDownStation) {
            throw new NotExistedStationException();
        }
    }

    private void isValidDeleteSection(Long stationId) {
        if (size() == 1)
            throw new RuntimeException("마지막 구간은 삭제할 수 없습니다.");
    }

    private List<Station> getUpStations(){
        List<Station> upStationList = new ArrayList<Station>();
        sections.stream().map(it -> it.getUpStation()).forEach(upStationList::add);
        return upStationList;
    }

    private List<Station> getDownStationList(){
        List<Station> downStationList = new ArrayList<Station>();
        sections.stream().map(it -> it.getDownStation()).forEach(downStationList::add);
        return downStationList;
    }

    private Section getLastSection() {
        List<Station> upStationList = getUpStations();
        List<Station> downStationList = getDownStationList();

        Station LastStation = getLastStation(upStationList,downStationList);
        return findDownSection(LastStation);
    }

    private Section getFirstSection() {
        List<Station> upStationList = getUpStations();
        List<Station> downStationList = getDownStationList();

        Station firstStation = getFirstStation(upStationList,downStationList);
        return findUpSection(firstStation);
    }

    private Station getFirstStation(List<Station> upStationList, List<Station> downStationList) {
        for (Station station : upStationList) {
            if (!downStationList.contains(station))
                return station;
        }
        throw new RuntimeException("첫번째 역을 찾을 수 없습니다.");
    }

    private Station getLastStation(List<Station> upStationList, List<Station> downStationList) {
        for (Station station : downStationList) {
            if (!upStationList.contains(station))
                return station;
        }
        throw new RuntimeException("마지막 역을 찾을 수 없습니다.");
    }

    private Section findUpSection(Station upStation) {
        return sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(NotExistedStationException::new);
    }

    private Section findDownSection(Station downStation) {
        return sections.stream()
                .filter(it -> it.getDownStation().equals(downStation))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public void addPrevSection(Section section) {
        Section findSection = findUpSection(section.getUpStation());
        checkSectionDistance(section, findSection);

        int index = sections.indexOf(findSection);
        Line line = section.getLine();

        int sectionDistance = section.getDistance() - findSection.getDistance();
        Section nextSection = new Section(line, section.getDownStation(), findSection.getDownStation(), sectionDistance);
        sections.set(index, section);
        sections.add(index + 1, nextSection);
    }

    public void addNextSection(Section section) {
        Section findSection = findDownSection(section.getDownStation());
        checkSectionDistance(section, findSection);

        int index = sections.indexOf(findSection);
        Line line = section.getLine();

        int sectionDistance = section.getDistance() - findSection.getDistance();
        Section prevSection = new Section(line, findSection.getUpStation(), section.getUpStation(), sectionDistance);
        sections.set(index, prevSection);
        sections.add(index + 1, section);
    }

    private void checkSectionDistance(Section section, Section findSection) {
        if (section.getDistance() >= findSection.getDistance()) {
            throw new InvaliadDistanceException();
        }
    }

    public void deleteSection(Station station) {
        isValidDeleteSection(station.getId());

        Section firstSection = getFirstSection();
        Section lastSection = getLastSection();
        if (station.equals(firstSection.getUpStation())) {
            sections.remove(sections.indexOf(firstSection));
            return;
        }

        if (station.equals(lastSection.getDownStation())) {
            sections.remove(sections.indexOf(lastSection));
            return;
        }

        Section section = findDownSection(station);
        removeStation(section);
    }

    public void removeStation(Section upSection) {
        Section downSection = findUpSection(upSection.getDownStation());

        int upIndex = sections.indexOf(upSection);
        Line line = upSection.getLine();

        int newDistance = downSection.getDistance() + upSection.getDistance();
        Section newSection = new Section(line, upSection.getUpStation(), downSection.getDownStation(), newDistance);
        sections.set(upIndex, newSection);
        sections.remove(sections.indexOf(downSection));
    }

    public List<Station> getAllStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> responses = new ArrayList<>();
        responses.add(sections.get(0).getUpStation());

        sections.stream().map(section -> section.getDownStation()).forEach(responses::add);
        return responses;
    }

    public List<Section> getAllSections() {
        return sections;
    }

}