package nextstep.subway.section.entity;

import nextstep.subway.section.exception.SectionException;
import nextstep.subway.station.entity.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.common.constant.ErrorCode.*;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LINE_ID")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        checkDuplicateSectionByStation(section.getUpStation(), section.getDownStation());

        if (isFirstSectionAdd(section.getUpStation().getId())) {
            addFirstSection(section);
            return;
        }

        addMiddleSections(section);
    }

    private void addFirstSection(Section section) {
        Section originalFirstSection = getSectionByUpStationId(section.getDownStation().getId());
        originalFirstSection.setPreviousSection(section);
        section.setNextSection(originalFirstSection);
        sections.add(section);
    }

    private void addMiddleSections(Section section) {
        Section originalPrevSection = getSectionByDownStationId(section.getUpStation().getId());
        Section originalPrevSNextSection = originalPrevSection.getNextSection();
        originalPrevSection.setNextSection(section);

        if (originalPrevSNextSection == null) {
            section.setPreviousSection(originalPrevSection);
            sections.add(section);
            return;
        }

        updateSectionData(section, originalPrevSNextSection);
        sections.add(section);
    }

    private void updateSectionData(Section newSection, Section nextSection) {
        checkSectionDistance(nextSection, newSection);
        newSection.setPreviousSection(nextSection.getPreviousSection());
        newSection.setNextSection(nextSection);
        nextSection.setPreviousSection(newSection);
        nextSection.setUpStation(newSection.getDownStation());
        nextSection.setDistance(nextSection.getDistance() - newSection.getDistance());
    }

    public void removeSection(Section section) {
        if (sections.size() <= 1) {
            throw new SectionException(String.valueOf(SECTION_NOT_PERMISSION_COUNT_TOO_LOW));
        }
        isExistSection(section);

        Section originalNextSection = section.getNextSection();
        Section originalPrevSection = section.getPreviousSection();

        deleteFirstSection(originalPrevSection, originalNextSection);
        deleteMiddleSection(originalPrevSection, originalNextSection, section);

        sections.remove(section);
    }

    private void isExistSection (Section section) {
        Optional<Section> findSection = sections.stream().filter(sectionValue -> sectionValue.equals(section))
                .findAny();
        if(findSection.isEmpty()) {
            throw new SectionException(String.valueOf(SECTION_NOT_PERMISSION_COUNT_TOO_LOW));
        }
    }

    public Section getRemoveTargetSection(Long stationId) {
        Optional<Section> isNotLastSection = getOptionalSectionByUpStationId(stationId);
        if (isNotLastSection.isEmpty()) {
            return getSectionByDownStationId(stationId);
        }
        return isNotLastSection.get();
    }

    private void deleteFirstSection(Section prevSection, Section nextSection) {
        if (prevSection == null) {
            if (nextSection != null) {
                nextSection.setPreviousSection(null);
            }
        }
    }

    private void deleteMiddleSection(Section prevSection, Section nextSection, Section originalSection) {
        if (prevSection != null) {
            if (nextSection != null) {
                prevSection.setNextSection(nextSection);
                prevSection.setDownStation(nextSection.getUpStation());
                prevSection.setDistance(originalSection.getDistance() + prevSection.getDistance());
                nextSection.setPreviousSection(prevSection);
            } else {
                prevSection.setNextSection(null);
            }
        }
    }

    public Section getFirstSection() {
        return sections.stream()
                .filter(section -> section.getPreviousSection() == null)
                .findFirst()
                .orElseThrow(() -> new SectionException(String.valueOf(SECTION_FIRST_STATION_NOT_FOUND)));
    }

    public Section getLastSection() {
        return sections.stream()
                .filter(section -> section.getNextSection() == null)
                .findFirst()
                .orElseThrow(() -> new SectionException(String.valueOf(SECTION_LAST_STATION_NOT_FOUND)));
    }

    public Optional<Section> getOptionalSectionByUpStationId(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStation().getId() == upStationId)
                .findFirst();
    }

    public Section getSectionByUpStationId(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStation().getId() == upStationId)
                .findFirst()
                .orElseThrow(() -> new SectionException(String.valueOf(SECTION_NOT_FOUND)));
    }

    public Section getSectionByDownStationId(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStation().getId() == downStationId)
                .findFirst()
                .orElseThrow(() -> new SectionException(String.valueOf(SECTION_NOT_FOUND)));
    }

    public void checkDuplicateSectionByStation(Station upStation, Station downStation) {
        boolean isUpStationExist = getSectionByUpStation(upStation);
        boolean isDownStationExist = getSectionByDownStation(downStation);

        if (isUpStationExist && isDownStationExist) {
            throw new SectionException(String.valueOf(SECTION_ALREADY_EXIST));
        }
    }

    public void checkSectionDistance(Section originalSection, Section section) {
        if (originalSection.getDistance() <= section.getDistance()) {
            throw new SectionException(String.valueOf(SECTION_DISTANCE_LESS_THAN_EXISTING));
        }
    }

    public boolean getSectionByUpStation(Station upStation) {
        return sections.stream().anyMatch(section -> section.getUpStation().equals(upStation));
    }

    public boolean getSectionByDownStation(Station downStation) {
        return sections.stream().anyMatch(section -> section.getDownStation().equals(downStation));
    }

    public boolean isFirstSectionAdd(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStation().getId() == downStationId)
                .findFirst()
                .isEmpty();

    }
}