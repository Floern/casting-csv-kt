package com.floern.castingcsv.typeadapter

import kotlin.reflect.KType
import kotlin.reflect.full.createType

private val StringAdapter: TypeAdapter<String> = createTypeAdapter({ it }, { it })
private val ByteAdapter: TypeAdapter<Byte> = createTypeAdapter(String::toByte)
private val ShortAdapter: TypeAdapter<Short> = createTypeAdapter(String::toShort)
private val IntAdapter: TypeAdapter<Int> = createTypeAdapter(String::toInt)
private val LongAdapter: TypeAdapter<Long> = createTypeAdapter(String::toLong)
private val FloatAdapter: TypeAdapter<Float> = createTypeAdapter(String::toFloat)
private val DoubleAdapter: TypeAdapter<Double> = createTypeAdapter(String::toDouble)
private val BooleanAdapter: TypeAdapter<Boolean> = createTypeAdapter(String::toBoolean)

private val defaultTypeAdapters = mapOf<KType, TypeAdapter<*>>(
	String::class.createType() to StringAdapter,
	String::class.createType(nullable = true) to StringAdapter,
	Byte::class.createType() to ByteAdapter,
	Byte::class.createType(nullable = true) to ByteAdapter,
	Short::class.createType() to ShortAdapter,
	Short::class.createType(nullable = true) to ShortAdapter,
	Int::class.createType() to IntAdapter,
	Int::class.createType(nullable = true) to IntAdapter,
	Long::class.createType() to LongAdapter,
	Long::class.createType(nullable = true) to LongAdapter,
	Double::class.createType() to DoubleAdapter,
	Double::class.createType(nullable = true) to DoubleAdapter,
	Float::class.createType() to FloatAdapter,
	Float::class.createType(nullable = true) to FloatAdapter,
	Boolean::class.createType() to BooleanAdapter,
	Boolean::class.createType(nullable = true) to BooleanAdapter,
)

private inline fun <T : Any> createTypeAdapter(
	crossinline deserializeImpl: (String) -> T?,
	crossinline serializeImpl: (T?) -> String? = { it?.toString() }
): TypeAdapter<T> {
	return object : TypeAdapter<T>() {
		override fun deserialize(token: String): T? = deserializeImpl(token)
		override fun serialize(value: T?): String? = serializeImpl(value)
	}
}

internal fun getTypeAdapter(type: KType): TypeAdapter<*> {
	return defaultTypeAdapters[type] ?: error("Adapter for type '${type}' not found")
}
