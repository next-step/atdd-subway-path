package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.SectionAddException;
import nextstep.subway.exception.SectionDeleteException;
import nextstep.subway.exception.SectionDeleteMinSizeException;
import nextstep.subway.exception.StationNotExistException;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.OneToOne;
import java.util.List;

@Embeddable
public class LineStationDetail {

    private static final int SECTIONS_MIN_SIZE = 1;

    @OneToOne
    private Station startStation;

    @OneToOne
    private Station endStation;

    @Embedded
    private Sections sections;

    protected LineStationDetail() {
    }

    public LineStationDetail(Line line, Station upStation, Station downStation, int distance) {
        this.startStation = upStation;
        this.endStation = downStation;
        this.sections = new Sections(line, upStation, downStation, distance);
    }

    public void addSection(Section section) {
        if (section.isUp(endStation)) {
            endStation = section.getDownStation();
        }
        if (section.isDown(startStation)) {
            startStation = section.getUpStation();
        }
        sections.add(section);
    }

    public void removeSection(Station station) {
        if (sections.size() == SECTIONS_MIN_SIZE) {
            throw new SectionDeleteMinSizeException();
        }
        if (endStation.equals(station)) {
            Section section = sections.findSectionByDownStation(station);
            endStation = section.getUpStation();
            sections.removeTerminus(section);
            return;
        }
        if (startStation.equals(station)) {
            Section section = sections.findSectionByUpStation(station);
            startStation = section.getDownStation();
            sections.removeTerminus(section);
            return;
        }
        sections.remove(sections.findSectionByDownStation(station));
    }

    public List<Station> getStations() {
        return sections.getStations(startStation);
    }

    public List<Section> getSections() {
        return sections.getSections(startStation);
    }

}
