package nextstep.subway.domain;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@AllArgsConstructor
public enum SectionAction {
    ADD_UP_STATION((sections, newSection) -> isAddUpStation(sections, newSection), (sections, newSection) -> addUpStation(sections,newSection)),
    ADD_MIDDLE_STATION((sections, newSection) -> isAddMiddleStation(sections, newSection), (sections, newSection) -> addMiddleStation(sections, newSection)),
    ADD_DOWN_STATION((sections, newSection) -> isAddDownStation(sections, newSection), (sections, newSection) -> addDownStation(sections, newSection));

    private final BiPredicate<List<Section>, Section> matchFunction;
    private final BiConsumer<List<Section>, Section> addFunction;

    public static SectionAction of(List<Section> sections, Section newSection) {
        return getSectionAction(sections, newSection);
    }

    private static SectionAction getSectionAction(List<Section> sections, Section newSection) {
        validate(sections, newSection);

        return Arrays.stream(values())
                .filter(e -> e.matchFunction.test(sections, newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("저장 액션을 찾을수 없습니다"));
    }

    private static void validate(List<Section> sections, Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();

        if (hasStation(sections, upStation) && hasStation(sections, downStation)) {
            throw new IllegalArgumentException("이미 등록된 구간은 등록 불가");
        }

        if (!hasStation(sections, upStation) && !hasStation(sections, downStation)) {
            throw new IllegalArgumentException("상행 하행역이 모두 노선에 등록되어 있지 않으면 등록 불가");
        }
    }

    public void add(List<Section> sections, Section newSection) {
        this.addFunction.accept(sections, newSection);
    }

    private static boolean hasStation(List<Section> sections, Station station) {
        return sections.stream()
                .anyMatch(s -> s.isSameUpStation(station) || s.isSameDownUpStation(station));
    }

    private static boolean isAddUpStation(List<Section> sections, Section newSection) {
        return sections.stream()
                .anyMatch(addUpStationPredicate(newSection));
    }

    private static Predicate<Section> addUpStationPredicate(Section newSection) {
        return s -> s.isSameUpStation(newSection.getDownStation());
    }

    private static boolean isAddMiddleStation(List<Section> sections, Section newSection) {
        return sections.stream()
                .anyMatch(addMiddlePredicate(newSection));
    }

    private static Predicate<Section> addMiddlePredicate(Section newSection) {
        return s -> s.isSameUpStation(newSection.getUpStation()) && s.isDistanceGreaterThen(newSection.getDistance());
    }

    private static boolean isAddDownStation(List<Section> sections, Section newSection) {
        return sections.stream()
                .anyMatch(s -> s.isSameDownUpStation(newSection.getUpStation()));
    }

    private static void addUpStation(List<Section> sections, Section newSection) {
        sections.add(0, newSection);
    }

    private static void addMiddleStation(List<Section> sections, Section newSection) {
        Section oldSection = sections.stream()
                .filter(addMiddlePredicate(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("구간을 찾을 수 없습니다."));

        sections.add(sections.indexOf(oldSection), newSection);


        oldSection.minusDistacne(newSection.getDistance());
        oldSection.changeUpStation(newSection.getDownStation());

        sections.set(sections.indexOf(oldSection), oldSection);
    }

    private static void addDownStation(List<Section> sections, Section newSection) {
        sections.add(newSection);
    }
}
