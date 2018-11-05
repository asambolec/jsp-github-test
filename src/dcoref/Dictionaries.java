package dcoref;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Provides accessors for various grammatical, semantic, and world knowledge
 * lexicons and word lists primarily used by the Sieve coreference system, but
 * sometimes also drawn on from other code.
 *
 * The source of the dictionaries on Stanford NLP machines is
 * /u/nlp/data/coref/gazetteers/dcoref/ . In models jars, they live in
 * edu/stanford/nlp/models/dcoref .
 */
public class Dictionaries {

	public enum MentionType {
		PRONOMINAL(1), NOMINAL(3), PROPER(4), LIST(2);

		/**
		 * A higher representativeness means that this type of mention is more preferred
		 * for choosing the representative mention. See
		 * {@link Mention#moreRepresentativeThan(Mention)}.
		 */
		public final int representativeness;

		MentionType(int representativeness) {
			this.representativeness = representativeness;
		}
	}

	public enum Gender {
		MALE, FEMALE, NEUTRAL, UNKNOWN
	}

	public enum Number {
		SINGULAR, PLURAL, UNKNOWN
	}

	public enum Animacy {
		ANIMATE, INANIMATE, UNKNOWN
	}

	public enum Person {
		I, YOU, HE, SHE, WE, THEY, IT, UNKNOWN
	}

	public final Set<String> reportVerb = new HashSet<>(Arrays.asList("accuse", "acknowledge", "add", "admit", "advise",
			"agree", "alert", "allege", "announce", "answer", "apologize", "argue", "ask", "assert", "assure", "beg",
			"blame", "boast", "caution", "charge", "cite", "claim", "clarify", "command", "comment", "compare",
			"complain", "concede", "conclude", "confirm", "confront", "congratulate", "contend", "contradict", "convey",
			"counter", "criticize", "debate", "decide", "declare", "defend", "demand", "demonstrate", "deny",
			"describe", "determine", "disagree", "disclose", "discount", "discover", "discuss", "dismiss", "dispute",
			"disregard", "doubt", "emphasize", "encourage", "endorse", "equate", "estimate", "expect", "explain",
			"express", "extoll", "fear", "feel", "find", "forbid", "forecast", "foretell", "forget", "gather",
			"guarantee", "guess", "hear", "hint", "hope", "illustrate", "imagine", "imply", "indicate", "inform",
			"insert", "insist", "instruct", "interpret", "interview", "invite", "issue", "justify", "learn", "maintain",
			"mean", "mention", "negotiate", "note", "observe", "offer", "oppose", "order", "persuade", "pledge",
			"point", "point out", "praise", "pray", "predict", "prefer", "present", "promise", "prompt", "propose",
			"protest", "prove", "provoke", "question", "quote", "raise", "rally", "read", "reaffirm", "realise",
			"realize", "rebut", "recall", "reckon", "recommend", "refer", "reflect", "refuse", "refute", "reiterate",
			"reject", "relate", "remark", "remember", "remind", "repeat", "reply", "report", "request", "respond",
			"restate", "reveal", "rule", "say", "see", "show", "signal", "sing", "slam", "speculate", "spoke", "spread",
			"state", "stipulate", "stress", "suggest", "support", "suppose", "surmise", "suspect", "swear", "teach",
			"tell", "testify", "think", "threaten", "told", "uncover", "underline", "underscore", "urge", "voice",
			"vow", "warn", "welcome", "wish", "wonder", "worry", "write"));

