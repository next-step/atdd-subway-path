package nextstep.subway.linesection;

import org.springframework.util.CollectionUtils;
import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public void add(LineSection addSection) {
        validateAddableSection(addSection);
        if (isAddableInFirst(addSection)) {
            this.sections.add(0, addSection);
            return;
        }
        if (isAddableInEnd(addSection)) {
            this.sections.add(addSection);
            return;
        }
        List<Station> stations = getStations();
        int idx = getIndex(addSection);

        if (stations.contains(addSection.getUpStation())) {
            LineSection section = sections.get(idx);
            checkDistance(section, addSection);
            sections.add(idx, LineSection.of(addSection.getLine(), section.getUpStation(), addSection.getDownStation(), addSection.getDistance()));
            sections.add(idx + 1, LineSection.of(addSection.getLine(), addSection.getDownStation(), section.getDownStation(), section.getDistance() - addSection.getDistance()));
            sections.remove(section);
        }
        if (stations.contains(addSection.getDownStation())) {
            LineSection section = sections.get(idx);
            checkDistance(section, addSection);
            sections.add(idx, LineSection.of(addSection.getLine(), section.getUpStation(), addSection.getUpStation(), section.getDistance() - addSection.getDistance()));
            sections.add(idx + 1, LineSection.of(addSection.getLine(), addSection.getUpStation(), section.getDownStation(), addSection.getDistance()));
            sections.remove(section);
        }
    }

    private int getIndex(LineSection section) {
        return IntStream.range(0, sections.size())
                .filter(idx -> sections.get(idx).getUpStation().equals(section.getUpStation()) || sections.get(idx).getDownStation().equals(section.getDownStation()))
                .findFirst()
                .getAsInt();
    }

    private void checkDistance(LineSection section, LineSection addSection) {
        if (section.getDistance() <= addSection.getDistance())
            throw new BadRequestException("request addSection distance must be smaller than exist lineSection disctance.");
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

    public boolean isAddableInFirst(LineSection lineSection) {
        return getFirstStation().equals(lineSection.getDownStation());
    }

    public boolean isAddableInEnd(LineSection lineSection) {
        Station lastStation = getLastStation();
        return lastStation.equals(lineSection.getUpStation());
    }

    private int countStationBySection(LineSection section) {
        return getStations()
                .stream()
                .filter(e -> e.equals(section.getUpStation()) || e.equals(section.getDownStation()))
                .collect(Collectors.toList()).size();
    }
}
