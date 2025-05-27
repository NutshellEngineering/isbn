---
weight: 1
bookFlatSection: true
title: "API Documentation"
---

# API Documentation

## ISBN

Immutable value types for representing, parsing, validating, and converting ISBNs in accordance with [ISO 2108](https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en).

The `isbn` package provides support for:

- [`Isbn10`](#isbn10-record) — 10-digit identifiers (pre-2007)
- [`Isbn13`](#isbn13-record) — 13-digit identifiers (with `978` or `979` prefix)
- [`Isbn`](#isbn-interface) — a sealed interface unifying both types

The library provides strict format and check digit validation, conversion between formats, and support for Jakarta Bean Validation.

---

## `Isbn` interface

Represents an ISBN identifier, either ISBN-10 or ISBN-13, as defined in [ISO 2108](https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en), the international standard for identifying books and related media.

This sealed interface abstracts the behaviour of both `Isbn10` and `Isbn13`, including parsing, version detection, and interconversion where possible.

Use `fromString(String)` to create an `Isbn` instance from a raw string. The string may contain hyphens or spaces and will be cleaned before processing.

### Usage Examples

```java
Isbn isbn = Isbn.fromString("978-0-306-40615-7");
System.out.println(isbn.version()); // ISBN_13
```

```java
Optional<Isbn10> maybeIsbn10 = isbn.toIsbn10();
maybeIsbn10.ifPresent(i10 -> System.out.println(i10.value())); // "0306406152"
```

See: [`Isbn10`](#isbn10-record), [`Isbn13`](#isbn13-record), [ISO 2108](https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en)

### `static Isbn fromString(String raw)`
Parses a raw ISBN string into an `Isbn10` or `Isbn13` instance. Hyphens and spaces are removed.

Returns: an instance of `Isbn10` or `Isbn13`  
Throws: `IllegalArgumentException` if the input is null or not a valid ISBN length

### `static boolean canConvert(Isbn isbn, Version target)`
Determines whether the given `Isbn` can be converted to the specified `Version`.

Returns: `true` if conversion is supported

### `static Optional<Isbn> convert(Isbn isbn, Version target)`
Attempts to convert the given `Isbn` to the specified `Version`.

Returns: an `Optional` containing the converted ISBN, or empty if not convertible

```java
Optional<Isbn> converted = Isbn.convert(isbn, Isbn.Version.ISBN_10);
converted.ifPresent(i -> System.out.println(i.value()));
```

### `static Comparator<Isbn> comparator()`
Returns a comparator that orders ISBNs by their 13-digit canonical form.

Returns: a comparator for consistent ordering

### `String value()`
Returns the 10- or 13-digit canonical ISBN string with no separators.

Returns: the unformatted ISBN string

### `Version version()`
Returns the version of this ISBN: `ISBN_10` or `ISBN_13`.

Returns: the `Version` of this ISBN

### `boolean canConvertTo(Version target)`
Indicates whether this ISBN can be converted to the specified `Version`.

Returns: `true` if conversion is supported

### `Optional<Isbn13> toIsbn13()`
Attempts to convert this ISBN to `Isbn13`.

Returns: an `Optional` containing the converted `Isbn13`, or empty if not convertible

### `Optional<Isbn10> toIsbn10()`
Attempts to convert this ISBN to `Isbn10`.

Returns: an `Optional` containing the converted `Isbn10`, or empty if not convertible

### `int compareTo(Isbn other)`
Orders ISBNs by their 13-digit canonical form.

Returns: result of comparing this ISBN to another  
Note: ISBN-10 values are promoted to ISBN-13 when possible

---

## Enum: `Isbn.Version`

Represents the two recognised ISBN formats:

- `ISBN_10` — the legacy 10-digit format
- `ISBN_13` — the current 13-digit format with `978` or `979` prefix

```java
Isbn isbn = Isbn.fromString("0-306-40615-2");
if (isbn.version() == Isbn.Version.ISBN_10) {
    System.out.println("This is an ISBN-10");
}
```

---

## `Isbn10` record

Represents an ISBN-10 identifier as defined in [ISO 2108](https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en), the international standard for book numbering. ISBN-10 is a legacy format consisting of 9 digits followed by a check character, which may be a digit or 'X'.

This class ensures format and check digit validity at construction. Hyphens and spaces are removed automatically, and the string is upper-cased before validation.

### Usage Example

```java
Isbn10 isbn10 = new Isbn10("0-306-40615-2");
System.out.println(isbn10.value());   // "0306406152"
System.out.println(isbn10.version()); // ISBN_10
```

See: [`Isbn13`](#isbn13-record), [ISO 2108](https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en)

### Constructor: `new Isbn10(String value)`
Constructs a validated ISBN-10 identifier, removing hyphens and spaces and enforcing ISO 2108 check digit rules.

Throws: `IllegalArgumentException` if the format or check digit is invalid

### `Version version()`
Returns: `ISBN_10`

### `boolean canConvertTo(Version target)`
Returns `true` if this ISBN-10 can be converted to ISBN-13. Always `true` for valid ISBN-10.

Returns: `true`

### `Optional<Isbn13> toIsbn13()`
Converts this ISBN-10 to its corresponding `Isbn13` form by prefixing with "978" and recalculating the check digit.

Returns: an `Optional` containing the equivalent `Isbn13`

### `Optional<Isbn10> toIsbn10()`
Returns this object wrapped in an `Optional`.

Returns: `Optional.of(this)`

### `static String computeCheckDigit(String nineDigits)`
Computes the check digit for the given 9-digit prefix of an ISBN-10.

Returns: the check digit as a string ("0"–"9" or "X")

---

## `Isbn13` record

Represents an ISBN-13 identifier, the internationally standardised book number format as defined in [ISO 2108](https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en).

All ISBN-13 identifiers begin with the prefixes `978` or `979` and consist of 13 numeric digits, including a check digit. This class enforces strict validation of both the format and the check digit. Hyphens and spaces are automatically removed.

Conversion to ISBN-10 is only supported for ISBN-13 identifiers beginning with `978`.

### Usage Example

```java
Isbn13 isbn13 = new Isbn13("978-0-306-40615-7");
System.out.println(isbn13.value());   // "9780306406157"
System.out.println(isbn13.version()); // ISBN_13
```

See: [`Isbn10`](#isbn10-record), [ISO 2108](https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en)

### Constructor: `new Isbn13(String value)`
Constructs a validated ISBN-13 instance. Input may contain hyphens or spaces, which are stripped before validation.

Throws: `IllegalArgumentException` if the value is not a valid ISBN-13 or has an incorrect check digit

### `Version version()`
Returns: `ISBN_13`

### `boolean canConvertTo(Version target)`
Indicates whether this ISBN-13 can be converted to `Isbn10`. This is only true if the value starts with `978`.

Returns: `true` if conversion is possible

### `Optional<Isbn13> toIsbn13()`
Returns this object wrapped in an `Optional`.

Returns: `Optional.of(this)`

### `Optional<Isbn10> toIsbn10()`
Attempts to convert this ISBN-13 to an `Isbn10`, if it begins with `978`.

Returns: an `Optional` containing the converted `Isbn10`, or empty if conversion is not possible

### `static String computeCheckDigit(String twelveDigits)`
Computes the check digit for a 12-digit ISBN-13 prefix using the modulo-10 algorithm.

Returns: the check digit as a string ("0"–"9")

---

## Validation Rules

- ISBN-10: 9 digits followed by a check digit (0–9 or X)
- ISBN-13: 13 digits starting with 978 or 979
- Validation is enforced during construction using ISO 2108 check digit algorithms
