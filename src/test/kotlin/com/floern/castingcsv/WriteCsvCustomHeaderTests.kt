package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test

class WriteCsvCustomHeaderTests {

	@Test
	fun toCsvCustomHeader() {
		data class DataClass(val a: String, val b: Int, val c: Boolean)

		val list = listOf(DataClass("x", 1, true))

		val csv = CastingCSV.create().toCSV(list, listOf("c", "b"))

		Assert.assertEquals("c,b\r\ntrue,1\r\n", csv)
	}

}
