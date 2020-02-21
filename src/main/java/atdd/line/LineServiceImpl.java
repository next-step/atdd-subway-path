package atdd.line;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LineServiceImpl implements LineService {

    private final LineRepository lineRepository;

    @Override
    public Line create(Line line) {
        return lineRepository.save(line);
    }

    @Override
    public void delete(Line line) {
        lineRepository.delete(line);
    }
}
