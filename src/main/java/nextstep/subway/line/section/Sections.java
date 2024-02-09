package nextstep.subway.line.section;


import nextstep.subway.Exception.ErrorCode;
import nextstep.subway.Exception.LineException;
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
            throw new LineException(ErrorCode.CANNOT_ADD_SECTION, "이미 등록된 구간입니다.");
        }

        if (isMiddleSection(newSection)) {
            addMiddleSection(newSection);
            return;
        }

        if (isFirstSection(newSection)) {
            addFirstSection(newSection);
            return;
        }

        if (isLastSection(newSection)) {
            addLastSection(newSection);
            return;
        }

        throw new LineException(ErrorCode.CANNOT_ADD_SECTION, "등록할 수 없는 구간입니다.");
    }

    private boolean isDuplicatedSection(Section newSection) {
        return sections.stream().anyMatch(section -> section.matchStations(newSection));
    }

    private void addMiddleSection(Section newSection) {
        Section originalSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .orElseThrow();

        int index = sections.indexOf(originalSection);
        sections.add(index - 1 == -1 ? 0 : index - 1, newSection);
        originalSection.separateFrom(newSection);
    }

    private void addFirstSection(Section newSection) {
        if (isDuplicatedUpStation(newSection.getUpStation())) {
            throw new LineException(ErrorCode.CANNOT_ADD_SECTION, "추가할 역이 이미 존재합니다.");
        }
        sections.add(0, newSection);
    }

    private boolean isDuplicatedUpStation(Station upStation) {
        return sections.stream().anyMatch(section -> section.getDownStation().equals(upStation));
    }

    private void addLastSection(Section newSection) {
        if (isDuplicatedDownStation(newSection.getDownStation())) {
            throw new LineException(ErrorCode.CANNOT_ADD_SECTION, "추가할 역이 이미 존재합니다.");
        }
        sections.add(newSection);
    }

    private boolean isDuplicatedDownStation(Station downStation) {
        return sections.stream().anyMatch(section -> section.getUpStation().equals(downStation));
    }

    private boolean isMiddleSection(Section newSection) {
        return sections.stream().anyMatch(section -> section.getUpStation().equals(newSection.getUpStation()));
    }

    private boolean isFirstSection(Section section) {
        return firstSection().getUpStation().equals(section.getDownStation());
    }

    private boolean isLastSection(Section section) {
        return lastSection().getDownStation().equals(section.getUpStation());
    }

    public void deleteSection(Long stationId) {
        if (sections.size() == 1)
            throw new LineException(ErrorCode.CANNOT_DELETE_SECTION, "구간이 한개인 경우 삭제할 수 없습니다.");
        if (!isLastDownStation(stationId)) {
            throw new LineException(ErrorCode.CANNOT_DELETE_SECTION, "삭제 역이 하행 종점역이 아닙니다.");
        }
        sections.remove(lastSection());
    }

    private boolean isLastDownStation(Long stationId) {
        return lastSection().getDownStation().match(stationId);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}
