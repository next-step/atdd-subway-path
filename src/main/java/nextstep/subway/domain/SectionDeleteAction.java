package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import nextstep.subway.exception.CanNotDeleteSectionException;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@AllArgsConstructor
public enum SectionDeleteAction {
    UP_STATION((sections, stationId) -> isDeleteUpStation(sections, stationId), (sections, stationId) -> deleteUpStation(sections)),
    MIDDLE_STATION((sections, stationId) -> isDeleteMiddleStation(sections, stationId), (sections, stationId) -> deleteMiddleStation(sections, stationId)),
    DOWN_STATION((sections, stationId) -> isDeleteDownStation(sections, stationId), (sections, stationId) -> deleteDownStation(sections));

    private final BiPredicate<List<Section>, Long> matchFunction;
    private final BiConsumer<List<Section>, Long> deleteFunction;

    public static SectionDeleteAction of(List<Section> sections, Long stationId) {
        return getSectionAction(sections, stationId);
    }

    private static SectionDeleteAction getSectionAction(List<Section> sections, Long stationId) {
        validate(sections, stationId);

        return Arrays.stream(values())
                .filter(a -> a.matchFunction.test(sections, stationId))
                .findAny()
                .orElseThrow(() -> new CanNotDeleteSectionException("삭제 액션을 찾을수 없습니다."));
    }

    private static void validate(List<Section> sections, Long stationId) {
        if (sections.size() == 1) {
            throw new CanNotDeleteSectionException("구간이 하나이면 삭제할 수 없습니다.");
        }

        if (!hasStation(sections, stationId)) {
            throw new CanNotDeleteSectionException("일치하는 역이 없습니다");
        }
    }

    public void delete(List<Section> sections, long stationId) {
        deleteFunction.accept(sections, stationId);
    }

    private static boolean hasStation(List<Section> sections, Long stationId) {
        return sections.stream()
                .anyMatch(s -> s.hasStationId(stationId));
    }

    private static boolean isDeleteUpStation(List<Section> sections, Long stationId) {
        return sections.stream()
                .anyMatch(s -> s.isSameUpStationId(stationId) && s.isFirst());
    }

    private static void deleteUpStation(List<Section> sections) {
        sections.remove(0);
    }

    private static boolean isDeleteMiddleStation(List<Section> sections, Long stationId) {
        return sections.stream()
                .anyMatch(deleteMiddlePredicate(stationId));
    }

    private static void deleteMiddleStation(List<Section> sections, Long stationId) {
        // sections가 orderSeq 순서대로 정렬된 것을 전제로함. 만약 로직이 변경되어 sections가 정렬이 안되어 있으면 비정상 동작한다
        IntStream.range(1, sections.size())
                .filter(i -> deleteMiddlePredicate(stationId).test(sections.get(i)))
                .findAny()
                .ifPresentOrElse(i -> {
                    Section before = sections.get(i - 1);
                    Section target = sections.get(i);

                    before.deleteMiddleStation(target);
                    sections.remove(i);
                }, () -> new CanNotDeleteSectionException("구간 중간에 역을 찾을 수 없습니다. 역id:" + stationId));
    }

    private static Predicate<Section> deleteMiddlePredicate(Long stationId) {
        return s -> s.isSameUpStationId(stationId) && !s.isFirst();
    }

    private static boolean isDeleteDownStation(List<Section> sections, Long stationId) {
        return sections.stream()
                .anyMatch(s -> s.getDownStationId().equals(stationId) && s.isLast(sections.size()));
    }

    private static void deleteDownStation(List<Section> sections) {
        sections.remove(getLastIndex(sections.size()));
    }

    private static int getLastIndex(int size) {
        return size - 1;
    }
}
