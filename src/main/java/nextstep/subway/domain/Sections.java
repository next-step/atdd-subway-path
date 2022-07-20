package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    public Sections(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    public boolean addSection(Section section) {
        if (section.getUpStation().equals(section.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역이 동일해서는 안됩니다.");
        }

        if (!sections.get(sections.size() - 1).getDownStation().equals(section.getUpStation())) {
            throw new IllegalArgumentException("현재 하행역과 등록하는 상행역이 같지 않습니다.");
        }

        if (sections.stream()
            .anyMatch(findSection -> findSection.getUpStation().equals(section.getDownStation())
                || findSection.getDownStation().equals(section.getDownStation()))) {
            throw new IllegalArgumentException("이미 존재하는 역입니다.");
        }

        return sections.add(section);
    }

    public void deleteSection(Long stationId) {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("하나 남은 노선은 삭제할 수 없습니다.");
        }

        if (!sections.get(sections.size() - 1).getDownStation().getId().equals(stationId)) {
            throw new IllegalArgumentException("하핵역의 노선만 삭제할 수 있습니다.");
        }

        sections.remove(sections.size() - 1);
    }
}
