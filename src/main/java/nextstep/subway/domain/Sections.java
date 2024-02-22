package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.CheckDuplicateStationException;
import nextstep.subway.exception.InvalidSectionDistanceException;
import nextstep.subway.exception.NotFoundException;
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

        if (getStartStation().equals(section.getDownStation())) {
            addStartSection(section);
        } else if (getEndStation().equals(section.getUpStation())) {
            addEndSection(section);
        } else {
            addMiddleSection(section);
        }

    }


    public void deleteSection(Long stationId) {
        if (this.sections.size() == 1) {
            throw new SingleSectionDeleteException("구간이 한개인 경우 삭제할 수 없습니다.");
        }

        if (isStartSection(stationId)) {
            removeStartSection();
        } else if (isEndSection(stationId)) {
            removeEndSection();
        } else {
            removeMiddleSection(stationId);
        }
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
                .orElseThrow(()-> new NotFoundException("하행역을 찾을 수 없습니다."));
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
                .orElseThrow(() -> new NotFoundException("상행역과 일치하는 구간을 찾을 수 없습니다."));
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

    private void addStartSection(Section section) {
        Section removedSection = this.sections.stream()
                .filter(s -> s.getUpStation().equals(getStartStation()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("시작역과 일치하는 구간을 찾을 수 없습니다."));
        this.sections.remove(removedSection);
        this.sections.add(section);
        this.sections.add(Section.builder()
                .upStation(removedSection.getDownStation())
                .downStation(section.getDownStation())
                .distance(removedSection.getDistance())
                .line(removedSection.getLine())
                .build());
    }

    private void addEndSection(Section section) {
        this.sections.add(section);
    }

    private void addMiddleSection(Section section) {
        Section changeSection = this.sections.stream()
                .filter(s -> s.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("구간을 변경할 상행역을 찾을 수 없습니다."));

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

    private boolean isStartSection(Long stationId) {
        return getOrderedStations().get(0).getId() == stationId;
    }

    private boolean isEndSection(Long stationId) {
        return getOrderedStations().get(this.sections.size()).getId() == stationId;
    }

    private void removeStartSection() {
        this.sections.remove(0);
    }

    private void removeEndSection() {
        this.sections.remove(this.sections.size() - 1);
    }

    private void removeMiddleSection(Long stationId) {
        Section previousSection = null;
        Section nextSection = null;
        for (Section section : sections) {
            if (section.getUpStation().getId().equals(stationId)) {
                nextSection = section;
            } else if (section.getDownStation().getId().equals(stationId)) {
                previousSection = section;
            }
        }

        Long newDistance = previousSection.getDistance() + nextSection.getDistance();
        Section newSection = Section.builder()
                .line(sections.get(0).getLine())
                .upStation(previousSection.getUpStation())
                .downStation(nextSection.getDownStation())
                .distance(newDistance)
                .build();

        sections.remove(previousSection);
        sections.remove(nextSection);
        sections.add(newSection);
    }
}
