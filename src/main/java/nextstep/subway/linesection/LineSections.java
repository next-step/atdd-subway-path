package nextstep.subway.linesection;

import nextstep.subway.line.LineRequest;
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

    public void remove(LineSection section) {
        validateRemovableSection();
        this.sections.remove(section);
    }

    public void remove(Station deleteStation) {
        validateRemovableSection();
        this.sections.remove(getLastSection());
    }

    private void validateRemovableSection() {
        if (this.sections.size() <= MINIMUM_SIZE) {
            throw new BadRequestException("the section cannot be removed because of minimum size.");
        }
    }

    private void validateAddableSection(LineSection section) {
        if (sections.contains(section))
            throw new BadRequestException(String.format("line's already has the station. stationId > %d", section.getDownStation().getId()));
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(e -> Arrays.asList(e.getUpStation(), e.getDownStation()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public LineSection getLastSection() {
        checkSectionsEmpty();
        return this.sections.get(this.sections.size() - 1);
    }

    private void checkSectionsEmpty() {
        if (CollectionUtils.isEmpty(sections))
            throw new BadRequestException("the line doesnt have any section.");
    }

    public Station getFirstStation() {
        return getFirstSection().getUpStation();
    }

    private Station getLastStation() {
        return getLastSection().getDownStation();
    }

    public List<LineSection> getSections() {
        return sections;
    }

    public LineSection getFirstSection() {
        checkSectionsEmpty();
        return getSections().get(0);
    }

    public boolean isFirst(LineSection lineSection) {
        return getFirstStation().equals(lineSection.getDownStation());
    }

    public boolean isEnd(LineSection lineSection) {
        return getLastStation().equals(lineSection.getUpStation());
    }

}
