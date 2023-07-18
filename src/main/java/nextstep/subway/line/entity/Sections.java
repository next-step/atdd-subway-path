package nextstep.subway.line.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.station.entity.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {

    private final int FIRST_IDX = 0;

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
        stations.add(getFirstStation());
        sections.forEach(section -> stations.add(section.getDownStation()));
        return stations;
    }

    public void addSection(Section section) {
        Validator.validateEnrollment(this, section);
        sections.add(section);
    }

    public void remove(Station station) {
        Validator.validateDeletion(this, station);
        sections.remove(getLastSection());
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public Station getFirstStation() {
        return sections.get(FIRST_IDX).getUpStation();
    }

    public Station getLastStation() {
        return getLastSection().getDownStation();
    }

    private int size() {
        return sections.size();
    }

    private boolean hasStation(Station downStation) {
        return getStations().contains(downStation);
    }

    private static class Validator {
        static void validateEnrollment(Sections sections, Section section) {
            validateNewSectionUpStationEqualsLineDownStation(sections, section);
            validateNewSectionDownStationIsNewcomer(sections, section);
        }

        private static void validateDeletion(Sections sections, Station Station) {
            validateDeletionEqualsLineDownStation(sections, Station);
            validateTwoMoreSectionExists(sections);
        }

        private static void validateNewSectionDownStationIsNewcomer(Sections sections, Section section) {
            if (sections.hasStation(section.getDownStation())) {
                throw new IllegalArgumentException("새로운 구간의 하행역이 해당 노선에 등록되어있는 역임.");
            }
        }

        private static void validateNewSectionUpStationEqualsLineDownStation(Sections sections, Section section) {
            if (!section.getUpStation().equalsId(sections.getLastStation())) {
                throw new IllegalArgumentException("새로운 구간의 상행 역이 해당 노선에 등록되어있는 하행 종착역이 아님.");
            }
        }

        private static void validateTwoMoreSectionExists(Sections sections) {
            if (sections.size() == 1) {
                throw new IllegalArgumentException("상행 종점역과 하행 종점역만 존재합니다.");
            }
        }

        private static void validateDeletionEqualsLineDownStation(Sections sections, Station station) {
            if (!sections.getLastStation().equalsId(station)) {
                throw new IllegalArgumentException(String.format("노선의 마지막 역이 아닙니다. 역id:%s", station.getId()));
            }
        }
    }
}
