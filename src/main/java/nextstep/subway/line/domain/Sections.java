package nextstep.subway.line.domain;

import nextstep.subway.line.exception.SectionConnectException;
import nextstep.subway.line.exception.SectionDisconnectException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections implements Iterable<Section> {
    private static final int MINIMUM_SECTION_COUNT = 1;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new LinkedList<>();

    protected Sections() {
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }

    public int getDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }

    public List<Station> getStations() {
        return sections.stream()
                .sorted()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public void connect(final Section section) {
        validateSectionConnection(section);
        if (isConnectToLastSection(section)) {
            this.sections.add(section);
            return;
        }
        if (isConnectToFirstSection(section)) {
            this.sections.add(0, section);
            return;
        }
        connectMiddle(section);
    }

    public int getLastSectionDistance() {
        return getLastSection()
                .map(Section::getDistance)
                .orElse(0);
    }

    public void disconnect(final Station station) {
        validateSectionDisconnection(station);
        if (isDisconnectLastStation(station)) {
            this.sections.remove(sections.size() - 1);
            return;
        }

        final Section upSection = findSectionByDownStation(station);
        final Section downSection = findSectionByUpStation(station);
        downSection.extend(upSection);

        this.sections.remove(upSection);
    }



    private void connectMiddle(final Section section) {
        if (getDistance() <= section.getDistance()) {
            throw new SectionConnectException("가운데에 생성할 구간의 길이가 해당 노선의 총 길이보다 길거나 같을 수 없습니다.");
        }

        final Section upSection = findSectionByUpStation(section.getUpStation());
        upSection.shorten(section);

        this.sections.add(sections.indexOf(upSection), section);
    }

    private void validateSectionDisconnection(final Station station) {
        if (sections.size() <= MINIMUM_SECTION_COUNT) {
            throw new SectionDisconnectException("더이상 구간을 제거할 수 없습니다.");
        }

        if (!containsStation(station)) {
            throw new SectionDisconnectException("제거할 역이 존재하지 않습니다.");
        }
    }

    private void validateSectionConnection(final Section section) {
        if (sections.isEmpty()) {
            return;
        }

        final boolean containsDownStation = containsStation(section.getDownStation());
        final boolean containsUpStation = containsStation(section.getUpStation());
        if (containsDownStation && containsUpStation) {
            throw new SectionConnectException("생성할 구간이 이미 해당 노선에 포함되어 있습니다.");
        }
        if (!containsDownStation && !containsUpStation) {
            throw new SectionConnectException("생성할 구간과 연결 가능한 구간이 존재하지 않습니다.");
        }
    }

    private boolean containsStation(final Station station) {
        return sections.stream().anyMatch(section -> section.contains(station));
    }

    private boolean isConnectToLastSection(final Section section) {
        return getLastDownStation()
                .map(station -> station.equals(section.getUpStation()))
                .orElse(true);
    }

    private boolean isConnectToFirstSection(final Section section) {
        return getFirstUpStation()
                .map(station -> station.equals(section.getDownStation()))
                .orElse(true);
    }

    private boolean isDisconnectLastStation(final Station targetStation) {
        return getLastDownStation()
                .map(station -> station.equals(targetStation))
                .orElse(true);
    }

    private Optional<Station> getLastDownStation() {
        return getLastSection().map(Section::getDownStation);
    }

    private Optional<Station> getFirstUpStation() {
        return getFirstSection().map(Section::getUpStation);
    }

    private Optional<Section> getLastSection() {
        if (sections.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(sections.get(sections.size() - 1));
    }

    private Optional<Section> getFirstSection() {
        if (sections.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(sections.get(0));
    }

    private Section findSectionByUpStation(final Station upStation) {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(() -> new SectionConnectException("해당 상행역을 가진 구간을 찾을 수 없습니다."));
    }

    private Section findSectionByDownStation(final Station downStation) {
        return sections.stream()
                .filter(s -> s.getDownStation().equals(downStation))
                .findFirst()
                .orElseThrow(() -> new SectionConnectException("해당 하행역을 가진 구간을 찾을 수 없습니다."));
    }
}
