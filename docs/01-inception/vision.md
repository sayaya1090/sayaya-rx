# 비전 (Vision)

## 문제

GWT로 작성한 웹 클라이언트에서 이벤트, 타이머, HTTP 응답, 공유 상태 같은 비동기 흐름을 다루려면 콜백을 직접 엮어야 합니다. JavaScript 쪽에는 RxJS라는 성숙한 반응형 라이브러리가 있지만, GWT 코드(Java/Kotlin)에서 타입을 유지한 채 쓸 방법이 필요합니다.

## 제품 개요

sayaya-rx(`dev.sayaya:rx`, GWT 모듈 `dev.sayaya.Rx`)는 브라우저에 이미 로드된 RxJS를 GWT JsInterop으로 감싸, Java/Kotlin에서 RxJS의 Observable, 연산자, Subject, Scheduler를 타입 있는 API로 쓰게 해 주는 **바인딩 라이브러리**입니다.

- RxJS를 다시 구현하지 않습니다. `@JsType(isNative = true, namespace = "rxjs")`로 전역 `rxjs` 객체를 직접 가리킵니다.
- RxJS를 번들하지 않습니다. 사용하는 HTML이 RxJS UMD 번들을 GWT `*.nocache.js`보다 먼저 로드해야 합니다.

## 이해관계자

| 이해관계자 | 관심사 |
|---|---|
| 애플리케이션 개발자(라이브러리 사용자) | Java다운 API(람다, `List`, `int`), RxJS와 같은 의미, 에러가 사라지지 않을 것 |
| 라이브러리 유지보수자 | RxJS 연산자 추가가 쉬울 것, 바인딩 결함을 브라우저에서 검출할 것, 릴리스가 재현 가능할 것 |
| 배포 저장소(Maven Central, GitHub Packages) | 서명된 산출물, 올바른 pom(라이선스, SCM) |

## 범위

포함:
- Observable 생성 함수, 연산자 체이닝, 구독과 해제
- Subject 4종, Scheduler(async/asap/queue/animationFrame)
- RxJS `ajax` 기반 HTTP 요청(`fromRequest`, `fetchJson`)
- 브라우저 테스트(GWT devMode 모듈 4개 + Kotest/Playwright)

제외(Non-goals):
- RxJS 동작 자체의 수정. RxJS의 결함은 고칠 수 없습니다.
- RxJS 전체 API의 1:1 노출. 사용 빈도가 높은 생성 함수와 연산자부터 점진적으로 추가합니다.
- 서버(JVM) 환경. 이 라이브러리는 GWT로 컴파일된 브라우저 코드에서만 동작합니다.

## 성공 기준

- 공개 API 하나하나가 브라우저 테스트로 검증됩니다(현재 4개 테스트 클래스, 79개 테스트 케이스).
- Maven Central과 GitHub Packages에 같은 좌표(`dev.sayaya:rx:<버전>`)로 배포됩니다.
