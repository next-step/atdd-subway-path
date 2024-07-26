package nextstep.subway.line.domain;


import nextstep.subway.exceptions.ErrorMessage;
import nextstep.subway.line.exception.InsufficientStationsException;
import nextstep.subway.line.exception.InvalidDownStationException;
import nextstep.subway.line.exception.LineHasNoStationException;
import nextstep.subway.line.exception.SectionNotFoundException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();
    private static final ChainSorter<Section, Long> chainSorter = new ChainSorter<>(Section::getUpStationId, Section::getDownStationId);

    public void add(final Section section) {
        if (this.sections.isEmpty()) {
            sections.add(section);
            return;
        }
        validateBeforeAdd(section);

        //만약 마지막 역에 추가하는거면 추가
        if (isLastStation(section.getUpStationId())) {
            sections.add(section);
            return;
        }

        Section originSection = findByUpStationId(section.getUpStationId())
                .orElseThrow(() -> new SectionNotFoundException(ErrorMessage.SECTION_NOT_FOUND));
        originSection.updateForNewSection(section);
        sections.add(section);
    }


    public List<Long> getSortedStationIds() {
        return chainSorter.getSortedStationIds(this.sections);
    }

    public void removeStation(final Long stationId) {
        if (hasOnlyOneSection()) {
            throw new InsufficientStationsException(ErrorMessage.INSUFFICIENT_STATIONS);
        }
        Optional<Section> previousSection = findByDownStationId(stationId);
        Optional<Section> nextSection = findByUpStationId(stationId);
        previousSection.ifPresent((section) -> sections.remove(section));
        nextSection.ifPresent((section -> sections.remove(section)));

        if (previousSection.isPresent() && nextSection.isPresent()) {
            mergeSection(previousSection.get(), nextSection.get());
            return;
        }
        if ((previousSection.isEmpty() && nextSection.isEmpty())) {
            throw new LineHasNoStationException(ErrorMessage.NON_EXISTENT_STATION);
        }
    }

    public void mergeSection(final Section previousSection, final Section nextSection) {
        Long distance = previousSection.getDistance() + nextSection.getDistance();
        Section newSection = new Section(previousSection.getUpStationId(), nextSection.getDownStationId(), distance);
        sections.add(newSection);
    }

    public Stream<Section> stream() {
        return sections.stream();
    }

    Optional<Section> findByUpStationId(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst();
    }

    private Optional<Section> findByDownStationId(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findFirst();
    }

    private boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }

    private void validateBeforeAdd(final Section section) {
        if (!getSortedStationIds().contains(section.getUpStationId())) {
            throw new LineHasNoStationException(ErrorMessage.LINE_HAS_NO_STATION);
        }

        if (isUpStationAlreadyExists(section.getDownStationId())) {
            throw new InvalidDownStationException(ErrorMessage.INVALID_DOWN_STATION);
        }
    }


    private boolean isUpStationAlreadyExists(Long stationId) {
        return sections.stream()
                .map(Section::getUpStationId)
                .anyMatch(id -> id.equals(stationId));
    }

    private boolean isLastStation(final Long stationId) {
        return findByUpStationId(stationId).isEmpty();
    }

}
