package nextstep.subway.line.domain.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.common.exception.DeletionValidationException;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.entity.addition.SectionAdditionHandler;
import nextstep.subway.line.domain.entity.addition.SectionAdditionHandlerMapping;
import nextstep.subway.line.domain.entity.deletion.SectionDeletionHandler;
import nextstep.subway.line.domain.entity.deletion.SectionDeletionHandlerMapping;
import nextstep.subway.line.exception.SectionNotFoundException;
import nextstep.subway.line.exception.StationNotFoundException;
import nextstep.subway.station.entity.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<Section> sections = new ArrayList<>();

    private Sections(Section section) {
        this.sections.add(section);
    }

    public static Sections init(Section section) {
        return new Sections(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station station = getFirstStation();
        Station lastStation = getLastStation();
        while (!station.equals(lastStation)) {
            stations.add(station);
            station = getDownStation(station);
        }
        stations.add(station);
        return stations;
    }

    private Station getDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findAny()
                .map(section -> section.getDownStation())
                .orElseThrow(() -> new StationNotFoundException("station.down.not.found"));
    }

    public void forceSectionAddition(Section section) {
        sections.add(section);
    }

    public void addSection(SectionAdditionHandlerMapping handlerMapping, Section section) {
        if (checkUpStationsContains(section.getUpStation()) && checkDownStationsContains(section.getDownStation())) {
            throw new CreationValidationException("section.0003");
        }

        SectionAdditionHandler handler = handlerMapping.getHandler(this, section);
        handler.validate(this, section);
        handler.apply(this, section);
    }

    public void remove(SectionDeletionHandlerMapping sectionDeletionHandlerMapping, Station station) {
        if (sections.size() == 1) {
            throw new DeletionValidationException("section.is.singular");
        }

        if (!hasStation(station)) {
            throw new DeletionValidationException(String.format("역이 존재하지 않습니다. 역 이름:%s", station.getId()));
        }

        SectionDeletionHandler handler = sectionDeletionHandlerMapping.getHandler(this, station);
        handler.apply(this, station);
    }


    public Section getSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(upStation))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException("section.not.found"));
    }

    public Section getSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(s -> s.getDownStation().equals(downStation))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException("section.not.found"));
    }

    public Station getFirstStation() {
        List<Station> downStations = getDownStations();
        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .map(section -> section.getUpStation())
                .findAny()
                .orElseThrow(() -> new StationNotFoundException("station.top.not.found"));
    }

    public Station getLastStation() {
        List<Station> upStations = getUpStations();
        return sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .map(section -> section.getDownStation())
                .findAny()
                .orElseThrow(() -> new StationNotFoundException("station.last.not.found"));
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    public boolean hasStation(Station station) {
        return getStations().contains(station);
    }

    public boolean equalsLastStation(Station station) {
        return getLastStation().equals(station);
    }

    private List<Station> getUpStations() {
        return sections.stream()
                .map(section -> section.getUpStation())
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(section -> section.getDownStation())
                .collect(Collectors.toList());
    }

    public boolean checkDownStationsContains(Station station) {
        return getDownStations().contains(station);
    }

    public boolean checkUpStationsContains(Station station) {
        return getUpStations().contains(station);
    }

    public void forceSectionRemove(Section section) {
        sections.remove(section);
    }
}
