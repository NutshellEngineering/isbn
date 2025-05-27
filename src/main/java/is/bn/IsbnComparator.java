package is.bn;

import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for {@link Isbn} values that orders by canonical 13-digit representation. <p>
 * This comparator promotes {@link Isbn10} instances to {@link Isbn13} using {@link Isbn10#toIsbn13()},
 * then compares the resulting 13-digit strings lexicographically. Native {@link Isbn13} values are compared directly. <p>
 * Ordering respects ISO 2108 encoding rules:
 * <ul>
 *   <li>{@code 978}-prefixed ISBN-13 values precede {@code 979}-prefixed ones</li>
 *   <li>{@link Isbn10} values are positioned according to their equivalent {@link Isbn13}</li>
 * </ul> <p>
 * This class is {@code final}, {@code Serializable}, and used internally via {@link Isbn#compareTo(Isbn)}. <p>
 * To obtain a comparator explicitly, use {@link Isbn#comparator()}.
 *
 * @implNote If an {@link Isbn10} cannot be converted (e.g. malformed or corrupted), an {@link IllegalStateException} is thrown.
 * @implNote If an unknown subtype of {@link Isbn} is provided, an {@link IllegalArgumentException} is thrown.
 * @see Isbn#compareTo(Isbn)
 * @see Isbn#comparator()
 */
@API(status = API.Status.INTERNAL)
final class IsbnComparator implements Comparator<Isbn>, Serializable {

    static final IsbnComparator INSTANCE = new IsbnComparator();

    private IsbnComparator() {
    }

    @Override
    public int compare(Isbn a, Isbn b) {
        var aValue = toComparableString(a);
        var bValue = toComparableString(b);
        return aValue.compareTo(bValue);
    }

    private String toComparableString(Isbn isbn) {
        if (isbn instanceof Isbn13) {
            return isbn.value();
        } else if (isbn instanceof Isbn10) {
            return isbn.toIsbn13()
                    .map(Isbn13::value)
                    .orElseThrow(() -> new IllegalStateException("Isbn10 could not be converted to Isbn13: " + isbn.value()));
        } else {
            throw new IllegalArgumentException("Unknown Isbn subtype: " + isbn.getClass());
        }
    }
}