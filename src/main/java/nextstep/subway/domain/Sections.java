package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Embeddable
public class Sections {

    private static final int FIRST_POSITION = 0;
    private static final int GAP_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        this(Collections.emptyList());
    }

    public Sections(final Line line, final Station upStation, final Station downStation, final int distance) {
        this(new Section(line, upStation, downStation, distance));
    }

    public Sections(final Section section) {
        this(new ArrayList<>(asList(section)));
    }

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(final Section section) {
        sections.add(section);
    }

    public void removeSection(final Station station) {
        validateRemoveSection(station);
        this.sections.remove(lastIndex());
    }

    private void validateRemoveSection(final Station station) {
        if (!sections.get(sections.size() - GAP_SIZE).getDownStation().equals(station)) {
            throw new IllegalArgumentException("invalid remove section");
        }
    }

    private int lastIndex() {
        return sections.size() - GAP_SIZE;
    }

    // Stations 는 공수비용 + 만들 이유 없어서 일급 컬렉션화 하지 않았습니다.
    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(FIRST_POSITION, sections.get(FIRST_POSITION).getUpStation());
        return Collections.unmodifiableList(stations);
    }
}
