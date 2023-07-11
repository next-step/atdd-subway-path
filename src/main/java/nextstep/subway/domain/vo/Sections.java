package nextstep.subway.domain.vo;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Embeddable
public class Sections {

    private static final int MINIMUM_SIZE = 1;

    @JoinColumn(name = "section_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public Station getUpStation() {
        return sections.get(0).getUpStation();
    }

    public Station getDownStation() {
        return sections.get(getLastIndex()).getDownStation();
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    public boolean isNotEndWith(Station targetStation) {
        return !this.endWith(targetStation);
    }

    public boolean endWith(Station targetStation) {
        return Objects.equals(getDownStation(), targetStation);
    }

    public boolean contains(Station targetStation) {
        return this.sections.stream()
                .anyMatch(isIncludeInSection(targetStation));
    }

    private Predicate<Section> isIncludeInSection(Station targetStation) {
        return section -> Objects.equals(section.getUpStation(), targetStation)
                || Objects.equals(section.getDownStation(), targetStation);
    }

    public Long sumOfDistance() {
        return this.sections.stream().mapToLong(Section::getDistance).sum();
    }

    public boolean isMinimumSize() {
        return this.sections.size() == MINIMUM_SIZE;
    }

    public void pop() {
        Section lastSection = this.sections.get(getLastIndex());
        this.sections.remove(lastSection);
    }
}
