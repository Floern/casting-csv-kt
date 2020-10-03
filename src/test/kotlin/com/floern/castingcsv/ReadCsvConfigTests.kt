package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test

class ReadCsvConfigTests {

	@Test
	fun fromCsvNullCode() {
		data class DataClass(val a: Int?)

		val list: List<DataClass> = CastingCSV.create { nullCode = "notset" }.fromCSV("a\n1\nnotset")

		Assert.assertEquals(2, list.size)

		Assert.assertEquals(1, list[0].a)
		Assert.assertNull(list[1].a)
	}

	@Test
	fun fromCsvNullCodeString() {
		data class DataClass(val a: String?)

		val list: List<DataClass> = CastingCSV.create { nullCode = "notset" }.fromCSV("a\nx\nnotset")

		Assert.assertEquals(2, list.size)

		Assert.assertEquals("x", list[0].a)
		Assert.assertNull(list[1].a)
	}

	@Test
	fun fromCsvDelimiter() {
		data class DataClass(val a: String, val b: String)

		val list: List<DataClass> = CastingCSV.create { delimiter = '\t' }.fromCSV("a\tb\nx\ty")

		Assert.assertEquals(1, list.size)

		Assert.assertEquals("x", list[0].a)
		Assert.assertEquals("y", list[0].b)
	}

	@Test
	fun fromCsvQuoteChar() {
		data class DataClass(val a: String, val b: String)

		val list: List<DataClass> = CastingCSV.create { quoteChar = '\'' }.fromCSV("a,b\n'x','y'")

		Assert.assertEquals(1, list.size)

		Assert.assertEquals("x", list[0].a)
		Assert.assertEquals("y", list[0].b)
	}

	@Test
	fun fromCsvEscapeChar() {
		data class DataClass(val a: String, val b: String)

		val list: List<DataClass> = CastingCSV.create { escapeChar = '\\' }.fromCSV("a,b\n\"\\\"x\\\"\",\"y\"")

		Assert.assertEquals(1, list.size)

		Assert.assertEquals("\"x\"", list[0].a)
		Assert.assertEquals("y", list[0].b)
	}

	@Test
	fun fromCsvSkipEmptyLine() {
		data class DataClass(val a: String)

		val list: List<DataClass> = CastingCSV.create { skipEmptyLine = true }.fromCSV("a\n\n\nx")

		Assert.assertEquals(1, list.size)

		Assert.assertEquals("x", list[0].a)
	}

	@Test
	fun fromCsvSkipMismatchedRow() {
		data class DataClass(val a: String)

		val list: List<DataClass> = CastingCSV.create { skipMismatchedRow = true }.fromCSV("a\nx\ny,z")

		Assert.assertEquals(1, list.size)

		Assert.assertEquals("x", list[0].a)
	}

}
