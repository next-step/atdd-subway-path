package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public Section getFirstStation() {
        return sections.stream()
                .filter(sec1 -> sections.stream()
                        .noneMatch(sec2 -> sec1.getUpStation().equals(sec2.getDownStation())))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("WRONG_SECTION_IMFORMATION"));
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }


    public void addSection(Section section) {

        int sectionSize = sections.size();
        if (sectionSize > 0) {
            //이미 존재 하는지 확인
            isAlreadyRegisted(section);
            //해당 구간을 찾으면서 상행 하행 둘다 없는 부분 확인
            Section targetSection = getTargetSection(section);
            //업데이트 전에 구간 길이 확인
            updateTargetSection(targetSection, section);
        }
        sections.add(section);
    }

    public void isAlreadyRegisted(Section section) {
        if (sections.stream()
                .anyMatch(sec -> sec.getUpStation().equals(section.getUpStation()) &&
                        sec.getDownStation().equals(section.getDownStation()))) {
            throw new IllegalArgumentException("SECTION_ALREADY_REGIST");
        }
    }

    private Section getTargetSection(Section section) {
        return sections.stream().filter(sec -> section.getStations().contains(sec.getUpStation()) ||
                        section.getStations().contains(sec.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("HAS_NO_STATION"));
    }

    private void updateTargetSection(Section targetSection, Section section) {

        //기존 구간에서 상행을 기준으로 중간 삽입시
        if (targetSection.getUpStation().equals(section.getUpStation())) {
            isTooLongDistance(targetSection, section);
            targetSection.setUpStation(section.getDownStation());
            targetSection.setDistance(targetSection.getDistance() - section.getDistance());
        }

        //기존 구간에서 하행을 기준으로 중간 삽입시
        if (targetSection.getDownStation().equals(section.getDownStation())) {
            isTooLongDistance(targetSection, section);
            targetSection.setDownStation(section.getUpStation());
            targetSection.setDistance(targetSection.getDistance() - section.getDistance());
        }
    }

    private void isTooLongDistance(Section targetSection, Section section) {
        if (section.getDistance() >= targetSection.getDistance()) {
            throw new IllegalArgumentException("DISTANCE_TOO_LONG");
        }
    }

    public void removeSection(Station station) {
        //station이 포함된 Section 검색( 사이라면 2개, 상행 혹은 하행이라면 1개)
        List<Section> targetSections = getTargetSections(station);

        if (targetSections.size() == 0) {
            throw new IllegalArgumentException("HAS_NO_STATION");
        }

        //구간이 1개만 있는지 확인
        hasOnlyOneSection(sections.size());

        if (targetSections.size() > 1) {
            Collections.sort(targetSections, new SectionComparator());
            removeUpdateSection(targetSections);
        }

        removeSection(targetSections.get(0));

    }

    public void hasOnlyOneSection(int sectionSize) {
        if (sectionSize <= 1) {
            throw new IllegalArgumentException("ONLY_ONE_SECTION");
        }
    }

    private void removeUpdateSection(List<Section> targetSections) {
        Section first = targetSections.get(0);
        Section second = targetSections.get(1);
        second.setUpStation(first.getUpStation());
        second.setDistance(first.getDistance() + second.getDistance());
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public List<Section> getTargetSections(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station) ||
                        section.getDownStation().equals(station))
                .collect(Collectors.toList());
    }

}
