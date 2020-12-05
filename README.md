# Casting CSV

A simple Kotlin library to read and write CSV directly from and to data classes with a single line of code.

[![Release](https://img.shields.io/bintray/v/floern/maven/casting-csv-kt?label=release)](https://bintray.com/floern/maven/casting-csv-kt)
[![CI](https://img.shields.io/github/workflow/status/Floern/casting-csv-kt/CI/main?label=ci)](https://github.com/Floern/casting-csv-kt/actions?query=workflow%3ACI)

## Usage

### Dependency

The library artifact is available on JCenter.

Gradle:
```kotlin
implementation "com.floern.castingcsv:casting-csv-kt:1.1"
```

### Example preliminaries

We will use the following data class in the examples:
```kotlin
data class Transaction(
    val sender: String?,
    val receiver: String,
    val amount: Int,
)
```

### Read CSV

Assume we have a CSV file with the following contents:
```text
sender,receiver,amount
"John","Fred",42
"Claire","Mary",123
,"Donald",16
```

We can read that file and print all transactions like so:
```kotlin
val transactions = castingCSV().fromCSV<Transaction>(csvFile)
transactions.forEach { transaction ->
    println("${transaction.sender ?: "anonymous"} sent $${transaction.amount} to ${transaction.receiver}.")
}
```

This would print:
```text
John sent $42 to Fred.
Claire sent $123 to Mary.
anonymous sent $16 to Donald.
```

Side note: An empty `sender` is interpreted as `null` and therefore replaced with "anonymous".

### Read CSV line by line

If you have a huge CSV file that would cause memory issues if it's loaded into memory all at once, 
you can use the `fromCsvAsSequence()` function to read the CSV input line by line.
```kotlin
val totalTransactions = castingCSV()
    .fromCsvAsSequence(file.inputStream()) { transactions: Sequence<Transaction> ->
        transactions.sumOf { transaction -> transaction.amount }
    }
println("Total transaction amount: $${totalTransactions}")
```

With the same data as above, this would print: 
```text
Total transaction amount: $181
```

If your data class only contains a subset of the CSV fields, or the CSV input does not contain a header row, 
you can specify a header filter with the `header` parameter to read and map the fields you want.

### Write CSV

```kotlin
val data = listOf(
    Transaction("Marc", "O'Polo", 65),
    Transaction("George", "Ronald", 12)
)
val csv = castingCSV().toCSV(data)
print(csv)
```

Output:
```text 
sender,receiver,amount
Marc,O'Polo,65
George,Ronald,12
```

If you want to specify the order of the CSV columns or only serialize a subset of the fields, 
you can specify a header list using the `header` parameter, e.g.:
```kotlin
val csv = castingCSV().toCSV(data, header = listOf("receiver", "amount"))
```

You can also write to an `OutputStream`:
```kotlin
val outputStream = csvFile.outputStream()
castingCSV().toCSV(data, outputStream)
```

And if you have to write a large amount of CSV you can use `toCSV()` with a Sequence too.

### Customized CSV options

You can specify custom CSV configurations when creating a `CastingCSV` instance with the following options. 
The example values shown also represent the default of each option:
```kotlin
castingCSV {
    // Charset to be used for the encoding of the CSV file:
    charset = Charset.forName("UTF-8")
    // Quote character to encapsulate fields:
    quoteChar = '"'
    // Delimiter separating the fields:
    delimiter = ','
    // Character to escape quotes inside strings:
    escapeChar = '"'
    // Skip empty lines when reading a CSV file, throw otherwise:
    skipEmptyLine = false
    // Skip lines with a different number of fields when reading a CSV file, throw otherwise:
    skipMismatchedRow = false
    // Value to write a `null` field:
    nullCode = ""
    // Line terminator:
    lineTerminator = "\r\n"
    // Append a line break at the end of file:
    outputLastLineTerminator = true
    // Quote mode: Only fields containing special characters, or all fields:
    quoteWriteMode = WriteQuoteMode.CANONICAL
}
```

### Supported types

The following field types, non-null and nullable, are natively supported: 
`String`, `Int`, `Long`, `Byte`, `Short`, `Float`, `Double`, `Boolean`

### Custom type adapters

Any other type can be supported by implementing a custom `TypeAdapter`. 
As shown in the following example we can serialize and deserialize a `Date` by creating a `DateAdapter` 
and linking it with the property of type `Date` using the `@CsvTypeAdapter` annotation:
```kotlin
// custom type adapter for Date
class DateAdapter : TypeAdapter<Date>() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    override fun serialize(value: Date?): String? = value?.let { dateFormat.format(it) }
    override fun deserialize(token: String): Date? = dateFormat.parse(token)
}

// data class
data class Transaction(
    @CsvTypeAdapter(DateAdapter::class)
    val date: Date
)

// parsing CSV
val list = castingCSV().fromCSV<Transaction>("date\n2021-10-25")
println(list.first().date) // Mon Oct 25 00:00:00 GMT 2021
```

## Appendix

### Dependencies

_Casting CSV_ uses [kotlin-csv by doyaaaaaken](https://github.com/doyaaaaaken/kotlin-csv) internally 
to read and write raw CSV.

The CSV fields and data class properties are mapped with Kotlin reflection. 
If you use Proguard make sure to add exclusion rules for your data classes.

For now only the JVM platform (including Android) is supported.

### Contributing

Feel free to [open issues and feature requests](https://github.com/Floern/casting-csv-kt/issues). 
You can also [create pull requests](https://github.com/Floern/casting-csv-kt/pulls) for bug fixes. 

### License

_Casting CSV_ is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

### Blog

> [CSV in Kotlin with Casting CSV &ndash; floern.com;;](https://floern.com/;;/csv-with-kotlin/)  
> <small>Introduction and deep dive into Casting CSV, my open source Kotlin library to automagically read and 
write CSV with a one-liner using Kotlin data classes and reflection.</small>
