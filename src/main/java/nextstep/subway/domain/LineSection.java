package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.applicaion.dto.SectionRequest;

@Embeddable
public class LineSection {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        List<Section> sortedSections = getSections();
        List<Station> stations = sortedSections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        stations.add(sortedSections.get(sortedSections.size()-1).getDownStation());
        return stations;
    }
    private Section getFirstSection() {
        List<Station> upStations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());

        List<Station> downStations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
        upStations.removeAll(downStations);
        Station upStation = upStations.get(0);
        for (Section section : sections) {
            if (section.getUpStation().equals(upStation)) {
                return section;
            }
        }
        return null;
    }
    private Section getNextSection(Section now) {
        for (Section section : sections) {
            if (now.getDownStation().equals(section.getUpStation())) {
                return section;
            }
        }
        return null;
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        if (section.getDownStation().equals(sections.get(0).getUpStation())) {
            sections.add(0,section);
            return;
        }
        if (section.getUpStation().equals(sections.get(sections.size()-1).getDownStation())) {
            sections.add(section);
            return;
        }

        addMiddleSection(section);
    }

    private void addMiddleSection(Section section) {
        Section target = findSectionByUpStationId(section.getUpStation().getId());
        if (target==null) {
            target = findSectionByDownStationId(section.getDownStation().getId());
            addMiddleDownSection(section,target);
            return;
        }
        addMiddleUpSection(section, target);
    }

    private void addMiddleUpSection(Section section, Section target) {
        Station middleStation = section.getDownStation();
        target.changeUpStation(middleStation);
        int index = findSectionIndex(section);
        sections.add(index, section);
    }

    private void addMiddleDownSection(Section section, Section target) {
        Station middleStation = section.getUpStation();
        target.changeDownStation(middleStation);
        int index = findSectionIndex(section);
        sections.add(index+1, section);
    }

    private Section findSectionByUpStationId(Long upStationId) {
        List<Section> foundSections = sections.stream()
            .filter(v -> v.getUpStation().getId().equals(upStationId))
            .collect(Collectors.toList());
        if (foundSections.isEmpty()) {
            return null;
        }
        return foundSections.get(0);
    }
    private Section findSectionByDownStationId(Long downStationId) {
        List<Section> foundSections = sections.stream()
            .filter(v -> v.getDownStation().getId().equals(downStationId))
            .collect(Collectors.toList());
        if (foundSections.isEmpty()) {
            return null;
        }
        return foundSections.get(0);
    }
    private int findSectionIndex(Section section) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getUpStation().equals(section.getUpStation())) {
                return i;
            }
        }
        return 0;
    }

    public void remove(String stationName) {
        sections.removeIf(v->v.getUpStation().getName().equals(stationName));
    }
    public void removeLast() {
        sections.remove(sections.size() - 1);
    }

    public List<Section> getSections() {
        List<Section> sortedSection = new ArrayList<>();
        Section section = getFirstSection();
        sortedSection.add(section);
        while((section=getNextSection(section)) != null) {
            sortedSection.add(section);
        }
        return sortedSection;
    }

    public int size() {
        return sections.size();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
    public void checkDeleteArgument(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
    }
    public void checkAddArgument(SectionRequest sectionRequest) {
        if (sections.isEmpty()) {
            return;
        }
        if (findSectionByUpStationId(sectionRequest.getDownStationId())!=null) {
            return;
        }
        if (findSectionByDownStationId(sectionRequest.getUpStationId()) != null) {
            return;
        }
        Section target = findSectionByUpStationId(sectionRequest.getUpStationId());
        if (target == null) {
            target = findSectionByDownStationId(sectionRequest.getDownStationId());
        }
        if (target==null) {
            throw new IllegalArgumentException("연결되는 구간의 역이 없습니다. upStationId="+sectionRequest.getUpStationId()
                +", downStationId="+sectionRequest.getDownStationId());
        }
        if (target.getDistance()<=sectionRequest.getDistance()) {
            throw new IllegalArgumentException("기존 구간보다 작은 길이의 구간을 입력해주세요. distance="+sectionRequest.getDistance());
        }
        if (target.getUpStation().getId().equals(sectionRequest.getUpStationId()) &&
                target.getDownStation().getId().equals(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException("동일한 역의 구간이 존재합니다. upStationId="+sectionRequest.getUpStationId()
                +", downStationId="+sectionRequest.getDownStationId());
        }
    }
}
