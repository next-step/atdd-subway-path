package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSections(final Line line, final Station upStation, final Station downStation, final int distance) {
        this.sections.add(new Section(line, upStation, downStation, distance));
    }

    public List<Section> sections() {
        return sections;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Station> stations = sections.stream().map(Section::getDownStation)
            .collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());
        return stations;
    }

    public void removeStations(final Station station) {
        if (sections.isEmpty()) {
            throw new IllegalStateException("등록된 구간이 없습니다.");
        }

        if (isNotEqualLastDownStation(station)) {
            throw new IllegalArgumentException("하행 종점역 정보가 다릅니다.");
        }

        sections.remove(getLastIndex());
    }

    private boolean isNotEqualLastDownStation(final Station station) {
        return !sections.get(getLastIndex()).getDownStation().equals(station);
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }
}
