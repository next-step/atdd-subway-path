package atdd.station.application;

import atdd.station.application.dto.SubwayCommonResponseDto;
import atdd.station.application.dto.SubwayLineResponseDto;
import atdd.station.application.exception.ResourceNotFoundException;
import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import atdd.station.domain.SubwaySection;
import atdd.station.utils.SubwaySectionQueueUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubwayLineQueryService {
    private SubwaySectionQueryService subwaySectionQueryService;
    private SubwayLineRepository subwayLineRepository;

    public SubwayLineQueryService(SubwaySectionQueryService subwaySectionQueryService,
                                  SubwayLineRepository subwayLineRepository) {

        this.subwaySectionQueryService = subwaySectionQueryService;
        this.subwayLineRepository = subwayLineRepository;
    }

    @Transactional
    public List<SubwayLineResponseDto> getSubwayLines() {
        List<SubwayLine> subwayLines = subwayLineRepository.findAll();

        return subwayLines.stream()
                .map(subwayLine -> SubwayLineResponseDto.of(subwayLine, getStationsFromSubwayLine(subwayLine)))
                .collect(Collectors.toList());
    }

    private List<SubwayCommonResponseDto> getStationsFromSubwayLine(SubwayLine subwayLine) {
        List<SubwaySection> savedSubwaySections = subwaySectionQueryService.findAllBySubwayLine(subwayLine);
        List<Station> savedStations = getStationsFromSubwaySection(savedSubwaySections);

        return savedStations.stream()
                .map(SubwayCommonResponseDto::of)
                .collect(Collectors.toList());
    }

    private List<Station> getStationsFromSubwaySection(List<SubwaySection> subwaySections) {
        Deque<SubwaySection> orderedSectionsQueue = new ArrayDeque<>();
        Deque<SubwaySection> waitingQueue = new ArrayDeque<>(subwaySections);

        if (waitingQueue.isEmpty()) {
            return Collections.emptyList();
        }
        orderedSectionsQueue.push(waitingQueue.pollFirst());

        while (!waitingQueue.isEmpty()) {
            SubwaySection waitingQueueSection = waitingQueue.pollFirst();

            SubwaySectionQueueUtils.pushSectionIfConditionCorrect(orderedSectionsQueue
                    , waitingQueue, waitingQueueSection, orderedSectionsQueue.getFirst());

            waitingQueueSection = waitingQueue.pollFirst();

            SubwaySectionQueueUtils.pushSectionIfConditionCorrect(orderedSectionsQueue
                    , waitingQueue, waitingQueueSection, orderedSectionsQueue.getLast());
        }

        Set<Station> stations = new LinkedHashSet<>();
        orderedSectionsQueue.forEach(section -> {
            stations.add(section.getSourceStation());
            stations.add(section.getTargetStation());
        });

        return new ArrayList<>(stations);
    }

    @Transactional
    public SubwayLineResponseDto getSubwayLine(Long id) {
        SubwayLine savedSubwayLine = findSubwayLineById(id);
        return SubwayLineResponseDto.of(savedSubwayLine, getStationsFromSubwayLine(savedSubwayLine));
    }

    @Transactional
    public SubwayLine findSubwayLineById(Long id) {
        return subwayLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 지하철 노선을 찾을 수 없습니다."));
    }

}
