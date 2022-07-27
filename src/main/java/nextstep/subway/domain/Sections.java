package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import lombok.Getter;
import nextstep.subway.exception.SectionException;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section insertSection) {
        if(!sections.isEmpty()) {
            isExistSection(insertSection);
            isExitsUpStationOrDownStation(insertSection);

            addSectionProcess(insertSection);
        }
        sections.add(insertSection);
    }

    private List<Section> getSortSectionList() {
        List<Section> sortSectionList = new ArrayList<>();
        Station findStation = findFirstUpStation();

        while(sortSectionList.size() != sections.size()) {
            Section nextSection = findNextLineStation(findStation);
            sortSectionList.add(nextSection);
            findStation = nextSection.getDownStation();
        }

        return sortSectionList;
    }

    private boolean isPassCondition(List<Section> sortSectionList, Section insertSection) {
        return sortSectionList.get(0).getUpStation().equals(insertSection.getDownStation())
            || sortSectionList.get(getLastIndexSections()).getDownStation().equals(insertSection.getUpStation());
    }

    private void addSectionProcess(Section insertSection) {
        List<Section> sortSectionList = getSortSectionList();

        if(isPassCondition(sortSectionList, insertSection)) {
            return;
        }

        for(int i = 0 ; i < sortSectionList.size() ; i++) {
            Section section = sortSectionList.get(i);

            if(section.isDownStationConnection(insertSection)) {
                section.changeDownStation(insertSection.getUpStation(), insertSection.getDistance());
            }

            if (section.isUpStationConnection(insertSection)) {
                section.changeUpStation(insertSection.getDownStation(), insertSection.getDistance());
            }
        }
    }

    private void isExistSection(Section addSection) {
        sections.stream()
            .filter(section -> addSection.isEqualsUpStationAndDownStation(section))
            .findFirst()
            .ifPresent(section -> {
                throw new SectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
            });
    }

    private Section isExitsUpStationOrDownStation(Section insertSection) {
        return sections.stream()
            .filter(section -> section.isConnection(insertSection))
            .findFirst()
            .orElseThrow(() -> new SectionException("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없습니다."));
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findFirstUpStation();
        stations.add(downStation);

        while (sections.size() + 1 != stations.size()) {
            downStation = findNextLineStation(downStation).getDownStation();
            stations.add(downStation);
        }

        return Collections.unmodifiableList(stations);
    }

    private Station findFirstUpStation() {
        return sections.stream()
            .map(Section::getUpStation)
            .filter(this::isFirstStation)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private Section findNextLineStation(Station downStation) {
        return sections.stream()
            .filter(it -> downStation.equals(it.getUpStation()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isFirstStation(Station station) {
        return sections.stream()
            .noneMatch(currentStation -> station.equals(currentStation.getDownStation()));
    }

    public void removeStation(Station removeStation) {
        isRemoveable();
        isExsitsRemove(removeStation);

        removeProcess(removeStation);
    }

    private void removeProcess(Station removeStation) {
        List<Section> sortSectionList = getSortSectionList();

        for(int i = 0 ; i < sortSectionList.size() ; i++) {
            if(i == 0 && sortSectionList.get(i).getUpStation().equals(removeStation)
                || i == getLastIndexSections() && sortSectionList.get(i).getDownStation().equals(removeStation)) {
                Section section = findNextLineStation(sortSectionList.get(i).getUpStation());
                sections.remove(section);
                break;
            }

            if(sortSectionList.get(i).getDownStation().equals(removeStation)) {
                Section nextSection = sortSectionList.get(i+1);
                findNextLineStation(sortSectionList.get(i).getUpStation()).removeUpdateStation(nextSection.getDownStation(), nextSection.getDistance());
                sections.remove(nextSection);
            }
        }
    }

    private void isRemoveable() {
        if(sections.size() == 1) {
            throw new SectionException("구간이 하나일때는 삭제할 수 없습니다.");
        }
    }

    private void isExsitsRemove(Station removeStation) {
        getStations().stream()
            .filter(station -> station.equals(removeStation))
            .findFirst()
            .orElseThrow(() -> new SectionException("존재하지 않는 지하철역이라 삭제할 수가 없습니다."));
    }

    private int getLastIndexSections() {
        return sections.size() - 1;
    }

}
