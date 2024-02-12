package nextstep.subway.line.entity;

import nextstep.subway.station.entity.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();;

    protected Sections() {
    }

    private Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(final Section section) {
        this.ensureNoDuplicateDownStation(section);
        this.sections.add(section);
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
        if (!this.isLastStation(stationId)) {
            throw new IllegalArgumentException();
        }
    }

    private void ensureNoDuplicateDownStation(final Section newSection) {
        final boolean hasDuplicateDownStation = this.getSections().stream()
                .anyMatch(section -> newSection.getDownStation().getId().equals(section.getUpStation().getId()));

        if (hasDuplicateDownStation) throw new IllegalArgumentException();
    }

    private Section findSectionByDownStation(final Station station) {
        final Section targetSection = this.getSections().stream()
                .filter(section -> section.getDownStation().getId().equals(station.getId()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException());

        return targetSection;
    }

    public List<Section> getSections() {
        return sections;
    }
}
