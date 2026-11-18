# 요구사항 목록과 보충 명세

번호는 [유스케이스](use-cases.md)와 [추적성 표](../traceability.md)에서 같은 이름으로 씁니다.

## 기능 요구사항 (FR)

| ID | 요구사항 | 대표 API |
|---|---|---|
| FR-01 | 값, 범위, 배열·List·Promise, 다른 Observable들로부터 Observable을 만든다 | `of`, `range`, `from`, `merge`, `concat`, `forkJoin`, `defer`, `empty`, `throwError` |
| FR-02 | 시간 기반 Observable을 만든다 | `interval`, `timer(due)`, `timer(start, delay)` |
| FR-03 | DOM 이벤트를 Observable로 만든다 | `fromEvent`, `fromMouseEvent`, `fromKeyboardEvent` |
| FR-04 | HTTP 요청 결과를 Observable로 받는다(요청·응답 헤더 포함) | `fromRequest(AjaxRequest)`, `fetchJson(url)` |
| FR-05 | Observable에 연산자를 메서드 체인으로 적용한다 | `map`, `filter`, `mergeMap`, `switchMap`, `scan`, `zip`, `combineLatest`, `bufferTime`, ... |
| FR-06 | 시간 기반 속도 제한을 적용한다(스케줄러·leading/trailing 설정 포함) | `debounceTime`, `debounce`, `throttleTime` |
| FR-07 | Observer 또는 람다로 구독하고 해제한다 | `subscribe(...)`, `Subscription.unsubscribe()` |
| FR-08 | 스트림 에러를 처리·재시도하고, 처리하지 않은 에러는 사라지지 않고 보고된다 | `catchError`, `retry`, `subscribe(Consumer)` |
| FR-09 | Subject로 값을 여러 구독자에게 멀티캐스트하고 상태를 공유한다 | `Subject.subject`, `BehaviorSubject.behavior`, `ReplaySubject.replay*`, `AsyncSubject.async/await/json` |
| FR-10 | 스케줄러로 작업을 지연·재귀 예약한다 | `Scheduler.asyncScheduler`, `asapScheduler`, `queueScheduler`, `animationFrameScheduler`, `AsyncScheduler.schedule` |
| FR-11 | 조건·상태·자원·외부 이벤트 API로부터 Observable을 만든다 | `race`, `iif`, `generate`, `never`, `using`, `scheduled`, `fromEventPattern`, `partition` |
| FR-12 | Observable을 Promise로 바꿔 async 코드와 연동한다 | `firstValueFrom`, `lastValueFrom` |
| FR-13 | 소스를 한 번만 구독해 여러 경로로 나누거나(멀티캐스트) key별로 분할한다 | `connectable`, `connect`, `share(config)`, `shareReplay(config)`, `groupBy` |

### 유지보수 요구사항 (MR)

| ID | 요구사항 |
|---|---|
| MR-01 | 새 RxJS 연산자·생성 함수를 정해진 절차로 추가하고, 같은 반복에서 브라우저 테스트로 검증한다 |
| MR-02 | 버전 태그 하나로 빌드·서명·배포·GitHub Release 생성을 재현 가능하게 수행한다 |

## 보충 명세 (비기능 요구사항, NFR)

| ID | 품질 속성 | 요구사항 | 근거·현재 상태 |
|---|---|---|---|
| NFR-01 | 상호운용성 | RxJS를 번들하지 않고 전역 `rxjs`(UMD, 7.x)를 사용한다 | 테스트 HTML이 `rxjs@7.8.2` UMD를 `nocache.js`보다 먼저 로드 |
| NFR-02 | 성능 | 바인딩이 별도 래퍼 객체를 만들지 않는다. 체인 메서드는 `@JsOverlay`로 컴파일 시 인라인되고, 네이티브 메서드는 RxJS 함수를 직접 호출한다 | `Observable`은 `@JsType(isNative = true)` |
| NFR-03 | 타입 안전성 | JS 값은 Java 타입으로 변환해 노출한다: JS number → `Integer`, JS 배열 → `List` | `toInt()`, `JsArrayLike::asList` |
| NFR-04 | 이식성 | GWT 2.13.1, `strict` 컴파일, JDK toolchain 21에서 빌드된다 | `build.gradle.kts`의 `gwt { compiler { strict = true } }`, `jvmToolchain(21)` |
| NFR-05 | 시험 가능성 | 모든 공개 API를 실제 브라우저에서 검증하고, 검증이 실행되지 않으면 테스트가 실패한다 | Playwright + `shouldPassAll(prefix)` |
| NFR-06 | 배포성 | 서명된 산출물을 Maven Central과 GitHub Packages에 같은 좌표로 배포한다. 라이선스는 Apache-2.0 | `com.vanniktech.maven.publish`, `signing` |
