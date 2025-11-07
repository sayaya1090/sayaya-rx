package dev.sayaya

import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec
import io.kotest.matchers.string.shouldNotContain
import java.lang.Thread.sleep

@GwtHtml("src/test/webapp/subject.html")
internal class SubjectTest: GwtTestSpec({
    Given("Subject 타입") {
        sleep(1000)
        val logs = document.getConsoleLogs()
        When("Subject를 사용할 때") {
            val log = logs.find { line -> line.toString().startsWith("Subject") } as String
            Then("모든 구독자에게 값을 멀티캐스트해야 한다") {
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }
        When("BehaviorSubject를 사용할 때") {
            val log = logs.find { line -> line.toString().startsWith("BehaviorSubject") } as String
            Then("초기값과 이후 값들을 방출해야 한다") {
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }
        When("ReplaySubject를 사용할 때") {
            val logsByReplay = logs.filter { line -> line.toString().startsWith("ReplaySubject") }.map { it.toString() }
            Then("버퍼 크기만큼 이전 값들을 재생해야 한다") {
                logsByReplay.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }
        When("AsyncSubject를 사용할 때") {
            val logsByAsync = logs.filter { line -> line.toString().startsWith("AsyncSubject") }.map { it.toString() }
            Then("완료 시 마지막 값만 방출해야 한다") {
                logsByAsync.forEach { log ->
                    println(log)
                    log shouldNotContain "Assertion failed!"
                }
            }
        }
    }
})