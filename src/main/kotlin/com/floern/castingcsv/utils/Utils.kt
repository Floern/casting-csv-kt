package com.floern.castingcsv.utils

import kotlin.reflect.full.createType

internal fun <T> List<T>.findDuplicate(): T? {
	val set = mutableSetOf<T>()
	forEach { v ->
		if (set.contains(v)) {
			return v
		} else {
			set.add(v)
		}
	}
	return null
}

internal val TYPE_STRING = String::class.createType()
