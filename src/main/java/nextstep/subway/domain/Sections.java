package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.SubwayRuntimeException;
import nextstep.subway.exception.message.SubwayErrorCode;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
@Getter
public class Sections {
    private static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        addValidate(newSection);

        /*
         * 기존 구간의 하행역 기준으로 새로운 구간을 등록
         */

        if (isConnectUpStation(newSection) || isConnectDownStation(newSection)) {
            sections.add(newSection);
            return;
        }

        // 기존 구간 A-C 에 신규 구간 A-B 를 추가하는 경우 검증
        addMiddleStation(newSection);
    }

    private void addValidate(Section newSection) {
        /*
         * 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
         */
        if (isAlreadyRegisterUpStation(newSection) && isAlreadyRegisterDownStation(newSection)) {
            throw new IllegalArgumentException("이미 등록된 구간 입니다.");
        }

        /*
         * 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
         */
        if (!(isContainUpStation(newSection) || isContainDownStation(newSection))) {
            throw new IllegalArgumentException("연결할 수 없는 구간 입니다.");
        }
    }

    // 이미 등록된 상행역인지 검증
    private boolean isAlreadyRegisterUpStation(final Section newSection) {
        return sections.stream().anyMatch(section -> section.isSameUpStation(newSection));
    }

    // 이미 등록된 하행역인지 검증
    private boolean isAlreadyRegisterDownStation(final Section newSection) {
        return sections.stream().anyMatch(section -> section.isSameDownStation(newSection));
    }

    // 상행역이 포함되어 있는지 검증
    private boolean isContainDownStation(final Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().equals(newSection.getUpStation()));
    }

    // 하행역이 포함되어 있는지 검증
    private boolean isContainUpStation(final Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(newSection.getUpStation()));
    }

    // 등록되어 있는 상행역과 새로운 구간의 하행역이 같은지 검증 (구간 연결 [새로운 구간이 앞으로 붙는경우])
    private boolean isConnectDownStation(final Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(newSection.getDownStation()));
    }

    // 등록되어 있는 하행역과 새로운 구간의 상행역이 같은지 검증 (구간연결 [새로운 구간이 뒤로 붙는경우])
    private boolean isConnectUpStation(final Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().equals(newSection.getUpStation()));
    }

    private void addMiddleStation(Section newSection) {
        for (Section section : sections) {
            if (addStation(section, newSection)) return;
        }
    }

    private boolean addStation(Section section, Section newSection) {
        return updateDownStation(section, newSection) || updateUpStation(section, newSection);
    }

    private boolean updateDownStation(Section section, Section newSection) {
        if (section.getUpStation().equals(newSection.getUpStation())) {
            sections.add(section.add(newSection.getDownStation(), section.getDistance() - newSection.getDistance()));
            return true;
        }

        return false;
    }

    private boolean updateUpStation(Section section, Section newSection) {
        if (section.getDownStation().equals(newSection.getDownStation())) {
            sections.add(section.add(newSection.getUpStation(), newSection.getDistance()));
            return true;
        }

        return false;
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        stations.add(this.getLastDownStation());

        return stations;
    }

    private Station getLastDownStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    public List<Section> getSections() {
        return sections;
    }

    private Section lastSection() {
        return sections.get(sections.size() - 1);
    }

    public void delete(final Station station) {
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new SubwayRuntimeException(SubwayErrorCode.CANNOT_DELETE_LAST_STATION);
        }

        Section lastSection = lastSection();
        if (!lastSection.isDownStation(station)) {
            throw new SubwayRuntimeException(SubwayErrorCode.ONLY_LAST_SEGMENT_CAN_BE_REMOVED);
        }

        sections.remove(lastSection);
    }
}
