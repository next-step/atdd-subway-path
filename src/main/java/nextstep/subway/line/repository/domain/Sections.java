package nextstep.subway.line.repository.domain;

import nextstep.subway.line.exception.SectionConnectException;
import nextstep.subway.line.exception.SectionDisconnectException;
import nextstep.subway.station.repository.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections implements Iterable<Section> {
    private static final int MINIMUM_SECTION_COUNT = 1;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }

    public Stream<Section> stream() {
        return sections.stream();
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public void connect(final Section section) {
        validateSectionConnection(section);
        this.sections.add(section);
    }

    public int getLastSectionDistance() {
        return getLastSection()
                .map(Section::getDistance)
                .orElse(0);
    }

    public void disconnectLastSection(final Station station) {
        validateLastSectionDisconnection(station);
        this.sections.remove(sections.size() - 1);
    }

    private void validateLastSectionDisconnection(final Station station) {
        if (sections.size() <= MINIMUM_SECTION_COUNT) {
            throw new SectionDisconnectException("더이상 구간을 제거할 수 없습니다.");
        }

        if (isNotDownStationOfLastSection(station)) {
            throw new SectionDisconnectException("마지막 구간만 제거할 수 있습니다.");
        }
    }

    private void validateSectionConnection(final Section section) {
        if (sections.isEmpty()) {
            return;
        }

        if (containsStation(section.getDownStation())) {
            throw new SectionConnectException("생성할 구간 하행역이 해당 노선에 이미 등록되어 있습니다.");
        }

        if (isNotLastSectionConnectable(section)) {
            throw new SectionConnectException("생성할 구간 상행역이 해당 노선의 하행 종점역이 아닙니다.");
        }

    }

    private boolean containsStation(final Station station) {
        return sections.stream().anyMatch(section -> section.contains(station));
    }

    private boolean isNotLastSectionConnectable(final Section section) {
        return !getLastDownStation()
                .map(station -> station.equals(section.getUpStation()))
                .orElse(false);
    }

    private boolean isNotDownStationOfLastSection(final Station targetStation) {
        return getLastDownStation().stream().noneMatch(station -> station.equals(targetStation));
    }

    private Optional<Station> getLastDownStation() {
        return getLastSection().map(Section::getDownStation);
    }

    private Optional<Section> getLastSection() {
        if (sections.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(sections.get(sections.size() - 1));
    }
}
