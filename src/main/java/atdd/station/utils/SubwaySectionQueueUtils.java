package atdd.station.utils;

import atdd.station.domain.Station;
import atdd.station.domain.SubwaySection;

import java.util.Deque;

public class SubwaySectionQueueUtils {

    public static void pushSectionIfConditionCorrect(Deque<SubwaySection> orderedSectionsQueue,
                                                     Deque<SubwaySection> waitingQueue,
                                                     SubwaySection waitingQueueSection,
                                                     SubwaySection targetSubwaySection) {

        Station sourceStation = targetSubwaySection.getSourceStation();
        Station targetStation = targetSubwaySection.getTargetStation();

        boolean isChanged = false;
        if (sourceStation.equals(waitingQueueSection.getTargetStation())) {
            waitingQueue.remove(waitingQueueSection);
            orderedSectionsQueue.addFirst(waitingQueueSection);
            isChanged = true;
        }

        if (targetStation.equals(waitingQueueSection.getSourceStation())) {
            waitingQueue.remove(waitingQueueSection);
            orderedSectionsQueue.addLast(waitingQueueSection);
            isChanged = true;
        }

        if (!isChanged) {
            waitingQueue.addLast(waitingQueueSection);
        }
    }
}
