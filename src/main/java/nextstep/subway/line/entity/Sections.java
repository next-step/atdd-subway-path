package nextstep.subway.line.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.common.exception.DeletionValidationException;
import nextstep.subway.common.exception.ValidationException;
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

    public void addSection(Section section) {
        Validator.validateEnrollment(this, section);

        if (newSectionUpStationMatchAnyUpStation(section)) {
            Section existingSection = getSectionByUpStation(section.getUpStation());
            existingSection.divideBy(section);
        } else if (newSectionDownStationMatchAnyDownStation(section)) {
            Section existingSection = getSectionByDownStation(section.getDownStation());
            existingSection.divideBy(section);
        }
        sections.add(section);
    }


    private Section getSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(s -> s.getUpStation().equalsId(upStation))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException(String.format("구간을 찾을 수 없습니다. 상행역id:%s", upStation.getId())));
    }

    private Section getSectionByDownStation(Station downStation) {
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

    private int size() {
        return sections.size();
    }

    private boolean hasStation(Station downStation) {
        return getStations().contains(downStation);
    }

    private boolean equalsLastStation(Station station) {
        return getLastStation().equalsId(station);
    }

    private boolean newSectionUpStationMatchAnyUpStation(Section section) {
        return getUpStations().contains(section.getUpStation());
    }

    private boolean newSectionDownStationMatchAnyDownStation(Section section) {
        return getDownStations().contains(section.getDownStation());
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

    private static class Validator {
        static void validateEnrollment(Sections sections, Section section) {

            validateOnlyOneStationIsEnrolledInLine(sections, section);

            if (newSectionUpStationMatchLastStation(sections, section)) {
                validateNewSectionDownStationIsNewcomer(sections, section);
                return;
            }
            if (newSectionDownStationMatchTopStation(sections, section)) {
                validateNewSectionUpStationIsNewcomer(sections, section);
                return;
            }
            if (newSectionUpStationMatchAnyUpStation(sections, section)) {
                validateNewSectionLengthSmaller(sections.getSectionByUpStation(section.getUpStation()), section);
                return;
            }
            if (newSectionDownStationMatchAnyDownStation(sections, section)) {
                validateNewSectionLengthSmaller(sections.getSectionByDownStation(section.getDownStation()), section);
                return;
            }

            throw new CreationValidationException(String.format("새 구간을 등록할 수 없습니다. 새구간 상행역id:%s, 하행역id:%d",
                    section.getUpStation().getId(), section.getDownStation().getId()));
        }

        private static boolean newSectionUpStationMatchLastStation(Sections sections, Section section) {
            return sections.equalsLastStation(section.getUpStation());
        }

        private static boolean newSectionDownStationMatchTopStation(Sections sections, Section section) {
            return sections.getFirstStation().equalsId(section.getDownStation());
        }
        private static boolean newSectionUpStationMatchAnyUpStation(Sections sections, Section section) {
            return sections.checkUpStationsContains(section.getUpStation());
        }
        private static boolean newSectionDownStationMatchAnyDownStation(Sections sections, Section section) {
            return sections.equalsLastStation(section.getDownStation());
        }

        private static void validateDeletion(Sections sections, Station Station) {
            validateDeletionEqualsLineDownStation(sections, Station);
            validateTwoMoreSectionExists(sections);
        }

        private static void validateOnlyOneStationIsEnrolledInLine(Sections sections, Section section) {
            if (sections.checkUpStationsContains(section.getUpStation()) &&
                    sections.checkDownStationsContains(section.getDownStation())) {
                throw new CreationValidationException("새로운 구간의 상행역과 하행역 둘중 한개는 노선에 등록돼있어야합니다.");
            }
        }

        private static void validateNewSectionDownStationIsNewcomer(Sections sections, Section section) {
            if (sections.hasStation(section.getDownStation())) {
                throw new CreationValidationException("새로운 구간의 하행역이 해당 노선에 등록되어있는 역임.");
            }
        }

        private static void validateNewSectionUpStationIsNewcomer(Sections sections, Section section) {
            if (sections.hasStation(section.getUpStation())) {
                throw new CreationValidationException("새로운 구간의 하행역이 해당 노선에 등록되어있는 역임.");
            }
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

        private static void validateNewSectionLengthSmaller(Section originalSection, Section section) {
            if (section.getDistance().compareTo(originalSection.getDistance()) != -1) {
                throw new CreationValidationException(String.format("구간의 길이가 기존 구간 보다 작아야합니다. 기존 구간 길이:%s 새 구간 길이:%s", originalSection.getDistance(), section.getDistance()));
            }
        }
    }

    private boolean checkDownStationsContains(Station station) {
        return getDownStations().contains(station);
    }

    private boolean checkUpStationsContains(Station station) {
        return getUpStations().contains(station);
    }
}
