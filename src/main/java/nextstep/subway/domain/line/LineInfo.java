package nextstep.subway.domain.line;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import nextstep.subway.domain.line.entity.Line;
import nextstep.subway.domain.station.StationInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineInfo {

    @Getter
    @RequiredArgsConstructor
    public static class Main {
        private final Long id;
        private final String name;
        private final String color;
        private final Sections sections;

        public static Main from(Line entity) {
            return new Main(entity.getId(), entity.getName(), entity.getColor(), Sections.from(entity.getSections()));
        }
    }

    @Getter
    public static class Sections {
        @Delegate
        private final List<Section> sections = new ArrayList<>();

        public static Sections from(nextstep.subway.domain.line.entity.Sections entity) {
            Sections sections = new Sections();
            sections.addAll(
                    entity.stream()
                            .map(Section::from)
                            .collect(Collectors.toList())
            );
            return sections;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Section {
        private final Long id;
        private final StationInfo.Main upStation;
        private final StationInfo.Main downStation;
        private final Long distance;

        public static Section from(nextstep.subway.domain.line.entity.Section entity) {
            StationInfo.Main upStation = StationInfo.Main.from(entity.getUpStation());
            StationInfo.Main downStation = StationInfo.Main.from(entity.getDownStation());
            return new Section(entity.getId(), upStation, downStation, entity.getDistance());
        }
    }
}
