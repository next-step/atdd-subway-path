package nextstep.subway.domain.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public boolean isSectionsNullOrEmpty() {
        return sections == null || sections.isEmpty();
    }

    public boolean hasMoreThanOne(Long stationId) {
        if (isSectionsNullOrEmpty()) {
            return false;
        }

        return sections.size() > 1;
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public void removeSection() {
        sections.remove(sections.size() - 1);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.add(Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build());
    }
}
