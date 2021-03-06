package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NamedArgumentsSpec : Spek({

    val defaultThreshold = 2
    val namedArguments by memoized { NamedArguments(threshold = defaultThreshold) }

    describe("NameArguments rule") {

        val errorMessage = "Function invocation with more than $defaultThreshold parameters must all be named"
        it("invocation with more than 2 parameters should throw error") {
            val code = """
                fun sum(a: Int, b:Int, c:Int) {
                    println(a + b + c)
                }
                fun call() {
                    sum(1, 2, 3)
                }
                """
            val findings = namedArguments.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(errorMessage)
        }

        it("Function invocation with more than 2 parameters should not throw error if named") {
            val code = """
                fun sum(a: Int, b:Int, c:Int) {
                    println(a + b + c)
                }
                fun call() {
                    sum(a = 1, b = 2, c = 3)
                }
                """
            val findings = namedArguments.compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("invocation with more than 2 parameters should throw error if even one is not named") {
            val code = """
                fun sum(a: Int, b:Int, c:Int) {
                    println(a + b + c)
                }
                fun call() {
                    sum(1, b = 2, c = 3)
                }
                """
            val findings = namedArguments.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(errorMessage)
        }

        it("invocation with less than 3 parameters should not throw error") {
            val code = """
                fun sum(a: Int, b:Int) {
                    println(a + b)
                }
                fun call() {
                    sum(1, 2)
                }
                """
            val findings = namedArguments.compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("invocation with less than 3 named parameters should not throw error") {
            val code = """
                fun sum(a: Int, b:Int) {
                    println(a + b)
                }
                fun call() {
                    sum(a = 1, b = 2)
                }
                """
            val findings = namedArguments.compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("constructor invocation with more than 3 non-named parameters should throw error") {
            val code = """
                class C(val a: Int, val b:Int, val c:Int)
                
                val obj = C(1, 2, 3)
            """
            val findings = namedArguments.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(errorMessage)
        }

        it("constructor invocation with more than 3 named parameters should not throw error") {
            val code = """
                class C(val a: Int, val b:Int, val c:Int)
                
                val obj = C(a = 1, b = 2, c= 3)
            """
            val findings = namedArguments.compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("constructor invocation with less than 3 non-named parameters should not throw error") {
            val code = """
                class C(val a: Int, val b:Int)
                
                val obj = C(1, 2)
            """
            val findings = namedArguments.compileAndLint(code)
            assertThat(findings).hasSize(0)
        }
    }
})
