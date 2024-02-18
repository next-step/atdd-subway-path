package nextstep.subway.line.section;


import nextstep.subway.Exception.ErrorCode;
import nextstep.subway.Exception.SubwayException;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> get() {
        return sections;
    }

    public List<Station> allStations() {
        return sections.stream().flatMap(section -> section.stations().stream()).distinct().collect(Collectors.toList());
    }

    private Section firstSection() {
        return sections.get(0);
    }

    private Section lastSection() {
        return sections.get(sections.size() - 1);
    }

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        if (isDuplicatedSection(newSection)) {
            throw new SubwayException(ErrorCode.CANNOT_ADD_SECTION, "이미 등록된 구간입니다.");
        }

        if (isFirstAdded(newSection)) {
            addInFirst(newSection);
            return;
        }

        if (isLastAdded(newSection)) {
            addInLast(newSection);
            return;
        }

        addInMiddle(newSection);
    }

    private boolean isDuplicatedSection(Section newSection) {
        return sections.stream().anyMatch(section -> section.matchStations(newSection));
    }

    private boolean isFirstAdded(Section section) {
        return firstSection().getUpStation().equals(section.getDownStation());
    }

    private boolean isLastAdded(Section section) {
        return lastSection().getDownStation().equals(section.getUpStation());
    }

    private void addInFirst(Section newSection) {
        if (isDuplicatedUpStation(newSection.getUpStation())) {
            throw new SubwayException(ErrorCode.CANNOT_ADD_SECTION, "추가할 역이 이미 존재합니다.");
        }
        sections.add(0, newSection);
    }

    private boolean isDuplicatedUpStation(Station upStation) {
        return sections.stream().anyMatch(section -> section.getDownStation().equals(upStation));
    }

    private void addInLast(Section newSection) {
        if (isDuplicatedDownStation(newSection.getDownStation())) {
            throw new SubwayException(ErrorCode.CANNOT_ADD_SECTION, "추가할 역이 이미 존재합니다.");
        }
        sections.add(newSection);
    }

    private void addInMiddle(Section newSection) {
        if (isDuplicatedDownStation(newSection.getDownStation())) {
            throw new SubwayException(ErrorCode.CANNOT_ADD_SECTION, "추가할 역이 이미 존재합니다.");
        }

        Section targetSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new SubwayException(ErrorCode.CANNOT_ADD_SECTION, "추가할 구간 정보를 찾을 수 없습니다ㅏ."));

        int index = sections.indexOf(targetSection);
        sections.add(index == 0 ? 0 : index - 1, newSection);
        targetSection.moveBackFrom(newSection);
    }

    private boolean isDuplicatedDownStation(Station downStation) {
        return sections.stream().anyMatch(section -> section.getUpStation().equals(downStation));
    }


    public void deleteSection(Long stationId) {
        if (sections.size() == 1) {
            throw new SubwayException(ErrorCode.CANNOT_DELETE_SECTION, "구간이 한개인 경우 삭제할 수 없습니다.");
        }

        if (isFirstUpStation(stationId)) {
            sections.remove(0);
            return;
        }

        if (isLastDownStation(stationId)) {
            sections.remove(lastSection());
            return;
        }

        deleteMiddleSection(stationId);
    }

    private boolean isFirstUpStation(Long stationId) {
        return firstSection().getUpStation().match(stationId);
    }

    private boolean isLastDownStation(Long stationId) {
        return lastSection().getDownStation().match(stationId);
    }

    private void deleteMiddleSection(Long stationId) {
        Section targetSection = sections.stream()
                .filter(section -> section.getDownStation().match(stationId))
                .findFirst()
                .orElseThrow(() -> new SubwayException(ErrorCode.CANNOT_DELETE_SECTION, "삭제할 역 정보를 찾을 수 없습니다."));

        int targetIndex = sections.indexOf(targetSection);
        Section nextSection = sections.get(targetIndex + 1);

        targetSection.mergeWith(nextSection);
        sections.remove(nextSection);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}
