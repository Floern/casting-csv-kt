# Casting CSV

A simple Kotlin library to read and write CSV directly from and to data classes.

## Usage

<!--
### Dependency

The library artifact is available on JCenter.

Gradle (Kotlin DSL):
```kotlin
implementation("com.floern.castingcsv:castingcsv-kt:1.0")
```
-->

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
val transactions = CastingCSV.create().fromCSV<Transaction>(csvFile)
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

### Read CSV line by line

If you have a huge CSV file that would cause memory issues if it's loaded into memory all at once, 
you can use the `fromCsvAsSequence()` function to read the CSV input line by line.
```kotlin
val totalTransactions = CastingCSV.create()
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
val csv = CastingCSV.create().toCSV(data)
print(csv)
```

Output:
```text 
sender,receiver,amount
Marc,O'Polo,65
George,Ronald,12
```

If you want to specify the order of the CSV columns or only serialize a subset of the fields, 
you can specify a header list with using the `header` parameter, e.g.:
```kotlin
val csv = CastingCSV.create().toCSV(data, header = listOf("sender", "receiver", "amount"))
```

### Customized CSV configuration

A `CastingCSV` instance can be created with custom CSV configurations:

```kotlin
CastingCSV.create {
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

The following field types, non-null and nullable, are supported: 
`String`, `Int`, `Long`, `Byte`, `Short`, `Float`, `Double`, `Boolean`

We are working on custom type adapters to support more built-in and user-defined types.

## Appendix

### Dependencies

_Casting CSV_ uses [kotlin-csv by doyaaaaaken](https://github.com/doyaaaaaken/kotlin-csv) internally to read and write raw CSV.

The CSV fields and data class properties are mapped with Kotlin reflection. 
If you use Proguard make sure you add exclusion rules the data classes.

For now only the JVM and Android is supported.

### Contributing

Feel free to open issues and feature requests. You can also create a pull request for small fixes. 

### License

This project is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
