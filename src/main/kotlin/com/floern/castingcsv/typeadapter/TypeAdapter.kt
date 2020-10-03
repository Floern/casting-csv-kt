package com.floern.castingcsv.typeadapter

public abstract class TypeAdapter<T : Any> {

	public abstract fun serialize(value: T?): String?

	public abstract fun deserialize(token: String): T?

	@Suppress("UNCHECKED_CAST")
	internal fun serializeAny(value: Any?): String? = serialize(value as T?)

}
