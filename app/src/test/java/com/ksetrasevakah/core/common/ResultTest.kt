package com.ksetrasevakah.core.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ResultTest {

    @Test
    fun `Success wraps data correctly`() {
        val result = Result.Success("hello")
        assertEquals("hello", result.data)
    }

    @Test
    fun `Error wraps message and throwable`() {
        val exception = RuntimeException("fail")
        val result = Result.Error("something failed", exception)
        assertEquals("something failed", result.message)
        assertEquals(exception, result.throwable)
    }

    @Test
    fun `getOrNull returns data for Success`() {
        val result: Result<Int> = Result.Success(42)
        assertEquals(42, result.getOrNull())
    }

    @Test
    fun `getOrNull returns null for Error`() {
        val result: Result<Int> = Result.Error("error")
        assertNull(result.getOrNull())
    }

    @Test
    fun `getOrThrow throws for Error`() {
        val result: Result<Int> = Result.Error("error")
        assertThrows<IllegalStateException> { result.getOrThrow() }
    }

    @Test
    fun `isSuccess isError isLoading flags are correct`() {
        assertTrue(Result.Success("x").isSuccess)
        assertFalse(Result.Success("x").isError)
        assertFalse(Result.Success("x").isLoading)

        assertTrue(Result.Error("e").isError)
        assertFalse(Result.Error("e").isSuccess)
        assertFalse(Result.Error("e").isLoading)

        assertTrue(Result.Loading.isLoading)
        assertFalse(Result.Loading.isSuccess)
        assertFalse(Result.Loading.isError)
    }
}
