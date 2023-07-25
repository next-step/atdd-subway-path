package nextstep.subway.section.entity;

import lombok.Getter;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.station.entity.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {}

    public Sections(Section section, Line line) {
        this.sections.add(section);
        section.addSection(line);
    }

    public void addNewStationAsAnDownStation(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.getDownStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> this.sections.add(section));
    }

    public void addNewStationAsAnUpStation(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.getUpStation().equals(section.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> this.sections.add(section));
    }

    public void addNewStationBetweenExistingStation(Section section, Line line) {
        sections.stream()
                .filter(oldSection -> oldSection.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    this.sections.add(Section.of(oldSection.getUpStation(), section.getDownStation(), validationDistance(oldSection.getDistance(), section.getDistance()), line));
                    this.sections.add(Section.of(section.getDownStation(), oldSection.getDownStation(), section.getDistance(), line));
                    this.sections.remove(oldSection);
                });
    }

    public boolean isUptoUp(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(newSection.getUpStation()));
    }

    public boolean isUpToDown(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(newSection.getDownStation()));
    }

    public boolean isDownToUp(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().equals(newSection.getUpStation()));
    }

    public boolean alreadySection(Section section) {
        return sections.stream()
                .anyMatch(oldSection -> oldSection.getUpStation().equals(section.getUpStation()))
                &&
                sections.stream()
                        .anyMatch(oldSection -> oldSection.getDownStation().equals(section.getDownStation()));
    }

    public boolean cannotAddSection(Section newSection) {
        return sections.stream()
                .anyMatch(oldSection -> {
                    boolean b1 = oldSection.getUpStation().equals(newSection.getUpStation());
                    boolean b2 = oldSection.getUpStation().equals(newSection.getDownStation());
                    return b1 || b2;
                }) ||
                sections.stream()
                        .anyMatch(oldSection -> {
                            boolean b1 = oldSection.getDownStation().equals(newSection.getUpStation());
                            boolean b2 = oldSection.getDownStation().equals(newSection.getDownStation());
                            return b1 || b2;
                        });
    }

    private int validationDistance(int oldDistance, int newDistance) {
        int distance = oldDistance - newDistance;
        if (distance <= 0) {
            throw new SubwayException(ErrorCode.INVALID_DISTANCE);
        }
        return distance;
    }

    public void removeSection(Station downStation) {
        if (isSectionOne()) {
            throw new SubwayException(ErrorCode.SECTION_IS_ONE);
        }
        if (!isLastSection(downStation)) {
            throw new SubwayException(ErrorCode.NOT_DOWN_STATION);
        }
        this.sections.removeIf(section -> section.isDownStation(downStation));
    }

    private boolean isSectionOne() {
        return this.sections.size() == 1;
    }

    private boolean isLastSection(Station downStation) {
        return getLastSection()
                .map(section -> section.isDownStation(downStation))
                .orElse(false);
    }

    private Optional<Section> getLastSection() {
        if (sections.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(sections.get(sections.size() - 1));
        }
    }
}
