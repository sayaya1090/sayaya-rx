# 추적성 표

요구사항 → 유스케이스 → 시스템 연산 → 설계(클래스·연산) → 테스트(모듈: 검증 로그 접두사)를 잇습니다. 테스트 열의 접두사는 Kotlin spec의 `shouldPassAll(...)` 인자이며, 같은 이름으로 Java EntryPoint가 콘솔에 검증 로그를 남깁니다.

모듈 약어: **OG** = ObservableGeneratorTest, **OP** = OperatorTest, **SU** = SubjectTest, **SC** = SchedulerTest.

## 기능 요구사항

| 요구사항 | UC | 시스템 연산 | 설계 (클래스.연산) | 테스트 |
|---|---|---|---|---|
| FR-01 | UC-01 | `of`, `range` | `Observable.of(T...)`, `Observable.range(int[, Integer])` → `toInt` | OG: `of`, `range` |
| FR-01 | UC-01 | `from` | `Observable.from(Object)`, `from(List)` | OP: `from()` (3건: 배열, List, Promise) |
| FR-01 | UC-01 | `merge`, `forkJoin`, `concat` | `Observable.merge`, `forkJoin`, `concat` | OG: `merge`, `forkJoin` / OP: `concat()` |
| FR-01 | UC-01 | `defer`, `empty`, `throwError` | `Observable.defer`, `empty` (`rxjs.EMPTY`), `throwError` → `throwErrorWith` | OP: `defer()`, `empty()`, `throwError()` |
| FR-02 | UC-01 | `interval`, `timer` | `Observable.interval(int)`, `interval()`, `timer(int, int)`, `timer(int)` → `toInt` | OG: `interval`, `timer` / OP: `timer(due)` |
| FR-03 | UC-01 | `fromEvent`, `fromMouseEvent` | `Observable.fromEvent(EventTarget, String)`, `fromMouseEvent` | OG: `fromEvent` / OP: `fromEvent(EventTarget)` |
| FR-03 | UC-01 | `fromKeyboardEvent` | `Observable.fromKeyboardEvent` | **없음** |
| FR-04 | UC-02 | `fromRequest`, `fetchJson` | `Observable.fromRequest(AjaxRequest[, Class])` (`rxjs.ajax.ajax`), `fetchJson(String[, Class])` (`rxjs.ajax.ajax.getJSON`), `AjaxRequest`, `AjaxResponse` | OG: `ajax`, `headers(ajax)` |
| FR-05 | UC-03 | 변환: `map`, `scan`, `reduce`, `toArray`, `pairwise` | `Observable.map` → `Operator.map`, `scan`, `reduce`, `toArray`, `pairwise` | OP: `map()`, `scan()`, `scanWithSeed()`, `reduce()`, `toArray()`, `pairwise()` |
| FR-05 | UC-03 | 필터: `filter`, `take`, `skip`, `distinct`, `distinctUntilChanged`, `first`, `last`, `takeUntil`, `takeWhile` | `Observable.*` → `Operator.*` | OP: `filter()`, `take()`, `skip()`, `distinct()`, `distinctUntilChanged()`, `first()`, `last()`, `takeUntil()`, `takeWhile()` |
| FR-05 | UC-03 | 결합: `zip`, `combineLatest`, `withLatestFrom`, `concatWith`, `startWith` | `Observable.*` → `Operator.*` | OP: `zip()`, `combineLatest()`, `withLatestFrom()`, `concatWith()`, `startWith()` |
| FR-05 | UC-03 | 평탄화: `mergeMap`, `concatMap`, `switchMap`, `exhaustMap` | `Observable.*` → `Operator.*` (`Observable` 인자 오버로드는 `*MapTo`) | OP: `mergeMap()`, `concatMap()`, `switchMap()`, `exhaustMap()` |
| FR-05 | UC-03 | 고차: `concatAll`, `switchAll`, `mergeAll`, `exhaustAll` | `Observable.*(Class)` → `flatten` → `Operator.*` | OP: `concatAll()`, `switchAll()`, `mergeAll()`, `exhaustAll()` |
| FR-05 | UC-03 | 버퍼: `bufferTime`, `windowTime` | `Observable.*` → `Operator.*` | OP: `bufferTime()`, `windowTime()` |
| FR-05 | UC-03 | 유틸리티: `tap`, `finalize`, `delay`, `share`, `shareReplay` | `Observable.*` → `Operator.*` | OP: `tap()`, `finalize()`, `delay()`, `share()`, `shareReplay()` |
| FR-06 | UC-03 | `debounceTime`, `debounce`, `throttleTime` | `Observable.debounceTime(int)`, `debounce(Function)`, `throttleTime(int[, AsyncScheduler])`, `throttleTime(int, boolean, boolean)` → `Operator.throttleConfig` | OP: `debounce`, `throttleTime()`, `throttleTimeScheduler()`, `throttleTimeConfig()` |
| FR-07 | UC-04 | `subscribe(...)`, `unsubscribe` | `Observable.subscribe(Observer)`, `subscribe(Consumer[, Consumer, Callback])`, `Observer.next(Consumer)`, `Subscription.unsubscribe` | OP: `subscribe(next,error,complete)`, `Observer.next()`; `unsubscribe`는 `finalize()` 검증 안에서 호출 |
| FR-08 | UC-04, UC-03 | `catchError`, `retry`, 람다 구독 에러 보고 | `Observable.catchError`, `retry`, `subscribeNext` | OP: `catchError()`, `retry()`, `subscribe(lambda)` |
| FR-09 | UC-05 | `subject`, `behavior`, `replayWithBuffer`, `async` | `Subject.subject`, `BehaviorSubject.behavior`, `ReplaySubject.replay*`, `AsyncSubject.async` | SU: `Subject`, `BehaviorSubject`, `ReplaySubject`, `AsyncSubject` / OP: `subjectFactories()` |
| FR-09 | UC-05 | `getValue`, `asObservable`, `await`, `json`, `replayWithBufferAndWindowTime` | `BehaviorSubject.getValue`, `Subject.asObservable`, `AsyncSubject.await/json`, `ReplaySubject.replayWithBufferAndWindowTime` | **없음** |
| FR-10 | UC-06 | `asyncScheduler`, `asapScheduler`, `queueScheduler`, `schedule` | `Scheduler.*`, `AsyncScheduler.schedule(...)`, `TaskScheduler.schedule` | SC: `asyncScheduler()`, `asapScheduler()`, `queueScheduler()`, `All schedulers`, `asyncScheduler.schedule(state)` / OP: `schedule() returns` |
| FR-10 | UC-06 | `animationFrameScheduler` | `Scheduler.animationFrameScheduler(Class)` | **없음** |

