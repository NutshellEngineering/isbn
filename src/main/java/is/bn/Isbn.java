package is.bn;

import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

/**
 * Represents an ISBN identifier, either ISBN-10 or ISBN-13, as defined in
 * <a href="https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en">ISO 2108</a>, the international standard for
 * identifying books and related media.
 *
 * <p>This sealed interface abstracts the behaviour of both {@link Isbn10} and {@link Isbn13}, including
 * parsing, version detection, and interconversion where possible.
 *
 * <p>Use {@link #fromString(String)} to create an {@code Isbn} instance from a raw string.
 * The string may contain hyphens or spaces and will be cleaned before processing.
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>{@code
 * Isbn isbn = Isbn.fromString("978-0-306-40615-7");
 * System.out.println(isbn.version()); // ISBN_13
 * }</pre>
 *
 * <pre>{@code
 * Optional<Isbn10> maybeIsbn10 = isbn.toIsbn10();
 * maybeIsbn10.ifPresent(i10 -> System.out.println(i10.value())); // "0306406152"
 * }</pre>
 *
 * @see Isbn10
 * @see Isbn13
 * @see <a href="https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en">ISO 2108</a>
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public sealed interface Isbn extends Serializable, Comparable<Isbn> permits Isbn10, Isbn13 {

    /**
     * Represents the two recognised ISBN formats: ISBN-10 and ISBN-13.
     */
    enum Version {
        ISBN_10,
        ISBN_13
    }

    /**
     * Parses a raw ISBN string and returns a corresponding {@link Isbn10} or {@link Isbn13} instance.
     * Hyphens and spaces are ignored.
     *
     * @param raw the input ISBN string (e.g., "978-0-306-40615-7")
     * @return an instance of {@link Isbn10} or {@link Isbn13}
     * @throws IllegalArgumentException if the input is null or not a valid ISBN length
     */
    static Isbn fromString(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("ISBN string must not be null");
        }
        var cleaned = raw.replaceAll("[- ]", "");
        if (cleaned.length() == 10) {
            return new Isbn10(cleaned);
        }
        if (cleaned.length() == 13) {
            return new Isbn13(cleaned);
        }
        throw new IllegalArgumentException("ISBN must be 10 or 13 digits long");
    }

    /**
     * Determines whether the given {@link Isbn} can be converted to the specified {@link Version}.
     *
     * @param isbn the ISBN to check
     * @param target the target version
     * @return {@code true} if the ISBN can be converted to the target version
     */
    static boolean canConvert(Isbn isbn, Version target) {
        return switch (target) {
            case ISBN_10 -> isbn instanceof Isbn13 i13 && i13.value().startsWith("978");
            case ISBN_13 -> isbn instanceof Isbn10;
        };
    }

    /**
     * Converts the given {@link Isbn} to the specified {@link Version}, if possible.
     *
     * @param isbn the source ISBN
     * @param target the desired version
     * @return an {@link Optional} containing the converted ISBN, or empty if not convertible
     */
    static Optional<Isbn> convert(Isbn isbn, Version target) {
        if (!canConvert(isbn, target)) {
            return Optional.empty();
        }
        return switch (target) {
            case ISBN_10 -> isbn.toIsbn10().map(i -> (Isbn) i);
            case ISBN_13 -> isbn.toIsbn13().map(i -> (Isbn) i);
        };
    }

    /**
     * Returns a comparator for {@link Isbn} values that orders by canonical 13-digit representation. <p>
     * This comparator promotes {@link Isbn10} instances to {@link Isbn13} using {@link Isbn10#toIsbn13()},
     * then compares the resulting 13-digit strings lexicographically. Native {@link Isbn13} values are compared directly. <p>
     * Ordering respects ISO 2108 encoding rules:
     * <ul>
     *   <li>{@code 978}-prefixed ISBN-13 values precede {@code 979}-prefixed ones</li>
     *   <li>{@link Isbn10} values are positioned according to their equivalent {@link Isbn13}</li>
     * </ul> <p>
     *
     * @implNote If an {@link Isbn10} cannot be converted (e.g. malformed or corrupted), an {@link IllegalStateException} is thrown.
     * @implNote If an unknown subtype of {@link Isbn} is provided, an {@link IllegalArgumentException} is thrown.
     * @see Isbn#compareTo(Isbn)
     */
    static Comparator<Isbn> comparator() {
        return IsbnComparator.INSTANCE;
    }

    /**
     * Returns the canonical string representation of the ISBN (without hyphens or spaces).
     *
     * @return a 10- or 13-digit ISBN string
     */
    String value();

    /**
     * Returns the {@link Version} of this ISBN, either {@link Version#ISBN_10} or {@link Version#ISBN_13}.
     *
     * @return the version of this ISBN
     */
    Version version();

    /**
     * Indicates whether this ISBN can be converted to the specified {@link Version}.
     *
     * @param target the target version
     * @return {@code true} if conversion is supported
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean canConvertTo(Version target);

    /**
     * Attempts to convert this ISBN to {@link Isbn13}.
     *
     * @return an {@link Optional} containing the converted {@link Isbn13}, or empty if not convertible
     */
    Optional<Isbn13> toIsbn13();

    /**
     * Attempts to convert this ISBN to {@link Isbn10}.
     *
     * @return an {@link Optional} containing the converted {@link Isbn10}, or empty if not convertible
     */
    Optional<Isbn10> toIsbn10();

    /**
     * Orders ISBNs by their 13-digit canonical form.
     * @apiNote ISBN-10 values are promoted to ISBN-13 when possible.
     */
    @Override
    default int compareTo(Isbn other) {
        return IsbnComparator.INSTANCE.compare(this, other);
    }
}