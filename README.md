# sayaya-rx

GWT(Google Web Toolkit)를 위한 RxJS 바인딩 라이브러리입니다. Java/Kotlin 코드에서 RxJS의 반응형 프로그래밍 기능을 활용할 수 있도록 합니다.

## 특징

- RxJS Observable을 Java/Kotlin에서 사용
- Subject (AsyncSubject, BehaviorSubject, ReplaySubject) 지원
- 다양한 Operator (map, filter, debounce, throttle 등) 제공
- Scheduler 지원 (AsyncScheduler)
- GWT JsInterop을 통한 네이티브 RxJS 연동

## 설치

Maven Central에서 받으려면 (인증 불필요):

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.sayaya:rx:3.0.0")
}
```

GitHub Packages에서 받으려면 (GitHub 토큰 필요):

```gradle
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/sayaya1090/maven")
        credentials {
            username = project.findProperty("github_username") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("github_password") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("dev.sayaya:rx:3.0.0")
}
```

GWT 모듈에 다음 내용을 추가하세요:
```xml
<inherits name="dev.sayaya.Rx"/>
```

## 주요 구성 요소

### Observable
- `of()`: 값을 Observable로 변환
- `range()`: 숫자 시퀀스 생성
- `from()`: 배열, Promise 등에서 Observable 생성
- `merge()`: 여러 Observable 병합
- `fromEvent()`: DOM 이벤트를 Observable로 변환
- `interval()`: 주기적으로 값 발행
- `fromRequest()` / `fetchJson()`: HTTP 요청(RxJS `ajax`)을 Observable로 변환
- `race()`, `iif()`, `generate()`, `never()`, `using()`, `scheduled()`, `fromEventPattern()`, `partition()`, `connectable()`
- `firstValueFrom()` / `lastValueFrom()`: Observable을 Promise로 변환

### Operators
- **변환**: map, mergeMap, concatMap, switchMap, exhaustMap, expand, mergeScan, switchScan, groupBy, materialize, dematerialize
- **필터링**: filter, take, takeLast, takeUntil, takeWhile, skip, skipLast, skipWhile, skipUntil, first, last, single, elementAt, find, findIndex, ignoreElements, distinct, distinctUntilChanged, distinctUntilKeyChanged
- **시간**: debounceTime, throttleTime, throttle, auditTime, audit, sampleTime, sample, delay, delayWhen, timeout, timestamp, timeInterval
- **버퍼·창**: bufferTime, bufferCount, buffer, bufferWhen, bufferToggle, windowTime, windowCount, window, windowWhen, windowToggle
- **조합**: zip, zipWith, zipAll, combineLatest, combineLatestWith, combineLatestAll, mergeWith, raceWith, concatWith, startWith, endWith, withLatestFrom
- **집계·조건**: count, every, isEmpty, defaultIfEmpty, throwIfEmpty, max, min, reduce, toArray, sequenceEqual
- **에러 처리**: catchError, retry(횟수·지연·delay 함수), onErrorResumeNextWith
- **멀티캐스트**: share(reset 설정), shareReplay(refCount·windowTime), connect
- **스케줄링**: observeOn, subscribeOn
- **기타**: tap, scan, finalize, repeat(횟수·지연)

### Subject
- **Subject**: 기본 멀티캐스트 Observable
- **BehaviorSubject**: 현재 값을 유지하는 Subject
- **ReplaySubject**: 이전 값을 재생하는 Subject
- **AsyncSubject**: 완료 시 마지막 값만 발행

### Scheduler
- **AsyncScheduler**: 비동기 작업 스케줄링
- **TaskScheduler**: 작업 지연/반복 실행

## 사용 예제

```java
// Observable 생성
Observable<Integer> numbers = Observable.range(1, 5);

// Operator 체이닝
numbers
    .map(x -> x * 2)
    .filter(x -> x > 5)
    .subscribe(System.out::println);

// Subject 사용
Subject<String> subject = Subject.subject();
subject.subscribe(value -> System.out.println("Observer 1: " + value));
subject.subscribe(value -> System.out.println("Observer 2: " + value));
subject.next("Hello");

// 이벤트 처리
Observable.fromMouseEvent(element, click)
    .debounceTime(300)
    .subscribe(event -> handleClick(event));

// HTTP 요청
Observable.fetchJson("https://api.example.com/data", MyClass.class)
    .map(response -> response.getData())
    .catchError(error -> Observable.of(defaultValue))
    .subscribe(data -> updateUI(data));
```

## 개발

### 빌드
```bash
./gradlew build
```

### 테스트
```bash
./gradlew test
```

### GWT 개발 모드
```bash
./gradlew gwtDevMode
```

### 개발 문서
Unified Process 형식의 개발 문서(비전, 요구사항, 유스케이스, 설계 모델, 아키텍처, 테스트, 릴리스 절차)는 [docs/](docs/README.md)에 있습니다.

## 기술 스택

- GWT 2.13.1
- Kotlin 2.4.21
- RxJS (JsInterop 바인딩)
- Elemento 2.5.14

## 버전

현재 버전: 3.0.0 (2.x에서 옮기는 방법은 [3.0 마이그레이션 가이드](docs/04-transition/migration-3.0.md))

## 라이선스

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)