## 유지보수·비기능 요구사항

| 요구사항 | UC | 실현 위치 | 검증 |
|---|---|---|---|
| MR-01 | UC-07 | [구현 가이드](03-construction/implementation-guide.md) | 새 바인딩마다 OP/OG/SU/SC 검증 추가 |
| MR-02 | UC-08 | `.github/workflows/publish.yml`, [릴리스 절차](04-transition/release.md) | 릴리스마다 Central pom 200 확인 |
| NFR-01 | - | 네이티브 `@JsType(namespace="rxjs")`, 테스트 HTML의 UMD 로드 순서 | 모든 브라우저 테스트 |
| NFR-02 | - | `@JsOverlay` 체인 메서드 | 설계 검토 |
| NFR-03 | - | `toInt`, `JsArrayLike::asList` | OG: `interval`, `range` / OP: `toArray()`, `zip()` |
| NFR-04 | - | `build.gradle.kts` (`strict`, `jvmToolchain(21)`) | `./gradlew clean build`, CI `test.yaml` |
| NFR-05 | - | `LogAssertions.shouldPassAll` | 접두사를 틀리게 바꾸면 실패하는지 확인 |
| NFR-06 | - | `mavenPublishing`, `signing` | 릴리스 확인 절차 |

## 테스트 공백

위 표에서 **없음**으로 표시한 공개 API는 브라우저 테스트가 직접 호출하지 않습니다(2026-11, `src/test/java`의 이름 검색 기준).

- `Observable.fromKeyboardEvent`
- `Subject.asObservable`, `BehaviorSubject.getValue`
- `AsyncSubject.await`, `AsyncSubject.json`
- `ReplaySubject.replayWithBufferAndWindowTime`
- `Scheduler.animationFrameScheduler`

다음 반복에서 검증을 추가할 후보입니다.
