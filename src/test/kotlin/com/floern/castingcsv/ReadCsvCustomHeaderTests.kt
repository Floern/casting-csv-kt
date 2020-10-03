package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test

class ReadCsvCustomHeaderTests {

	@Test
	fun fromCsvCustomHeader() {
		data class DataClass(val a: String, val b: Int, val c: Boolean)

		val list: List<DataClass> = CastingCSV
			.create()
			.fromCSV(
				"\"hello\",123,true\n\"world\",\"456\",\"false\"",
				listOf("a", "b", "c")
			)

		Assert.assertEquals(2, list.size)

		Assert.assertEquals("hello", list[0].a)
		Assert.assertEquals(123, list[0].b)
		Assert.assertEquals(true, list[0].c)

		Assert.assertEquals("world", list[1].a)
		Assert.assertEquals(456, list[1].b)
		Assert.assertEquals(false, list[1].c)
	}

}
