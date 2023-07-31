package nextstep.subway.section.entity;

import lombok.*;
import nextstep.subway.station.entity.Station;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@ToString
@Builder
@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private Integer distance;

}
