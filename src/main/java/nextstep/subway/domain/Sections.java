package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.CheckDuplicateStationException;
import nextstep.subway.exception.InvalidDownStationException;
import nextstep.subway.exception.InvalidUpStationException;
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
        if (this.sections.size() > 0) {
            validUpStation(section);
            validateDuplicateStation(section);
        }
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
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateDuplicateStation(Section section) {
        if (isDuplicateStation(section.getDownStation())) {
            throw new CheckDuplicateStationException("이미 등록되어있는 역입니다.");
        }
    }

    private boolean isDuplicateStation(Station station) {
        return this.sections.stream()
                .anyMatch(section -> section.getUpStation().equals(station));
    }

    private void validUpStation(Section section) {
        if (!getEndStation().equals(section.getUpStation())) {
            throw new InvalidUpStationException("구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이 아닙니다.");
        }
    }

    private Station getEndStation() {
        return sections.stream()
                .map(Section::getDownStation)
                .filter(downStation -> isEndStation(downStation))
                .findFirst()
                .get();
    }

    private boolean isEndStation(Station downStation) {
        return sections.stream()
                .noneMatch(section -> section.getUpStation().equals(downStation));
    }
}
