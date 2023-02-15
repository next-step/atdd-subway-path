package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
}
