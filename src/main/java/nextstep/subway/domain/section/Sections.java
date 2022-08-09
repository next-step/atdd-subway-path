package nextstep.subway.domain.section;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getList() {
        return sections;
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public void remove(Section section) {
        this.sections.remove(section);
    }

    public Section getLastSection() {
        return this.sections.get(this.sections.size() - 1);
    }

    public Section getUpStationSection() {
        final Section section = this.sections.get(0);
        return getUpStationSection(section);
    }

    private Section getUpStationSection(Section section) {
        while (true) {
            final Optional<Section> previousSection = this.sections.stream()
                    .filter(it -> it.getDownStation().equals(section.getUpStation()))
                    .findFirst();
            if (previousSection.isPresent()) {
                return getUpStationSection(previousSection.get());
            }
            return section;
        }
    }
}
