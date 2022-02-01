package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StationResponse {
    private static final int IS_NOT_REQUIRED_RE_ORDER_COUNT = 1;
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static StationResponse of(final Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCreatedDate(),
                station.getModifiedDate()
        );
    }

    public static List<StationResponse> toStations(final Sections sections) {
        List<Station> stations = isRequiredReOrdering(sections)
                ? createStations(createReOrderedSections(sections))
                : createStations(sections.getSections());

        return stations.stream()
                .map(StationResponse::of)
                .sorted(Comparator.comparing(StationResponse::getId))
                .collect(Collectors.toList());
    }

    private StationResponse(final Long id, final String name, final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    private static boolean isRequiredReOrdering(final Sections sections) {
        return sections.getSections().size() > IS_NOT_REQUIRED_RE_ORDER_COUNT;
    }

    private static List<Section> createReOrderedSections(final Sections sections) {
        List<Section> sectionStorage = new ArrayList<>();
        Section start = sections.findSectionHasUpStationEndPoint();
        sectionStorage.add(start);
        reOrderSections(sections, start, sectionStorage);
        return sectionStorage;
    }

    private static void reOrderSections(
            final Sections sections,
            final Section start,
            final List<Section> sectionStorage
    ) {
        Section findSection = sections.findAnotherSectionWhereDownStationOfTheSectionIsTheUpStation(start);
        sectionStorage.add(findSection);
        if(sections.getDownStationEndPoint().equals(findSection.getDownStation())) {
            return;
        }

        reOrderSections(sections, findSection, sectionStorage);
    }

    private static List<Station> createStations(final List<Section> sections) {
        return Stream.concat(
                sections.stream().map(Section::getUpStation),
                sections.stream().map(Section::getDownStation)
        ).distinct().collect(Collectors.toList());
    }
}
