package com.floern.castingcsv.serialization

import com.floern.castingcsv.CsvConfig
import com.floern.castingcsv.typeadapter.getTypeAdapter
import com.floern.castingcsv.utils.findDuplicate
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import com.github.doyaaaaaken.kotlincsv.dsl.context.CsvWriterContext
import java.io.OutputStream
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

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
			val fields: List<KProperty1<T, *>> = getSerializableFields(elementType, header)
			val headerNames = fields.map { property -> property.name }
			headerNames.findDuplicate()?.let {
				throw CsvSerializationException("Duplicate header '$it'")
			}
			writeRow(headerNames)
			writeRows(data.map { element -> serializeElement(element, fields) })
		}
	}

	private fun <T : Any> getSerializableFields(elementType: KClass<T>, header: List<String>?): List<KProperty1<T, *>> {
		val properties = elementType.memberProperties.toList()
		return header
			?.map { headerField ->
				properties.find { property ->
					headerField == property.name
				} ?: throw CsvSerializationException("Property for header '$headerField' not found")
			}
			?: properties
	}

	private fun <T : Any> serializeElement(element: T, fields: List<KProperty1<T, *>>): List<String> {
		return fields.map { property ->
			getTypeAdapter(property.returnType).serializeAny(property.get(element)) ?: csvConfig.nullCode
		}
	}

}