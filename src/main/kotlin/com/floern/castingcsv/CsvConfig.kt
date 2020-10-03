package com.floern.castingcsv

import com.github.doyaaaaaken.kotlincsv.dsl.context.WriteQuoteMode
import java.nio.charset.Charset

/**
 * Configuration for the CSV reader and writer.
 */
public class CsvConfig {

	/** Charset to be used for the encoding of the CSV file. */
	public var charset: Charset = Charset.forName("UTF-8")

	/** Quote character to encapsulate fields. */
	public var quoteChar: Char = '"'

	/** Delimiter separating the fields. */
	public var delimiter: Char = ','

	/** Character to escape quotes inside strings. */
	public var escapeChar: Char = '"'

	/** Skip empty lines when reading a CSV file, throw otherwise. */
	public var skipEmptyLine: Boolean = false

	/** Skip lines with a different number of fields when reading a CSV file, throw otherwise. */
	public var skipMismatchedRow: Boolean = false

	/** Value to write a `null` field. */
	public var nullCode: String = ""

	/** Line terminator. */
	public var lineTerminator: String = "\r\n"

	/** Append a line break at the end of file. */
	public var outputLastLineTerminator: Boolean = true

	/** Quote mode: Only fields containing special characters, or all fields. */
	public var quoteWriteMode: WriteQuoteMode = WriteQuoteMode.CANONICAL

}