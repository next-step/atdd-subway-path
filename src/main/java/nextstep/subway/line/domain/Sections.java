package nextstep.subway.line.domain;

import lombok.Getter;
import nextstep.subway.line.domain.exception.CannotAddSectionException;
import nextstep.subway.line.domain.exception.CannotDeleteSectionException;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        Optional<Section> includingSectionOpt = sections.stream()
                .filter(s -> s.includes(section))
                .findAny();

        if (includingSectionOpt.isPresent()) {
            Section includingSection = includingSectionOpt.get();
            if (!includingSection.isLonger(section)) {
                throw new CannotAddSectionException("기존 구간 사이에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같을 수 없습니다.");
            }
        }

        sections.add(section);
    }

    public void removeSection(Long stationId) {
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

    public List<Long> stationIds() {
        List<Long> stationIds = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());

        stationIds.add(0, firstSection().getUpStationId());
        return stationIds;
    }

    private Section firstSection() {
        return sections.get(0);
    }
}
