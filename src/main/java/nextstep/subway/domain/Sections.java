package nextstep.subway.domain;

import nextstep.subway.domain.exception.IllegalAddSectionException;
import nextstep.subway.domain.exception.IllegalRemoveMinSectionSize;
import nextstep.subway.domain.exception.NotFoundSectionsException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Line line, Station requestUpStation, Station requestDownStation, int requestDistance) {
        if (sections.isEmpty()) {
            sections.add(new Section(line, requestUpStation, requestDownStation, requestDistance));
            return;
        }

        Section section = getSectionToAdd(requestUpStation, requestDownStation);

        sections.add(section.makeNext(line, requestUpStation, requestDownStation, requestDistance));

        if (canAddInTheMiddleStation(requestUpStation, section)) {
            section.changeUpStation(requestDownStation, requestDistance);
        }
    }

    public List<Station> getOrderedStations() {
        return sections.stream().sorted()
            .map(section -> List.of(section.getUpStation(), section.getDownStation()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void removeSection(Station station) {
        validateWhenRemoveSection();

        // 타겟 구간 찾기
        Section targetSection = findSectionByStation(station);

        // 마지막 역인지
        if (isLastStation(station)) {
            sections.remove(sections.size() - 1);
            return;
        }


        // 첫 역인지
        if (isFirstSection(targetSection)) {
            sections.remove(0);
            return;
        }

        // 중간 역 제거
        Section targetBeforeSection = getBeforeSection(targetSection);
        targetBeforeSection.changeDownStation(targetSection.getDownStation(), targetSection.getDistance());
        sections.remove(targetSection);
    }
    
    public Section findSectionByStation(Station station) {
        Section targetSection = sections.stream().filter(section -> section.getUpStation().equals(station))
            .findFirst()
            .orElseThrow(NotFoundSectionsException::new);

        return targetSection;
    }

    private Section getBeforeSection(Section targetSection) {
        return sections.stream().filter(section -> section.getDownStation().equals(targetSection.getUpStation()))
            .findFirst()
            .orElseThrow(NotFoundSectionsException::new);
    }

    private boolean isFirstSection(Section targetSection) {
        return sections.get(0).equals(targetSection);
    }

    private boolean isLastStation(Station targetStation) {
        return sections.get(sections.size() - 1).getDownStation().equals(targetStation);
    }

    public boolean isSectionsEmpty() {
        return sections.isEmpty();
    }

    private Section getSectionToAdd(Station requestUpStation, Station requestDownStation) {
        return sections.stream().filter(section -> isPossibleToAddSection(requestUpStation, requestDownStation, section))
            .findFirst()
            .orElseThrow(IllegalAddSectionException::new);
    }

    private static boolean isPossibleToAddSection(Station requestUpStation, Station requestDownStation, Section section) {
        // 상행역 또는 하행역에 추가
        if (canAddUpOrDownStation(requestUpStation, requestDownStation, section)) {
            return true;
        }

        // 상행역 하행역 사이 추가
        return canAddInTheMiddleStation(requestUpStation, section);
    }

    private static boolean canAddUpOrDownStation(Station requestUpStation, Station requestDownStation, Section section) {
        return section.getUpStation().equals(requestDownStation) || section.getDownStation().equals(requestUpStation);
    }

    private static boolean canAddInTheMiddleStation(Station requestUpStation, Section section) {
        return section.getUpStation().equals(requestUpStation);
    }

    private void validateWhenRemoveSection() {
        if (sections.size() == 1) {
            throw new IllegalRemoveMinSectionSize();
        }
    }

}
