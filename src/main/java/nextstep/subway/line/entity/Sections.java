package nextstep.subway.line.entity;

import nextstep.subway.station.entity.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(final Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        Optional<Section> addableAtUpStation = this.findAddableAtUpStation(newSection);
        if (addableAtUpStation.isPresent()) {
            this.addAtUpStation(addableAtUpStation.get(), newSection);
            return;
        }

        Optional<Section> addableAtDownStation = this.canAddableAtDownStation(newSection);
        if (addableAtDownStation.isPresent()) {
            this.addAtDownStation(addableAtDownStation.get(), newSection);
            return;
        }

        throw new IllegalArgumentException();
    }

    private void addAtDownStation(final Section section, final Section newSection) {
        this.ensureNoDuplicateDownStation(newSection);

        // A-B + B-C -> A-B-C
        if (this.canAddSectionAfterExistingDownStation(newSection, section)) {
            sections.add(sections.indexOf(section) +1, newSection);
            return;
        }

        if (this.canAddSectionBeforeExistingDownStation(newSection, section)) {
            this.addSectionBeforeExistingDownStation(newSection, section);
            return;
        }

        throw new IllegalArgumentException();
    }

    private void addAtUpStation(final Section section, final Section newSection) {
        // A-B + C-A -> C-A-B
        if (this.canAddSectionBeforeExistingUpStation(newSection, section)) {
            sections.add(sections.indexOf(section), newSection);
            return;
        }

        if (this.canAddSectionAfterExistingUpStation(newSection, section)) {
            this.addSectionAfterExistingUpStation(newSection, section);
            return;
        }

        throw new IllegalArgumentException();
    }

    private void addSectionBeforeExistingDownStation(final Section newSection, final Section section) {
        if (this.isEqualsDistance(newSection, section)) {
            throw new IllegalArgumentException();
        }

        final List<Section> newSections = sections;
        final int prevIdx = newSections.indexOf(section);
        final int distanceDifference = Math.abs(newSection.getDistance() - section.getDistance());

        // A-B + C-B -> C-A-B
        if (this.isNewSectionLongerThanExisting(newSection, section)) {
            final Section adjustedNewSection = new Section(
                    newSection.getLine()
                    , newSection.getUpStation()
                    , section.getUpStation()
                    , distanceDifference
            );

            newSections.add(prevIdx, adjustedNewSection);
            sections = newSections;
            return;
        }

        // A-B + C-B -> A-C-B
        final Section adjustedSection = new Section(
                newSection.getLine()
                , section.getUpStation()
                , newSection.getUpStation()
                , distanceDifference
        );

        newSections.remove(section);
        newSections.add(prevIdx, adjustedSection);
        newSections.add(prevIdx +1, newSection);
        sections = newSections;
    }

    private void addSectionAfterExistingUpStation(final Section newSection, final Section section) {
        if (this.isEqualsDistance(newSection, section)) {
            throw new IllegalArgumentException();
        }

        final List<Section> newSections = sections;
        final int prevIdx = newSections.indexOf(section);
        final int distanceDifference = Math.abs(newSection.getDistance() - section.getDistance());

        // A-B + A-C -> A-B-C
        if (this.isNewSectionLongerThanExisting(newSection, section)) {
            final Section adjustedNewSection = new Section(
                    newSection.getLine()
                    , section.getDownStation()
                    , newSection.getDownStation()
                    , distanceDifference
            );

            newSections.add(prevIdx +1, adjustedNewSection);
            sections = newSections;
            return;
        }

        // A-B + A-C -> A-C-B
        final Section adjustedSection = new Section(
                newSection.getLine()
                , newSection.getDownStation()
                , section.getDownStation()
                , distanceDifference
        );

        newSections.remove(section);
        newSections.add(prevIdx, newSection);
        newSections.add(prevIdx +1, adjustedSection);
        sections = newSections;
    }

    private boolean isEqualsDistance(final Section newSection, final Section section) {
        return newSection.getDistance() == section.getDistance();
    }

    private boolean isNewSectionLongerThanExisting(final Section newSection, final Section section) {
        return section.getDistance() < newSection.getDistance();
    }

    private boolean canAddSectionBeforeExistingDownStation(final Section newSection, final Section section) {
        return !section.getUpStation().equals(newSection.getUpStation()) &&
                section.getDownStation().equals(newSection.getDownStation());
    }

    private boolean canAddSectionAfterExistingDownStation(final Section newSection, final Section section) {
        return !section.getUpStation().equals(newSection.getDownStation()) &&
                section.getDownStation().equals(newSection.getUpStation());
    }

    private boolean canAddSectionAfterExistingUpStation(final Section newSection, final Section section) {
        return section.getUpStation().equals(newSection.getUpStation()) &&
                !section.getDownStation().equals(newSection.getDownStation());
    }

    private boolean canAddSectionBeforeExistingUpStation(final Section newSection, final Section section) {
        return section.getUpStation().equals(newSection.getDownStation()) &&
                !section.getDownStation().equals(newSection.getUpStation());
    }

    private Optional<Section> findAddableAtUpStation(final Section newSection) {
        return this.sections.stream()
                .filter(section -> this.isMatchedForUpStation(newSection, section))
                .findFirst();
    }

    private Optional<Section> canAddableAtDownStation(final Section newSection) {
        return this.sections.stream()
                .filter(section -> this.isMatchedForDownStation(newSection, section))
                .findFirst();
    }

    private boolean isMatchedForUpStation(final Section newSection, final Section section) {
        return this.canAddSectionBeforeExistingUpStation(newSection, section)
                || this.canAddSectionAfterExistingUpStation(newSection, section);
    }

    private boolean isMatchedForDownStation(final Section newSection, final Section section) {
        return this.canAddSectionBeforeExistingDownStation(newSection, section)
                || this.canAddSectionAfterExistingDownStation(newSection, section);
    }

    public void removeSection(final Station station) {
        this.ensureRemovableSection(station.getId());

        final Section sectionToRemove = this.findSectionByDownStation(station);
        this.sections.remove(sectionToRemove);
    }

    public static Sections from(final List<Section> sections) {
        return new Sections(sections);
    }

    public int size() {
        return sections.size();
    }

    public boolean isLastStation(final long stationId) {
        if (sections.isEmpty()) return false;

        Section lastSection = sections.get(sections.size() - 1);
        return lastSection.getDownStation().getId().equals(stationId);
    }

    public boolean isSectionRegistered(final long upStationId, final long downStationId) {
        return this.getSections().stream()
                .anyMatch(section ->
                        section.getUpStation().getId().equals(upStationId) && section.getDownStation().getId().equals(downStationId)
                );
    }

    private void ensureRemovableSection(final Long stationId) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("Cannot remove the only section in the line.");
        }

        if (!this.isLastStation(stationId)) {
            throw new IllegalArgumentException("Can only remove the last station of the line.");
        }
    }

    private void ensureNoDuplicateDownStation(final Section newSection) {
        final boolean hasDuplicateDownStation = this.getSections().stream()
                .anyMatch(section -> newSection.getDownStation().getId().equals(section.getUpStation().getId()));

        if (hasDuplicateDownStation) {
            throw new IllegalArgumentException("The down station of the new section matches the up station of an existing section, causing a duplication.");
        }
    }

    private Section findSectionByDownStation(final Station station) {
        final Section targetSection = this.getSections().stream()
                .filter(section -> section.getDownStation().getId().equals(station.getId()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No section found with the down station ID: " + station.getId()));

        return targetSection;
    }

    public List<Section> getSections() {
        return sections;
    }
}
