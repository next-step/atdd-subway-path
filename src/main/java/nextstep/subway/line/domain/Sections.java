package nextstep.subway.line.domain;

import nextstep.subway.line.exception.SectionConnectException;
import nextstep.subway.line.exception.SectionDisconnectException;
import nextstep.subway.line.exception.SectionsStateException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateConnection(section);
        if (isLastStation(section.getUpStation())) {
            sections.add(section);
            return;
        }
        if (isFirstStation(section.getDownStation())) {
            sections.add(0, section);
            return;
        }
        connectMiddle(section);
    }

    private void validateConnection(final Section section) {
        final boolean containsDownStation = containsStation(section.getDownStation());
        final boolean containsUpStation = containsStation(section.getUpStation());
        if (containsDownStation && containsUpStation) {
            throw new SectionConnectException("생성할 구간이 이미 해당 노선에 포함되어 있습니다.");
        }
        if (!containsDownStation && !containsUpStation) {
            throw new SectionConnectException("생성할 구간과 연결 가능한 구간이 존재하지 않습니다.");
        }
    }

    private void connectMiddle(final Section section) {
        if (getDistance() <= section.getDistance()) {
            throw new SectionConnectException("가운데에 생성할 구간의 길이가 해당 노선의 총 길이보다 길거나 같을 수 없습니다.");
        }

        final Section upSection = findSectionByUpStation(section.getUpStation());
        upSection.shorten(section);

        sections.add(sections.indexOf(upSection), section);
    }

    public void disconnect(final Station station) {
        validateDisconnection(station);
        if (isLastStation(station)) {
            sections.remove(sections.size() - 1);
            return;
        }
        if (isFirstStation(station)) {
            sections.remove(0);
            return;
        }
        disconnectMiddle(station);
    }

    private void validateDisconnection(final Station station) {
        if (sections.size() <= MINIMUM_SECTION_COUNT) {
            throw new SectionDisconnectException("더이상 구간을 제거할 수 없습니다.");
        }

        if (!containsStation(station)) {
            throw new SectionDisconnectException("제거할 역이 존재하지 않습니다.");
        }
    }

    private void disconnectMiddle(final Station station) {
        final Section upSection = findSectionByDownStation(station);
        final Section downSection = findSectionByUpStation(station);
        downSection.extend(upSection);

        sections.remove(upSection);
    }

    private boolean containsStation(final Station station) {
        return sections.stream().anyMatch(section -> section.contains(station));
    }

    private boolean isLastStation(final Station targetStation) {
        return getLastSection().getDownStation().equals(targetStation);
    }

    private boolean isFirstStation(final Station targetStation) {
        return getFirstSection().getUpStation().equals(targetStation);
    }

    private Section getLastSection() {
        if (sections.isEmpty()) {
            throw new SectionsStateException("Sections 가 비어있습니다.");
        }
        return sections.get(sections.size() - 1);
    }

    private Section getFirstSection() {
        if (sections.isEmpty()) {
            throw new SectionsStateException("Sections 가 비어있습니다.");
        }
        return sections.get(0);
    }

    private Section findSectionByUpStation(final Station upStation) {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(() -> new SectionsStateException("해당 상행역을 가진 구간을 찾을 수 없습니다."));
    }

    private Section findSectionByDownStation(final Station downStation) {
        return sections.stream()
                .filter(s -> s.getDownStation().equals(downStation))
                .findFirst()
                .orElseThrow(() -> new SectionsStateException("해당 하행역을 가진 구간을 찾을 수 없습니다."));
    }
}
