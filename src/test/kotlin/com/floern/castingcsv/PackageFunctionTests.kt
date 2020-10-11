package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test

class PackageFunctionTests {

	@Test
	fun fromCsvString() {
		data class DataClass(val a: String)

		val list = castingCSV().fromCSV<DataClass>("a\nx")

		Assert.assertEquals(1, list.size)
		Assert.assertEquals("x", list[0].a)
	}

	@Test
	fun fromCsvNullCode() {
		data class DataClass(val a: Int?)

		val list = castingCSV { nullCode = "notset" }.fromCSV<DataClass>("a\n1\nnotset")

		Assert.assertEquals(2, list.size)

		Assert.assertEquals(1, list[0].a)
		Assert.assertNull(list[1].a)
	}

}
