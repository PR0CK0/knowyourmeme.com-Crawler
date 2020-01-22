package model;
import java.util.function.Consumer;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MemeBODYCrawlerUpdated
{
	public MemeBODYCrawlerUpdated(Document anHtmlPage, Meme aMeme)
	{
		htmlPage = anHtmlPage;
		meme = aMeme;
		
		// Added because of specific cases (see: Plastic Love)
		possibleMemeYear = meme.getOriginYear();
	}
	
	public void findCorrectMemeInfo()
	{
		Elements body = htmlPage.getElementsByClass("bodycopy");
		
		for (int i = 0; i < body.get(0).childNodeSize(); i++)
		{
			try
			{
				String headerText = body.get(0).child(i).text();
				
				if (headerText.equalsIgnoreCase("origin"))
				{
					if (body.get(0).child(i + 1).tagName().equalsIgnoreCase("p"))
					{
						String[] originTextWords = body.get(0).child(i + 1).text().split(" ");
						findContentOrigin(originTextWords);
					}
					// Some meme pages have separate headers in the Origin section (see: Big Chungus)
					// I speculate the probability of this is under 5%
					else
					{
						String[] originTextWords = body.get(0).child(i + 3).text().split(" ");
						findContentOrigin(originTextWords);
					}
				}
				
				if (headerText.equalsIgnoreCase("spread"))
				{
					String[] spreadTextWords = body.get(0).child(i + 1).text().split(" ");
					findMemeOrigin(spreadTextWords);
					return;
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				continue;
			}
		}
	}
	
	private void findContentOrigin(String[] textToCheck)
	{
		// Read first sentence for content origin only
		for (int i = 0; i < textToCheck.length && !isEndOfSentence(textToCheck[i]); i++)
		{			
			for (int j = 0; j < sites.length; j++)
			{
				// will probably update this if statement
				if (textToCheck[i].toLowerCase().contains(sites[j]) || (textToCheck[i].toLowerCase().contains(sites[j]) && textToCheck[i+1].toLowerCase().contains(sites[j+1])))
				{
					String contentOrigin = sites[j];
					meme.setContentOrigin(new String[] {contentOrigin});
					findContentOriginYear(textToCheck, i);
					return;
				}
			}
		}
		
		meme.setContentOrigin(new String[] {UNKNOWN_VALUE});
		findContentOriginYearWithNoSite(textToCheck);
	}
	
	private void findMemeOrigin(String[] textToCheck)
	{
		for (int i = 0; i < textToCheck.length; i++)
		{
			for (int j = 0; j < sites.length; j++)
			{
				// will probably update this if statement
				if (textToCheck[i].toLowerCase().contains(sites[j]) || (textToCheck[i].toLowerCase().contains(sites[j]) && textToCheck[i+1].toLowerCase().contains(sites[j+1])))
				{
					String memeOrigin = sites[j];
					meme.setMemeOrigin(memeOrigin);
					findMemeOriginYear(textToCheck, i);
					return;
				}
			}
		}
		
		meme.setMemeOrigin(UNKNOWN_VALUE);
		findMemeOriginYearWithNoSite(textToCheck);
	}
	
	private void findContentOriginYear(String[] textToCheck, int siteIndex)
	{
		genericYear((String[])textToCheck, (Integer)siteIndex, year ->
		{
			if (isNewContentOriginYearAccurate(year))
			{
				meme.setOriginYear(year);				
			}
		});
	}
		
	private void findMemeOriginYear(String[] textToCheck, int siteIndex)
	{
		genericYear((String[])textToCheck, (Integer)siteIndex, year ->
		{
			meme.setMemeYear(year);
		});
		
		setMemeYearIfEmptyAndAfterOriginYear();
	}
	
	private void genericYear(String[] textToCheck, int siteIndex, Consumer<Integer> op)
	{
		for (int i = siteIndex; i >= 0; i--)
		{
			String currentStringUnsanitized = textToCheck[i];
			if (isEndOfSentence(currentStringUnsanitized))	
			{
				break;
			}	
			
			String currentStringToCheck = textToCheck[i];
			if (isStringAValidYear(currentStringToCheck))
			{
				op.accept(Integer.valueOf(sanitizedText(currentStringToCheck)));
				return;
			}
		}
		
		for (int i = siteIndex; i < textToCheck.length; i++)
		{
			String currentStringUnsanitized = textToCheck[i];
						
			if (isEndOfSentence(currentStringUnsanitized))	
			{
				break;
			}
			
			String currentStringToCheck = textToCheck[i];
			if (isStringAValidYear(currentStringToCheck))
			{
				op.accept(Integer.valueOf(sanitizedText(currentStringToCheck)));
				return;
			}
		}
	}
	
	private void findContentOriginYearWithNoSite(String[] textToCheck)
	{				
		genericYearNoSite((String[])textToCheck, year -> 
		{			
			if (isNewContentOriginYearAccurate(year))
			{	
				meme.setOriginYear(year);
			}
		});
	}
	
	private void findMemeOriginYearWithNoSite(String[] textToCheck)
	{
		genericYearNoSite((String[])textToCheck, year -> 
		{			
			meme.setMemeYear(year);
		});
	}
	
	private void genericYearNoSite(String[] textToCheck, Consumer<Integer> op)
	{
		for (int i = 0; i < textToCheck.length; i++)
		{
			String currentStringToCheck = sanitizedText(textToCheck[i]);
			if (isStringAValidYear(currentStringToCheck))
			{
				int newYear = Integer.valueOf(currentStringToCheck);
				op.accept(newYear);
				return;
			}
		}
	}
	
	private void setMemeYearIfEmptyAndAfterOriginYear()
	{
		if (meme.getMemeYear() == -1 && meme.getOriginYear() < possibleMemeYear)
		{
			meme.setMemeYear(possibleMemeYear);
		}
	}
	
	private boolean isEndOfSentence(String str)
	{
		String normalSentenceEndRegex = ".{0,}[. !]";
		String citationSentenceEndRegex = ".{1,}\\.\\[[0-9]{1,}\\]";
		
		return str.matches(normalSentenceEndRegex) || str.matches(citationSentenceEndRegex);
	}
	
	private boolean isStringAValidYear(String str)
	{
		String sanitizedCopy = sanitizedText(str);
		
		int yearToCheck;
		try
		{			
			yearToCheck = Integer.valueOf(sanitizedCopy);
			
			boolean isTheStringActuallyA4DigitNumberOfLikesAndNotAYearWithACommaRightAfterIt = str.substring(0, str.length() - 1).contains(",");
						
			if (isTheStringActuallyA4DigitNumberOfLikesAndNotAYearWithACommaRightAfterIt)
			{
				return false;
			}
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return sanitizedCopy.matches("^\\d{4}") && yearToCheck > EARLIEST_MEME && yearToCheck < CURRENT_YEAR;
	}
		
	private boolean isNewContentOriginYearAccurate(int contentOriginYearToCheck)
	{		
		return contentOriginYearToCheck < meme.getOriginYear();
	}
	
	private String sanitizedText(String str)
	{		
		String wordEndRegex = "[, ; . - ( )]";
		String citationRegex = "\\[[0-9]{1,}\\]";
		
		str = str.replaceAll(wordEndRegex, "");
		str = str.replaceAll(citationRegex, "");
		
		return str;
	}
	
	private Document htmlPage;
	private Meme meme;
	
	private int possibleMemeYear;
	
	private final String[] sites = {"reddit", "tumblr", "facebook", "instagram", "twitter",
			"snapchat", "ifunny", "funnyjunk", "vine", "youtube", "9gag", "4chan", "flickr",
			"imgur", "/r/", "/b/", "/pol/", "urban dictionary", "deviantart", "twitch"};
	
	private final int CURRENT_YEAR = 2020;
	private final int EARLIEST_MEME = 1400;
	
	private final String UNKNOWN_VALUE = "Unknown";
}