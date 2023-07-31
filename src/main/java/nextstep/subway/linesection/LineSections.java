package nextstep.subway.linesection;

import org.springframework.util.CollectionUtils;
import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Embeddable
public class LineSections {
    @OrderColumn
    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private final List<LineSection> sections = new ArrayList<>();

    private static List<LineSectionAppender> appenders = List.of(new AppendInFirstLineSectionAppender(), new AppendInEndLineSectionAppender(),
            new UpStationLineSectionAppender(), new DownStationLineSectionAppender());
    private static final int MINIMUM_SIZE = 1;

    public static LineSections of(Line line, Station upStation, Station downStation, Integer distance) {
        LineSections lineSections = new LineSections();
        lineSections.sections.add(LineSection.of(line, upStation, downStation, distance));
        return lineSections;
    }

    public void add(LineSection addSection) {
        validateAddableSection(addSection);
        for (var appender : appenders) {
            boolean isExecuted = appender.append(this, addSection);
            if (isExecuted)
                return;
        }
    }

    public int getIndex(LineSection section) {
        return IntStream.range(0, sections.size())
                .filter(idx -> containsUpStationOrDownStation(section, sections.get(idx)))
                .findFirst()
                .getAsInt();
    }

    private boolean containsUpStationOrDownStation(LineSection section, LineSection target) {
        return target.getUpStation().equals(section.getUpStation()) || target.getDownStation().equals(section.getDownStation());
    }

    public boolean containsStation(Station station) {
        return getStations().contains(station);
    }

    public void remove(Station deleteStation) {
        validateRemovableSection();
        if (deleteStation.equals(getFirstStation())) {
            this.sections.remove(0);
            return;
        }
        if (deleteStation.equals(getLastStation())) {
            this.sections.remove(getLastSection());
            return;
        }
        LineSection beforeSection = findSectionByCondition(section -> section.getDownStation().equals(deleteStation));
        LineSection nextSection = findSectionByCondition(section -> section.getUpStation().equals(deleteStation));
        sections.remove(beforeSection);
        sections.remove(nextSection);
        sections.add(LineSection.of(beforeSection.getLine(), beforeSection.getUpStation(), nextSection.getDownStation(), beforeSection.getDistance() + nextSection.getDistance()));
    }

    private LineSection findSectionByCondition(Predicate<LineSection> condition) {
        return sections.stream()
                .filter(condition::test)
                .findAny()
                .get();
    }

    private void validateRemovableSection() {
        if (this.sections.size() <= MINIMUM_SIZE) {
            throw new BadRequestException("the section cannot be removed because of minimum size.");
        }
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
        return sections;
    }
}
