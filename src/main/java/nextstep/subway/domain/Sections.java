package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public void add(Section other) {
        if (elements.isEmpty()) {
            elements.add(other);
            return;
        }

        validSection(other);

        final Optional<Section> upSection = elements.stream()
            .filter(it -> it.containsUpStation(other))
            .findFirst();
        final Optional<Section> downSection = upSection.flatMap(
            it -> elements.stream()
                .filter(it::isDownConnected)
                .findFirst()
        );

        if (upSection.isEmpty() || downSection.isEmpty()) {
            elements.add(other);
            return;
        }

        final Section section = downSection.get();
        elements.remove(section);

        final Section nextSection = section.nextSection(other);
        elements.add(nextSection);
        elements.add(other);
    }

    private void validSection(Section other) {
        if (elements.stream().noneMatch(it -> it.containsStation(other))) {
            throw new IllegalArgumentException(
                String.format("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음 Section: %s", other)
            );
        }

        if (
            elements.stream().anyMatch(it -> it.containsUpStation(other)) &&
            elements.stream().anyMatch(it -> it.containsDownStation(other))
        ) {
            throw new IllegalArgumentException(
                String.format("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음 Section: %s", other)
            );
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sections)) {
            return false;
        }
        Sections sections = (Sections) o;
        return Objects.equals(elements, sections.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        return "Sections{" +
            "elements=" + elements +
            '}';
    }
}
