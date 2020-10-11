package com.floern.castingcsv.serialization

import com.floern.castingcsv.CsvConfig
import com.floern.castingcsv.typeadapter.getTypeAdapter
import com.floern.castingcsv.utils.SerializableField
import com.floern.castingcsv.utils.findDuplicate
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import com.github.doyaaaaaken.kotlincsv.dsl.context.CsvWriterContext
import java.io.OutputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Serialize a sequence of objects to a CSV output.
 */
internal class Serializer(private val csvConfig: CsvConfig) {

	private val csvWriterContext = CsvWriterContext().apply {
		charset = csvConfig.charset.name()
		delimiter = csvConfig.delimiter
		nullCode = csvConfig.nullCode
		lineTerminator = csvConfig.lineTerminator
		outputLastLineTerminator = csvConfig.outputLastLineTerminator
		quote {
			char = csvConfig.quoteChar
			mode = csvConfig.quoteWriteMode
		}
	}

	internal fun <T : Any> toCSV(
		data: Sequence<T>,
		output: OutputStream,
		elementType: KClass<T>,
		header: List<String>?
	) {
		CsvWriter(csvWriterContext).open(output) {
			val fields: List<SerializableField<T>> = getSerializableFields(elementType, header)
			val headerNames = fields.map { field -> field.property.name }
			headerNames.findDuplicate()?.let {
				throw CsvSerializationException("Duplicate header '$it'")
			}
			writeRow(headerNames)
			writeRows(data.map { element -> serializeElement(element, fields) })
		}
	}

	private fun <T : Any> getSerializableFields(
		elementType: KClass<T>,
		header: List<String>?
	): List<SerializableField<T>> {
		val primaryConstructor = elementType.primaryConstructor
		if (!elementType.isData || primaryConstructor == null) {
			throw IllegalArgumentException("${elementType.qualifiedName} must be a data class")
		}

		val properties = elementType.memberProperties.toList()
		val fields: List<SerializableField<T>> = primaryConstructor.parameters
			.mapNotNull { parameter ->
				properties
					.find { property ->
						property.name == parameter.name
					}
					?.let { property ->
						SerializableField(parameter, property, getTypeAdapter(parameter.type, parameter))
					}
			}

		val serializableFields: List<SerializableField<T>> = header
			?.map { headerName ->
				fields.find { field ->
					headerName == field.name
				} ?: throw CsvSerializationException("Property for header '$headerName' not found")
			}
			?: fields

		return serializableFields
	}

	private fun <T : Any> serializeElement(
		element: T,
		fields: List<SerializableField<T>>
	): List<String> {
		return fields.map { field ->
			field.typeAdapter.serializeAny(field.property.get(element)) ?: csvConfig.nullCode
		}
	}

}