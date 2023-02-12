package nextstep.subway.domain;

import nextstep.subway.exception.SubwayRestApiException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorResponseEnum.ERROR_ADD_SECTION_INVAILD_DISTANCE;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_ADD_SECTION_INVAILD_STATION;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_DELETE_SECTION_COUNT_LINE;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_DELETE_SECTION_NO_LAST_SECTION_LINE;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_NO_FOUND;


@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addFront(Section section) {
        isValidationSection(section);

        Section standardSection = this.sections.stream()
                .filter(a -> section.isSameDownStation(a.getUpStation()))
                .findFirst()
                .orElse(null);;

        this.sections.add(this.sections.indexOf(standardSection), section);
    }

    public void addMiddle(Section section) {
        isValidationSection(section);

        Section standardSection = this.sections.stream()
                .filter(a -> section.isSameUpStation(a.getUpStation()))
                .findFirst()
                .orElse(null);

        isValidationSectionDistance(standardSection, section);

        int standardIndex = this.sections.indexOf(standardSection);

        Section newSection = Section.builder()
                .line(section.getLine())
                .upStation(section.getDownStation())
                .downStation(standardSection.getDownStation())
                .distance(standardSection.getDistance() - section.getDistance())
                .build();

        standardSection.change(standardSection.getUpStation(), section.getDownStation(), section.getDistance());

        this.sections.set(standardIndex, standardSection);
        this.sections.add(standardIndex+1, newSection);
    }

    public void addBack(Section section) {
        isValidationSection(section);

        this.sections.add(section);
    }

    public void remove(Station station) {
        Section section = this.sections.stream()
                .filter(a -> station.equals(a.getDownStation()))
                .findFirst()
                .orElse(null);

        this.validationDeleteSection(section);

        this.sections.remove(this.sections.size()-1);
    }

    public List<Station> getStations() {
        Section topSection = getTopSection();

        List<Station> stations = new ArrayList<>();
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

    public boolean isEmpty() {
        return this.sections.size() <= 0 ? true : false;
    }

    private Section getTopSection() {
        List<Station> stations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return this.sections.stream()
                .filter(a -> a.isContaionsUpStations(stations) == false)
                .findFirst()
                .orElse(null);
    }

    private void validationDeleteSection(Section section) {
        if (null == section) {
            throw new SubwayRestApiException(ERROR_NO_FOUND);
        }

        if (this.isNotValidSectionCount()) {
            throw new SubwayRestApiException(ERROR_DELETE_SECTION_COUNT_LINE);
        }

        if (!this.isSameLastSection(section)) {
            throw new SubwayRestApiException(ERROR_DELETE_SECTION_NO_LAST_SECTION_LINE);
        }
    }

    private boolean isNotValidSectionCount() {
        return this.sections.size() <= 1;
    }

    private boolean isSameLastSection(Section section) {
        return this.getLastSection().equals(section);
    }

    private Section getLastSection() {
        return this.sections.get(this.sections.size()-1);
    }

    private void isValidationSection(Section section) {
        if (this.sections.size() > 0 && isExistUpOrDownStation(section)){
            throw new SubwayRestApiException(ERROR_ADD_SECTION_INVAILD_STATION);
        }
    }

    private boolean isExistUpOrDownStation (Section section) {
        int existStationCnt = this.getStations().stream().filter(a -> a.equals(section.getUpStation())
                || a.equals(section.getDownStation())).collect(Collectors.toList()).size();
        return existStationCnt != 1;
    }

    private void isValidationSectionDistance(Section standardSection, Section section) {
        if (standardSection.getDistance() <= section.getDistance()) {
            throw new SubwayRestApiException(ERROR_ADD_SECTION_INVAILD_DISTANCE);
        }
    }
}
