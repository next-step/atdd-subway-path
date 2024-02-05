package nextstep.subway.domain;

import nextstep.subway.exception.ApplicationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private static final long NON_SECTION_DISTANCE = 0;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSize(sections);
        this.sections = new ArrayList<>(sections);
    }

    private void validateSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간이 존재하지 않습니다.");
        }
    }

    public void validateRegisterStationBy(Station upStation, Station downStation) {
        validateUpStation(upStation);
        validateDownStation(downStation);
    }

    private void validateUpStation(Station upStation) {
        if (sections.stream().anyMatch(section -> section.isUpStation(upStation))) {
            throw new ApplicationException("새로운 구간의 상행역은 노선의 하행 종점역에만 생성할 수 있습니다.");
        }
    }

    private void validateDownStation(Station downStation) {
        boolean isDownStationRegistered = sections.stream()
                .anyMatch(section -> section.matchesStation(downStation));

        if (isDownStationRegistered) {
            throw new ApplicationException("새로운 구간의 하행역은 노선에 존재하는 역에 생성할 수 없습니다.");
        }
    }

    public void validateDeleteSection(Long stationId) {
        validateSectionCount();
        validateLastSection(stationId);
    }

    private void validateSectionCount() {
        if (sections.size() == 1) {
            throw new ApplicationException("구간이 한개만 있을 경우 구간을 제거할 수 없습니다.");
        }
    }

    private void validateLastSection(Long stationId) {
        Section lastSection = findLastSection();
        if (!lastSection.isSameId(stationId)) {
            throw new ApplicationException("마지막 구간이 아닐 경우 구간을 제거할 수 없습니다.");
        }
    }

    private Section findLastSection() {
        List<Station> upStations = upStations();
        List<Station> downStations = downStations();

        Station downTerminalStation = downTerminalStation(downStations, upStations);

        return lastSection(downTerminalStation);
    }

    private List<Station> downStations() {
        return sections.stream()
                .map(Section::downStation)
                .collect(Collectors.toList());
    }

    private List<Station> upStations() {
        return sections.stream()
                .map(Section::upStation)
                .collect(Collectors.toList());
    }

    private Station downTerminalStation(List<Station> downStations, List<Station> upStations) {
        return downStations.stream()
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("하행 종점역을 찾을 수 없습니다."));
    }

    private Section lastSection(Station downTerminalStation) {
        return sections.stream()
                .filter(section -> section.downStation().equals(downTerminalStation))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("하행 종점역을 포함하는 구간을 찾을 수 없습니다."));
    }

}
