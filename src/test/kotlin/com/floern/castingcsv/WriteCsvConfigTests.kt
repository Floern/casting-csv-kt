package com.floern.castingcsv

import com.github.doyaaaaaken.kotlincsv.dsl.context.WriteQuoteMode
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class WriteCsvConfigTests {

	@Test
	fun toCsvNullCode() {
		data class DataClass(val a: Int?)

		val list = listOf(DataClass(null))

		val csv = CastingCSV.create { nullCode = "notset" }.toCSV(list)

		Assert.assertEquals("a\r\nnotset\r\n", csv)
	}

	@Test
	fun toCsvNullCodeString() {
		data class DataClass(val a: String?)

		val list = listOf(DataClass(null), DataClass("x"))

		val csv = CastingCSV.create { nullCode = "notset" }.toCSV(list)

		Assert.assertEquals("a\r\nnotset\r\nx\r\n", csv)
	}

	@Test
	fun toCsvDelimiter() {
		data class DataClass(val a: Int, val b: Int)

		val list = listOf(DataClass(1, 2))

		val csv = CastingCSV.create { delimiter = '\t' }.toCSV(list)

		Assert.assertEquals("a\tb\r\n1\t2\r\n", csv)
	}

	@Test
	fun toCsvQuoteCharAll() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("x"), DataClass("'"))

		val csv = CastingCSV.create { quoteChar = '\''; quoteWriteMode = WriteQuoteMode.ALL }.toCSV(list)

		Assert.assertEquals("'a'\r\n'x'\r\n''''\r\n", csv)
	}

	@Test
	fun toCsvQuoteCharCanonical() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("x"), DataClass(","), DataClass("\'"))

		val csv = CastingCSV.create { quoteChar = '\''; quoteWriteMode = WriteQuoteMode.CANONICAL }.toCSV(list)

		Assert.assertEquals("a\r\nx\r\n','\r\n''''\r\n", csv)
	}

	@Test
	@Ignore("Requires support for custom escape character in dependency: https://github.com/doyaaaaaken/kotlin-csv")
	fun toCsvEscapeCharAll() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("\""))

		val csv = CastingCSV.create { escapeChar = '\\'; quoteWriteMode = WriteQuoteMode.ALL }.toCSV(list)

		Assert.assertEquals("\"a\"\r\n\"\\\"\"\r\n", csv)
	}

	@Test
	@Ignore("Requires support for custom escape character in dependency: https://github.com/doyaaaaaken/kotlin-csv")
	fun toCsvEscapeCharCanonical() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("\""))

		val csv = CastingCSV.create { escapeChar = '\\'; quoteWriteMode = WriteQuoteMode.CANONICAL }.toCSV(list)

		Assert.assertEquals("a\r\n\"\\\"\"\r\n", csv)
	}

	@Test
	fun toCsvLastLineTerminatorNone() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("x"))

		val csv = CastingCSV.create { outputLastLineTerminator = false }.toCSV(list)

		Assert.assertEquals("a\r\nx", csv)
	}

	@Test
	fun toCsvLineTerminator() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("x"))

		val csv = CastingCSV.create { lineTerminator = "\n" }.toCSV(list)

		Assert.assertEquals("a\nx\n", csv)
	}

}
