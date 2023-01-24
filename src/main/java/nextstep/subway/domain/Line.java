package nextstep.subway.domain;

import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineInfo lineInfo;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    protected Line() {
    }

    public Line(final LineInfo lineInfo) {
        this.lineInfo = lineInfo;
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(final Section section) {
        this.sections.add(section);
    }

    public void removeSection(final Station downStation) {

        validateSections();
        final int index = this.sections.size() - 1;
        validateMatchStation(downStation, index);

        this.sections.remove(index);
    }

    private void validateMatchStation(final Station downStation, final int index) {
        final Section section = this.sections.get(index);
        if (!section.getDownStation().equals(downStation)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateSections() {
        if (CollectionUtils.isEmpty(this.sections)) {
            throw new IllegalArgumentException();
        }
    }

    public void updateLine(final LineInfo lineInfo) {
        this.lineInfo = lineInfo;
    }
}
