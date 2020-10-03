package com.floern.castingcsv

import com.floern.castingcsv.deserialization.CsvDeserializationException
import org.junit.Assert
import org.junit.Test

class ReadCsvExceptionsTests {

	@Test(expected = CsvDeserializationException::class)
	fun fromCsvDuplicateHeader() {
		data class DataClass(val a: String, val b: Int, val c: Boolean)

		CastingCSV.create().fromCSV<DataClass>("\"a\",\"a\",\"c\"\n\"hello\",123,true")

		Assert.fail()
	}

	@Test(expected = CsvDeserializationException::class)
	fun fromCsvDuplicateCustomHeader() {
		data class DataClass(val a: String, val b: Int, val c: Boolean)

		CastingCSV
			.create()
			.fromCSV<DataClass>(
				"\"hello\",123,true",
				listOf("a", "a", "c")
			)

		Assert.fail()
	}

	@Test
	fun fromCsvTruncatedNullables() {
		data class DataClass(val a: Float?, val b: Float?, val c: Float?, val d: Float?)

		val list: List<DataClass> = CastingCSV.create().fromCSV("a,b,c,d\n,")

		Assert.assertEquals(1, list.size)
		Assert.assertNull(list[0].d)
	}

	@Test(expected = CsvDeserializationException::class)
	fun fromCsvTruncatedRequiredFields() {
		data class DataClass(val a: Float?, val b: Float?, val c: Float?, val d: Float)

		CastingCSV.create().fromCSV<DataClass>("a,b,c,d\n,")

		Assert.fail()
	}

	@Test(expected = CsvDeserializationException::class)
	fun fromCsvInvalidValueNullable() {
		data class DataClass(val a: Float?)

		CastingCSV.create().fromCSV<DataClass>("a\nsunk")

		Assert.fail()
	}

	@Test(expected = CsvDeserializationException::class)
	fun fromCsvInvalidValueRequired() {
		data class DataClass(val a: Float)

		CastingCSV.create().fromCSV<DataClass>("a\nsunk")

		Assert.fail()
	}

	@Test(expected = CsvDeserializationException::class)
	fun fromCsvMissingRequiredField() {
		data class DataClass(val a: Int, val b: Int)

		CastingCSV.create().fromCSV<DataClass>("b\n1")

		Assert.fail()
	}

	@Test(expected = CsvDeserializationException::class)
	fun fromCsvNullTokenNonEmptyStringToNonnullableEmptyStringParameter() {
		data class DataClass(val a: String)

		CastingCSV.create { nullCode = "-" }.fromCSV<DataClass>("a\n\"-\"")

		Assert.fail()
	}

}
