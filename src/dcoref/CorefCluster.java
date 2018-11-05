//
// StanfordCoreNLP -- a suite of NLP tools
// Copyright (c) 2009-2010 The Board of Trustees of
// The Leland Stanford Junior University. All Rights Reserved.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
// For more information, bug reports, fixes, contact:
//    Christopher Manning
//    Dept of Computer Science, Gates 1A
//    Stanford CA 94305-9010
//    USA
//

package dcoref;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dcoref.Dictionaries.Animacy;
import dcoref.Dictionaries.Gender;
import dcoref.Dictionaries.Number;

/**
 * One cluster for the SieveCoreferenceSystem.
 *
 * @author Heeyoung Lee
 */
public class CorefCluster implements Serializable {

	private static final long serialVersionUID = 8655265337578515592L;

	protected final Set<String> corefMentions;
	protected final int clusterID;

	// Attributes for cluster - can include multiple attribute e.g., {singular,
	// plural}
	protected final Set<Number> numbers;
	protected final Set<Gender> genders;
	protected final Set<Animacy> animacies;
	private static final Set<String> nerStrings = new HashSet<>(Arrays.asList("singular", "plural"));
	protected final Set<String> heads;

	/** All words in this cluster - for word inclusion feature */
	public final Set<String> words;

	/** The first mention in this cluster */
	protected String firstMention;

	/**
	 * Return the most representative mention in the chain. A proper noun mention or
	 * a mention with more pre-modifiers is preferred.
	 */
	protected String representative;

	public int getClusterID() {
		return clusterID;
	}

	public Set<String> getCorefMentions() {
		return corefMentions;
	}

	public String getFirstMention() {
		return firstMention;
	}

	public String getRepresentativeMention() {
		return representative;
	}

	public CorefCluster(int ID) {
		clusterID = ID;
		corefMentions = new HashSet<>();
		numbers = EnumSet.noneOf(Number.class);
		genders = EnumSet.noneOf(Gender.class);
		animacies = EnumSet.noneOf(Animacy.class);
		heads = new HashSet<>();
		words = new HashSet<>();
		firstMention = null;
		representative = null;
	}

	public CorefCluster(int ID, Set<String> mentions) {
		this(ID);
		// Register mentions
		corefMentions.addAll(mentions);
		// Get list of mentions in textual order
		List<String> sortedMentions = new ArrayList<>(mentions.size());
		sortedMentions.addAll(mentions);
		// Set default for first / representative mention
		if (sortedMentions.size() > 0) {
			firstMention = sortedMentions.get(0);
			representative = sortedMentions.get(0); // will be updated below
		}
	}

}
