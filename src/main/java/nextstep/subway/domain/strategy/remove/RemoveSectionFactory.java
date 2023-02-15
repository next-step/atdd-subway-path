package nextstep.subway.domain.strategy.remove;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionCollection;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.strategy.add.AddSectionStrategy;
import nextstep.subway.domain.strategy.add.BasicAddSection;
import nextstep.subway.domain.strategy.add.MiddleAddSection;

import java.util.Objects;

public class RemoveSectionFactory {
    public RemoveSectionStrategy createRemoveSectionStrategy(SectionCollection sectionCollection, Station station) {

        Station firstStation = sectionCollection.getFirstStation();
        Station lastStation = sectionCollection.getLastStation();

        if (Objects.equals(station, firstStation) || Objects.equals(station, lastStation)) {
            return new BasicRemoveSection();
        }

        return new MiddleRemoveSection();
    }
}
