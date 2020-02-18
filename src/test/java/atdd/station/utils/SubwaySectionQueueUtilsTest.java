package atdd.station.utils;

import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwaySection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("이미 순서에 맞게 배열된 SubwaySection 그룹(orderedSectionsQueue), 순서에 맞게 배열되기 위해 기다리는 SubwaySection 그룹(waitingQueue)")
class SubwaySectionQueueUtilsTest {

    @DisplayName("orderedSectionsQueue에 맨 앞에 위치한 SubwaySection 과 waitingQueue에 맨 앞에 위치한 SubwaySection에 순서가 있다면, orderedSectionsQueue에 순서에 맞게 재배열시킨다.")
    @Test
    void pushSubwaySection() {
        // given
        Deque<SubwaySection> orderedSectionsQueue = new ArrayDeque<>(Collections.singletonList(SubwaySection.of(SubwayLine.of("8호선"), Station.of("잠실역"), Station.of("석촌역"))));
        Deque<SubwaySection> waitingQueue = new ArrayDeque<>(Collections.singletonList(SubwaySection.of(SubwayLine.of("8호선"), Station.of("몽촌토성역"), Station.of("잠실역"))));

        SubwaySection targetSubwaySection = orderedSectionsQueue.getFirst();
        SubwaySection waitingQueueSection = waitingQueue.removeFirst();

        // when
        SubwaySectionQueueUtils.pushSectionIfConditionCorrect(orderedSectionsQueue, waitingQueue, waitingQueueSection, targetSubwaySection);

        // then
        assertThat(orderedSectionsQueue).hasSize(2);
        assertThat(orderedSectionsQueue.getFirst()).isEqualTo(waitingQueueSection);
        assertThat(orderedSectionsQueue.getLast()).isEqualTo(targetSubwaySection);
    }

    @DisplayName("orderedSectionsQueue에 맨 앞에 위치한 SubwaySection 과 waitingQueue에 맨 앞에 위치한 SubwaySection에 순서가 없다면, orderedSectionsQueue는 변화가 없다.")
    @Test
    void noPushSubwaySection() {
        // given
        Deque<SubwaySection> orderedSectionsQueue = new ArrayDeque<>(Collections.singletonList(SubwaySection.of(SubwayLine.of("8호선"), Station.of("몽촌토성역"), Station.of("잠실역"))));
        Deque<SubwaySection> waitingQueue = new ArrayDeque<>(Collections.singletonList(SubwaySection.of(SubwayLine.of("8호선"), Station.of("광나루역"), Station.of("천호역"))));

        SubwaySection targetSubwaySection = orderedSectionsQueue.getFirst();
        SubwaySection waitingQueueSection = waitingQueue.removeFirst();

        // when
        SubwaySectionQueueUtils.pushSectionIfConditionCorrect(orderedSectionsQueue, waitingQueue, waitingQueueSection, targetSubwaySection);

        // then
        assertThat(orderedSectionsQueue).hasSize(1);
        assertThat(orderedSectionsQueue).isEqualTo(orderedSectionsQueue);
    }

}
