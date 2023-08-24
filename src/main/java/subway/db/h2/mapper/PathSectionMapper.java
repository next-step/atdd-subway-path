package subway.db.h2.mapper;

import subway.db.common.Mapper;
import subway.db.h2.entity.SubwaySectionJpa;
import subway.domain.Kilometer;
import subway.domain.PathSection;
import subway.domain.PathStation;

@Mapper
public class PathSectionMapper {

    public PathSection from(SubwaySectionJpa subwaySectionJpa) {
        if (subwaySectionJpa.isNew()) {
            throw new IllegalArgumentException("지하철 구간 영속성 모델이 DB에 저장되어있지 않습니다.");
        }
        return PathSection.of(
                PathSection.Id.of(subwaySectionJpa.getId()),
                from(subwaySectionJpa.getUpStationId(), subwaySectionJpa.getUpStationName()),
                from(subwaySectionJpa.getDownStationId(), subwaySectionJpa.getDownStationName()),
                Kilometer.of(subwaySectionJpa.getDistance()));
    }

    private PathStation from(Long id, String name) {
        return PathStation.of(PathStation.Id.of(id), name);
    }
}
