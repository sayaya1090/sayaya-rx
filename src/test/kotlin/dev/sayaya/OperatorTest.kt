package dev.sayaya

import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec
import io.kotest.matchers.string.shouldNotContain
import java.lang.Thread.sleep

@GwtHtml("src/test/webapp/operator.html")
internal class OperatorTest: GwtTestSpec({
    Given("Operator 연산자") {
        sleep(5000) // 비동기 작업 대기
        val logs = document.getConsoleLogs()

        When("변환 연산자를 사용할 때") {
            Then("map()은 각 값을 변환해야 한다") {
                val log = logs.find { it.toString().startsWith("map()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
            Then("scan()은 값을 누적해야 한다") {
                val log = logs.find { it.toString().startsWith("scan()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }

        When("필터링 연산자를 사용할 때") {
            Then("filter()는 조건을 통과한 값만 방출해야 한다") {
                val log = logs.find { it.toString().startsWith("filter()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
            Then("take()는 방출을 제한해야 한다") {
                val log = logs.find { it.toString().startsWith("take()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
            Then("skip()은 초기 값들을 건너뛰어야 한다") {
                val log = logs.find { it.toString().startsWith("skip()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
            Then("distinct()는 중복 값을 필터링해야 한다") {
                val logsByDistinct = logs.filter { it.toString().startsWith("distinct()") }.map { it.toString() }
                logsByDistinct.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
            Then("distinctUntilChanged()는 연속된 중복 값을 필터링해야 한다") {
                val logsByDistinct = logs.filter { it.toString().startsWith("distinctUntilChanged()") }.map { it.toString() }
                logsByDistinct.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }

        When("결합 연산자를 사용할 때") {
            Then("zip()은 같은 인덱스의 값을 쌍으로 묶어야 한다") {
                val log = logs.find { it.toString().startsWith("zip()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
            Then("combineLatest()는 최신 값들을 결합해야 한다") {
                val log = logs.find { it.toString().startsWith("combineLatest()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }

        When("평탄화 연산자를 사용할 때") {
            Then("mergeMap()은 내부 Observable들을 병합해야 한다") {
                val logsByMergeMap = logs.filter { it.toString().startsWith("mergeMap()") }.map { it.toString() }
                logsByMergeMap.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
            Then("concatMap()은 내부 Observable들을 순차적으로 연결해야 한다") {
                val logsByConcatMap = logs.filter { it.toString().startsWith("concatMap()") }.map { it.toString() }
                logsByConcatMap.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
            Then("switchMap()은 최신 Observable로 전환해야 한다") {
                val logsBySwitchMap = logs.filter { it.toString().startsWith("switchMap()") }.map { it.toString() }
                logsBySwitchMap.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
            Then("concatWith()는 Observable들을 연결해야 한다") {
                val log = logs.find { it.toString().startsWith("concatWith()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }

        When("고차 연산자를 사용할 때") {
            Then("concatAll()은 모든 내부 Observable을 순차적으로 연결해야 한다") {
                val log = logs.find { it.toString().startsWith("concatAll()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
            Then("switchAll()은 최신 Observable로 전환해야 한다") {
                val log = logs.find { it.toString().startsWith("switchAll()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
            Then("mergeAll()은 모든 내부 Observable을 병합해야 한다") {
                val log = logs.find { it.toString().startsWith("mergeAll()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
            Then("exhaustAll()은 활성 중에는 새 Observable을 무시해야 한다") {
                val log = logs.find { it.toString().startsWith("exhaustAll()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }

        When("유틸리티 연산자를 사용할 때") {
            Then("tap()은 부수 효과를 수행해야 한다") {
                val logsByTap = logs.filter { it.toString().startsWith("tap()") }.map { it.toString() }
                logsByTap.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
            Then("startWith()는 초기 값을 방출해야 한다") {
                val log = logs.find { it.toString().startsWith("startWith()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
            Then("finalize()는 완료 시 콜백을 실행해야 한다") {
                val logsByFinalize = logs.filter { it.toString().startsWith("finalize()") }.map { it.toString() }
                logsByFinalize.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }

        When("속도 제한 연산자를 사용할 때") {
            Then("debounce()는 침묵 기간 후에 값을 방출해야 한다") {
                val logsByDebounce = logs.filter { it.toString().startsWith("debounce") }.map { it.toString() }
                logsByDebounce.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
            Then("throttleTime()은 방출을 조절해야 한다") {
                val log = logs.find { it.toString().startsWith("throttleTime()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }

        When("버퍼링 연산자를 사용할 때") {
            Then("bufferTime()은 값을 버퍼링해야 한다") {
                val logsByBuffer = logs.filter { it.toString().startsWith("bufferTime()") }.map { it.toString() }
                logsByBuffer.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
            Then("windowTime()은 시간 윈도우를 생성해야 한다") {
                val logsByWindow = logs.filter { it.toString().startsWith("windowTime()") }.map { it.toString() }
                logsByWindow.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }

        When("에러 처리 연산자를 사용할 때") {
            Then("retry()는 에러 발생 시 재구독해야 한다") {
                val log = logs.find { it.toString().startsWith("retry()") } as String
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }
    }
})