package dev.sayaya

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain

/**
 * GWT EntryPoint가 콘솔에 남긴 검증 로그 중 [prefix]로 시작하는 줄을 모두 찾아 검사합니다.
 *
 * - 해당 줄이 하나도 없으면 실패합니다(검증이 실행되지 않았는데 통과하는 것을 막습니다).
 * - [expectedCount]를 주면 줄 수가 정확히 일치해야 합니다.
 * - 모든 줄에 "Assertion failed!"가 없어야 합니다.
 */
internal fun List<Any?>.shouldPassAll(prefix: String, expectedCount: Int? = null): List<String> {
    val found = filter { it.toString().startsWith(prefix) }.map { it.toString() }
    withClue("'$prefix'로 시작하는 콘솔 로그가 없습니다. 검증이 실행되지 않았습니다.") {
        found.shouldNotBeEmpty()
    }
    if (expectedCount != null) withClue("'$prefix' 로그 수") { found.size shouldBe expectedCount }
    found.forEach { log ->
        println(log)
        log shouldNotContain "Assertion failed!"
    }
    return found
}
