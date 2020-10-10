package com.floern.castingcsv

import com.floern.castingcsv.typeadapter.CsvTypeAdapter
import com.floern.castingcsv.typeadapter.TypeAdapter
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class CustomAdapterTests {

	class DateAdapter : TypeAdapter<Date>() {
		val format = SimpleDateFormat("yyyy-MM-dd")
		override fun serialize(value: Date?): String? = value?.let { format.format(it) }
		override fun deserialize(token: String): Date? = token.takeIf { it.isNotBlank() }.let { format.parse(it) }
	}

	data class DateClass(
		@param:CsvTypeAdapter(DateAdapter::class)
		@property:CsvTypeAdapter(DateAdapter::class)
		val a: Date
	)

	@Test
	fun parseDate() {
		val list = CastingCSV.create().fromCSV<DateClass>("a\n2020-10-25")

		Assert.assertEquals(1, list.size)
		Assert.assertEquals("2020-10-25", DateAdapter().format.format(list[0].a))
	}

	@Test
	fun serializeDate() {
		val date = DateAdapter().format.parse("2020-10-28")
		val csv = CastingCSV.create().toCSV(listOf(DateClass(date)))

		Assert.assertEquals("a\r\n2020-10-28\r\n", csv)
	}

	@Test(expected = IllegalArgumentException::class)
	fun incompatibleTypeAdapter() {
		data class DataClass(
			@param:CsvTypeAdapter(DateAdapter::class)
			@property:CsvTypeAdapter(DateAdapter::class)
			val a: String
		)

		CastingCSV.create().fromCSV<DataClass>("a\nx")

		Assert.fail()
	}

}