	public final Set<String> reportNoun = new HashSet<>(Arrays.asList("acclamation", "account", "accusation",
			"acknowledgment", "address", "addressing", "admission", "advertisement", "advice", "advisory", "affidavit",
			"affirmation", "alert", "allegation", "analysis", "anecdote", "annotation", "announcement", "answer",
			"antiphon", "apology", "applause", "appreciation", "argument", "arraignment", "article", "articulation",
			"aside", "assertion", "asseveration", "assurance", "attestation", "attitude", "averment", "avouchment",
			"avowal", "axiom", "backcap", "band-aid", "basic", "belief", "bestowal", "bill", "blame", "blow-by-blow",
			"bomb", "book", "bow", "break", "breakdown", "brief", "briefing", "broadcast", "broadcasting", "bulletin",
			"buzz", "cable", "calendar", "call", "canard", "canon", "card", "cause", "censure", "certification",
			"characterization", "charge", "chat", "chatter", "chitchat", "chronicle", "chronology", "citation", "claim",
			"clarification", "close", "cognizance", "comeback", "comment", "commentary", "communication", "communique",
			"composition", "concept", "concession", "conference", "confession", "confirmation", "conjecture",
			"connotation", "construal", "construction", "consultation", "contention", "contract", "convention",
			"conversation", "converse", "conviction", "counterclaim", "credenda", "creed", "critique", "cry",
			"declaration", "defense", "definition", "delineation", "delivery", "demonstration", "denial", "denotation",
			"depiction", "deposition", "description", "detail", "details", "detention", "dialogue", "diction", "dictum",
			"digest", "directive", "disclosure", "discourse", "discovery", "discussion", "dispatch", "display",
			"disquisition", "dissemination", "dissertation", "divulgence", "dogma", "editorial", "ejaculation",
			"emphasis", "enlightenment", "enunciation", "essay", "evidence", "examination", "example", "excerpt",
			"exclamation", "excuse", "execution", "exegesis", "explanation", "explication", "exposing", "exposition",
			"expounding", "expression", "eye-opener", "feedback", "fiction", "findings", "fingerprint", "flash",
			"formulation", "fundamental", "gift", "gloss", "goods", "gospel", "gossip", "gratitude", "greeting",
			"guarantee", "hail", "hailing", "handout", "hash", "headlines", "hearing", "hearsay", "ideas", "idiom",
			"illustration", "impeachment", "implantation", "implication", "imputation", "incrimination", "indication",
			"indoctrination", "inference", "info", "information", "innuendo", "insinuation", "insistence",
			"instruction", "intelligence", "interpretation", "interview", "intimation", "intonation", "issue", "item",
			"itemization", "justification", "key", "knowledge", "leak", "letter", "locution", "manifesto", "meaning",
			"meeting", "mention", "message", "missive", "mitigation", "monograph", "motive", "murmur", "narration",
			"narrative", "news", "nod", "note", "notice", "notification", "oath", "observation", "okay", "opinion",
			"oral", "outline", "paper", "parley", "particularization", "phrase", "phraseology", "phrasing", "picture",
			"piece", "pipeline", "pitch", "plea", "plot", "portraiture", "portrayal", "position", "potboiler",
			"prating", "precept", "prediction", "presentation", "presentment", "principle", "proclamation",
			"profession", "program", "promulgation", "pronouncement", "pronunciation", "propaganda", "prophecy",
			"proposal", "proposition", "prosecution", "protestation", "publication", "publicity", "publishing",
			"quotation", "ratification", "reaction", "reason", "rebuttal", "receipt", "recital", "recitation",
			"recognition", "record", "recount", "recountal", "refutation", "regulation", "rehearsal", "rejoinder",
			"relation", "release", "remark", "rendition", "repartee", "reply", "report", "reporting", "representation",
			"resolution", "response", "result", "retort", "return", "revelation", "review", "rule", "rumble", "rumor",
			"rundown", "saying", "scandal", "scoop", "scuttlebutt", "sense", "showing", "sign", "signature",
			"significance", "sketch", "skinny", "solution", "speaking", "specification", "speech", "statement", "story",
			"study", "style", "suggestion", "summarization", "summary", "summons", "tale", "talk", "talking", "tattle",
			"telecast", "telegram", "telling", "tenet", "term", "testimonial", "testimony", "text", "theme", "thesis",
			"tract", "tractate", "tradition", "translation", "treatise", "utterance", "vent", "ventilation",
			"verbalization", "version", "vignette", "vindication", "warning", "warrant", "whispering", "wire", "word",
			"work", "writ", "write-up", "writeup", "writing", "acceptance", "complaint", "concern", "disappointment",
			"disclose", "estimate", "laugh", "pleasure", "regret", "resentment", "view"));

