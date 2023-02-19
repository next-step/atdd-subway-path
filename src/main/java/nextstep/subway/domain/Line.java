package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    @Embedded
    @Getter(AccessLevel.NONE)
    private final Sections sections = new Sections();

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * 노선 정보를 수정합니다.
     *
     * @param name  수정할 노선 이름
     * @param color 수정할 노선 색깔
     */
    public void update(final String name, final String color) {
        updateName(name);
        updateColor(color);
    }

    /**
     * 지하철 노선의 구간을 등록합니다.
     *
     * @param section 등록할 지하철 구간 정보
     */
    public void addSection(final Section section) {
        this.sections.add(section);
    }

    /**
     * 지하철 노선의 구간을 삭제합니다.
     *
     * @param station 삭제할 지하철 역 정보
     */
    public void removeSection(final Station station) {
        this.sections.remove(station);
    }

    /**
     * 지하철 노선의 구간이 비어있는지 확인합니다.
     *
     * @return 구간 목록이 비어있으면 true, 그렇지 않으면 false
     */
    public boolean isEmptySections() {
        return this.sections.isEmpty();
    }

    /**
     * 지하철 노선에 포함된 역 목록을 조회합니다.
     *
     * @return 지하철 노선에 포함된 졍렬된 역 목록 반환
     */
    public List<Station> getAllStations() {
        return this.sections.getAllStations();
    }


    private void updateName(final String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    private void updateColor(final String color) {
        if (color != null && !color.isBlank()) {
            this.color = color;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
