package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.SectionDeleteMinSizeException;
import nextstep.subway.exception.SectionExistException;
import nextstep.subway.exception.SectionNotFoundException;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Embeddable
public class Sections {

    private static final int SECTIONS_MIN_SIZE = 1;

    @OneToMany(fetch = LAZY, mappedBy = "line", cascade = ALL, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Line line, Station upStation, Station downStation, int distance) {
        values.add(new Section(line, upStation, downStation, distance));
    }

    public void add(Section section) {
        if (isExistedDownStation(section)) {
            throw new SectionExistException();
        }
        values.add(section);
    }

    public void remove(Section section) {
        if (values.size() == SECTIONS_MIN_SIZE) {
            throw new SectionDeleteMinSizeException();
        }
        values.remove(section);
    }

    public List<Station> getStations(Station station) {
        List<Station> stations = new ArrayList<>(Arrays.asList(station));
        while (hasNext(station)) {
            Section section = next(station);
            stations.add(section.getDownStation());
            station = section.getDownStation();
        }
        return stations;
    }

    public boolean isExistedDownStation(Section addSection) {
        return values.stream().anyMatch(section -> section.isExistedDownStation(addSection));
    }

    public Section findSectionByDownStation(Station station) {
        return values.stream()
                .filter(section -> section.isDown(station))
                .findAny()
                .orElseThrow(SectionNotFoundException::new);
    }

    private boolean hasNext(Station station) {
        return values.stream().anyMatch(section -> section.isUp(station));
    }

    private Section next(Station station) {
        return values.stream()
                .filter(section -> section.isUp(station))
                .findAny()
                .orElseThrow(SectionNotFoundException::new);
    }

}
