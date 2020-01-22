package model;

public class syntaxTagger
{
	public static void main(String[] args)
	{
		String[] cultures = {"Art", "Country", "Food", "Movement", "Music", "Religion", "Sport", "Technology"};
		String[] events = {"Auction", "AwardCeremony", "Campaign", "Competition", 
				"Controversy", "Convention", "Crime", "Disaster", "Election", "FlashMob", "Gaffe", "Hack", "Holiday", 
				"Law", "Leak", "Performance", "Prank", "Protest", "Raid", "Trial"};
		String[] memes = {"Advertisement", "Animal", "Axiom", "Catchphrase", "Character", "Cliche", "ConspiracyTheory", "Copypasta",
				"Creepypasta", "Dance", "Emoticon", "Exploitable", "FanArt", "FanLabor", "Hashtag", "Hoax", "ImageMacro",
				"OpticalIllusion", "Parody", "ParticipatoryMedia", "Photoshop", "PopCultureReference", "Reaction", "Remix",
				"Slang", "Snowclone", "SocialGame", "Song", "ViralDebate", "ViralVideo", "VisualEffect"};
		String[] people = {"Activist", "Actor", "Artist", "Athlete", "Businessperson", "Comedian", "Filmmaker", "Gamer", "Hacker",
				"HistoricalFigure", "Influencer", "Model", "Musician", "Organization", "Politician", "Programmer", "Scientist",
				"TVPersonality", "Vlogger", "Writer"};
		String[] sites = {"Application", "Blog", "Forum", "Generator", "Marketplace", "MediaHost",
				"NewsPublication", "Reference", "SocialNetwork"};
		String[] subcultures ={"Album", "Anime", "Book", "Cartoon", "ComicBook", "Company",
				"Fauna", "Fetish", "Film", "Manga", "Podcast", "Product", "TVShow", "Theater", "VideoGame", "WebSeries", "Webcomic"};
		
		//http://erau-semantic-research.com/2019/memo/0.1/
		String prefix = "<owl:NamedIndividual rdf:about=\"http://erau-semantic-research.com/2019/memo/0.1/";
		String suffix = "CategorySpecification\">\n  <rdf:type rdf:resource=\"http://erau-semantic-research.com/2019/memo/0.1/CategorySpecification\"/>\n"
				+ "  <rdf:isDefinedBy rdf:resource=\"http://erau-semantic-research.com/2019/memo/0.1/\"/>\n</owl:NamedIndividual>";
		
	    for (int i = 0; i < subcultures.length; i++)
	    {
	    	System.out.println(prefix + subcultures[i] + suffix);
	    	System.out.println();
	    }
	}
}