package nextstep.subway.application.dto;

import nextstep.subway.domain.model.Section;

public class SectionResponse {
    private Long lineId;
    private Long sectionId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public SectionResponse(Long lineId, Long sectionId, Long upStationId, Long downStationId, Integer distance) {
        this.lineId = lineId;
        this.sectionId = sectionId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionResponseBuilder builder() {
        return new SectionResponseBuilder();
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public static SectionResponse from(Section section) {
        return new SectionResponseBuilder()
            .lineId(section.getLine().getId())
            .sectionId(section.getId())
            .upStationId(section.getUpStationId())
            .downStationId(section.getDownStationId())
            .distance(section.getDistance())
            .build();
    }

    public static class SectionResponseBuilder {
        private Long lineId;
        private Long sectionId;
        private Long upStationId;
        private Long downStationId;
        private Integer distance;

        SectionResponseBuilder() {
        }

        public SectionResponseBuilder lineId(Long lineId) {
            this.lineId = lineId;
            return this;
        }


        public SectionResponseBuilder sectionId(Long sectionId) {
            this.sectionId = sectionId;
            return this;
        }

        public SectionResponseBuilder upStationId(Long upStationId) {
            this.upStationId = upStationId;
            return this;
        }

        public SectionResponseBuilder downStationId(Long downStationId) {
            this.downStationId = downStationId;
            return this;
        }

        public SectionResponseBuilder distance(Integer distance) {
            this.distance = distance;
            return this;
        }

        public SectionResponse build() {
            return new SectionResponse(this.lineId, this.sectionId, this.upStationId, this.downStationId, this.distance);
        }
    }
}
