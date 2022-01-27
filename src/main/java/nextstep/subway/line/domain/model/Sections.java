package nextstep.subway.line.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.model.Station;

@Embeddable
public class Sections {
    @OneToMany(
        mappedBy = "line",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        orphanRemoval = true
    )
    private List<Section> values = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        values.add(section);
    }

    public void remove(long stationId) {
        if (!isRemovable(stationId)) {
            throw new IllegalArgumentException();
        }
        values.removeIf(
            eachSection -> eachSection.matchDownStationId(stationId)
        );
    }

    private boolean isRemovable(long downStationId) {
        return values.get(values.size() - 1)
                     .matchDownStationId(downStationId);
    }

    public List<Station> toStations() {
        return Stream.concat(values.stream().map(Section::getUpStation),
                             values.stream().map(Section::getDownStation)
                     )
                     .distinct()
                     .collect(Collectors.toList());
    }
}
