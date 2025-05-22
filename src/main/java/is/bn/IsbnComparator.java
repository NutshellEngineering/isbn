package is.bn;

import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.Comparator;

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