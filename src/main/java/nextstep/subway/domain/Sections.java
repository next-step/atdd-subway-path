package nextstep.subway.domain;

import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Embeddable
public class Sections {
    private static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("id")
    private List<Section> sections = new ArrayList<>();

    public boolean isEmpty() {
        return sections == null || sections.isEmpty();
    }

    public void add(Section section) {
        checkAddValidation(section.getUpStation().getId(), section.getDownStation().getId());
        sections.add(section);
    }

    public int count() {
        return sections.size();
    }

    public void removeLastStation(Station station) {
        if (isEmpty())
            throw new IllegalArgumentException();

        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public List<Station> getStationList() {
        if (isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = sections.stream().map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());
        return stations;
    }

    private void checkAddValidation(Long upStationId, Long downStationId) {
//        if (!isEmpty()) {
//            checkUpStationNone(upStationId);
//            checkDownStationNone(downStationId);
//            checkUpDownStation(upStationId);
//        }
    }

    private void checkUpStationNone(Long upStationId) {
        Assert.isTrue(sections.stream().noneMatch(section ->
                Objects.equals(section.getUpStation().getId(), upStationId)

        ), "새로운 구간의 상행역은 현재 등록되어있는 상행역일 수 없다.");
    }

    private void checkDownStationNone(Long downStationId) {
        Assert.isTrue(sections.stream().noneMatch(section ->
                Objects.equals(section.getUpStation().getId(), downStationId) || Objects.equals(section.getDownStation().getId(), downStationId)
        ), "새로운 구간의 하행역은 현재 등록되어있는 역일 수 없다.");
    }

    private void checkUpDownStation(Long upStationId) {
        Assert.isTrue(sections.stream().anyMatch(section ->
                Objects.equals(section.getDownStation().getId(), upStationId)
        ), "구간의 상행역은 현재 등록되어있는 하행 종점역이어야 합니다.");
    }

    private void checkRemoveOnlyStation() {
        Assert.isTrue(sections.size() != MIN_SECTION_SIZE, "지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 " + MIN_SECTION_SIZE + "개인 경우) 역을 삭제할 수 없다.");
    }

    private void checkRemoveDownStation(Long downStationId) {
        Assert.isTrue(Objects.equals(sections.get(sections.size() - 1).getDownStation().getId(), downStationId)
                , "지하철 노선에 등록된 마지막 역(하행 종점역)만 제거할 수 있다.");
    }
}
