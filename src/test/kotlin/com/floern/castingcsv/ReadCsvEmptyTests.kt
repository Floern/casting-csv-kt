package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test

class ReadCsvEmptyTests {

	@Test
	fun fromEmptyCsv() {
		data class DataClass(val a: String, val b: Int, val c: Boolean)

		val list: List<DataClass> = CastingCSV
			.create()
			.fromCSV("\"a\",\"b\",\"c\"\n")

		Assert.assertEquals(0, list.size)
	}

	@Test
	fun fromEmptyCsvCustomHeader() {
		data class DataClass(val a: String, val b: Int, val c: Boolean)

		val list: List<DataClass> = CastingCSV
			.create()
			.fromCSV(
				"",
				listOf("a", "c", "b")
			)

		Assert.assertEquals(0, list.size)
	}

}
