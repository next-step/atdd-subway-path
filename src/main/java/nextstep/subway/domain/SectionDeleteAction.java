package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import nextstep.subway.exception.CanNotDeleteSectionException;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@AllArgsConstructor
public enum SectionDeleteAction {
    UP_STATION((sections, stationId) -> isDeleteUpStation(sections, stationId), (sections, stationId) -> deleteUpStation(sections,stationId)),
    MIDDLE_STATION((sections, stationId) -> isDeleteMiddleStation(sections, stationId), (sections, stationId) -> deleteMiddleStation(sections, stationId)),
    DOWN_STATION((sections, stationId) -> isDeleteDownStation(sections, stationId), (sections, stationId) -> deleteDownStation(sections, stationId));

    private final BiPredicate<List<Section>, Section> matchFunction;
    private final BiConsumer<List<Section>, Section> deleteFunction;

    public static SectionDeleteAction of(List<Section> sections, Long stationId) {
        return getSectionAction(sections, stationId);
    }

    private static SectionDeleteAction getSectionAction(List<Section> sections, Long stationId) {
        validate(sections, stationId);

        return null;
    }

    private static void validate(List<Section> sections, Long stationId) {
        if (sections.size() == 1) {
            throw new CanNotDeleteSectionException("구간이 하나이면 삭제할 수 없습니다.");
        }

        if (!hasStation(sections, stationId)) {
            throw new CanNotDeleteSectionException("일치하는 역이 없습니다");
        }
    }

    public void delete(List<Section> sections, Section stationId) {
        throw new UnsupportedOperationException();
    }

    private static boolean hasStation(List<Section> sections, Long stationId) {
        return sections.stream()
                .anyMatch(s -> s.hasStationId(stationId));
    }

    private static boolean isDeleteUpStation(List<Section> sections, Section stationId) {
        throw new UnsupportedOperationException();
    }

    private static Predicate<Section> deleteUpStationPredicate(Section stationId) {
        throw new UnsupportedOperationException();
    }

    private static boolean isDeleteMiddleStation(List<Section> sections, Section stationId) {
        throw new UnsupportedOperationException();
    }

    private static Predicate<Section> deleteMiddlePredicate(Section stationId) {
        throw new UnsupportedOperationException();
    }

    private static boolean isDeleteDownStation(List<Section> sections, Section stationId) {
        throw new UnsupportedOperationException();
    }

    private static void deleteUpStation(List<Section> sections, Section stationId) {
    }

    private static void deleteMiddleStation(List<Section> sections, Section stationId) {

    }

    private static void deleteDownStation(List<Section> sections, Section stationId) {
    }
}
