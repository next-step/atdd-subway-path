package nextstep.subway.domain;
import java.util.List;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getList() {
        return sections;
    }
}
