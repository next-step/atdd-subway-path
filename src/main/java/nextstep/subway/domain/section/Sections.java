package nextstep.subway.domain.section;

import nextstep.subway.domain.station.Station;
import nextstep.subway.error.exception.BusinessException;
import nextstep.subway.error.exception.ErrorCode;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.domain.section.SectionValidator.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        if (this.sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        validateIsUpStationAndDownStationInSection(section.getUpStation(), section.getDownStation());
        this.validateSectionIsAlreadyExists(section);

        // 특정 구간 다음으로 추가해야하는지 검색
        final Section previousSection = this.getPreviousSectionAboutNewSection(section);

        // 역과 역 사이에 구간할 시, 이미 존재하는 구간의 upStation 을 추가하려는 구간의 downStation 을 바라보도록 변경
        if (previousSection != null && previousSection.getUpStation().equals(section.getUpStation())) {
            updatePreviousSectionBeforeAddNewSection(previousSection, section);
        }
        this.sections.add(section);
    }

    public void remove(Station station) {
        validateSectionSizeBeforeRemove(this);
        validateRemoveSectionIsLastSection(this, station);
        this.sections.remove(this.getLastSection());
    }

    public Integer size() {
        return this.sections.size();
    }

    public Boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public Section getLastSection() {
        final Section section = this.sections.stream().findFirst().orElseThrow(() -> new BusinessException(ErrorCode.SECTION_LIST_IS_EMPTY));
        return getLastSection(section);
    }

    public Section getFirstSection() {
        final Section section = this.sections.stream().findFirst().orElseThrow(() -> new BusinessException(ErrorCode.SECTION_LIST_IS_EMPTY));
        return getFirstSection(section);
    }

    public Section getPreviousSectionAboutNewSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getDownStation().equals(section.getUpStation()) ||
                        it.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElse(null);
    }

    public Section getNextSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findFirst()
                .orElse(null);
    }

    private Section getFirstSection(Section section) {
        while (true) {
            final Optional<Section> previousSection = this.sections.stream()
                    .filter(it -> it.getDownStation().equals(section.getUpStation()))
                    .findFirst();
            if (previousSection.isPresent()) {
                return getFirstSection(previousSection.get());
            }
            return section;
        }
    }

    private Section getLastSection(Section section) {
        while (true) {
            final Optional<Section> nextSection = this.sections.stream()
                    .filter(it -> it.getUpStation().equals(section.getDownStation()))
                    .findFirst();
            if (nextSection.isPresent()) {
                return getLastSection(nextSection.get());
            }
            return section;
        }
    }

    private void updatePreviousSectionBeforeAddNewSection(Section previousSection, Section newSection) {
        validateSectionDistance(previousSection, newSection);
        previousSection.update(
                newSection.getDownStation(),
                null,
                previousSection.getDistance() - newSection.getDistance()
        );
    }

    private void validateIsUpStationAndDownStationInSection(Station upStation, Station downStation) {
        if (this.sections.stream()
                .noneMatch(it -> validateIsStationInSection(it, upStation) || validateIsStationInSection(it, downStation))) {
            throw new BusinessException(ErrorCode.SECTION_NOT_FOUND_ABOUT_UP_AND_DOWN_STATION);
        }
    }

    private Boolean validateIsStationInSection(Section section, Station station) {
        return section.getUpStation().equals(station) || section.getDownStation().equals(station);
    }

    private void validateSectionIsAlreadyExists(Section section) {
        if (this.sections.stream()
                .anyMatch(it -> it.getUpStation().equals(section.getUpStation()) &&
                        it.getDownStation().equals(section.getDownStation()))) {
            throw new BusinessException(ErrorCode.SECTION_ALREADY_EXISTS);
        }
    }
}
