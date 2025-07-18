---
description: "Rock-solid ISBN support for Java, built on the ISO 2108 standard"
---

# isbn

Rock-solid ISBN support for Java, built on the [ISO 2108 standard](https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en).

This library provides immutable value types for parsing, validating,
comparing, and converting ISBN identifiers. It is suitable for use in
domain models, APIs, and form handling.

Supported features:

- Strongly-typed `Isbn10` and `Isbn13` records
- Unified `Isbn` interface for version detection and conversion
- Strict format and check digit validation
- ISO 2108-conforming conversion logic

## Getting Started

### Add the Dependency

{{% tabs "id" %}}
{{% tab "Maven" %}} 

Add `isbn` to your `pom.xml`.

``` xml
<dependency>
    <groupId>io.github.nutshellengineering</groupId>
    <artifactId>isbn</artifactId>
    <version>1.0.0</version>
</dependency>
```

{{% /tab %}}
{{% tab "Gradle Groovy" %}} 
Add `isbn` to your `build.gradle` file.

``` groovy
dependencies {
    implementation 'io.github.nutshellengineering:isbn:1.0.0'
}
```

{{% /tab %}} 
{{% tab "Gradle Kotlin" %}} 
Add `isbn` to your `build.gradle.kts` file.

``` kotlin
dependencies {
    implementation("io.github.nutshellengineering:isbn:1.0.0")
}
```

{{% /tab %}} 
{{% /tabs %}}

## Example Usage

### Parse an ISBN

``` java
import is.bn.Isbn;

Isbn isbn = Isbn.fromString("978-0-306-40615-7");
System.out.println(isbn.version()); // ISBN_13
System.out.println(isbn.value());   // 9780306406157
```

### Convert ISBN versions

``` java
import is.bn.*;

Isbn13 isbn13 = new Isbn13("9780306406157");
Optional<Isbn10> maybe10 = isbn13.toIsbn10();
maybe10.ifPresent(i10 -> System.out.println(i10.value())); // "0306406152"
```

### Validate format at construction

``` java
new Isbn10("0306406152");   // valid
new Isbn10("030640615X");   // throws IllegalArgumentException (invalid check digit)
```
