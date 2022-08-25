package nextstep.subway.domain;

import nextstep.subway.error.exception.BusinessException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.domain.SectionValidator.*;
import static nextstep.subway.error.exception.ErrorCode.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    void add(Section section) {
        if (this.sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        this.validateAddSection(section);

        // 역과 역 사이에 구간할 시, 이미 존재하는 구간의 upStation 을 추가하려는 구간의 downStation 을 바라보도록 변경
        if (this.isNewStationBetweenStations(section)) {
            final Section previousSection = this.getPreviousSectionAboutNewSection(section);
            updatePreviousSectionBeforeAddNewSection(previousSection, section);
        }
        this.sections.add(section);
    }

    void remove(Station station) {
        this.validateRemoveStation(station);

        if (isDownStation(station)) {
            this.sections.remove(this.getLastSection());
            return;
        }
        if (isUpStation(station)) {
            this.sections.remove(this.getFirstSection());
            return;
        }
        final Section currentSection = getSectionWithUpStation(station);
        updatePreviousSectionBeforeRemoveSectionBetweenStations(currentSection);
        this.sections.remove(currentSection);
    }

    List<Section> getSections() {
        return this.sections;
    }

    Stations getStations() {
        if (this.sections.isEmpty()) {
            return new Stations(Collections.emptyList());
        }

        // Todo: upStationSection 변수 final 로 리팩터링
        // upStationSection 을 final 로 정의하고 싶은데 재귀함수를 구현하는데에 시간이 너무 많이 소요되어
        // 해당 부분은 우선 final 을 쓰지 않는 방향으로 작성하겠습니다..
        final Stations stations = new Stations(new ArrayList<>());
        Section upStationSection = getFirstSection();
        stations.add(upStationSection);

        while (true) {
            final Optional<Section> nextSection = getNextSection(upStationSection);
            if (nextSection.isEmpty()) {
                break;
            }
            stations.add(nextSection.get().getDownStation());
            upStationSection = nextSection.get();
        }
        return stations;
    }

    Integer size() {
        return this.sections.size();
    }

    Integer getTotalDistance() {
        return this.sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    private Section getFirstSection() {
        final Section section = this.sections.stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(SECTION_LIST_IS_EMPTY));
        return getFirstSection(section);
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

    private Section getLastSection() {
        final Section section = this.sections.stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(SECTION_LIST_IS_EMPTY));
        return getLastSection(section);
    }

    private Station getFirstStation() {
        return this.getFirstSection().getUpStation();
    }

    private Station getLastStation() {
        return this.getLastSection().getDownStation();
    }

    private Boolean isPreviousSectionExistsAboutNewSection(Section section) {
        return this.sections.stream()
                .anyMatch(it -> it.getDownStation().equals(section.getUpStation()) ||
                        it.getUpStation().equals(section.getUpStation()));
    }

    private Section getPreviousSectionAboutNewSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getDownStation().equals(section.getUpStation()) ||
                        it.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(PREVIOUS_SECTION_NOT_FOUND));
    }

    private Section getPreviousSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getDownStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(PREVIOUS_SECTION_NOT_FOUND));
    }

    private Optional<Section> getNextSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findFirst();
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

    private Section getSectionWithUpStation(Station station) {
        return this.sections.stream()
                .filter(it -> it.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new BusinessException(STATION_NOT_FOUND_IN_SECTION));
    }

    private void updatePreviousSectionBeforeAddNewSection(Section previousSection, Section newSection) {
        validateSectionDistance(previousSection, newSection);
        final Section updatedPreviousSection = previousSection.updateUpStation(newSection.getDownStation())
                .updateDistance(previousSection.getDistance() - newSection.getDistance());
        this.sections.remove(previousSection);
        this.sections.add(updatedPreviousSection);
    }

    private void updatePreviousSectionBeforeRemoveSectionBetweenStations(Section removeSection) {
        final Section previousSection = getPreviousSection(removeSection);
        final Section updatedPreviousSection = previousSection.updateDownStation(removeSection.getDownStation())
                .updateDistance(previousSection.getDistance() + removeSection.getDistance());
        this.sections.remove(previousSection);
        this.sections.add(updatedPreviousSection);
    }

    private void validateRemoveStation(Station station) {
        validateSectionSizeBeforeRemove(this);
        if (this.sections.stream()
                .noneMatch(it -> it.getUpStation().equals(station) || it.getDownStation().equals(station))) {
            throw new BusinessException(STATION_NOT_FOUND_IN_SECTION);
        }
    }

    private void validateAddSection(Section section) {
        validateIsUpStationAndDownStationInSections(section.getUpStation(), section.getDownStation());
        validateSectionIsAlreadyExists(section);
    }

    private void validateIsUpStationAndDownStationInSections(Station upStation, Station downStation) {
        if (this.sections.stream()
                .noneMatch(it -> validateIsStationInSection(it, upStation) || validateIsStationInSection(it, downStation))) {
            throw new BusinessException(SECTION_NOT_FOUND_ABOUT_UP_AND_DOWN_STATION);
        }
    }

    private void validateSectionIsAlreadyExists(Section section) {
        if (this.sections.stream()
                .anyMatch(it -> it.getUpStation().equals(section.getUpStation()) &&
                        it.getDownStation().equals(section.getDownStation()))) {
            throw new BusinessException(SECTION_ALREADY_EXISTS);
        }
    }

    private boolean isDownStation(Station station) {
        return this.getLastStation().equals(station);
    }

    private boolean isUpStation(Station station) {
        return this.getFirstStation().equals(station);
    }

    private boolean isNewStationBetweenStations(Section section) {
        return this.isPreviousSectionExistsAboutNewSection(section) &&
                this.getPreviousSectionAboutNewSection(section).isEqualToUpStation(section.getUpStation());
    }
}
