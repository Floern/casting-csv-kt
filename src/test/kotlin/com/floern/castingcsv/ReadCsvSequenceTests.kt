package com.floern.castingcsv

import org.junit.Assert
import org.junit.Test
import java.io.InputStream

class ReadCsvSequenceTests {

	@Test
	fun fromCsvAsSequenceA() {
		data class DataClass(val a: String)

		val bis: InputStream = "a\na\nb\nc\nd\ne\nf\ng\nh\ni".byteInputStream()
		val f = CastingCSV.create().fromCsvAsSequence(bis) { seq: Sequence<DataClass> ->
			seq.first { it.a == "f" }
		}

		Assert.assertEquals("f", f.a)
	}

	@Test
	fun fromCsvAsSequenceB() {
		data class DataClass(val a: String)

		val bis: InputStream = "a\na\nb\nc\nd\ne\nf\ng\nh\ni".byteInputStream()
		val f = CastingCSV.create().fromCsvAsSequence<DataClass, DataClass>(bis) { seq ->
			seq.first { it.a == "f" }
		}

		Assert.assertEquals("f", f.a)
	}

	@Test
	fun fromCsvAsSequenceC() {
		data class DataClass(val a: String, val b: Int)

		val bis: InputStream = "a,b\na,1\nb,2\nc,3\nd,4\ne,5\nf,6\ng,7\nh,8\ni,9".byteInputStream()
		val f: Int = CastingCSV.create().fromCsvAsSequence(bis) { seq: Sequence<DataClass> ->
			seq.first { it.a == "f" }.b
		}

		Assert.assertEquals(6, f)
	}

	@Test
	fun fromCsvAsSequenceD() {
		data class DataClass(val a: String, val b: Int)

		val bis: InputStream = "a,b\na,1\nb,2\nc,3\nd,4\ne,5\nf,6\ng,7\nh,8\ni,9".byteInputStream()
		val f = CastingCSV.create().fromCsvAsSequence<DataClass, Int>(bis) { seq ->
			seq.first { it.a == "f" }.b
		}

		Assert.assertEquals(6, f)
	}

}
