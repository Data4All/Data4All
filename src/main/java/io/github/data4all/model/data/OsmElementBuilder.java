package io.github.data4all.model.data;

import android.os.Parcel;
import android.util.SparseArray;

/**
 * This class provides methods to write and read an osm element defined in the IDS array to a parcel.
 *
 * @author tbrose
 *
 */
public final class OsmElementBuilder {
	
	/**
	 * Maps integer to osm element classes. 
	 */
	private static final SparseArray<Class<? extends OsmElement>> IDS = new SparseArray<Class<? extends OsmElement>>();

	/**
	 * Private constructor, prevents this class from being instantiated. 
	 */
	private OsmElementBuilder() {
	}

	/**
	 * Prepares the sparse array.
	 */
	static {
		IDS.put(1, Node.class);
		IDS.put(2, Way.class);
		IDS.put(3, Relation.class);
	}

	/**
	 * Writes the specific parcelable osm element to the parcel.
	 * @param parcel
	 * @param element
	 * @param flags
	 */
	public static final void write(Parcel parcel, OsmElement element, int flags) {
		if (element != null) {
			int id = IDS.keyAt(IDS.indexOfValue(element.getClass()));
			parcel.writeInt(id);
			parcel.writeParcelable(element, flags);
		} else {
			throw new IllegalStateException("OsmObject is null");
		}
	}

	/**
	 * Reads the specific osm element from the parcel. 
	 * @param parcel
	 * @return
	 */
	public static final OsmElement read(Parcel parcel) {
		Class<? extends OsmElement> elementClass = IDS.get(parcel.readInt());

		if (elementClass == null) {
			throw new IllegalStateException("OsmObject is not in ID-Map");
		} else {
			return (OsmElement) parcel.readParcelable(elementClass
					.getClassLoader());
		}
	}
	
}
