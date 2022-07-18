package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> value = new ArrayList<>();

    public void addSection(Section section) {
        this.value.add(section);
    }

    public List<Section> getValue() {
        return unmodifiableList(value);
    }

    public List<Station> allStations() {
        if (value.isEmpty()) {
            return emptyList();
        }

        List<Station> stations = value.stream()
                .map(Section::getDownStation)
                .collect(toList());

        stations.add(0, value.get(0).getUpStation());

        return unmodifiableList(stations);
    }

    public void removeSection(Station station) {
        if (!lastSection().getDownStation().equals(station)) {
            throw new IllegalArgumentException("삭제하려는 역이 노선에 등록되지 않은 역이거나, 마지막 구간의 역이 아닙니다.");
        }

        value.remove(lastSection());
    }

    private Section lastSection() {
        return value.get(value.size() - 1);
    }
}
