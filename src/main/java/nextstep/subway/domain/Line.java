package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    // TODO 일급 컬렉션 작성
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void updateLine(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(final Section section) {
        // 새 라인의 경우 section 검증 제외
        if (!isNewLine()) {
            LineValidator.checkSectionForAddition(this, section);
        }

        this.sections.add(section);
    }

    public void removeSection(final Section section) {
        LineValidator.checkSectionForRemove(this, section);

        this.sections.remove(section);
    }

    private boolean isNewLine() {
        return this.getSections().isEmpty();
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
