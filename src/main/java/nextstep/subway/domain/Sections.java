package nextstep.subway.domain;

import nextstep.subway.common.error.SubwayError;
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

    public void removeSection(final Station station) {
        validateOnlyOneSection();
        final Section lastSection = findLastSection();
        validateMatchLastStation(lastSection, station);
        this.sections.remove(lastSection);
    }

    public Section findByIndex(final int index) {
        return this.sections.get(index);
    }

    public void remove(final Station downStation) {
        validateSections();
        final int index = this.sections.size() - 1;
        validateMatchStation(downStation, index);
        this.sections.remove(index);
    }

    public void addLine(final Line line) {
        for (Section section : this.sections) {
            section.addLine(line);
        }
    }

    private void validateMatchStation(final Station downStation, final int index) {
        final Section section = findByIndex(index);
        if (!section.getDownStation().equals(downStation)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateSections() {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    private void add(final Line line, final Station upStation, final Station downStation, final Integer distance) {
        for (Section section : this.sections) {
            if (section.matchUpStation(upStation)) {
                addMiddleSection(line, downStation, distance, section);
                return ;
            }
            if (section.matchDownStation(upStation)) {
                addDownSection(line, upStation, downStation, distance);
                return ;
            }
            if (section.matchUpStation(downStation)) {
                addUpSection(line, upStation, downStation, distance, section);
                return ;
            }
        }
    }

    private void addUpSection(final Line line, final Station upStation, final Station downStation, final Integer distance, final Section section) {
        final Section addSection = new Section(line, downStation, section.getDownStation(), section.getDistance());
        this.sections.add(addSection);
        section.changeSectionOfUpStation(upStation, distance);
    }

    private void addDownSection(final Line line, final Station upStation, final Station downStation, final Integer distance) {
        final Section addSection = new Section(line, upStation, downStation, distance);
        this.sections.add(addSection);
    }

    private void addMiddleSection(final Line line, final Station downStation, final Integer distance, final Section section) {
        final Section addSection = new Section(line, downStation, section.getDownStation(), distance);
        section.changeSectionOfMiddleStation(downStation, distance);
        this.sections.add(addSection);
    }

    private void validateAddStation(final List<Station> stations, final Station upStation, final Station downStation) {
        validateMatchStation(stations, upStation, downStation);
        validateAnyMatchStation(stations, upStation, downStation);
    }

    public List<Station> convertToStations() {
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

    public List<Station> findAllStationsOrderBu() {
        if (CollectionUtils.isEmpty(this.sections)) {
            return Collections.emptyList();
        }
        return getStations();
    }

    private List<Station> getStations() {
        final List<Station> stations = new ArrayList<>();
        final List<Section> sections = getSections();
        final Section firstSection = sections.get(0);
        stations.add(firstSection.getUpStation());
        Station downStation = firstSection.getDownStation();
        addStations(stations, sections, downStation);
        return stations;
    }

    private void validateMatchStation(final List<Station> stations, final Station upStation, final Station downStation) {
        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new AlreadyExistException(NO_REGISTER_EXIST_STATION);
        }
    }

    private void validateAnyMatchStation(final List<Station> stations, final Station upStation, final Station downStation) {
        final boolean resultOfUpStation = stations.contains(upStation);
        final boolean resultOfDownStation = stations.contains(downStation);
        if (!resultOfUpStation && !resultOfDownStation) {
            throw new NoExistStationException(NO_REGISTER_NO_EXIST_STATION);
        }
    }

    private void validateOnlyOneSection() {
        if (this.sections.size() == ONE_SECTION) {
            throw new NoDeleteOneSectionException(NO_DELETE_ONE_SECTION);
        }
    }

    private void validateMatchLastStation(final Section findSection, final Station upStation) {
        validateMatchStation(findSection, upStation, NO_REGISTER_LAST_LINE_STATION);
    }

    private void validateMatchStation(final Section findSection, final Station upStation, final SubwayError subwayError) {
        if (canNotMatchDownStation(findSection, upStation)) {
            throw new NoRegisterStationException(subwayError);
        }
    }

    private boolean canNotMatchDownStation(final Section findSection, final Station station) {
        return !findSection.matchDownStation(station);
    }

    private Section findLastSection() {
        if (CollectionUtils.isEmpty(this.sections)) {
            throw new NoLastSectionException(NO_LAST_SECTION);
        }
        return this.sections.get(this.sections.size() -1);
    }

    private void addStations(final List<Station> stations, final List<Section> sections, Station downStation) {
        for(int index = 0; index < sections.size(); index++) {
            if (sections.get(index).matchUpStation(downStation)) {
                stations.add(sections.get(index).getUpStation());
                downStation = sections.get(index).getDownStation();
                index = 0;
            }
        }
        stations.add(downStation);
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
