package com.floern.castingcsv.typeadapter

import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
public annotation class CsvTypeAdapter(
	val typeAdapterClass: KClass<out TypeAdapter<*>>
)
