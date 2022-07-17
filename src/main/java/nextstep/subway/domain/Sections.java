package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Sections {

    private static final int EMPTY_VALUE = 0;
    private static final int ONE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    public List<Station> getStations() {
        return values.stream()
                .map(Section::stations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Section section) {
        values.add(section);
    }

    public void delete(Station station) {
        Section lastSection = findLastSection();
        if (lastSection == null) {
            throw new IllegalStateException();
        }
        if (!lastSection.isMatchDownStation(station)) {
            throw new IllegalArgumentException();
        }
        values.remove(lastSection);
    }

    private Section findLastSection() {
        if (lastIndex() < EMPTY_VALUE) {
            return null;
        }
        return values.get(lastIndex());
    }

    private int lastIndex() {
        return values.size() - ONE;
    }

}
