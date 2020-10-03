package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test

class WriteCsvEmptyTests {

	@Test
	fun toEmptyCsv() {
		data class DataClass(val a: Int)

		val list = listOf<DataClass>()

		val csv = CastingCSV.create().toCSV(list)

		Assert.assertEquals("a\r\n\r\n", csv)
	}

	@Test
	fun toEmptyCsvCustomHeader() {
		data class DataClass(val a: String, val b: Int, val c: Boolean)

		val list = listOf<DataClass>()

		val csv = CastingCSV.create().toCSV(list, listOf("a", "c", "b"))

		Assert.assertEquals("a,c,b\r\n\r\n", csv)
	}

}
