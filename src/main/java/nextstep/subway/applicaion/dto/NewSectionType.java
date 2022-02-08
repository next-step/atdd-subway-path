package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

import java.util.List;

public enum NewSectionType {

    UP_BETWEEN{
        @Override
        void insert(Line line, List<Section> sections, Section oldSection, Section newSection) {
            sections.add(new Section(
                    line,
                    newSection.getDownStation(),
                    oldSection.getDownStation(),
                    (oldSection.getDistance() - newSection.getDistance())
            ));
        }
    },
    DOWN_BETWEEN {
        @Override
        void insert(Line line, List<Section> sections, Section oldSection, Section newSection) {
            sections.add(new Section(
                    line,
                    newSection.getUpStation(),
                    oldSection.getDownStation(),
                    newSection.getDistance()
            ));
            oldSection.update(new Section(
                    line,
                    oldSection.getUpStation(),
                    newSection.getUpStation(),
                    (oldSection.getDistance() - newSection.getDistance())
            ));
        }
    },
    LAST_UP {
        @Override
        void insert(Line line, List<Section> sections, Section oldSection, Section newSection) {
            sections.add(
                    sections.indexOf(oldSection),
                    newSection
            );
        }
    },
    LAST_DOWN {
        @Override
        void insert(Line line, List<Section> sections, Section oldSection, Section newSection) {
            sections.add(newSection);
        }
    };

    abstract void insert(Line line, List<Section> sections, Section oldSection, Section newSection);
}
