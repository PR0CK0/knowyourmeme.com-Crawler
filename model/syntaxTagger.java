package model;

public class syntaxTagger
{
	public static void main(String[] args)
	{
		String[] abc = {"Art", "Country", "Food", "Movement", "Music", "Religion", "Sport", "Technology", 
				"Auction", "AwardCeremony", "Campaign", "Competition", 
				"Controversy", "Convention", "Crime", "Disaster", "Election", "FlashMob", "Gaffe", "Hack", "Holiday", 
				"Law", "Leak", "Performance", "Prank", "Protest", "Raid", "Trial", 
				"Advertisement", "Animal", "Axiom", "Catchphrase", "Character", "Cliche", "ConspiracyTheory", "Copypasta",
				"Creepypasta", "Dance", "Emoticon", "Exploitable", "FanArt", "FanLabor", "Hashtag", "Hoax", "ImageMacro",
				"OpticalIllusion", "Parody", "ParticipatoryMedia", "Photoshop", "PopCultureReference", "Reaction", "Remix",
				"Slang", "Snowclone", "SocialGame", "Song", "ViralDebate", "ViralVideo", "VisualEffect", 
				"Activist", "Actor", "Artist", "Athlete", "Businessperson", "Comedian", "Filmmaker", "Gamer", "Hacker",
				"HistoricalFigure", "Influencer", "Model", "Musician", "Organization", "Politician", "Programmer", "Scientist",
				"TVPersonality", "Vlogger", "Writer", "Application", "Blog", "Forum", "Generator", "Marketplace", "MediaHost",
				"NewsPublication", "Reference", "SocialNetwork", "Album", "Anime", "Book", "Cartoon", "ComicBook", "Company",
				"Fauna", "Fetish", "Film", "Manga", "Podcast", "Product", "TVShow", "Theater", "VideoGame", "WebSeries", "Webcomic"};
		
		//http://erau-semantic-research.com/2019/memo/0.1/
		String prefix = "<owl:NamedIndividual rdf:about=\"memo:";
		String suffix = "Category\">\n  <rdf:type rdf:resource=\"memo:Category\"/>\n"
				+ "  <rdf:isDefinedBy rdf:resource=\"http://erau-semantic-research.com/2019/memo/0.1/\"/>\n</owl:NamedIndividual>";
		
	    for (int i = 0; i < abc.length; i++)
	    {
	    	System.out.println(prefix + abc[i] + suffix);
	    	System.out.println();
	    }
	}
}