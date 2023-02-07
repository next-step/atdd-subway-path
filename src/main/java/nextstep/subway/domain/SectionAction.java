package nextstep.subway.domain;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

@AllArgsConstructor
public enum SectionAction {
    ADD_UP_STATION((sections, newSection) -> sections.isAddUpStation(newSection), (sections, newSection) -> sections.addUpStation(newSection)),
    ADD_MIDDLE_STATION((sections, newSection) -> sections.isAddMiddleStation(newSection), (sections, newSection) -> sections.addMiddleStation(newSection)),
    ADD_DOWN_STATION((sections, newSection) -> sections.isAddDownStation(newSection), (sections, newSection) -> sections.addDownStation(newSection));

    private final BiPredicate<Sections, Section> matchFunction;
    private final BiConsumer<Sections, Section> addFunction;

    public static SectionAction of(Sections sections, Section newSection) {
        SectionAction sectionAction = getSectionAction(sections, newSection);
        return sectionAction;
    }

    private static SectionAction getSectionAction(Sections sections, Section newSection) {
        validate(sections, newSection);

        return Arrays.stream(values())
                .filter(e -> e.matchFunction.test(sections, newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("저장 액션을 찾을수 없습니다"));
    }

    private static void validate(Sections sections, Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();

        if (sections.hasStation(upStation) && sections.hasStation(downStation)) {
            throw new IllegalArgumentException("이미 등록된 구간은 등록 불가");
        }

        if (!sections.hasStation(upStation) && !sections.hasStation(downStation)) {
            throw new IllegalArgumentException("상행 하행역이 모두 노선에 등록되어 있지 않으면 등록 불가");
        }
    }

    public void add(Sections sections, Section newSection) {
        this.addFunction.accept(sections, newSection);
    }
}
