package com.floern.castingcsv.deserialization

import com.floern.castingcsv.CsvConfig
import com.floern.castingcsv.typeadapter.getTypeAdapter
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

			val parameterToColumn = mapParameterToColumnIndices(primaryConstructor.parameters, headerNames)

			val sequence = readAllAsSequence().mapIndexed { row, tokens ->
				deserializeElement(
					tokens,
					primaryConstructor,
					parameterToColumn,
					row
				)
			}
			return@open sequenceConsumer(sequence)
		}
	}

	private fun mapParameterToColumnIndices(parameters: List<KParameter>, headerNames: List<String>): Map<KParameter, Int> {
		return parameters
			.associateWith { parameter ->
				headerNames.indexOf(parameter.name)
			}
			.onEach { (parameter, index) ->
				if (index < 0 && !parameter.isOptional) {
					throw CsvDeserializationException("Required field '${parameter.name}' not found in CSV header")
				}
			}
			.filter { (_, index) -> index >= 0 }
	}

	private fun <T : Any> deserializeElement(
		tokens: List<String>,
		constructor: KFunction<T>,
		parameterToColumn: Map<KParameter, Int>,
		row: Int
	): T {
		return constructor.callBy(
			parameterToColumn.mapValues { (parameter, index) ->
				val token = tokens.getOrNull(index)
				val value = token
					?.takeIf { it != csvConfig.nullCode || deserializeEmptyTokenToNonnullString(parameter) }
					?.runCatching { getTypeAdapter(parameter.type).deserialize(this) }
					?.onFailure { e ->
						throw CsvDeserializationException("Invalid value for field '${parameter.name}' on row ${row + 1}", e)
					}
					?.getOrNull()
				if (value == null && !parameter.type.isMarkedNullable) {
					throw CsvDeserializationException("Missing value for field '${parameter.name}' on row ${row + 1}")
				}
				return@mapValues value
			}
		)
	}

	@Suppress("NOTHING_TO_INLINE")
	private inline fun deserializeEmptyTokenToNonnullString(parameter: KParameter): Boolean {
		return csvConfig.nullCode.isEmpty() && parameter.type == TYPE_STRING
	}

}