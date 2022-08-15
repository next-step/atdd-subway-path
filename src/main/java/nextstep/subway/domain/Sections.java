package nextstep.subway.domain;

import nextstep.subway.error.exception.BusinessException;
import nextstep.subway.error.exception.ErrorCode;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.domain.SectionValidator.*;

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

        validateIsUpStationAndDownStationInSections(section.getUpStation(), section.getDownStation());
        this.validateSectionIsAlreadyExists(section);

        // 특정 구간 다음으로 추가해야하는지 검색
        final Optional<Section> previousSection = this.getPreviousSectionAboutNewSection(section);

        // 역과 역 사이에 구간할 시, 이미 존재하는 구간의 upStation 을 추가하려는 구간의 downStation 을 바라보도록 변경
        if (previousSection.isPresent() && previousSection.get().getUpStation().equals(section.getUpStation())) {
            updatePreviousSectionBeforeAddNewSection(previousSection.get(), section);
        }
        this.sections.add(section);
    }

    void remove(Station station) {
        this.validateRemoveStation(station);

        if (isDownStation(this, station)) {
            this.sections.remove(this.getLastSection());
            return;
        }
        if (isUpStation(this, station)) {
            this.sections.remove(this.getFirstSection());
            return;
        }
        final Section currentSection = getSectionWithUpStation(station);
        final Section previousSection = getPreviousSection(currentSection);
        previousSection.updateDownStation(currentSection.getDownStation());
        previousSection.updateDistance(previousSection.getDistance() + currentSection.getDistance());
        this.sections.remove(currentSection);
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

    private Section getFirstSection() {
        final Section section = this.sections.stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.SECTION_LIST_IS_EMPTY));
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
                .orElseThrow(() -> new BusinessException(ErrorCode.SECTION_LIST_IS_EMPTY));
        return getLastSection(section);
    }

    private Section getPreviousSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getDownStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PREVIOUS_SECTION_NOT_FOUND));
    }

    private Optional<Section> getPreviousSectionAboutNewSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getDownStation().equals(section.getUpStation()) ||
                        it.getUpStation().equals(section.getUpStation()))
                .findFirst();
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
                .orElseThrow(() -> new BusinessException(ErrorCode.STATION_NOT_FOUND_IN_SECTION));
    }

    private void updatePreviousSectionBeforeAddNewSection(Section previousSection, Section newSection) {
        validateSectionDistance(previousSection, newSection);
        previousSection.updateUpStation(newSection.getDownStation());
        previousSection.updateDistance(previousSection.getDistance() - newSection.getDistance());
    }

    private void validateRemoveStation(Station station) {
        validateSectionSizeBeforeRemove(this);
        if (this.sections.stream()
                .noneMatch(it -> it.getUpStation().equals(station) || it.getDownStation().equals(station))) {
            throw new BusinessException(ErrorCode.STATION_NOT_FOUND_IN_SECTION);
        }
    }

    private void validateIsUpStationAndDownStationInSections(Station upStation, Station downStation) {
        if (this.sections.stream()
                .noneMatch(it -> validateIsStationInSection(it, upStation) || validateIsStationInSection(it, downStation))) {
            throw new BusinessException(ErrorCode.SECTION_NOT_FOUND_ABOUT_UP_AND_DOWN_STATION);
        }
    }

    private void validateSectionIsAlreadyExists(Section section) {
        if (this.sections.stream()
                .anyMatch(it -> it.getUpStation().equals(section.getUpStation()) &&
                        it.getDownStation().equals(section.getDownStation()))) {
            throw new BusinessException(ErrorCode.SECTION_ALREADY_EXISTS);
        }
    }

    private Boolean isDownStation(Sections sections, Station station) {
        return sections.getLastSection().getDownStation().equals(station);
    }

    private Boolean isUpStation(Sections sections, Station station) {
        return sections.getFirstSection().getUpStation().equals(station);
    }
}
