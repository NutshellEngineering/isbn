package is.bn;

import jakarta.validation.constraints.Pattern;
import org.apiguardian.api.API;

import java.util.Optional;

/**
 * Represents an ISBN-10 identifier as defined in
 * <a href="https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en">ISO 2108</a>, the international standard
 * for book numbering. ISBN-10 is a legacy format consisting of 9 digits followed by a check character,
 * which may be a digit or 'X'.
 *
 * <p>This class ensures format and check digit validity at construction.
 * Hyphens and spaces are removed automatically, and the string is upper-cased before validation.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Isbn10 isbn10 = new Isbn10("0-306-40615-2");
 * System.out.println(isbn10.value());   // "0306406152"
 * System.out.println(isbn10.version()); // ISBN_10
 * }</pre>
 *
 * @param value a 10-character string containing 9 digits followed by a digit or 'X'
 * @see Isbn13
 * @see <a href="https://www.iso.org/obp/ui/en/#iso:std:iso:2108:ed-4:v1:en">ISO 2108</a>
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public record Isbn10(
        @Pattern(regexp = "^\\d{9}[\\dX]$", message = "Invalid ISBN-10 format")
        String value
) implements Isbn {

    /**
     * Constructs a validated ISBN-10 identifier, removing hyphens and spaces and enforcing ISO 2108 check digit rules.
     *
     * @param value the raw ISBN-10 string
     * @throws IllegalArgumentException if the format or check digit is invalid
     */
    public Isbn10 {
        var cleaned = value.replaceAll("[- ]", "").toUpperCase();
        if (!cleaned.matches("^\\d{9}[\\dX]$")) {
            throw new IllegalArgumentException("Invalid ISBN-10 format");
        }
        if (!isValid(cleaned)) {
            throw new IllegalArgumentException("Invalid ISBN-10 check digit");
        }
        value = cleaned;
    }

    /**
     * Returns {@link Version#ISBN_10}.
     */
    @Override
    public Version version() {
        return Version.ISBN_10;
    }

    /**
     * Returns {@code true} if this ISBN-10 can be converted to ISBN-13.
     * Always {@code true} for valid ISBN-10.
     */
    @Override
    public boolean canConvertTo(Version target) {
        return target == Version.ISBN_13;
    }

    /**
     * Converts this ISBN-10 to its corresponding {@link Isbn13} form by prefixing with "978"
     * and recalculating the check digit.
     *
     * @return an {@link Optional} containing the equivalent {@link Isbn13}
     */
    @Override
    public Optional<Isbn13> toIsbn13() {
        if (!canConvertTo(Version.ISBN_13)) {
            return Optional.empty();
        }
        var raw = "978" + value.substring(0, 9);
        var check = Isbn13.computeCheckDigit(raw);
        return Optional.of(new Isbn13(raw + check));
    }

    /**
     * Returns this object wrapped in an {@link Optional}.
     */
    @Override
    public Optional<Isbn10> toIsbn10() {
        return Optional.of(this);
    }

    private static boolean isValid(String isbn) {
        var expected = computeCheckDigit(isbn.substring(0, 9));
        return expected.equals(String.valueOf(isbn.charAt(9)));
    }

    /**
     * Computes the check digit for the given 9-digit prefix of an ISBN-10.
     *
     * @param nineDigits the first 9 digits of the ISBN
     * @return the check digit as a string (\"0\"–\"9\" or \"X\")
     */
    static String computeCheckDigit(String nineDigits) {
        var sum = 0;
        for (var i = 0; i < 9; i++) {
            sum += (10 - i) * Character.getNumericValue(nineDigits.charAt(i));
        }
        var remainder = sum % 11;
        var check = (11 - remainder) % 11;
        return check == 10 ? "X" : String.valueOf(check);
    }
}