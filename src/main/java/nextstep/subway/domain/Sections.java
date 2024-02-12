package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.IllegalSectionException;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL)
    private List<Section> sections;

    protected Sections() {
        this.sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        Section lastSection = sections.get(sections.size() - 1);
        if (!lastSection.isPossibleToAdd(section.getUpStation())) {
            throw new IllegalSectionException("기존 노선의 하행 종점역과 추가하려는 구간의 상행역이 달라 추가할 수 없습니다.");
        }
        sections.add(section);
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void remove() {
        sections.remove(sections.size() - 1);
    }
}
