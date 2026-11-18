# 3.0 마이그레이션 가이드

3.0.0은 [설계 모델의 설계 부채](../02-elaboration/design-model.md#알려진-설계-부채)를 해소하기 위해 **호환성을 깨는 변경**을 포함합니다. 아래 표대로 고치면 2.x 코드를 3.0.0으로 옮길 수 있습니다. 런타임 동작(RxJS 호출)은 바뀌지 않았고, Java 시그니처만 바뀌었습니다.

## 1. 결합 결과는 모두 `List`

`zip`, `toArray`, `pairwise`와 같은 방식으로 맞췄습니다.

| API | 2.x | 3.0 |
|---|---|---|
| `Observable.forkJoin(Observable<?>...)` | `Observable<Object[]>` | `Observable<List<Object>>` |
| `observable.combineLatest(other)` | `Observable<Object[]>` | `Observable<List<Object>>` |
| `observable.withLatestFrom(other)` | `Observable<Object[]>` | `Observable<List<Object>>` |
| `observable.bufferTime(...)` | `Observable<T[]>` | `Observable<List<T>>` |
| `Operator.bufferTime(...)` | `OperatorFunction<T, T[]>` | `OperatorFunction<T, JsArray<T>>` |

```java
// 2.x
forkJoin(a, b).map(arr -> arr[0] + "/" + arr[1]);
source.bufferTime(500).subscribe(arr -> Arrays.stream(arr).forEach(...));
// 3.0
forkJoin(a, b).map(list -> list.get(0) + "/" + list.get(1));
source.bufferTime(500).subscribe(list -> list.forEach(...));
```

## 2. `Class<T>` 타입 토큰 오버로드 제거

무인자 제네릭 팩토리를 쓰고, 타입은 대입 대상이나 타입 인자로 정합니다.

| 2.x | 3.0 |
|---|---|
| `Subject.subject(Integer.class)` | `Subject<Integer> s = Subject.subject();` 또는 `Subject.<Integer>subject()` |
| `ReplaySubject.replay(Integer.class)` | `ReplaySubject.replay()` |
| `ReplaySubject.replayWithBuffer(Integer.class, n)` | `ReplaySubject.replayWithBuffer(n)` |
| `ReplaySubject.replayWithBufferAndWindowTime(Integer.class, n, ms)` | `ReplaySubject.replayWithBufferAndWindowTime(n, ms)` |
| `AsyncSubject.async(Integer.class)` | `AsyncSubject.async()` |
| `Scheduler.asyncScheduler(Integer.class)` | `Scheduler.asyncScheduler()` (이제 `<T> AsyncScheduler<T>`; 2.x의 무인자 버전은 `AsyncScheduler<Void>`였음) |
| `Scheduler.animationFrameScheduler(Void.class)` | `Scheduler.animationFrameScheduler()` |

유지되는 `Class` 인자: `fromRequest(request, Class<T>)`, `fetchJson(url, Class<T>)`, `concatAll/switchAll/mergeAll/exhaustAll(Class<V>)`. 이들은 대체할 무인자 형태가 없어 체인 중간에서 타입을 정하는 유일한 수단이므로 그대로 둡니다.

## 3. 시간 인자는 `int`, 스케줄러는 `AsyncScheduler<?>`

| API | 2.x | 3.0 |
|---|---|---|
| `Operator.debounceTime` | `(JsNumber due)` | `(int due)` |
| `Operator.throttleTime` | `(JsNumber due, AsyncScheduler<V>[, config])` | `(int due, AsyncScheduler<?>[, config])` |
| `observable.throttleTime(ms, scheduler)` | `AsyncScheduler<T>`만 허용 | 아무 `AsyncScheduler<?>`나 허용 (비파괴 확장) |

`Observable`의 체인 메서드 `debounceTime(int)`/`throttleTime(int, ...)`만 쓰던 코드는 바꿀 필요가 없습니다. `Operator`를 직접 `pipe`에 넘기던 코드만 `new JsNumber(ms)`를 `ms`로 바꿉니다.

## 4. RxJS 7 deprecated `*MapTo` 바인딩 제거

RxJS 7에서 deprecated된 `mergeMapTo`/`concatMapTo`/`switchMapTo`의 네이티브 바인딩을 제거했습니다.

| 2.x | 3.0 |
|---|---|
| `Operator.mergeMapTo(target)` | `Operator.mergeMap(v -> target)` |
| `Operator.concatMapTo(target)` | `Operator.concatMap(v -> target)` |
| `Operator.switchMapTo(target)` | `Operator.switchMap(v -> target)` |

`observable.mergeMap(target)`/`concatMap(target)`/`switchMap(target)`(Observable 인자 오버로드)는 그대로 쓸 수 있습니다. 내부적으로 `v -> target` 매퍼로 바뀌었고 동작은 같습니다.

## 5. 추가된 API (기존 코드에 영향 없음)

RxJS 7.8.2 타입 정의에서 deprecated가 아닌 시그니처만 바인딩했습니다. `thisArg`·`scheduler` 인자처럼 deprecated된 오버로드는 넣지 않았습니다.

| 분류 | API |
|---|---|
| 생성 함수 | `race`, `iif`, `generate`, `never`(`rxjs.NEVER`), `using`, `scheduled`, `fromEventPattern`, `partition`, `connectable` |
| Promise 연동 | `firstValueFrom`, `lastValueFrom` (값이 없을 때의 `defaultValue` 오버로드 포함) |
| 필터·집계 | `takeLast`, `skipLast`, `skipWhile`, `skipUntil`, `elementAt`, `ignoreElements`, `single`, `count`, `every`, `find`, `findIndex`, `isEmpty`, `defaultIfEmpty`, `throwIfEmpty`, `max`, `min`, `sequenceEqual`, `distinctUntilKeyChanged` |
| 결합 | `mergeWith`, `combineLatestWith`, `zipWith`, `raceWith`, `endWith`, `combineLatestAll`, `zipAll`, `onErrorResumeNextWith` |
| 시간 | `auditTime`, `audit`, `sampleTime`, `sample`, `throttle`, `timeout`, `delayWhen`, `timestamp`, `timeInterval` |
| 버퍼·창 | `bufferCount`, `buffer`, `bufferWhen`, `bufferToggle`, `windowCount`, `window`, `windowWhen`, `windowToggle` |
| 변환·멀티캐스트 | `expand`, `mergeScan`, `switchScan`, `groupBy`, `materialize`, `dematerialize`, `connect`, `observeOn`, `subscribeOn`, `repeat` |
| 설정 오버로드 | `retry(count, delayMs[, resetOnSuccess])`, `retry(count, (error, n) -> Observable)`, `repeat(count, delayMs)`, `share(resetOnError, resetOnComplete, resetOnRefCountZero)`, `shareReplay(bufferSize, [windowTime,] refCount)`, `timeout(each, fallback)` |
| 새 타입 | `GroupedObservable<K, T>`, `Connectable<T>`, `ObservableNotification<T>`, `Timestamp<T>`, `TimeInterval<T>`, 함수 인터페이스 `Comparer`, `IndexedAccumulator`, `RetryDelay`, `Subscription.closed` |

## 변경 없음

- 패키지 구조(순환 의존은 [AD-07](../02-elaboration/architecture.md#아키텍처-결정-요약)로 수용)
- GWT 모듈 이름 `dev.sayaya.Rx`, Maven 좌표 `dev.sayaya:rx`
- 테스트 페이지에 필요한 RxJS UMD(7.x) 로드 순서
