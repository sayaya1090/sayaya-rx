package dev.sayaya

import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec
import io.kotest.matchers.string.shouldNotContain
import java.lang.Thread.sleep
import kotlin.collections.map

@GwtHtml("src/test/webapp/observable_generator.html")
internal class ObservableGeneratorTest: GwtTestSpec({
    Given("Observable 생성 함수") {
        sleep(5000) // Ajax 포함 지연이 필요한 경우가 있어 대기
        val logs = document.getConsoleLogs()
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
    }
})