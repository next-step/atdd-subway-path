package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.applicaion.dto.SectionRequest;

@Embeddable
public class LineSection {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.ALL}, orphanRemoval = true)
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
        return sections.stream()
            .filter(v -> v.getUpStation().equals(upStation))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }
    private Section getNextSection(Section now) {
        for (Section section : sections) {
            if (now.getDownStation().equals(section.getUpStation())) {
                return section;
            }
        }
        return null;
    }

    private boolean hasNextSection(Section now) {
        for (Section section : sections) {
            if (now.getDownStation().equals(section.getUpStation())) {
                return true;
            }
        }
        return false;
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        if (isFirstAdd(section)) {
            sections.add(0,section);
            return;
        }
        if (isLastAdd(section)) {
            sections.add(section);
            return;
        }

        addMiddleSection(section);
    }

    private boolean isLastAdd(Section section) {
        return section.getUpStation().equals(sections.get(sections.size() - 1).getDownStation());
    }

    private boolean isFirstAdd(Section section) {
        return section.getDownStation().equals(sections.get(0).getUpStation());
    }

    private void addMiddleSection(Section section) {
        if (hasSectionByUpStationId(section.getUpStation().getId())) {
            addMiddleUpSection(section, findSectionByUpStationId(section.getUpStation().getId()));
            return;
        }
        addMiddleDownSection(section, findSectionByDownStationId(section.getDownStation().getId()));
    }

    private void addMiddleUpSection(Section section, Section target) {
        Station middleStation = section.getDownStation();
        target.changeUpStation(middleStation);
        target.changeDistance(target.getDistance()-section.getDistance());
        int index = findSectionIndex(section);
        sections.add(index, section);
    }

    private void addMiddleDownSection(Section section, Section target) {
        Station middleStation = section.getUpStation();
        target.changeDownStation(middleStation);
        target.changeDistance(target.getDistance()-section.getDistance());
        int index = findSectionIndex(section);
        sections.add(index+1, section);
    }

    private Section findSectionByUpStationId(Long upStationId) {
        return sections.stream()
            .filter(v -> v.getUpStation().getId().equals(upStationId))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }
    private boolean hasSectionByUpStationId(Long upStationId) {
        List<Section> foundSections = sections.stream()
            .filter(v -> v.getUpStation().getId().equals(upStationId))
            .collect(Collectors.toList());
        return !foundSections.isEmpty();
    }

    private boolean findSectionByUpStation(Long upStationId) {
        List<Section> foundSections = sections.stream()
            .filter(v -> v.getUpStation().getId().equals(upStationId))
            .collect(Collectors.toList());
        return !foundSections.isEmpty();
    }
    private Section findSectionByDownStationId(Long downStationId) {
        return sections.stream()
            .filter(v -> v.getDownStation().getId().equals(downStationId))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }
    private boolean findSectionByDownStation(Long downStationId) {
        List<Section> foundSections = sections.stream()
            .filter(v -> v.getDownStation().getId().equals(downStationId))
            .collect(Collectors.toList());
        return !foundSections.isEmpty();

    }
    private int findSectionIndex(Section section) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getUpStation().equals(section.getUpStation())) {
                return i;
            }
        }
        return 0;
    }

    public void remove(Long stationId) {
        if (hasUpStationId(stationId)) {
            if (!isFirst(stationId)) {
                Section leftSection = getSectionByDownStationId(stationId);
                Section deleteSection = getSectionByUpStationId(stationId);
                Station newStation = deleteSection.getDownStation();
                leftSection.changeDistance(leftSection.getDistance() + deleteSection.getDistance());
                leftSection.changeDownStation(newStation);
            }
            sections.removeIf(v->v.getUpStation().getId().equals(stationId));
            return;
        }
        if (hasDownStationId(stationId)) {
            sections.removeIf(v->v.getDownStation().getId().equals(stationId));
            return;
        }
        throw new RuntimeException();
    }

    private boolean isFirst(Long stationId) {
        return getStations().get(0).getId().equals(stationId);
    }

    private int getDistance(Long stationId) {
        return sections.stream()
            .filter(v -> v.getUpStation().getId().equals(stationId))
            .findFirst()
            .orElseThrow(NoSuchElementException::new)
            .getDistance();
    }

    private boolean hasUpStationId(Long stationId) {
        return sections.stream()
            .map(v -> v.getUpStation().getId())
            .anyMatch(v -> v.equals(stationId));
    }
    private Section getSectionByUpStationId(Long stationId) {
        return sections.stream()
            .filter(v->v.getUpStation().getId().equals(stationId))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }
    private boolean hasDownStationId(Long stationId) {
        return sections.stream()
            .map(v -> v.getDownStation().getId())
            .anyMatch(v -> v.equals(stationId));
    }
    private Section getSectionByDownStationId(Long stationId) {
        return sections.stream()
            .filter(v->v.getDownStation().getId().equals(stationId))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }

    public List<Section> getSections() {
        List<Section> sortedSection = new ArrayList<>();
        Section section = getFirstSection();
        sortedSection.add(section);
        while(hasNextSection(section)) {
            section=getNextSection(section);
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
        if (getSections().size()<=1) {
            throw new IllegalArgumentException("최소 1개 이상의 구간이 존재해야합니다.");
        }
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException("노선에 등록되지 않은 역입니다.");
        }
    }
    public void checkAddArgument(SectionRequest sectionRequest) {
        if (sections.isEmpty()) {
            return;
        }
        if (findSectionByUpStation(sectionRequest.getDownStationId())) {
            return;
        }
        if (findSectionByDownStation(sectionRequest.getUpStationId())) {
            return;
        }
        Section target = findSectionByStation(sectionRequest);
        if (target.getDistance()<=sectionRequest.getDistance()) {
            throw new IllegalArgumentException("기존 구간보다 작은 길이의 구간을 입력해주세요. distance="+sectionRequest.getDistance());
        }
        if (target.getUpStation().getId().equals(sectionRequest.getUpStationId()) &&
                target.getDownStation().getId().equals(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException("동일한 역의 구간이 존재합니다. upStationId="+sectionRequest.getUpStationId()
                +", downStationId="+sectionRequest.getDownStationId());
        }
    }

    private Section findSectionByStation(SectionRequest sectionRequest) {
        for (Section section : sections) {
            if (section.getUpStation().getId().equals(sectionRequest.getUpStationId())) {
                return section;
            }
            if (section.getDownStation().getId().equals(sectionRequest.getDownStationId())) {
                return section;
            }
        }
        throw new IllegalArgumentException("연결되는 구간의 역이 없습니다. upStationId="+ sectionRequest.getUpStationId()
            +", downStationId="+ sectionRequest.getDownStationId());
    }
}
