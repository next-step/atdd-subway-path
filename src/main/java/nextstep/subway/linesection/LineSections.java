package nextstep.subway.linesection;

import org.springframework.util.CollectionUtils;
import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class LineSections {
    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private final List<LineSection> sections = new ArrayList<>();

    private static final int MINIMUM_SIZE = 1;

    public static LineSections of(Line line, Station upStation, Station downStation, Integer distance) {
        LineSections lineSections = new LineSections();
        lineSections.sections.add(LineSection.of(line, upStation, downStation, distance));
        return lineSections;
    }

    public void add(LineSection section) {
        validateAddableSection(section);
        this.sections.add(section);
    }

    public void remove(Station toDeleteStation) {
        validateRemovableSection(toDeleteStation);
        this.sections.remove(getLastLineSection());
    }

    private void validateRemovableSection(Station toDeleteStation) {
        if (!getEndStation().equals(toDeleteStation))
            throw new BadRequestException("the section cannot be removed, because request station is not last station in the line.");
        if (this.sections.size() <= MINIMUM_SIZE) {
            throw new BadRequestException("the section cannot be removed because of minimum size.");
        }
    }

    private void validateAddableSection(LineSection section) {
        if (!getEndStation().equals(section.getUpStation()))
            throw new BadRequestException(String.format("line's endStation must be equal to lineSectionRequest's upStation."));

        if (getStations().contains(section.getDownStation()))
            throw new BadRequestException(String.format("line's already has the station. stationId > %d", section.getDownStation().getId()));
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(e -> Arrays.asList(e.getUpStation(), e.getDownStation()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public LineSection getLastLineSection() {
        checkSectionsEmpty();
        return this.sections.get(this.sections.size() - 1);
    }

    public Station getEndStation() {
        return getLastLineSection().getDownStation();
    }


    private void checkSectionsEmpty() {
        if (CollectionUtils.isEmpty(sections))
            throw new BadRequestException("the line doesnt have any section.");
    }
}
