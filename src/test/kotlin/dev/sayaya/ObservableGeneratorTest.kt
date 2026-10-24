package dev.sayaya

import com.microsoft.playwright.Page
import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotContain
import kotlin.collections.map

@GwtHtml("observable_generator.html")
internal class ObservableGeneratorTest: GwtTestSpec({
    Given("Observable 생성 함수") {
        // 비동기 작업 대기
        page.waitForFunction(
            "window.__TEST_DONE__ === true",
            null,
            Page.WaitForFunctionOptions().setTimeout(30_000.0)
        )
        val logs = page.getConsoleLogs()
        When("of()로 Observable을 생성할 때") {
            val log = logs.find { line -> line.toString().startsWith("of") } as String
            Then("값들이 순서대로 방출되고 완료되어야 한다") {
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }
        When("range()로 Observable을 생성할 때") {
            val log = logs.find { line -> line.toString().startsWith("range") } as String
            Then("값들이 순서대로 방출되고 완료되어야 한다") {
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }
        When("fromEvent()로 Observable을 생성할 때") {
            val log = logs.find { line -> line.toString().startsWith("fromEvent") } as String
            Then("DOM 이벤트를 캡처해야 한다") {
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }
        When("ajax()로 Observable을 생성할 때") {
            val logsByAjax = logs.filter { line -> line.toString().startsWith("ajax") }.map { it.toString() }
            Then("HTTP 요청을 통해 데이터를 가져와야 한다") {
                logsByAjax.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }
        When("timer()로 Observable을 생성할 때") {
            val logsByTimer = logs.filter { line -> line.toString().startsWith("timer") }.map { it.toString() }
            Then("일정 간격으로 값을 방출해야 한다") {
                logsByTimer.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }
        When("merge()로 Observable을 생성할 때") {
            val logsByMerge = logs.filter { line -> line.toString().startsWith("merge") }.map { it.toString() }
            Then("여러 Observable의 값들을 병합해야 한다") {
                logsByMerge.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }
        When("forkJoin()으로 Observable을 생성할 때") {
            val logsByForkJoin = logs.filter { line -> line.toString().startsWith("forkJoin") }.map { it.toString() }
            Then("모든 Observable이 완료된 후 마지막 값들을 조합해야 한다") {
                logsByForkJoin.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }
        When("interval()로 Observable을 생성할 때") {
            val logsByInterval = logs.filter { line -> line.toString().startsWith("interval") }.map { it.toString() }
            Then("지정한 주기로 정수를 순서대로 방출해야 한다") {
                logsByInterval.shouldNotBeEmpty()
                logsByInterval.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }
        When("ajax 요청/응답 headers를 다룰 때") {
            val logsByHeaders = logs.filter { line -> line.toString().startsWith("headers(ajax)") }.map { it.toString() }
            Then("요청 헤더가 전달되고 응답 헤더를 읽을 수 있어야 한다") {
                logsByHeaders.shouldNotBeEmpty()
                logsByHeaders.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }
    }
})
