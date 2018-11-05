/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    SerializedObject.java
 *    Copyright (C) 2001-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class for storing an object in serialized form in memory. It can be used to
 * make deep copies of objects, and also allows compression to conserve memory.
 * <p>
 *
 * @author Richard Kirkby (rbk1@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class SerializedObject implements Serializable, RevisionHandler {

	/** for serialization */
	private static final long serialVersionUID = 6635502953928860434L;

	/** The array storing the object. */
	private byte[] mStoredObjectArray;

	/** Whether or not the object is compressed. */
	private boolean mIsCompressed;

	/** Whether it is a Jython object or not */
	private boolean mIsJython;

	/**
	 * Creates a new serialized object (without compression).
	 *
	 * @param toStore the object to store
	 * @exception Exception if the object couldn't be serialized
	 */
	public SerializedObject(Object toStore) throws Exception {

		this(toStore, false);
	}

	/**
	 * Creates a new serialized object.
	 *
	 * @param toStore  the object to store
	 * @param compress whether or not to use compression
	 * @exception Exception if the object couldn't be serialized
	 */
	public SerializedObject(Object toStore, boolean compress) throws Exception {

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		OutputStream os = ostream;
		ObjectOutputStream p;
		if (!compress) {
			p = new ObjectOutputStream(new BufferedOutputStream(os));
		} else {
			p = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(os)));
		}
		p.writeObject(toStore);
		p.flush();
		p.close(); // used to be ostream.close() !
		mStoredObjectArray = ostream.toByteArray();

		mIsCompressed = compress;
	}

	/*
	 * Checks to see whether this object is equal to another.
	 * 
	 * @param compareTo the object to compare to
	 * 
	 * @return whether or not the objects are equal
	 */
	@Override
	public final boolean equals(Object compareTo) {

		if (compareTo == null) {
			return false;
		}
		if (!compareTo.getClass().equals(this.getClass())) {
			return false;
		}
		byte[] compareArray = ((SerializedObject) compareTo).mStoredObjectArray;
		if (compareArray.length != mStoredObjectArray.length) {
			return false;
		}
		for (int i = 0; i < compareArray.length; i++) {
			if (compareArray[i] != mStoredObjectArray[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a hashcode for this object.
	 *
	 * @return the hashcode
	 */
	@Override
	public int hashCode() {

		return mStoredObjectArray.length;
	}

	/**
	 * Returns a serialized object. Uses org.python.util.PythonObjectInputStream for
	 * Jython objects (read
	 * <a href= "http://aspn.activestate.com/ASPN/Mail/Message/Jython-users/1001401"
	 * >here</a> for more details).
	 *
	 * @return the restored object
	 */
	public Object getObject() {
		try (ByteArrayInputStream istream = new ByteArrayInputStream(mStoredObjectArray)) {
			ObjectInputStream p;
			Object toReturn = null;
			if (mIsJython) {
			} else {

				toReturn = null;
			}
			return toReturn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	@Override
	public String getRevision() {
		return RevisionUtils.extract("$Revision$");
	}
}
