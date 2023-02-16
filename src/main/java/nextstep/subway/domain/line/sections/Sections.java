package nextstep.subway.domain.line.sections;

import nextstep.subway.applicaion.line.sections.addtional.Additional;
import nextstep.subway.applicaion.line.sections.addtional.SectionAdder;
import nextstep.subway.applicaion.line.sections.addtional.SectionFrontAdder;
import nextstep.subway.applicaion.line.sections.addtional.SectionMiddleAdder;
import nextstep.subway.domain.section.Distance;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.SubwayRestApiException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorResponseEnum.ERROR_ADD_SECTION_INVAILD_STATION;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_DELETE_SECTION_COUNT_LINE;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_DELETE_SECTION_INVAILD_STATION;


@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        Additional additional = searchAdditionalSection(section);
        additional.add(this, section);
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public void remove(Station station) {
        this.validationDeleteSection(station);

        int deleteIndex = getDeleteSectionIndex(station);

        if (deleteIndex > 0) {
            Section section = this.sections.get(deleteIndex);
            Section beforeSection = this.sections.get(getLastIndext(deleteIndex));

            section.change(beforeSection.getUpStation(), section.getDownStation(), new Distance(beforeSection.addDistance(section.getDistance())));

            this.sections.set(getLastIndext(deleteIndex), section);
        }

        if (isLastStation(station)) {
            deleteIndex = getLastIndext(this.sections.size());
        }

        this.sections.remove(deleteIndex);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Optional<Section> opTopSection = getTopSection();

        if (!opTopSection.isPresent()) {
            return stations;
        }

        Section topSection = opTopSection.get();

        stations.add(topSection.getUpStation());

        Station downStation = topSection.getDownStation();

        for (int i = 0; i < this.sections.size(); i++) {
            Station tempStation = downStation;
            stations.add(downStation);

            Section section = this.sections.stream()
                    .filter(a -> tempStation.equals(a.getUpStation()))
                    .findFirst()
                    .orElse(null);

            if (null == section) {
                break;
            }

            downStation = section.getDownStation();
        }

        return stations;
    }

    public Optional<Section> getStandardSectionByDownStation(Section section) {
        return this.sections.stream()
                .filter(a -> section.isSameDownStation(a.getUpStation()))
                .findFirst();
    }

    public Optional<Section> getStandardSectionByUpStation(Section section) {
        return this.sections.stream()
                .filter(a -> section.isSameUpStation(a.getUpStation()))
                .findFirst();
    }

    public int getSectionIndex(Section section) {
        return this.sections.indexOf(section);
    }

    public void isValidationSection(Section section) {
        if (this.sections.size() > 0 && isExistUpOrDownStation(section)){
            throw new SubwayRestApiException(ERROR_ADD_SECTION_INVAILD_STATION);
        }
    }

    private boolean isExistUpOrDownStation (Section section) {
        int existStationCnt = this.getStations().stream().filter(a -> a.equals(section.getUpStation())
                || a.equals(section.getDownStation())).collect(Collectors.toList()).size();
        return existStationCnt != 1;
    }

    private int getDeleteSectionIndex(Station station) {
        Optional<Section> section = this.sections.stream()
                .filter(a -> a.isSameUpStation(station))
                .findFirst();

        if (!section.isPresent()) {
            return -1;
        }

        return this.sections.indexOf(section.get());
    }

    private boolean isLastStation(Station station) {
        return this.sections.get(getLastIndext(this.sections.size())).isSameDownStation(station);
    }

    private int getLastIndext(int size) {
        return size - 1;
    }

    private Optional<Section> getTopSection() {
        List<Station> stations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return this.sections.stream()
                .filter(a -> a.isContaionsUpStations(stations) == false)
                .findFirst();
    }

    private void validationDeleteSection(Station station) {
        if (this.isNotValidSectionCount()) {
            throw new SubwayRestApiException(ERROR_DELETE_SECTION_COUNT_LINE);
        }

        if (!this.isExistStationInLine(station)) {
            throw new SubwayRestApiException(ERROR_DELETE_SECTION_INVAILD_STATION);
        }
    }

    private boolean isExistStationInLine(Station station) {
        return this.getStations().contains(station);
    }

    private boolean isNotValidSectionCount() {
        return this.sections.size() <= 1;
    }

    private Additional searchAdditionalSection(Section section) {
        if (isPosibleFrontAddSection(section)) {
            return new SectionFrontAdder();
        }

        if (isPosibleMiddleAddSection(section)) {
            return new SectionMiddleAdder();
        }

        return new SectionAdder();
    }

    private boolean isPosibleFrontAddSection(Section section) {
        return this.sections.stream()
                .filter(a -> section.isSameDownStation(a.getUpStation()))
                .findFirst()
                .isPresent();
    }

    private boolean isPosibleMiddleAddSection(Section section) {
        return this.sections.stream()
                .filter(a -> section.isSameUpStation(a.getUpStation()))
                .findFirst()
                .isPresent();
    }
}
