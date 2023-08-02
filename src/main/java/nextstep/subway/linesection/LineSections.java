package nextstep.subway.linesection;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.linesection.append.LineSectionAppenderHolder;
import nextstep.subway.linesection.remove.LineSectionRemover;
import nextstep.subway.linesection.remove.LineSectionRemoverHolder;
import nextstep.subway.station.Station;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Embeddable
public class LineSections {
    @OrderColumn
    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private final List<LineSection> sections = new ArrayList<>();
    private static final int MINIMUM_SIZE = 1;

    public static LineSections of(Line line, Station upStation, Station downStation, Integer distance) {
        LineSections lineSections = new LineSections();
        lineSections.sections.add(LineSection.of(line, upStation, downStation, distance));
        return lineSections;
    }

    public void add(LineSection addSection) {
        validateAddableSection(addSection);
        for (var appender : LineSectionAppenderHolder.getContext()) {
            boolean isExecuted = appender.append(this, addSection);
            if (isExecuted)
                return;
        }
    }

    public int getIndex(LineSection section) {
        return IntStream.range(0, sections.size())
                .filter(idx -> containsUpStationOrDownStation(section, sections.get(idx)))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("the section is not eixisted in line."));
    }

    private boolean containsUpStationOrDownStation(LineSection section, LineSection target) {
        return target.getUpStation().equals(section.getUpStation()) || target.getDownStation().equals(section.getDownStation());
    }

    public boolean containsStation(Station station) {
        return getStations().contains(station);
    }

    public void remove(Station deleteStation) {
        validateRemovableSection(deleteStation);
        for (LineSectionRemover remover : LineSectionRemoverHolder.getContext()) {
            if (remover.remove(this, deleteStation))
                return;
        }
    }

    public LineSection findSectionByCondition(Predicate<LineSection> condition) {
        return sections.stream()
                .filter(condition::test)
                .findAny()
                .orElseThrow(() -> new BadRequestException("result finding by condition is not existed."));
    }

    private void validateRemovableSection(Station deleteStation) {
        if (this.sections.size() <= MINIMUM_SIZE) {
            throw new BadRequestException("the section cannot be removed because of minimum size.");
        }
        if (!containsStation(deleteStation))
            throw new BadRequestException("the station is not on the line.");
    }

    private void validateAddableSection(LineSection section) {
        if (countStationBySection(section) != 1) {
            throw new BadRequestException(String.format("lineSection must have one station to save"));
        }
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

    public Station getLastStation() {
        return getLastSection().getDownStation();
    }

    public LineSection getFirstSection() {
        checkSectionsEmpty();
        return sections.get(0);
    }

    private int countStationBySection(LineSection section) {
        return getStations()
                .stream()
                .filter(e -> e.equals(section.getUpStation()) || e.equals(section.getDownStation()))
                .collect(Collectors.toList()).size();
    }

    public List<LineSection> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void addInEnd(LineSection addSection) {
        sections.add(addSection);
    }

    public void addInFirst(LineSection addSection) {
        sections.add(0, addSection);
    }

    public void addToIndex(int idx, LineSection addSection) {
        sections.add(idx, addSection);
    }

    public void removeSection(LineSection section) {
        sections.remove(section);
    }

    public void removeByIndex(int idx) {
        sections.remove(idx);
    }
}
