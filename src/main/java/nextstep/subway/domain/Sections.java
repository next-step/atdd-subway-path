package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public boolean addSection(Line line, Station upStation, Station downStation, int distance) {
        return this.sections.add(new Section(line, upStation, downStation, distance));
    }

    public boolean removeSection(Station station) {
        if (!this.sections.get(this.sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        return this.sections.remove(this.sections.get(this.sections.size() - 1));
    }

    public List<Section> list() {
        return this.sections;
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }
}
