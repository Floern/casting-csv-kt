package com.floern.castingcsv.typeadapter

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.withNullability

internal fun getTypeAdapter(type: KType, element: KAnnotatedElement): TypeAdapter<*> {
	return findCustomTypeAdapter(type, element) ?: getDefaultTypeAdapter(type) ?: error("Adapter for type '$type' not found")
}

private fun findCustomTypeAdapter(type: KType, element: KAnnotatedElement): TypeAdapter<*>? {
	return element.annotations
		.find { it is CsvTypeAdapter }
		.let { it as CsvTypeAdapter? }
		?.typeAdapterClass?.let { typeAdapterClass ->
			typeAdapterClass.runCatching { createInstance() }
				.getOrElse { cause -> failedTypeAdapterInit(typeAdapterClass, cause) }
		}
		?.also { typeAdapter ->
			ensureTypeMatch(typeAdapter, type)
		}
}

private fun ensureTypeMatch(typeAdapter: TypeAdapter<*>, type: KType) {
	if (typeAdapter::class.supertypes[0].arguments[0].type?.withNullability(type.isMarkedNullable) != type) {
		throw IllegalArgumentException("TypeAdapter '${typeAdapter::class.simpleName}' not compatible with type '$type'")
	}
}

@Suppress("NOTHING_TO_INLINE")
private inline fun failedTypeAdapterInit(kClass: KClass<*>, cause: Throwable): Nothing {
	throw IllegalArgumentException("Cannot create instance of TypeAdapter '${kClass.simpleName}'", cause)
}
