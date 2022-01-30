package nextstep.subway.domain;

import nextstep.subway.exception.IllegalSectionArgumentException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    public static final String NOT_LAST_SECTION = "마지막 구간이 아닙니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Section newSection) {
        //method로 추출 추출해서 의도를 알수있게
        //상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가 불가
        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        Section previousSection = getPreviousSection(newSection);
        if (previousSection != null) {
            addPreviousSection(previousSection, newSection);
        }

        Section nextSection = getNextSection(newSection);
        if (nextSection != null) {
            addNextSection(nextSection, newSection);
        }
    }

    private void addNextSection(Section nextSection, Section newSection) {
        int index = this.sections.indexOf(nextSection);
        this.sections.add(index, newSection);
    }

    private void addPreviousSection(Section previousSection, Section newSection) {
        int index = this.sections.indexOf(previousSection);
        this.sections.add(index, newSection);
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        Section section = this.sections.get(0);
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());

        while (true) {
            section = getNextSection(section);
            if (section == null) {
                break;
            }

            stations.add(section.getDownStation());
        }

        return stations;
    }

    private Section getNextSection(Section section) {
        return this.sections.stream()
                .filter(s -> s.isNextSection(section))
                .findFirst().orElse(null);
    }

    private Section getPreviousSection(Section section) {
        return this.sections.stream()
                .filter(s -> s.isPreviousSection(section))
                .findFirst().orElse(null);
    }

    public void remove(Station station) {
        Section lastSection = getLastSection();
        if (!lastSection.isLast(station)) {
            throw new IllegalSectionArgumentException(NOT_LAST_SECTION);
        }

        this.sections.remove(lastSection);
    }

    private Section getLastSection() {
        return this.sections.get(this.sections.size() - 1);
    }

}
