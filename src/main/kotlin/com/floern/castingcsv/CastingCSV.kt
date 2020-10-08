package com.floern.castingcsv

import com.floern.castingcsv.deserialization.Deserializer
import com.floern.castingcsv.serialization.Serializer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.KClass

public class CastingCSV private constructor(private val csvConfig: CsvConfig) {

	/**
	 * Read and write CSV directly from and to data classes with a single line of code.
	 *
	 * Read CSV:
	 *
	 * `val list = CastingCSV.create().fromCSV<MyRecord>(csv)`
	 *
	 * Write CSV:
	 *
	 * `val csv = CastingCSV.create().toCSV(list)`
	 * or
	 * `CastingCSV.create().toCSV(data, outputStream)`
	 */
	public companion object {

		/**
		 * @param config (optional) custom configuration for the CSV reader and writer.
		 */
		public fun create(config: CsvConfig.() -> Unit = {}): CastingCSV {
			val csvConfig = CsvConfig().apply(config)
			return CastingCSV(csvConfig)
		}

	}

	private val serializer by lazy { Serializer(csvConfig) }
	private val deserializer by lazy { Deserializer(csvConfig) }

	@PublishedApi
	internal val charset = csvConfig.charset

	/**
	 * Read CSV to a list of objects.
	 * @param data CSV data.
	 * @param header (optional) list of field names, required if the CSV source does not contain a header row.
	 * @return list of parsed objects.
	 */
	public inline fun <reified T : Any> fromCSV(
		data: String,
		header: List<String>? = null
	): List<T> {
		return fromCsvAsSequence(data.byteInputStream(), T::class, header) { it.toList() }
	}

	/**
	 * Read CSV to a list of objects.
	 * @param file CSV file.
	 * @param header (optional) list of field names, required if the CSV source does not contain a header row.
	 * @return list of parsed objects.
	 */
	public inline fun <reified T : Any> fromCSV(
		file: File,
		header: List<String>? = null
	): List<T> {
		return fromCSV(file.inputStream(), header)
	}

	/**
	 * Read CSV to a list of objects.
	 * @param inputStream input stream of a CSV source, will be closed automatically.
	 * @param header (optional) list of field names, required if the CSV source does not contain a header row.
	 * @return list of parsed objects.
	 */
	public inline fun <reified T : Any> fromCSV(
		inputStream: InputStream,
		header: List<String>? = null
	): List<T> {
		return fromCsvAsSequence(inputStream, T::class, header) { it.toList() }
	}

	/**
	 * Read CSV without loading everything into memory at once, using a Sequence.
	 * @param inputStream input stream of a CSV source, will be closed automatically.
	 * @param header (optional) list of field names, required if the CSV source does not contain a header row.
	 * @param sequenceConsumer callback to read one object at a time from a sequence.
	 */
	public inline fun <reified T : Any, R> fromCsvAsSequence(
		inputStream: InputStream,
		header: List<String>? = null,
		noinline sequenceConsumer: (Sequence<T>) -> R
	): R {
		return fromCsvAsSequence(inputStream, T::class, header, sequenceConsumer)
	}

	@PublishedApi
	internal fun <T : Any, R> fromCsvAsSequence(
		inputStream: InputStream,
		elementType: KClass<T>,
		header: List<String>? = null,
		sequenceConsumer: (Sequence<T>) -> R
	): R {
		return deserializer.fromCsvAsSequence(inputStream, elementType, header, sequenceConsumer)
	}

	/**
	 * Write a list of objects to CSV.
	 * @param data list of objects to serialize.
	 * @param header (optional) list of field names to serialize. If not set, all properties of the objects are serialized.
	 * @return CSV string.
	 */
	public inline fun <reified T : Any> toCSV(
		data: List<T>,
		header: List<String>? = null
	): String {
		return ByteArrayOutputStream().also { bos -> toCSV(data, bos, header) }.toString(charset.name())
	}

	/**
	 * Write a list of objects to CSV.
	 * @param data list of objects to serialize.
	 * @param output output stream to write the CSV to, will be closed automatically.
	 * @param header (optional) list of field names to serialize. If not set, all properties of the objects are serialized.
	 */
	public inline fun <reified T : Any> toCSV(
		data: List<T>,
		output: OutputStream,
		header: List<String>? = null
	) {
		toCSV(data.asSequence(), output, header)
	}

	/**
	 * Write a sequence of objects to CSV.
	 * @param data sequence of objects to serialize.
	 * @param output output stream to write the CSV to, will be closed automatically.
	 * @param header (optional) list of field names to serialize. If not set, all properties of the objects are serialized.
	 */
	public inline fun <reified T : Any> toCSV(
		data: Sequence<T>,
		output: OutputStream,
		header: List<String>? = null
	) {
		toCSV(data, output, T::class, header)
	}

	@PublishedApi
	internal fun <T : Any> toCSV(
		data: Sequence<T>,
		output: OutputStream,
		elementType: KClass<T>,
		header: List<String>? = null
	) {
		serializer.toCSV(data, output, elementType, header)
	}

}


/**
 * Read and write CSV directly from and to data classes with a single line of code.
 *
 * Read CSV:
 *
 * `val list = castingCSV().fromCSV<MyRecord>(csv)`
 *
 * Write CSV:
 *
 * `val csv = castingCSV().toCSV(list)`
 * or
 * `castingCSV().toCSV(data, outputStream)`
 *
 * @param config (optional) custom configuration for the CSV reader and writer.
 */
public fun castingCSV(config: CsvConfig.() -> Unit = {}): CastingCSV = CastingCSV.create(config)
