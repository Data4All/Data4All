package io.github.data4all.model.data;

import android.os.Parcel;
import android.util.SparseArray;

/**
 * This class provides methods to write and read subclasses of
 * {@link DataElement} to and from a {@link Parcel}<br/>
 * <br/>
 * Currently supported subclasses:
 * <ul>
 * <li>{@link Node}
 * <li>{@link Way}
 * <li>{@link Relation}
 * </ul>
 *
 * @author tbrose
 *
 * @see android.os.Parcelable Parcelable
 */
public final class DataElementBuilder {
    /**
     * Reads the specific {@link DataElement} from the {@link Parcel}.
     * 
     * @param parcel
     *            The {@link Parcel} to read the object's data from
     * @return The read {@link DataElement} or {@code null}, if {@code null} was
     *         stored
     * 
     * @see android.os.Parcelable Parcelable
     */
    public static final DataElement read(Parcel parcel) {
        int osmId = parcel.readInt();

        if (osmId == NULL_OBJECT) {
            return null;
        } else {
            Class<? extends DataElement> elementClass = IDS.get(osmId);

            if (elementClass == null) {
                throw new IllegalStateException("OsmObject is not in ID-Map");
            } else {
                return (DataElement) parcel.readParcelable(elementClass
                        .getClassLoader());
            }
        }
    }

    /**
     * Writes the specific {@link DataElement} to the {@link Parcel}
     * 
     * @param parcel
     *            The {@link Parcel} in which the object should be written
     * @param element
     *            the element which should be written - can be null
     * @param flags
     *            Additional flags about how the object should be written
     * 
     * @see android.os.Parcelable Parcelable
     */
    public static final void write(Parcel parcel, DataElement element, int flags) {
        if (element == null) {
            parcel.writeInt(NULL_OBJECT);
        } else {
            int id = IDS.keyAt(IDS.indexOfValue(element.getClass()));

            parcel.writeInt(id);
            parcel.writeParcelfable(element, flags);
        }
    }

    /**
     * Maps integer ids to subclasses of DataElement
     */
    private static final SparseArray<Class<? extends DataElement>> IDS = new SparseArray<Class<? extends DataElement>>();

    /**
     * The id which indicates that a {@code null}-reference is stored
     */
    private static final int NULL_OBJECT = 0;

    /**
     * Prepares the sparse array.
     */
    static {
        IDS.put(1, Node.class);
        IDS.put(2, Way.class);
        IDS.put(3, Relation.class);
    }

    /**
     * Private constructor, prevents this class from being instantiated
     */
    private DataElementBuilder() {
    }
}