	public final Set<String> nonWords = new HashSet<>(Arrays.asList("mm", "hmm", "ahem", "um"));
	public final Set<String> copulas = new HashSet<>(Arrays.asList("is", "are", "were", "was", "be", "been", "become",
			"became", "becomes", "seem", "seemed", "seems", "remain", "remains", "remained"));
	public final Set<String> quantifiers = new HashSet<>(
			Arrays.asList("not", "every", "any", "none", "everything", "anything", "nothing", "all", "enough"));
	public final Set<String> parts = new HashSet<>(Arrays.asList("half", "one", "two", "three", "four", "five", "six",
			"seven", "eight", "nine", "ten", "hundred", "thousand", "million", "billion", "tens", "dozens", "hundreds",
			"thousands", "millions", "billions", "group", "groups", "bunch", "number", "numbers", "pinch", "amount",
			"amount", "total", "all", "mile", "miles", "pounds"));
	public final Set<String> temporals = new HashSet<>(Arrays.asList("second", "minute", "hour", "day", "week", "month",
			"year", "decade", "century", "millennium", "monday", "tuesday", "wednesday", "thursday", "friday",
			"saturday", "sunday", "now", "yesterday", "tomorrow", "age", "time", "era", "epoch", "morning", "evening",
			"day", "night", "noon", "afternoon", "semester", "trimester", "quarter", "term", "winter", "spring",
			"summer", "fall", "autumn", "season", "january", "february", "march", "april", "may", "june", "july",
			"august", "september", "october", "november", "december"));

