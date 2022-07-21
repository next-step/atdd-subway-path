package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.domain.exception.CannotDeleteSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
public class Sections {

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public void removeSection(long stationId) {
        if (sections.isEmpty()) {
            throw new CannotDeleteSectionException("현재 존재하는 구간이 없습니다.");
        }

        Section lastSection = lastSection();
        if (!lastSection.isDownStation(stationId)) {
            throw new CannotDeleteSectionException("노선의 종점만 삭제할 수 있습니다.");
        }
        sections.remove(lastSection);
    }

    private Section lastSection() {
        return sections.get(sections.size() - 1);
    }
}
