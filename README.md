# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

# merge 이후 미션 수행 명령어
```
git checkout songteaheon  
git fetch upstream songteaheon  
git rebase upstream/songteaheon
```  


# 할 일 
## 1단계 - 지하철 구간 추가 기능 개션
- [X] 인수 테스트 목록 작성
- [X] 인수 테스트 작성
- [X] 코드 구현

## 2단계 - 지하철 구간 삭제 기능 개션
- [X] 인수 테스트 목록 작성
- [X] 인수 테스트 작성
- [X] 코드 구현

## 3단계 - 경로 조회 기능
- [X] 인수 테스트 목록 작성
- [X] 인수 테스트 작성
- [X] 코드 구현

# 최단 거리 알고리즘 라이브러리
## DijkstraShortestPath
https://jgrapht.org/javadoc/org.jgrapht.core/org/jgrapht/alg/shortestpath/DijkstraShortestPath.html

## KShortestPaths
https://jgrapht.org/javadoc-1.0.1/org/jgrapht/alg/shortestpath/KShortestPaths.html
- k개의 최단거리를 weight의 오름차순으로 찾는다.
- Bellman-Ford algorithm
- 음수 간선도 포함될 수 있음.
- 다익스트라에 비해 시간 오래 걸림
