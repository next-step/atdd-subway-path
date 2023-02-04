package nextstep.subway.domain;

import nextstep.subway.common.exception.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.error.SubwayError.*;

@Embeddable
public class Sections {

    private static final int ONE_SECTION = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {}

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void addSection(final Line line, final Station upStation, final Station downStation, final Integer distance) {
        final List<Station> stations = convertToStations();
        if (CollectionUtils.isEmpty(stations)) {
            this.sections.add(new Section(line, upStation, downStation, distance));
            return ;
        }
        if (!CollectionUtils.isEmpty(stations)) {
            validateAddStation(stations, upStation, downStation);
            this.add(line, upStation, downStation, distance);
        }
    }

    private void add(final Line line, final Station upStation, final Station downStation, final Integer distance) {
        for (Section section : this.sections) {
            if (section.matchUpStation(upStation)) {
                validateGreaterDistance(section, distance);
                final Section addSection = new Section(line, downStation, section.getDownStation(), distance);
                section.minus(distance);
                section.changeDownStation(downStation);
                this.sections.add(addSection);
                return ;
            }
            if (section.matchDownStation(upStation)) {
                final Section addSection = new Section(line, upStation, downStation, distance);
                this.sections.add(addSection);
                return ;
            }
            if (section.matchUpStation(downStation)) {
                final Section addSection = new Section(line, downStation, section.getDownStation(), section.getDistance().getDistance());
                this.sections.add(addSection);
                section.changeStation(upStation);
                section.changeDistance(distance);
                return ;
            }
        }
    }

    public void removeSection(final Station station) {
        validateOnlyOneSection();
        final Section lastSection = findLastSection();
        validateMatchLastStation(lastSection, station);
        this.sections.remove(lastSection);
    }

    private void validateAddStation(final List<Station> stations, final Station upStation, final Station downStation) {
        validateMatchStation(stations, upStation, downStation);
        validateAnyMatchStation(stations, upStation, downStation);
    }

    private List<Station> convertToStations() {
        if (CollectionUtils.isEmpty(this.sections)) {
            return Collections.emptyList();
        }
        final List<Station> stations = new ArrayList<>();
        stations.add(this.sections.get(0).getUpStation());
        List<Station> collect = this.sections
                .stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.addAll(collect);
        return stations;
    }

    private void validateMatchStation(final List<Station> stations, final Station upStation, final Station downStation) {
        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new AlreadyExistException(NO_REGISTER_EXIST_STATION.getMessage());
        }
    }

    private void validateAnyMatchStation(final List<Station> stations, final Station upStation, final Station downStation) {
        final boolean resultOfUpStation = stations.contains(upStation);
        final boolean resultOfDownStation = stations.contains(downStation);
        if (!resultOfUpStation && !resultOfDownStation) {
            throw new NoExistStationException(NO_REGISTER_NO_EXIST_STATION.getMessage());
        }
    }

    private void validateGreaterDistance(final Section findSection, final Integer distance) {
        findSection.validateDistanceGreaterThan(distance);
    }

    private void validateOnlyOneSection() {
        if (this.sections.size() == ONE_SECTION) {
            throw new NoDeleteOneSectionException(NO_DELETE_ONE_SECTION.getMessage());
        }
    }

    private void validateMatchLastStation(final Section findSection, final Station upStation) {
        validateMatchStation(findSection, upStation, NO_REGISTER_LAST_LINE_STATION.getMessage());
    }

    private void validateMatchStation(final Section findSection, final Station upStation, final String message) {
        if (canNotMatchDownStation(findSection, upStation)) {
            throw new NoRegisterStationException(message);
        }
    }

    private boolean canNotMatchDownStation(final Section findSection, final Station station) {
        return !findSection.matchDownStation(station);
    }

    private Section findLastSection() {
        if (CollectionUtils.isEmpty(this.sections)) {
            throw new NoLastSectionException(NO_LAST_SECTION.getMessage());
        }
        return this.sections.get(this.sections.size() -1);
    }

    public Section findByIndex(final int index) {
        return this.sections.get(index);
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(this.sections);
    }

    public void remove(final int index) {
        this.sections.remove(index);
    }

    public void addLine(final Line line) {
        for (Section section : this.sections) {
            section.addLine(line);
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
