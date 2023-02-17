package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.SubwayRuntimeException;
import nextstep.subway.exception.message.SubwayErrorCode;
import org.springframework.util.ObjectUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

         // 기존 구간의 하행역 기준으로 새로운 구간을 등록
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
            throw new SubwayRuntimeException(SubwayErrorCode.DUPLICATE_SECTION.getMessage());
        }

        /*
         * 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
         */
        if ((isContainStation(newSection.getUpStation()) || isContainStation(newSection.getDownStation())) == false) {
            throw new SubwayRuntimeException(SubwayErrorCode.NOT_CONTAIN_STATION.getMessage());
        }
    }

    // 이미 등록된 상행역인지 검증
    private boolean isAlreadyRegisterUpStation(Section newSection) {
        return sections.stream().anyMatch(section -> section.isSameUpStation(newSection));
    }

    // 이미 등록된 하행역인지 검증
    private boolean isAlreadyRegisterDownStation(Section newSection) {
        return sections.stream().anyMatch(section -> section.isSameDownStation(newSection));
    }

    // 해당 역이 포함되어 있는지 검증
    private boolean isContainStation(Station newSection) {
        return sections.stream()
                .anyMatch(section -> section.isContainStation(newSection));
    }

    // 등록되어 있는 상행역과 새로운 구간의 하행역이 같은지 검증 (구간 연결 [새로운 구간이 앞으로 붙는경우])
    private boolean isConnectDownStation(Section newSection) {
        return getUpStation().equals(newSection.getDownStation());
    }

    // 등록되어 있는 하행역과 새로운 구간의 상행역이 같은지 검증 (구간연결 [새로운 구간이 뒤로 붙는경우])
    private boolean isConnectUpStation(Section newSection) {
        return getDownStation().equals(newSection.getUpStation());
    }

    private Station getUpStation() {
        Station cur = null;
        Section next = sections.get(0);

        while (!ObjectUtils.isEmpty(next)) {
            cur = next.getUpStation();
            next = findSectionByDownStation(next.getUpStation());
        }

        return cur;
    }

    private Section findSectionByDownStation(Station downStation) {
        for (Section section : sections) {
            if (section.getDownStation().equals(downStation)) {
                return section;
            }
        }

        return null;
    }

    private Station getDownStation() {
        Station cur = null;
        Section next = sections.get(0);

        while (!ObjectUtils.isEmpty(next)) {
            cur = next.getDownStation();
            next = findSectionByUpStation(next.getDownStation());
        }

        return cur;
    }

    private Section findSectionByUpStation(Station upStation) {
        for (Section section : sections) {
            if (section.getUpStation().equals(upStation)) {
                return section;
            }
        }

        return null;
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
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        stations.add(getUpStation());
        Section next = findSectionByUpStation(getUpStation());
        while (!ObjectUtils.isEmpty(next)) {
            stations.add(next.getDownStation());
            next = findSectionByUpStation(next.getDownStation());
        }

        return stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    private Section lastSection() {
        return sections.get(sections.size() - 1);
    }

    public void delete(final Station station) {
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new SubwayRuntimeException(SubwayErrorCode.CANNOT_DELETE_LAST_STATION.getMessage());
        }

        Section lastSection = lastSection();
        if (!lastSection.isDownStation(station)) {
            throw new SubwayRuntimeException(SubwayErrorCode.DELETE_LAST_SECTION.getMessage());
        }

        sections.remove(lastSection);
    }
}
