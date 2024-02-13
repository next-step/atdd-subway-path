package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.InvalidDownStationException;
import nextstep.subway.exception.SingleSectionDeleteException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void deleteSection(Long stationId) {
        if (this.sections.size() == 1)
            throw new SingleSectionDeleteException("구간이 한개인 경우 삭제할 수 없습니다.");

        Long downStationId = findDownStationId();
        if (downStationId != stationId) {
            throw new InvalidDownStationException("삭제 역이 하행 종점역이 아닙니다.");
        }
        this.sections.remove(this.sections.size() - 1);
    }
    public Long findDownStationId() {
        return this.sections.get(this.sections.size()-1).getDownStation().getId();
    }

    public List<Long> getStationIds() {
        return this.sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation().getId(), section.getDownStation().getId()))
                .collect(Collectors.toList());

    }
}
