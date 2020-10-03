package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayOutputStream

class WriteCsvSequenceTests {

	@Test
	fun toCsvSequence() {
		data class DataClass(val a: String)

		val list = listOf(DataClass("x"), DataClass("y"))
		val out = ByteArrayOutputStream()

		CastingCSV.create().toCSV(list.asSequence(), out)
		val csv = out.toString("UTF-8")

		Assert.assertEquals("a\r\nx\r\ny\r\n", csv)
	}

	@Test
	fun toCsvSequenceEmpty() {
		data class DataClass(val a: String)

		val out = ByteArrayOutputStream()

		CastingCSV.create().toCSV(emptySequence<DataClass>(), out)
		val csv = out.toString("UTF-8")

		Assert.assertEquals("a\r\n\r\n", csv)
	}

}
