# sayaya-rx 개발 문서

이 문서는 Unified Process(UP)의 산출물 구성을 따릅니다. 요구사항에서 유스케이스, 시스템 연산, 설계(클래스·연산), 테스트까지 이름으로 추적할 수 있게 썼습니다.

> 기준 시점: 버전 2.4.0 이후의 `main`(GWT 2.13.1, Elemento 2.5.14, JDK toolchain 21, gwt-test 2.3.0, 테스트용 RxJS 7.8.2).
> 코드가 바뀌면 문서의 해당 산출물과 [추적성 표](traceability.md)를 함께 고칩니다.

## 개발 사례 (Development Case)

이 프로젝트가 UP에서 실제로 쓰는 산출물과 그 위치입니다.

| 단계 | 분야 | 산출물 | 문서 |
|---|---|---|---|
| 인셉션 | 요구사항 | 비전 | [01-inception/vision.md](01-inception/vision.md) |
| 인셉션 | 요구사항 | 요구사항 목록·보충 명세(비기능) | [01-inception/requirements.md](01-inception/requirements.md) |
| 인셉션 | 요구사항 | 유스케이스 모델 | [01-inception/use-cases.md](01-inception/use-cases.md) |
| 전 단계 | 요구사항 | 용어집 | [01-inception/glossary.md](01-inception/glossary.md) |
| 엘라보레이션 | 요구사항(분석) | 시스템 시퀀스 다이어그램(SSD) | [02-elaboration/ssd.md](02-elaboration/ssd.md) |
| 엘라보레이션 | 분석 | 도메인 모델 | [02-elaboration/domain-model.md](02-elaboration/domain-model.md) |
| 엘라보레이션 | 설계 | 설계 모델(설계 시퀀스, 설계 클래스 다이어그램) | [02-elaboration/design-model.md](02-elaboration/design-model.md) |
| 엘라보레이션 | 설계 | 아키텍처 문서(패키지·컴포넌트·배포) | [02-elaboration/architecture.md](02-elaboration/architecture.md) |
| 컨스트럭션 | 구현 | 구현 가이드(바인딩 추가 절차) | [03-construction/implementation-guide.md](03-construction/implementation-guide.md) |
| 컨스트럭션 | 테스트 | 테스트 설계 | [03-construction/test.md](03-construction/test.md) |
| 트랜지션 | 배포 | 릴리스·배포 절차 | [04-transition/release.md](04-transition/release.md) |
| 전 단계 | 형상·변경 관리 | 추적성 표 | [traceability.md](traceability.md) |

쓰지 않는 산출물: 데이터 모델(영속 데이터 없음), 사용자 인터페이스 명세(UI 없음).

## 반복(Iteration) 운영

- 한 반복은 "바인딩 추가 또는 결함 수정 하나 + 그 검증 테스트"입니다. 큰 API 변경(호환성을 깨는 변경)은 메이저 버전 반복으로 따로 묶습니다.
- 위험 우선: JsInterop 경계(숫자·배열·가변인자·에러 전달)처럼 런타임에서만 드러나는 위험을 먼저 브라우저 테스트로 고정합니다. 과거 결함 사례는 [구현 가이드](03-construction/implementation-guide.md#알려진-jsinterop-함정)에 있습니다.

## 다이어그램 표기

다이어그램은 GitHub에서 렌더링되는 Mermaid로 그렸습니다. Mermaid에는 유스케이스·패키지·컴포넌트·배포 다이어그램 타입이 없어서 `flowchart`로 근사했고, 각 문서에 그 사실을 적었습니다.

## 문서에 그리지 않은 것

이 문서는 **바인딩 라이브러리**의 개발 문서입니다. RxJS 자체의 의미론(각 연산자의 동작)은 [rxjs.dev](https://rxjs.dev)를 따르며 여기서 다시 설명하지 않습니다.
