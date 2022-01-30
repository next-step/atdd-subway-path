<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-6.14.15-blue">
  <img alt="node" src="https://img.shields.io/badge/node-14.18.2-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
</p>

<br>

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```

---

## 단위 테스트 실습

- 단위 테스트 코드 작성
  - [ ] LineServiceMockTest
  - [ ] LineServiceTest
- 비즈니스 로직 리팩토링
  - [ ] LineTest 작성 (TDD 사이클)
    - [ ] LineService 의 비즈니스 로직을 도메인으로 이동
          
---

## 1단계 - 구간 추가 기능 변경

- [ ] 인수 조건 도출
- [ ] 인수 테스트 작성
- [ ] 신규 구간 추가 (TDD 사이클 진행)
  - [ ] 상행 종점역 추가
    - [ ] 신규 구간의 하행역이 기존 구간의 상행 종점역인 경우
  - [ ] 하행 종점역 추가
    - [ ] 신규 구간의 상행역이 기존 구간의 하행 종점역이 경우
  - [ ] 신규 구간의 상행역과 하행역 중 하나라도 기존 구간에 포함되지 않으면 추가 불가
  - [ ] 신규 구간과 기존 구간과 동일한 경우 추가 불가
  - [ ] 역과 역 사이 신규 역 추가 (기존 구간 사이에 구간 추가)
    - [ ] 신규 구간의 상행역이 기존 구간의 상행역중 하나인 경우
    - [ ] 신규 구간의 하행역이 기존 구간의 하행역중 하나인 경우
    - [ ] 신규 구간의 거리가 기존 구간의 거리보다 같거나 크면 추가 불가
    - [ ] 신규 구간의 거리에 따라 기존 구간 거리 수정
- [ ] 노선 조회 시 역 목록 순서 수정
    - [ ] 상행 종점역 -> 하행 종점역

