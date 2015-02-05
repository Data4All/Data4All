/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.model.data;

import android.os.Parcel;
import android.util.SparseArray;

/**
 * This class provides methods to write and read subclasses of
 * {@link OsmElement} to and from a {@link Parcel}.<br/>
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
public final class OsmElementBuilder {

    /**
     * The id which indicates that a {@code null}-reference is stored.
     */
    private static final int NULL_OBJECT = 0;

    /**
     * The type id of an {@link Node}.
     */
    private static final int ID_NODE = 1;

    /**
     * The type id of an {@link Way}.
     */
    private static final int ID_WAY = 2;

    /**
     * The type id of an {@link Relation}.
     */
    private static final int ID_RELATION = 3;

    /**
     * Maps integer ids to subclasses of OsmElement.
     */
    private static final SparseArray<Class<? extends OsmElement>> IDS = new SparseArray<Class<? extends OsmElement>>();

    /**
     * Prepares the sparse array.
     */
    static {
        IDS.put(ID_NODE, Node.class);
        IDS.put(ID_WAY, Way.class);
        IDS.put(ID_RELATION, Relation.class);
    }

    /**
     * Private constructor, prevents this class from being instantiated.
     */
    private OsmElementBuilder() {
    }

    /**
     * Reads the specific {@link OsmElement} from the {@link Parcel}.
     * 
     * @param parcel
     *            The {@link Parcel} to read the object's data from
     * @return The read {@link OsmElement} or {@code null}, if {@code null} was
     *         stored
     * 
     * @see android.os.Parcelable Parcelable
     */
    public static OsmElement read(Parcel parcel) {
        final int osmId = parcel.readInt();

        if (osmId == NULL_OBJECT) {
            return null;
        } else {
            final Class<? extends OsmElement> elementClass = IDS.get(osmId);

            if (elementClass == null) {
                throw new IllegalStateException("OsmObject is not in ID-Map");
            } else {
                return (OsmElement) parcel.readParcelable(elementClass
                        .getClassLoader());
            }
        }
    }

    /**
     * Writes the specific {@link OsmElement} to the {@link Parcel}.
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
    public static void write(Parcel parcel, OsmElement element, int flags) {
        if (element == null) {
            parcel.writeInt(NULL_OBJECT);
        } else {
            final int id = IDS.keyAt(IDS.indexOfValue(element.getClass()));

            parcel.writeInt(id);
            parcel.writeParcelable(element, flags);
        }
    }
}
