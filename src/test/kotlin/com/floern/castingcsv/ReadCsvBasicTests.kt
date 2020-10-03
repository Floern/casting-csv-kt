package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test
import kotlin.test.fail

class ReadCsvBasicTests {

	@Test
	fun fromCsvString() {
		data class DataClass(val a: String)

		val list = CastingCSV.create().fromCSV<DataClass>("a\nx")

		Assert.assertEquals(1, list.size)
		Assert.assertEquals("x", list[0].a)
	}

	@Test
	fun fromCsvStringNullable() {
		data class DataClass(val a: String?)

		val list = CastingCSV.create().fromCSV<DataClass>("a\nx")

		Assert.assertEquals(1, list.size)
		Assert.assertEquals("x", list[0].a)
	}

	@Test
	fun fromCsvInt() {
		data class DataClass(val a: Int)

		val list: List<DataClass> = CastingCSV.create().fromCSV("a\n1")

		Assert.assertEquals(1, list.size)
		Assert.assertEquals(1, list[0].a)
	}

	@Test
	fun fromCsvIntNullable() {
		data class DataClass(val a: Int?)

		val list: List<DataClass> = CastingCSV.create().fromCSV("a\n1")

		Assert.assertEquals(1, list.size)
		Assert.assertEquals(1, list[0].a)
	}

	@Test
	fun fromCsvFloat() {
		data class DataClass(val a: Float)

		val list: List<DataClass> = CastingCSV.create().fromCSV("a\n1.23")

		Assert.assertEquals(1, list.size)
		Assert.assertEquals(1.23f, list[0].a, 0.0000001f)
	}

	@Test
	fun fromCsvFloatNullable() {
		data class DataClass(val a: Float?)

		val list: List<DataClass> = CastingCSV.create().fromCSV("a\n1.23")

		Assert.assertEquals(1, list.size)
		Assert.assertEquals(1.23f, list[0].a ?: fail("value is null"), 0.0000001f)
	}

	@Test
	fun fromCsvMultiField() {
		data class DataClass(val a: String, val b: Int, val c: Boolean)

		val list: List<DataClass> = CastingCSV
			.create()
			.fromCSV(
				"a,b,c\n\"hello\",123,true\n\"world\",\"456\",\"false\""
			)

		Assert.assertEquals(2, list.size)

		Assert.assertEquals("hello", list[0].a)
		Assert.assertEquals(123, list[0].b)
		Assert.assertEquals(true, list[0].c)

		Assert.assertEquals("world", list[1].a)
		Assert.assertEquals(456, list[1].b)
		Assert.assertEquals(false, list[1].c)
	}

	@Test
	fun fromCsvOptionalParameter() {
		data class DataClass(val a: Int, val b: Int = -1)

		val list: List<DataClass> = CastingCSV.create().fromCSV("a\n1")

		Assert.assertEquals(1, list.size)

		Assert.assertEquals(1, list[0].a)
		Assert.assertEquals(-1, list[0].b)
	}

	@Test
	fun fromCsvNativeTypesNullable() {
		data class DataClass(
			val a: Int?,
			val b: Long?,
			val c: Byte?,
			val d: Short?,
			val e: Float?,
			val f: Double?,
			val g: String?,
			val h: Boolean?
		)

		val list: List<DataClass> = CastingCSV.create().fromCSV("a,b,c,d,e,f,g,h\n,,,,,,,")

		Assert.assertEquals(1, list.size)
		Assert.assertNull(list[0].a)
		Assert.assertNull(list[0].b)
		Assert.assertNull(list[0].c)
		Assert.assertNull(list[0].d)
		Assert.assertNull(list[0].e)
		Assert.assertNull(list[0].f)
		Assert.assertNull(list[0].g)
		Assert.assertNull(list[0].h)
	}

	@Test
	fun fromCsvNativeTypesNonnullable() {
		data class DataClass(
			val a: Int,
			val b: Long,
			val c: Byte,
			val d: Short,
			val e: Float,
			val f: Double,
			val g: String,
			val h: Boolean
		)

		val list: List<DataClass> = CastingCSV.create().fromCSV("a,b,c,d,e,f,g,h\n1,2,3,4,5,6,\"7\",true")

		Assert.assertEquals(1, list.size)
		Assert.assertEquals(1, list[0].a)
		Assert.assertEquals(2L, list[0].b)
		Assert.assertEquals(3.toByte(), list[0].c)
		Assert.assertEquals(4.toShort(), list[0].d)
		Assert.assertEquals(5.0, list[0].e.toDouble(), 0.0000001)
		Assert.assertEquals(6.0, list[0].f, 0.0000001)
		Assert.assertEquals("7", list[0].g)
		Assert.assertEquals(true, list[0].h)
	}

	@Test
	fun fromCsvNullTokenToNonnullableEmptyString() {
		data class DataClass(val a: String)

		// assume default nullCode = ""
		val list: List<DataClass> = CastingCSV.create().fromCSV("a\n\"\"")

		Assert.assertEquals(1, list.size)

		Assert.assertEquals("", list[0].a)
	}

	@Test
	fun fromCsvNullTokenToNullableNullString() {
		data class DataClass(val a: String?)

		// assume default nullCode = ""
		val list: List<DataClass> = CastingCSV.create().fromCSV("a\n\"\"")

		Assert.assertEquals(1, list.size)

		Assert.assertNull(list[0].a)
	}
}
