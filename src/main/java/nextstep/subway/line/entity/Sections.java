package nextstep.subway.line.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.DeletionValidationException;
import nextstep.subway.line.entity.handler.SectionAdditionHandler;
import nextstep.subway.line.entity.handler.SectionAdditionHandlerMapping;
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
        while (!station.equalsId(lastStation)) {
            stations.add(station);
            station = getDownStation(station);
        }
        stations.add(station);
        return stations;
    }

    private Station getDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equalsId(station))
                .findAny()
                .map(section -> section.getDownStation())
                .orElseThrow(() -> new StationNotFoundException(String.format("하행역이 존재하지 않습니다. 기준역id:%s", station.getId())));
    }

    public void forceSectionAddition(Section section) {
        sections.add(section);
    }

    public void addSection(Section section) {
//        Validator.validateEnrollment(this, section);

        SectionAdditionHandler handler = SectionAdditionHandlerMapping.getHandler(this, section);
        handler.validate(this, section);
        handler.apply(this, section);
    }


    public Section getSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(s -> s.getUpStation().equalsId(upStation))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException(String.format("구간을 찾을 수 없습니다. 상행역id:%s", upStation.getId())));
    }

    public Section getSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(s -> s.getDownStation().equalsId(downStation))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException(String.format("구간을 찾을 수 없습니다. 하역id:%s", downStation.getId())));
    }

    public void remove(Station station) {
        Validator.validateDeletion(this, station);
        sections.remove(getLastSection());
    }

    public Section getLastSection() {
        return getSectionByDownStation(getLastStation());
    }

    public Station getFirstStation() {
        List<Station> downStations = getDownStations();
        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .map(section -> section.getUpStation())
                .findAny()
                .orElseThrow(() -> new StationNotFoundException("노선 내 상행종착역을 찾을 수 없습니다."));
    }

    public Station getLastStation() {
        List<Station> upStations = getUpStations();
        return sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .map(section -> section.getDownStation())
                .findAny()
                .orElseThrow(() -> new StationNotFoundException("노선 내 하행종착역을 찾을 수 없습니다."));
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    private int size() {
        return sections.size();
    }

    public boolean hasStation(Station downStation) {
        return getStations().contains(downStation);
    }

    public boolean equalsLastStation(Station station) {
        return getLastStation().equalsId(station);
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

    private static class Validator {

        private static void validateDeletion(Sections sections, Station Station) {
            validateDeletionEqualsLineDownStation(sections, Station);
            validateTwoMoreSectionExists(sections);
        }

        private static void validateTwoMoreSectionExists(Sections sections) {
            if (sections.size() == 1) {
                throw new DeletionValidationException("상행 종점역과 하행 종점역만 존재합니다.");
            }
        }

        private static void validateDeletionEqualsLineDownStation(Sections sections, Station station) {
            if (!sections.getLastStation().equalsId(station)) {
                throw new DeletionValidationException(String.format("노선의 마지막 역이 아닙니다. 역id:%s", station.getId()));
            }
        }
    }
}
