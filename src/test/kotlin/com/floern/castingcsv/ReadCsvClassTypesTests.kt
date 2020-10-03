package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test

class ReadCsvClassTypesTests {

	@Test
	fun fromCsvDataClass() {
		data class DataClass(val a: Int)
		CastingCSV.create().fromCSV<DataClass>("a\n1")
	}

	@Test(expected = IllegalArgumentException::class)
	fun fromCsvNormalClass() {
		class DataClass(val a: Int)
		CastingCSV.create().fromCSV<DataClass>("a\n1")
		Assert.fail()
	}

	@Test(expected = IllegalArgumentException::class)
	fun fromCsvInterface() {
		CastingCSV.create().fromCSV<CharSequence>("a\n1")
		Assert.fail()
	}

	@Test(expected = IllegalArgumentException::class)
	fun fromCsvPrimitiveInt() {
		CastingCSV.create().fromCSV<Int>("a\n1")
		Assert.fail()
	}

	@Test(expected = IllegalArgumentException::class)
	fun fromCsvPrimitiveString() {
		CastingCSV.create().fromCSV<String>("a\n1")
		Assert.fail()
	}

	@Test(expected = IllegalArgumentException::class)
	fun fromCsvAbstractClass() {
		abstract class DataClass(val a: Int)
		CastingCSV.create().fromCSV<DataClass>("a\n1")
		Assert.fail()
	}

}
