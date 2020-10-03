package com.floern.castingcsv

import com.floern.castingcsv.serialization.CsvSerializationException
import org.junit.Assert
import org.junit.Test

class WriteCsvExceptionTests {

	@Test(expected = CsvSerializationException::class)
	fun toCsvCustomHeaderUnknown() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("x"))

		CastingCSV.create().toCSV(list, listOf("b"))

		Assert.fail()
	}

	@Test(expected = CsvSerializationException::class)
	fun toCsvCustomHeaderDuplicate() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("x"))

		CastingCSV.create().toCSV(list, listOf("a", "a"))

		Assert.fail()
	}

}
