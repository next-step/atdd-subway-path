package nextstep.subway.line.domain;

import nextstep.subway.exceptions.AlreadyExistsEntityException;
import nextstep.subway.exceptions.NotEqualsStationException;
import nextstep.subway.exceptions.OnlyOneSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {
    public static final String ONLY_DOWN_STATION_CAN_DELETED = "하행 종점역만 삭제가 가능합니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sectionList = new ArrayList<>();

    public Sections() {
    }

    public Sections(Line line, List<Section> sections) {
        sections.forEach(section -> this.addSection(line, section));
    }

    public static Sections of(Line line, Section... sections) {
        return new Sections(line, Arrays.asList(sections));
    }

    public void addSection(Line line, Section section) {
        boolean isPresent = sectionList.stream()
                .anyMatch(section::equals);

        if (!isPresent) {
            sectionList.add(section);
            section.updateLine(line);
        }
    }

    public void isValidSection(Section newSection) {
        if (isEmpty()) {
            return;
        }

        Station downStation = getDownStation();
        if (!newSection.getUpStation().equals(downStation)) {
            throw new NotEqualsStationException();
        }

        if (isExistsDownStation(newSection.getDownStation())) {
            throw new AlreadyExistsEntityException();
        }
    }

    private boolean isExistsDownStation(Station station) {
        return sectionList.stream()
                .anyMatch(section -> isEqualDownAndUpStation(station, section));
    }

    private boolean isEqualDownAndUpStation(Station station, Section section) {
        return Objects.equals(section.getUpStation(), station) || Objects.equals(section.getDownStation(), station);
    }

    public boolean isEmpty() {
        return sectionList.isEmpty();
    }

    public List<StationResponse> getStationsAll() {
        if (isEmpty()) {
            return Arrays.asList();
        }
        List<StationResponse> responses = new ArrayList<>();
        responses.add(StationResponse.of(sectionList.get(0).getUpStation()));

        sectionList.forEach(section -> responses.add(StationResponse.of(section.getDownStation())));

        return responses;
    }


    public Section getLastSection() {
        return sectionList.get(sectionList.size() - 1);
    }

    public Station getDownStation() {
        return sectionList.get(sectionList.size() - 1).getDownStation();
    }

    public void deleteLastSection(Long stationId) {
        isValidDeleteSection(stationId);
        sectionList.remove(getLastSection());
    }

    private void isValidDeleteSection(Long stationId) {
        Section lastSection = getLastSection();
        boolean isEqualDownStationAndTarget = lastSection.getDownStation()
                .getId()
                .equals(stationId);

        if (!isEqualDownStationAndTarget) {
            throw new IllegalArgumentException(ONLY_DOWN_STATION_CAN_DELETED);
        }

        if (sectionList.size() == 1) {
            throw new OnlyOneSectionException();
        }
    }
}
