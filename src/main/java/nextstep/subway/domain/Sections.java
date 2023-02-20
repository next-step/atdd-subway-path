package nextstep.subway.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
    private Set<Section> elements = new HashSet<>();

    protected Sections() {
    }

    public Sections(Set<Section> elements) {
        this.elements = elements;
    }

    public Sections(Section... elements) {
        this(
            Arrays.stream(elements)
                .collect(Collectors.toSet())
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
            throw new IllegalArgumentException("구간이 하나인 경우 삭제할 수 없음");
        }

        final Optional<Section> upSection = findUpSectionByStationId(stationId);
        upSection.ifPresent(it -> elements.remove(it));

        final Optional<Section> downSection = findDownSectionByStationId(stationId);
        downSection.ifPresent(it -> elements.remove(it));

        if (downSection.isPresent() && upSection.isPresent()) {
            final Section section = upSection.get().mergeSection(downSection.get());
            elements.add(section);
        }
    }

    private Optional<Section> findUpSectionByStationId(Long stationId) {
        return elements.stream()
            .filter(it -> it.isSameDownStation(stationId))
            .findFirst();
    }

    private Optional<Section> findDownSectionByStationId(Long stationId) {
        return elements.stream()
            .filter(it -> it.isSameUpStation(stationId))
            .findFirst();
    }

    public Set<Section> getElements() {
        return elements;
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