	public final Set<String> femalePronouns = new HashSet<>(
			Arrays.asList(new String[] { "her", "hers", "herself", "she" }));
	public final Set<String> malePronouns = new HashSet<>(
			Arrays.asList(new String[] { "he", "him", "himself", "his" }));
	public final Set<String> neutralPronouns = new HashSet<>(
			Arrays.asList(new String[] { "it", "its", "itself", "where", "here", "there", "which" }));
	public final Set<String> possessivePronouns = new HashSet<>(
			Arrays.asList(new String[] { "my", "your", "his", "her", "its", "our", "their", "whose" }));
	public final Set<String> otherPronouns = new HashSet<>(
			Arrays.asList(new String[] { "who", "whom", "whose", "where", "when", "which" }));
	public final Set<String> thirdPersonPronouns = new HashSet<>(Arrays.asList(new String[] { "he", "him", "himself",
			"his", "she", "her", "herself", "hers", "her", "it", "itself", "its", "one", "oneself", "one's", "they",
			"them", "themself", "themselves", "theirs", "their", "they", "them", "'em", "themselves" }));
	public final Set<String> secondPersonPronouns = new HashSet<>(
			Arrays.asList(new String[] { "you", "yourself", "yours", "your", "yourselves" }));
	public final Set<String> firstPersonPronouns = new HashSet<>(Arrays.asList(
			new String[] { "i", "me", "myself", "mine", "my", "we", "us", "ourself", "ourselves", "ours", "our" }));
	public final Set<String> moneyPercentNumberPronouns = new HashSet<>(Arrays.asList(new String[] { "it", "its" }));
	public final Set<String> dateTimePronouns = new HashSet<>(Arrays.asList(new String[] { "when" }));
	public final Set<String> organizationPronouns = new HashSet<>(
			Arrays.asList(new String[] { "it", "its", "they", "their", "them", "which" }));
	public final Set<String> locationPronouns = new HashSet<>(
			Arrays.asList(new String[] { "it", "its", "where", "here", "there" }));
	public final Set<String> inanimatePronouns = new HashSet<>(
			Arrays.asList(new String[] { "it", "itself", "its", "where", "when" }));
	public final Set<String> animatePronouns = new HashSet<>(
			Arrays.asList(new String[] { "i", "me", "myself", "mine", "my", "we", "us", "ourself", "ourselves", "ours",
					"our", "you", "yourself", "yours", "your", "yourselves", "he", "him", "himself", "his", "she",
					"her", "herself", "hers", "her", "one", "oneself", "one's", "they", "them", "themself",
					"themselves", "theirs", "their", "they", "them", "'em", "themselves", "who", "whom", "whose" }));
	public final Set<String> indefinitePronouns = new HashSet<>(
			Arrays.asList(new String[] { "another", "anybody", "anyone", "anything", "each", "either", "enough",
					"everybody", "everyone", "everything", "less", "little", "much", "neither", "no one", "nobody",
					"nothing", "one", "other", "plenty", "somebody", "someone", "something", "both", "few", "fewer",
					"many", "others", "several", "all", "any", "more", "most", "none", "some", "such" }));
	public final Set<String> relativePronouns = new HashSet<>(
			Arrays.asList(new String[] { "that", "who", "which", "whom", "where", "whose" }));
	public final Set<String> GPEPronouns = new HashSet<>(
			Arrays.asList(new String[] { "it", "itself", "its", "they", "where" }));
	public final Set<String> pluralPronouns = new HashSet<>(
			Arrays.asList(new String[] { "we", "us", "ourself", "ourselves", "ours", "our", "yourself", "yourselves",
					"they", "them", "themself", "themselves", "theirs", "their" }));
	public final Set<String> singularPronouns = new HashSet<>(
			Arrays.asList(new String[] { "i", "me", "myself", "mine", "my", "yourself", "he", "him", "himself", "his",
					"she", "her", "herself", "hers", "her", "it", "itself", "its", "one", "oneself", "one's" }));
	public final Set<String> facilityVehicleWeaponPronouns = new HashSet<>(
			Arrays.asList(new String[] { "it", "itself", "its", "they", "where" }));
	public final Set<String> miscPronouns = new HashSet<>(
			Arrays.asList(new String[] { "it", "itself", "its", "they", "where" }));
	public final Set<String> reflexivePronouns = new HashSet<>(Arrays.asList(new String[] { "myself", "yourself",
			"yourselves", "himself", "herself", "itself", "ourselves", "themselves", "oneself" }));
	public final Set<String> transparentNouns = new HashSet<>(
			Arrays.asList(new String[] { "bunch", "group", "breed", "class", "ilk", "kind", "half", "segment", "top",
					"bottom", "glass", "bottle", "box", "cup", "gem", "idiot", "unit", "part", "stage", "name",
					"division", "label", "group", "figure", "series", "member", "members", "first", "version", "site",
					"side", "role", "largest", "title", "fourth", "third", "second", "number", "place", "trio", "two",
					"one", "longest", "highest", "shortest", "head", "resident", "collection", "result", "last" }));
	public final Set<String> stopWords = new HashSet<>(Arrays.asList(new String[] { "a", "an", "the", "of", "at", "on",
			"upon", "in", "to", "from", "out", "as", "so", "such", "or", "and", "those", "this", "these", "that", "for",
			",", "is", "was", "am", "are", "'s", "been", "were" }));

	public final Set<String> notOrganizationPRP = new HashSet<>(Arrays.asList(new String[] { "i", "me", "myself",
			"mine", "my", "yourself", "he", "him", "himself", "his", "she", "her", "herself", "hers", "here" }));

