package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test

class WriteCsvBasicTests {

	@Test
	fun toCsvString() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("x"))

		val csv = CastingCSV.create().toCSV(list)

		Assert.assertEquals("a\r\nx\r\n", csv)
	}

	@Test
	fun toCsvInt() {
		data class DataClass(val a: Int)

		val list = listOf(DataClass(1))

		val csv = CastingCSV.create().toCSV(list)

		Assert.assertEquals("a\r\n1\r\n", csv)
	}

	@Test
	fun toCsvBoolean() {
		data class DataClass(val a: Boolean)

		val list = listOf(DataClass(true), DataClass(false))

		val csv = CastingCSV.create().toCSV(list)

		Assert.assertEquals("a\r\ntrue\r\nfalse\r\n", csv)
	}

	@Test
	fun toCsvNullable() {
		data class DataClass(val a: String?)

		val list = listOf(DataClass(null))

		val csv = CastingCSV.create().toCSV(list)

		Assert.assertEquals("a\r\n\r\n", csv)
	}

	@Test
	fun toCsvMultiField() {
		data class DataClass(val a: String, val b: Double, val c: Boolean)

		val list = listOf(DataClass("x", 1.0, true))

		val csv = CastingCSV.create().toCSV(list)

		Assert.assertEquals("a,b,c\r\nx,1.0,true\r\n", csv)
	}

}
