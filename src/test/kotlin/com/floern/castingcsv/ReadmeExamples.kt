package com.floern.castingcsv

import com.floern.castingcsv.typeadapter.CsvTypeAdapter
import com.floern.castingcsv.typeadapter.TypeAdapter
import com.github.doyaaaaaken.kotlincsv.dsl.context.WriteQuoteMode
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class ReadmeExamples {

	data class Transaction(
		val sender: String?,
		val receiver: String,
		val amount: Int,
	)

	@Test
	fun readAllFromFile() {
		val data = """
				sender,receiver,amount
				"John","Fred",42
				"Claire","Mary",123
				,"Donald",16
				""".trimIndent().byteInputStream()

		val transactions = castingCSV().fromCSV<Transaction>(data)
		transactions.forEach { transaction ->
			println("${transaction.sender ?: "anonymous"} sent $${transaction.amount} to ${transaction.receiver}.")
		}

		Assert.assertEquals(3, transactions.size)
	}

	@Test
	@Ignore("literal example")
	fun readAllFromFileExample() {
		val file = File("foobar.csv")

		val transactions = castingCSV().fromCSV<Transaction>(file)
		transactions.forEach { transaction ->
			println("${transaction.sender ?: "anonymous"} sent $${transaction.amount} to ${transaction.receiver}.")
		}
	}

	@Test
	fun readSequenceFromFile() {
		val data = """
				sender,receiver,amount
				"John","Fred",42
				"Claire","Mary",123
				,"Donald",16
				""".trimIndent().byteInputStream()

		val totalTransactions = castingCSV()
			.fromCsvAsSequence(data) { transactions: Sequence<Transaction> ->
				transactions.sumOf { transaction -> transaction.amount }
			}
		println("Total transaction amount: $${totalTransactions}")

		Assert.assertEquals(181, totalTransactions)
	}

	@Test
	fun writeToFile() {
		val data = listOf(
			Transaction("Marc", "O'Polo", 65),
			Transaction("George", "Ronald", 12)
		)

		val csv = castingCSV().toCSV(data)
		print(csv)

		Assert.assertEquals("sender,receiver,amount\r\n" +
				"Marc,O'Polo,65\r\n" +
				"George,Ronald,12\r\n", csv)
	}

	@Test
	fun customConfig() {
		castingCSV {
			/** Charset to be used for the encoding of the CSV file. */
			charset = Charset.forName("UTF-8")
			/** Quote character to encapsulate fields. */
			quoteChar = '"'
			/** Delimiter separating the fields. */
			delimiter = ','
			/** Character to escape quotes inside strings. */
			escapeChar = '"'
			/** Skip empty lines when reading a CSV file, throw otherwise. */
			skipEmptyLine = false
			/** Skip lines with a different number of fields when reading a CSV file, throw otherwise. */
			skipMismatchedRow = false
			/** Value to write a `null` field. */
			nullCode = ""
			/** Line terminator. */
			lineTerminator = "\r\n"
			/** Append a line break at the end of file. */
			outputLastLineTerminator = true
			/** Quote mode: Only fields containing special characters, or all fields. */
			quoteWriteMode = WriteQuoteMode.CANONICAL
		}
	}

	@Test
	fun customTypeAdapter() {
		// our custom type adapter for a Date
		class DateAdapter : TypeAdapter<Date>() {
			val dateFormat = SimpleDateFormat("yyyy-MM-dd")
			override fun serialize(value: Date?): String? = value?.let { dateFormat.format(it) }
			override fun deserialize(token: String): Date? = dateFormat.parse(token)
		}

		// our data class
		data class Transaction(
			@CsvTypeAdapter(DateAdapter::class)
			val date: Date
		)

		// parsing CSV
		val list = castingCSV().fromCSV<Transaction>("date\n2021-10-25")
		println(list.first().date)
	}

}