	public final Set<String> quantifiers2 = new HashSet<>(Arrays.asList("all", "both", "neither", "either"));
	public final Set<String> determiners = new HashSet<>(
			Arrays.asList("the", "this", "that", "these", "those", "his", "her", "my", "your", "their", "our"));
	private static final Set<String> negations = new HashSet<>(Arrays.asList("n't", "not", "nor", "neither", "never",
			"no", "non", "any", "none", "nobody", "nothing", "nowhere", "nearly", "almost", "if", "false", "fallacy",
			"unsuccessfully", "unlikely", "impossible", "improbable", "uncertain", "unsure", "impossibility",
			"improbability", "cancellation", "breakup", "lack", "long-stalled", "end", "rejection", "failure", "avoid",
			"bar", "block", "break", "cancel", "cease", "cut", "decline", "deny", "deprive", "destroy", "excuse",
			"fail", "forbid", "forestall", "forget", "halt", "lose", "nullify", "prevent", "refrain", "reject", "rebut",
			"remain", "refuse", "stop", "suspend", "ward"));
	private static final Set<String> neg_relations = new HashSet<>(
			Arrays.asList("nmod:without", "acl:without", "advcl:without", "nmod:except", "acl:except", "advcl:except",
					"nmod:excluding", "acl:excluding", "advcl:excluding", "nmod:if", "acl:if", "advcl:if",
					"nmod:whether", "acl:whether", "advcl:whether", "nmod:away_from", "acl:away_from", "advcl:away_fom",
					"nmod:instead_of", "acl:instead_of", "advcl:instead_of"));
	private static final Set<String> modals = new HashSet<>(
			Arrays.asList("can", "could", "may", "might", "must", "should", "would", "seem", "able", "apparently",
					"necessarily", "presumably", "probably", "possibly", "reportedly", "supposedly", "inconceivable",
					"chance", "impossibility", "improbability", "encouragement", "improbable", "impossible", "likely",
					"necessary", "probable", "possible", "uncertain", "unlikely", "unsure", "likelihood", "probability",
					"possibility", "eventual", "hypothetical", "presumed", "supposed", "reported", "apparent"));

	public final Set<String> personPronouns = new HashSet<>();
	public final Set<String> allPronouns = new HashSet<>();

	public final Map<String, String> statesAbbreviation = new HashMap<>();
	private final Map<String, Set<String>> demonyms = new HashMap<>();
	public final Set<String> demonymSet = new HashSet<>();
	private final Set<String> adjectiveNation = new HashSet<>();

	public final Set<String> countries = new HashSet<>();
	public final Set<String> statesAndProvinces = new HashSet<>();

	public final Set<String> neutralWords = new HashSet<>();
	public final Set<String> femaleWords = new HashSet<>();
	public final Set<String> maleWords = new HashSet<>();

	public final Set<String> pluralWords = new HashSet<>();
	public final Set<String> singularWords = new HashSet<>();

	public final Set<String> inanimateWords = new HashSet<>();
	public final Set<String> animateWords = new HashSet<>();

	public final Map<List<String>, Gender> genderNumber = new HashMap<>();

	private void setPronouns() {
		animatePronouns.forEach((s) -> {
			personPronouns.add(s);
		});

		allPronouns.addAll(firstPersonPronouns);
		allPronouns.addAll(secondPersonPronouns);
		allPronouns.addAll(thirdPersonPronouns);
		allPronouns.addAll(otherPronouns);

		stopWords.addAll(allPronouns);
	}

	/**
	 * If the input string is an abbreviation of a U.S. state name or the canonical
	 * name, the canonical name is returned. Otherwise, null is returned.
	 *
	 * @param name Is treated as a cased string. ME != me
	 */
	public String lookupCanonicalAmericanStateName(String name) {
		return statesAbbreviation.get(name);
	}

	/**
	 * Returns a set of demonyms for a country (or city or region).
	 * 
	 * @param name Some string perhaps a country name like "Australia"
	 * @return A Set of demonym Strings, perhaps { "Australian", "Aussie", "Aussies"
	 *         }. If none are known (including if the argument isn't a
	 *         country/region name, then the empty set will be returned.
	 */
	public Set<String> getDemonyms(String name) {
		Set<String> result = demonyms.get(name);
		if (result == null) {
			result = Collections.emptySet();
		}
		return result;
	}

	/**
	 * Returns whether this mention (possibly multi-word) is the adjectival form of
	 * a demonym, like "African" or "Iraqi". True if it is an adjectival form, even
	 * if also a name for a person of that country (such as "Iraqi").
	 */
	public boolean isAdjectivalDemonym(String token) {
		return adjectiveNation.contains(token.toLowerCase(Locale.ENGLISH));
	}

	public Dictionaries(String demonymWords, String animateWords, String inanimateWords, String maleWords,
			String neutralWords, String femaleWords, String pluralWords, String singularWords, String statesWords,
			String genderNumber, String countries, String states, boolean loadCorefDict, String[] corefDictFiles,
			String corefDictPMIFile, String signaturesFile) {
		setPronouns();
	}

}
