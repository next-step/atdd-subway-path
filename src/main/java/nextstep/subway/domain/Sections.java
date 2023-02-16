package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(
        mappedBy = "line",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true
    )
    private List<Section> elements = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> elements) {
        this.elements = elements;
    }

    public Sections(Section... elements) {
        this(
            Arrays.stream(elements)
                .collect(Collectors.toList())
        );
    }

    public void add(Section section) {
        if (elements.size() == 0) {
            elements.add(section);
            return;
        }

        if (elements.stream().anyMatch(it -> it.containsLastStation(section))) {
            throw new IllegalArgumentException("새로운 구간의 하행역은 해당 노선에 등록되어있는 역일 수 없습니다");
        }

        final Section lastSection = findLastSection();
        if (lastSection.isConnectable(section)) {
            elements.add(section);
            return;
        }
        throw new IllegalArgumentException(
            String.format(
                "새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다. lastSection: %s, newSection: %s",
                lastSection,
                section
            )
        );
    }

    public void remove(Long stationId) {
        if (elements.size() == 1) {
            throw new IllegalArgumentException();
        }

        final Section lastSection = findLastSection();

        if (lastSection.isSameDownStation(stationId)) {
            elements.remove(lastSection);
            return;
        }
        throw new IllegalArgumentException();
    }

    private Section findLastSection() {
        return elements.get(elements.size() - 1);
    }

    public List<Section> getElements() {
        return Collections.unmodifiableList(elements);
    }
}
