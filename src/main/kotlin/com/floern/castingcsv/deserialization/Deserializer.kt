package com.floern.castingcsv.deserialization

import com.floern.castingcsv.CsvConfig
import com.floern.castingcsv.typeadapter.getTypeAdapter
import com.floern.castingcsv.utils.DeserializableField
import com.floern.castingcsv.utils.TYPE_STRING
import com.floern.castingcsv.utils.findDuplicate
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.context.CsvReaderContext
import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

/**
 * Deserialize a CSV input to a sequence of objects.
 */
internal class Deserializer(private val csvConfig: CsvConfig) {

	private val csvReaderContext = CsvReaderContext().apply {
		charset = csvConfig.charset.name()
		delimiter = csvConfig.delimiter
		quoteChar = csvConfig.quoteChar
		escapeChar = csvConfig.escapeChar
		skipEmptyLine = csvConfig.skipEmptyLine
		skipMissMatchedRow = csvConfig.skipMismatchedRow
	}

	internal fun <T : Any, R> fromCsvAsSequence(
		inputStream: InputStream,
		elementType: KClass<T>,
		header: List<String>?,
		sequenceConsumer: (Sequence<T>) -> R
	): R {
		return CsvReader(csvReaderContext).open(inputStream) {
			val primaryConstructor = elementType.primaryConstructor
			if (!elementType.isData || primaryConstructor == null) {
				throw IllegalArgumentException("${elementType.qualifiedName} must be a data class")
			}

			val headerNames = header ?: readNext() ?: return@open sequenceConsumer(emptySequence())

			headerNames.findDuplicate()?.let {
				throw CsvDeserializationException("Duplicate header '$it'")
			}

			val fields = getDeserializableFields(primaryConstructor.parameters, headerNames)

			val sequence = readAllAsSequence().mapIndexed { row, tokens ->
				deserializeElement(
					tokens,
					primaryConstructor,
					fields,
					row
				)
			}
			return@open sequenceConsumer(sequence)
		}
	}

	private fun getDeserializableFields(
		parameters: List<KParameter>,
		headerNames: List<String>
	): List<DeserializableField> {
		return parameters
			.map { parameter ->
				DeserializableField(
					parameter,
					headerNames.indexOf(parameter.name),
					getTypeAdapter(parameter.type, parameter)
				)
			}
			.onEach { field ->
				if (field.columnIndex < 0 && !field.parameter.isOptional) {
					throw CsvDeserializationException("Required field '${field.name}' not found in CSV header")
				}
			}
			.filter { it.columnIndex >= 0 }
	}

	private fun <T : Any> deserializeElement(
		tokens: List<String>,
		constructor: KFunction<T>,
		fields: List<DeserializableField>,
		row: Int
	): T {
		return constructor.callBy(
			fields.associateBy({ field -> field.parameter }) { field ->
				val value = tokens.getOrNull(field.columnIndex)
					?.takeIf { it != csvConfig.nullCode || deserializeEmptyTokenToNonnullString(field.parameter) }
					?.runCatching { field.typeAdapter.deserialize(this) }
					?.onFailure { e ->
						throw CsvDeserializationException("Invalid value for field '${field.name}' on row ${row + 1}", e)
					}
					?.getOrNull()
				if (value == null && !field.type.isMarkedNullable) {
					throw CsvDeserializationException("Missing value for field '${field.name}' on row ${row + 1}")
				}
				return@associateBy value
			}
		)
	}

	@Suppress("NOTHING_TO_INLINE")
	private inline fun deserializeEmptyTokenToNonnullString(parameter: KParameter): Boolean {
		return csvConfig.nullCode.isEmpty() && parameter.type == TYPE_STRING
	}

}
