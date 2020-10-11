package com.floern.castingcsv.utils

import com.floern.castingcsv.typeadapter.TypeAdapter
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1

internal abstract class FieldInfo(
	val parameter: KParameter,
	val typeAdapter: TypeAdapter<*>
) {
	val type = parameter.type
	val name = parameter.name
}

internal class DeserializableField(
	parameter: KParameter,
	val columnIndex: Int,
	typeAdapter: TypeAdapter<*>
) : FieldInfo(
	parameter, typeAdapter
)

internal class SerializableField<E>(
	parameter: KParameter,
	val property: KProperty1<E, *>,
	typeAdapter: TypeAdapter<*>
) : FieldInfo(
	parameter, typeAdapter
)