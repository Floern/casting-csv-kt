package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test

class ReadCsvInputStreamTests {

	@Test
	fun fromCsvMultiField() {
		data class DataClass(val a: String, val b: Int, val c: Boolean)

		val bis = "a,b,c\n\"hello\",123,true\n\"world\",\"456\",\"false\"".byteInputStream()
		val list: List<DataClass> = CastingCSV.create().fromCSV(bis)

		Assert.assertEquals(2, list.size)

		Assert.assertEquals("hello", list[0].a)
		Assert.assertEquals(123, list[0].b)
		Assert.assertEquals(true, list[0].c)

		Assert.assertEquals("world", list[1].a)
		Assert.assertEquals(456, list[1].b)
		Assert.assertEquals(false, list[1].c)
	}


}
