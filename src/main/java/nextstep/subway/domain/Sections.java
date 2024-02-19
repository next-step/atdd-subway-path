package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.CheckDuplicateStationException;
import nextstep.subway.exception.InvalidDownStationException;
import nextstep.subway.exception.InvalidSectionDistanceException;
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
        if (this.sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        validateDuplicateStation(section);

        if (getEndStation().equals(section.getUpStation())) {
            this.sections.add(section);
        }else if (getStartStation().equals(section.getDownStation())) {
            Section removedSection = this.sections.stream()
                    .filter(s -> s.getUpStation().equals(getStartStation()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("상행역과 일치하는 섹션이 없습니다."));
            this.sections.remove(removedSection);
            this.sections.add(section);
            this.sections.add(removedSection);
        } else {
            Section changeSection = this.sections.stream()
                    .filter(s -> s.getUpStation().equals(section.getUpStation()))
                    .findFirst()
                    .get();

            Long distance = changeSection.getDistance() - section.getDistance();
            if (distance.compareTo(0L) <= 0L) {
                throw new InvalidSectionDistanceException("추가 하려는 구간의 길이는 기존 구간의 길이 보다 크거나 같을 수 없습니다.");
            }

            Section changeNewSection = Section.builder()
                    .upStation(changeSection.getDownStation())
                    .downStation(section.getDownStation())
                    .distance(distance)
                    .line(changeSection.getLine())
                    .build();


            this.sections.remove(changeSection);
            this.sections.add(section);
            this.sections.add(changeNewSection);
        }

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
        return this.sections.get(this.sections.size() - 1).getDownStation().getId();
    }

    public List<Long> getStationIds() {
        return this.sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation().getId(), section.getDownStation().getId()))
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateDuplicateStation(Section section) {
        if (isDuplicateUpStation(section.getUpStation()) && isDuplicateDownStation(section.getDownStation())) {
            throw new CheckDuplicateStationException("이미 등록되어있는 역입니다.");
        }
    }

    private boolean isDuplicateUpStation(Station station) {
        return this.sections.stream()
                .anyMatch(section -> section.getUpStation().equals(station));
    }

    private boolean isDuplicateDownStation(Station station) {
        return this.sections.stream()
                .anyMatch(section -> section.getDownStation().equals(station));
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

    private Station getStartStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(startStation -> isStartStation(startStation))
                .findFirst()
                .get();
    }

    private boolean isStartStation(Station upStation) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().equals(upStation));
    }

    public List<Station> getOrderedStations() {
        return sections.stream()
                .sorted()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
