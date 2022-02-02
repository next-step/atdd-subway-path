package nextstep.fake;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LineFakeRepository implements LineRepository {

    private Map<Long, Line> lines = new ConcurrentHashMap<>();

    @Override
    public Optional<Line> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<Line> findAll() {
        return null;
    }

    @Override
    public List<Line> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Line> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Line> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Line entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Line> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Line save(Line entity) {
        Line line = new Line(Long.valueOf(lines.size()),entity.getName(),entity.getColor());
        lines.put(Long.valueOf(lines.size()),line);
        return line;
    }

    @Override
    public <S extends Line> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Line> findById(Long aLong) {
        return Optional.of(lines.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Line> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Line> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Line> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Line getOne(Long aLong) {
        return null;
    }

    @Override
    public Line getById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Line> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Line> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Line> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Line> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Line> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Line> boolean exists(Example<S> example) {
        return false;
    }
}
