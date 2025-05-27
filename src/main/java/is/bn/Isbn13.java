package is.bn;

import org.apiguardian.api.API;

import java.util.Optional;

/**
 * Represents an ISBN-13 identifier, the internationally standardised book number format as defined in
 * <a href="https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en">ISO 2108</a>.
 *
 * <p>All ISBN-13 identifiers begin with the prefixes {@code 978} or {@code 979} and consist of 13 numeric digits,
 * including a check digit. This class enforces strict validation of both the format and the check digit.
 * Hyphens and spaces are automatically removed.
 *
 * <p>Conversion to ISBN-10 is only supported for ISBN-13 identifiers beginning with {@code 978}.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Isbn13 isbn13 = new Isbn13("978-0-306-40615-7");
 * System.out.println(isbn13.value());   // "9780306406157"
 * System.out.println(isbn13.version()); // ISBN_13
 * }</pre>
 *
 * @param value the raw ISBN-13 value (must be 13 digits beginning with 978 or 979)
 * @see Isbn10
 * @see <a href="https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en">ISO 2108</a>
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public record Isbn13(
        String value
) implements Isbn {

    /**
     * Constructs a validated ISBN-13 instance.
     * Input may contain hyphens or spaces, which are stripped before validation.
     *
     * @param value the raw ISBN-13 string
     * @throws IllegalArgumentException if the value is not a valid ISBN-13 or has an incorrect check digit
     */
    public Isbn13 {
        var cleaned = value.replaceAll("[- ]", "");
        if (!cleaned.matches("^97[89]\\d{10}$")) {
            throw new IllegalArgumentException("Invalid ISBN-13 format");
        }
        if (!isValid(cleaned)) {
            throw new IllegalArgumentException("Invalid ISBN-13 check digit");
        }
        value = cleaned;
    }

    /**
     * Returns {@link Version#ISBN_13}.
     */
    @Override
    public Version version() {
        return Version.ISBN_13;
    }

    /**
     * Indicates whether this ISBN-13 can be converted to {@link Isbn10}.
     * This is only true if the value starts with {@code 978}.
     *
     * @param target the target version
     * @return true if conversion is possible
     */
    @Override
    public boolean canConvertTo(Version target) {
        return target == Version.ISBN_10 && value.startsWith("978");
    }

    /**
     * Returns this object wrapped in an {@link Optional}.
     */
    @Override
    public Optional<Isbn13> toIsbn13() {
        return Optional.of(this);
    }

    /**
     * Attempts to convert this ISBN-13 to an {@link Isbn10}, if it begins with {@code 978}.
     *
     * @return an {@link Optional} containing the converted ISBN-10, or empty if conversion is not possible
     */
    @Override
    public Optional<Isbn10> toIsbn10() {
        if (!canConvertTo(Version.ISBN_10)) {
            return Optional.empty();
        }
        var raw = value.substring(3, 12);
        var check = Isbn10.computeCheckDigit(raw);
        return Optional.of(new Isbn10(raw + check));
    }

    private static boolean isValid(String isbn) {
        var expected = computeCheckDigit(isbn.substring(0, 12));
        return expected.equals(String.valueOf(isbn.charAt(12)));
    }

    /**
     * Computes the check digit for a 12-digit ISBN-13 prefix using the modulo-10 algorithm.
     *
     * @param twelveDigits the first 12 digits of an ISBN-13
     * @return the check digit as a string ("0"–"9")
     */
    static String computeCheckDigit(String twelveDigits) {
        var sum = 0;
        for (var i = 0; i < 12; i++) {
            var digit = Character.getNumericValue(twelveDigits.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        var check = (10 - (sum % 10)) % 10;
        return String.valueOf(check);
    }
}