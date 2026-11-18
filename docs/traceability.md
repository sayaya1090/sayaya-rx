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
| FR-03 | UC-01 | `fromEvent`, `fromMouseEvent` | `Observable.fromEvent(EventTarget, String)`, `fromMouseEvent(EventTarget, EventType<MouseEvent, ?>)` | OG: `fromEvent`, `fromMouseEvent(mousedown)` / OP: `fromEvent(EventTarget)` |
| FR-03 | UC-01 | `fromKeyboardEvent` | `Observable.fromKeyboardEvent(EventTarget, EventType<KeyboardEvent, ?>)` | OG: `fromKeyboardEvent()` |
| FR-04 | UC-02 | `fromRequest`, `fetchJson` | `Observable.fromRequest(AjaxRequest[, Class])` (`rxjs.ajax.ajax`), `fetchJson(String[, Class])` (`rxjs.ajax.ajax.getJSON`), `AjaxRequest`, `AjaxResponse` | OG: `ajax`, `headers(ajax)` |
| FR-05 | UC-03 | 변환: `map`, `scan`, `reduce`, `toArray`, `pairwise` | `Observable.map` → `Operator.map`, `scan`, `reduce`, `toArray`, `pairwise` | OP: `map()`, `scan()`, `scanWithSeed()`, `reduce()`, `toArray()`, `pairwise()` |
| FR-05 | UC-03 | 필터: `filter`, `take`, `skip`, `distinct`, `distinctUntilChanged`, `first`, `last`, `takeUntil`, `takeWhile` | `Observable.*` → `Operator.*` | OP: `filter()`, `take()`, `skip()`, `distinct()`, `distinctUntilChanged()`, `first()`, `last()`, `takeUntil()`, `takeWhile()` |
| FR-05 | UC-03 | 결합: `zip`, `combineLatest`, `withLatestFrom`, `concatWith`, `startWith` | `Observable.*` → `Operator.*` | OP: `zip()`, `combineLatest()`, `withLatestFrom()`, `concatWith()`, `startWith()` |
| FR-05 | UC-03 | 평탄화: `mergeMap`, `concatMap`, `switchMap`, `exhaustMap` | `Observable.*` → `Operator.*` (`Observable` 인자 오버로드는 `v -> target` 매퍼로 위임. deprecated `*MapTo`는 3.0.0에서 제거) | OP: `mergeMap()`, `concatMap()`, `switchMap()`, `exhaustMap()` |
| FR-05 | UC-03 | 고차: `concatAll`, `switchAll`, `mergeAll`, `exhaustAll` | `Observable.*(Class)` → `flatten` → `Operator.*` | OP: `concatAll()`, `switchAll()`, `mergeAll()`, `exhaustAll()` |
| FR-05 | UC-03 | 버퍼: `bufferTime`, `windowTime` | `Observable.*` → `Operator.*` | OP: `bufferTime()`, `windowTime()` |
| FR-05 | UC-03 | 유틸리티: `tap`, `finalize`, `delay`, `share`, `shareReplay` | `Observable.*` → `Operator.*` | OP: `tap()`, `finalize()`, `delay()`, `share()`, `shareReplay()` |
| FR-06 | UC-03 | `debounceTime`, `debounce`, `throttleTime` | `Observable.debounceTime(int)`, `debounce(Function)`, `throttleTime(int[, AsyncScheduler])`, `throttleTime(int, boolean, boolean)` → `Operator.throttleConfig` | OP: `debounce`, `throttleTime()`, `throttleTimeScheduler()`, `throttleTimeConfig()` |
| FR-07 | UC-04 | `subscribe(...)`, `unsubscribe` | `Observable.subscribe(Observer)`, `subscribe(Consumer[, Consumer, Callback])`, `Observer.next(Consumer)`, `Subscription.unsubscribe` | OP: `subscribe(next,error,complete)`, `Observer.next()`; `unsubscribe`는 `finalize()` 검증 안에서 호출 |
| FR-08 | UC-04, UC-03 | `catchError`, `retry`, 람다 구독 에러 보고 | `Observable.catchError`, `retry`, `subscribeNext` | OP: `catchError()`, `retry()`, `subscribe(lambda)` |
| FR-09 | UC-05 | `subject`, `behavior`, `replayWithBuffer`, `async` | `Subject.subject`, `BehaviorSubject.behavior`, `ReplaySubject.replay*`, `AsyncSubject.async` | SU: `Subject`, `BehaviorSubject`, `ReplaySubject`, `AsyncSubject` / OP: `subjectFactories()` |
| FR-09 | UC-05 | `getValue`, `asObservable`, `await`, `json`, `replayWithBufferAndWindowTime` | `BehaviorSubject.getValue`, `Subject.asObservable`, `AsyncSubject.await/json`, `ReplaySubject.replayWithBufferAndWindowTime` | SU: `getValue()`, `asObservable()`, `await()`, `json()`, `replayWithBufferAndWindowTime()` |
| FR-10 | UC-06 | `asyncScheduler`, `asapScheduler`, `queueScheduler`, `schedule` | `Scheduler.*`, `AsyncScheduler.schedule(...)`, `TaskScheduler.schedule` | SC: `asyncScheduler()`, `asapScheduler()`, `queueScheduler()`, `All schedulers`, `asyncScheduler.schedule(state)` / OP: `schedule() returns` |
| FR-10 | UC-06 | `animationFrameScheduler` | `Scheduler.animationFrameScheduler()` | SC: `animationFrameScheduler()` |
| FR-05 | UC-03 | 필터(RxJS 7 추가): `takeLast`, `skipLast`, `skipWhile`, `skipUntil`, `elementAt`, `ignoreElements`, `single`, `distinctUntilKeyChanged` | `Observable.*` → `Operator.*` | OP: `takeLast()`, `skipLast()`, `skipWhile()`, `skipUntil()`, `elementAt()`, `ignoreElements()`, `single()`, `distinctUntilKeyChanged()` |
| FR-05 | UC-03 | 집계·조건: `count`, `every`, `find`, `findIndex`, `isEmpty`, `defaultIfEmpty`, `throwIfEmpty`, `max`, `min`, `sequenceEqual` | `Observable.*` → `Operator.*` (`count`/`findIndex`는 `toInt`) | OP: `count()`, `every()`, `find()`, `isEmpty()`, `defaultIfEmpty()`, `max()`, `sequenceEqual()` |
| FR-05 | UC-03 | 결합(RxJS 7 추가): `mergeWith`, `combineLatestWith`, `zipWith`, `raceWith`, `endWith`, `combineLatestAll`, `zipAll` | `Observable.*` → `Operator.*` (결합 결과는 `List<Object>`) | OP: `mergeWith()`, `combineLatestWith()`, `raceWith()`, `endWith()`, `combineLatestAll()` |
| FR-05 | UC-03 | 변환(RxJS 7 추가): `expand`, `mergeScan`, `switchScan`, `groupBy`, `materialize`, `dematerialize` | `Observable.*` → `Operator.*`, `GroupedObservable`, `ObservableNotification`, `IndexedAccumulator` | OP: `expand()`, `mergeScan()`, `groupBy()`, `materialize()` |
| FR-05 | UC-03 | 버퍼·창(RxJS 7 추가): `bufferCount`, `buffer`, `bufferWhen`, `bufferToggle`, `windowCount`, `window`, `windowWhen`, `windowToggle` | `Observable.*` → `Operator.*` (버퍼는 `List<T>`) | OP: `bufferCount()`, `buffer()`, `bufferWhen()`, `bufferToggle()`, `window()`, `windowToggle()`, `windowWhen()` |
| FR-05 | UC-03 | 유틸리티(RxJS 7 추가): `timestamp`, `timeInterval`, `repeat`, `observeOn`, `subscribeOn`, `delayWhen` | `Observable.*` → `Operator.*`, `Timestamp`, `TimeInterval`, `Operator.repeatConfig` | OP: `timestamp()`, `repeat()`, `repeat(config)`, `observeOn()`, `subscribeOn()`, `delayWhen()` |
| FR-06 | UC-03 | `auditTime`, `audit`, `sampleTime`, `sample`, `throttle`, `timeout` | `Observable.*` → `Operator.*` (`throttle`은 `Operator.throttleConfig`, `timeout(each, fallback)`은 TimeoutConfig) | OP: `auditTime()`, `audit()`, `sampleTime()`, `sample()`, `throttle()`, `throttle(trailing)`, `timeout()` |
| FR-08 | UC-04, UC-03 | `retry(config)`, `onErrorResumeNextWith` | `Observable.retry(int, int[, boolean])`, `retry(int, BiFunction)` → `Operator.retryConfig`, `RetryDelay` | OP: `retry(config)`, `onErrorResumeNextWith()` |
| FR-11 | UC-01 | `race`, `iif`, `generate`, `never`, `using`, `scheduled`, `fromEventPattern`, `partition` | `Observable.race`, `iif` → `iifNative`, `generate` → `generateNative`, `never` (`rxjs.NEVER`), `using`, `scheduled`, `fromEventPattern`, `partition` → `partitionNative` | OG: `never()`, `iif()`, `generate()`, `using()`, `scheduled()`, `fromEventPattern()`, `partition()` / OP: `raceWith()` |
| FR-12 | UC-04 | `firstValueFrom`, `lastValueFrom` | `Observable.firstValueFrom(Observable[, T])`, `lastValueFrom(Observable[, T])` | OG: `firstValueFrom()`, `lastValueFrom()` |
| FR-13 | UC-05 | `connectable`, `connect`, `share(config)`, `shareReplay(config)`, `groupBy` | `Observable.connectable(...)` → `Connectable.connect()`, `Observable.connect(Function)`, `share(boolean, boolean, boolean)`, `shareReplay(int, [int,] boolean)` | OG: `connectable()` / OP: `connect()`, `share(config)`, `groupBy()` |

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

2026-11 기준, 위 표의 모든 공개 API를 브라우저 테스트가 직접 호출합니다(공백 없음).

이전에 공백이던 7개 API(`fromKeyboardEvent`, `asObservable`, `getValue`, `await`, `json`, `replayWithBufferAndWindowTime`, `animationFrameScheduler`)에 검증을 추가하는 과정에서 결함 하나를 찾아 고쳤습니다. `fromKeyboardEvent`/`fromMouseEvent`가 `EventType<…, Element>`만 받아서, Elemento가 `EventType<…, EventTarget>`으로 선언한 `keydown`·`keyup`·`keypress`·`mousedown` 등을 넘길 수 없었습니다. 지금은 `EventType<…, ?>`를 받습니다(기존 호출은 그대로 컴파일됨).
