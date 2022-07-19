package nextstep.subway.domain;

import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author a1101466 on 2022/07/13
 * @project subway
 * @description
 */
@Embeddable
@Getter
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("id asc")
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section addSection) {
        this.sections.add(addSection);
    }

    /*public void remove(Station station) {
        sections.removeIf(section -> section.isRemovable(station.getId()));
    }*/

}