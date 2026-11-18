package dev.sayaya

import com.microsoft.playwright.Page
import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec

@GwtHtml("operator.html")
internal class OperatorTest: GwtTestSpec({
    Given("Operator 연산자") {
        // 비동기 작업 대기
        page.waitForFunction(
            "window.__TEST_DONE__ === true",
            null,
            Page.WaitForFunctionOptions().setTimeout(30_000.0)
        )
        val logs = page.getConsoleLogs()

        When("변환 연산자를 사용할 때") {
            Then("map()은 각 값을 변환해야 한다") { logs.shouldPassAll("map()") }
            Then("scan()은 값을 누적해야 한다") { logs.shouldPassAll("scan()") }
        }

        When("필터링 연산자를 사용할 때") {
            Then("filter()는 조건을 통과한 값만 방출해야 한다") { logs.shouldPassAll("filter()") }
            Then("take()는 방출을 제한해야 한다") { logs.shouldPassAll("take()") }
            Then("skip()은 초기 값들을 건너뛰어야 한다") { logs.shouldPassAll("skip()") }
            Then("distinct()는 중복 값을 필터링해야 한다") { logs.shouldPassAll("distinct()") }
            Then("distinctUntilChanged()는 연속된 중복 값을 필터링해야 한다") { logs.shouldPassAll("distinctUntilChanged()") }
        }

        When("결합 연산자를 사용할 때") {
            Then("zip()은 같은 인덱스의 값을 쌍으로 묶어야 한다") { logs.shouldPassAll("zip()") }
            Then("combineLatest()는 최신 값들을 결합해야 한다") { logs.shouldPassAll("combineLatest()") }
        }

        When("평탄화 연산자를 사용할 때") {
            Then("mergeMap()은 내부 Observable들을 병합해야 한다") { logs.shouldPassAll("mergeMap()") }
            Then("concatMap()은 내부 Observable들을 순차적으로 연결해야 한다") { logs.shouldPassAll("concatMap()") }
            Then("switchMap()은 최신 Observable로 전환해야 한다") { logs.shouldPassAll("switchMap()") }
            Then("concatWith()는 Observable들을 연결해야 한다") { logs.shouldPassAll("concatWith()") }
        }

        When("고차 연산자를 사용할 때") {
            Then("concatAll()은 모든 내부 Observable을 순차적으로 연결해야 한다") { logs.shouldPassAll("concatAll()") }
            Then("switchAll()은 최신 Observable로 전환해야 한다") { logs.shouldPassAll("switchAll()") }
            Then("mergeAll()은 모든 내부 Observable을 병합해야 한다") { logs.shouldPassAll("mergeAll()") }
            Then("exhaustAll()은 활성 중에는 새 Observable을 무시해야 한다") { logs.shouldPassAll("exhaustAll()") }
        }

        When("유틸리티 연산자를 사용할 때") {
            Then("tap()은 부수 효과를 수행해야 한다") { logs.shouldPassAll("tap()") }
            Then("startWith()는 초기 값을 방출해야 한다") { logs.shouldPassAll("startWith()") }
            Then("finalize()는 완료 시 콜백을 실행해야 한다") { logs.shouldPassAll("finalize()") }
        }

        When("속도 제한 연산자를 사용할 때") {
            Then("debounce()는 침묵 기간 후에 값을 방출해야 한다") { logs.shouldPassAll("debounce") }
            Then("throttleTime()은 방출을 조절해야 한다") { logs.shouldPassAll("throttleTime()") }
        }

        When("버퍼링 연산자를 사용할 때") {
            Then("bufferTime()은 값을 버퍼링해야 한다") { logs.shouldPassAll("bufferTime()") }
            Then("windowTime()은 시간 윈도우를 생성해야 한다") { logs.shouldPassAll("windowTime()") }
        }

        When("에러 처리 연산자를 사용할 때") {
            Then("retry()는 에러 발생 시 재구독해야 한다") { logs.shouldPassAll("retry()") }
        }

        When("람다로 구독한 스트림에서 에러가 날 때") {
            Then("에러가 조용히 사라지지 않고 처리되지 않은 에러로 보고되어야 한다") { logs.shouldPassAll("subscribe(lambda)") }
        }

        When("from()으로 Observable을 생성할 때") {
            Then("배열, List, Promise를 입력으로 받아야 한다") { logs.shouldPassAll("from()", expectedCount = 3) }
        }

        When("추가된 연산자와 API를 사용할 때") {
            listOf(
                "takeUntil()", "first()", "last()", "reduce()", "delay()", "share()", "shareReplay()",
                "withLatestFrom()", "exhaustMap()", "toArray()", "takeWhile()", "pairwise()",
                "concat()", "defer()", "empty()", "throwError()", "subscribe(next,error,complete)",
                "timer(due)", "fromEvent(EventTarget)", "schedule() returns", "catchError()",
                "scanWithSeed()", "throttleTimeScheduler()", "throttleTimeConfig()", "subjectFactories()", "Observer.next()"
            ).forEach { prefix ->
                Then("$prefix 검증이 실행되고 통과해야 한다") { logs.shouldPassAll(prefix) }
            }
        }

        When("RxJS 7 추가 연산자를 사용할 때") {
            listOf(
                "takeLast()", "skipLast()", "skipWhile()", "skipUntil()", "elementAt()", "ignoreElements()", "single()",
                "count()", "every()", "find()", "isEmpty()", "defaultIfEmpty()", "max()",
                "mergeWith()", "combineLatestWith()", "raceWith()", "endWith()", "onErrorResumeNextWith()",
                "auditTime()", "audit()", "sampleTime()", "sample()", "throttle()", "throttle(trailing)", "timeout()", "delayWhen()", "timestamp()",
                "repeat()", "repeat(config)", "retry(config)", "share(config)", "connect()",
                "bufferCount()", "buffer()", "bufferWhen()", "bufferToggle()", "window()", "windowToggle()", "windowWhen()",
                "observeOn()", "subscribeOn()", "groupBy()", "materialize()", "expand()", "mergeScan()",
                "sequenceEqual()", "combineLatestAll()", "distinctUntilKeyChanged()"
            ).forEach { prefix ->
                Then("$prefix 검증이 실행되고 통과해야 한다") { logs.shouldPassAll(prefix) }
            }
        }
    }
})
