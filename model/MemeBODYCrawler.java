package model;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/* 
 * 
 * PROCKO - March 2019
 * 
 * This class is a bit deprecated. Use MemeBODYCrawlerUpdated.java.
 *
 * The point of this class is to gather more accurate data about each meme
 * from the knowyourmeme page, as the information in the column on the
 * righthand side of each meme can often be ambiguous, due to users. 
 * 
 * This is accomplished by using, if they exist on a specific meme, the
 * ORIGIN and SPREAD sections of the meme's description. If either of
 * these sections were included by the author of the meme's page, then
 * the data will be harvested.
 * 
 * Otherwise, and this is key: the data will remain the same. 
 * 
 * The code checks the plaintext for any "common" meme origins, like
 * Youtube, Twitter, etc... If it is not found, then, most likely,
 * the content origin is too specific and will come from the column
 * on the righthand side of the meme. 
 * 
 * This ensures that the data is as accurate as possible.
 * 
 */

@Deprecated
public class MemeBODYCrawler
{
	// So much redundancy, should have done a predicate but idc
	
	public MemeBODYCrawler(Document anHtmlPage, Meme aMeme)
	{
		htmlPage = anHtmlPage;
		meme = aMeme;
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
					String[] originTextWords = body.get(0).child(i + 1).text().split(" ");
					findContentOrigin(originTextWords);
				}
				
				if (headerText.equalsIgnoreCase("spread"))
				{
					String[] spreadTextWords = body.get(0).child(i + 1).text().split(" ");
					findMemeOrigin(spreadTextWords);
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
		for (int i = 0; i < textToCheck.length; i++)
		{
			for (int j = 0; j < sites.length; j++)
			{
				if (textToCheck[i].toLowerCase().contains(sites[j]))
				{
					String contentOrigin = sites[j];
					meme.setContentOrigin(new String[] {contentOrigin});
					findContentOriginYear(textToCheck, i);
					return;
				}
			}
		}
		
		findContentOriginYearWithNoSite(textToCheck);
	}
	
	private void findMemeOrigin(String[] textToCheck)
	{
		for (int i = 0; i < textToCheck.length; i++)
		{
			for (int j = 0; j < sites.length; j++)
			{
				if (textToCheck[i].toLowerCase().contains(sites[j]))
				{
					String memeOrigin = sites[j];
					meme.setMemeOrigin(memeOrigin);
					findMemeOriginYear(textToCheck, i);
					return;
				}
			}
		}
		
		findMemeOriginYearWithNoSite(textToCheck);
	}
	
	private void findContentOriginYear(String[] textToCheck, int siteIndex)
	{
		for (int i = siteIndex; i > 0; i--)
		{
			String currentStringUnsanitized = textToCheck[i];
						
			if (isEndOfSentence(currentStringUnsanitized))	
			{
				break;
			}
			
			String currentStringToCheck = sanitizedText(textToCheck[i]);
			if (isStringAYear(currentStringToCheck))
			{
				int newContentOriginYear = Integer.valueOf(currentStringToCheck);
				if (isNewContentOriginYearAccurate(newContentOriginYear))
				{					
					meme.setOriginYear(newContentOriginYear);					
					return;
				}
			}
		}
		
		for (int i = siteIndex; i < textToCheck.length; i++)
		{
			String currentStringUnsanitized = textToCheck[i];
			if (isEndOfSentence(currentStringUnsanitized))	
			{
				break;
			}
			
			String currentStringToCheck = sanitizedText(textToCheck[i]);
			if (isStringAYear(currentStringToCheck))
			{
				int newContentOriginYear = Integer.valueOf(currentStringToCheck);
				if (isNewContentOriginYearAccurate(newContentOriginYear))
				{					
					meme.setOriginYear(newContentOriginYear);					
					return;
				}
			}
		}
	}
	
	private void findMemeOriginYear(String[] textToCheck, int siteIndex)
	{
		for (int i = siteIndex; i > 0; i--)
		{
			String currentStringUnsanitized = textToCheck[i];
			if (isEndOfSentence(currentStringUnsanitized))	
			{
				break;
			}	
			
			String currentStringToCheck = sanitizedText(textToCheck[i]);
			if (isStringAYear(currentStringToCheck))
			{	
				meme.setMemeYear(Integer.valueOf(currentStringToCheck));
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
			
			String currentStringToCheck = sanitizedText(textToCheck[i]);
			if (isStringAYear(currentStringToCheck))
			{
				meme.setMemeYear(Integer.valueOf(currentStringToCheck));
				return;
			}
		}
	}
	
	private void findContentOriginYearWithNoSite(String[] textToCheck)
	{		
		for (int i = 0; i < textToCheck.length; i++)
		{
			String currentStringToCheck = sanitizedText(textToCheck[i]);
			if (isStringAYear(currentStringToCheck))
			{
				int newContentOriginYear = Integer.valueOf(currentStringToCheck);
				if (isNewContentOriginYearAccurate(newContentOriginYear))
				{					
					meme.setOriginYear(newContentOriginYear);					
					return;
				}
			}
		}
	}
	
	private void findMemeOriginYearWithNoSite(String[] textToCheck)
	{
		for (int i = 0; i < textToCheck.length; i++)
		{
			String currentStringToCheck = sanitizedText(textToCheck[i]);
			if (isStringAYear(currentStringToCheck))
			{
				int newMemeYear = Integer.valueOf(currentStringToCheck);
				meme.setOriginYear(newMemeYear);					
				return;
			}
		}
	}
	
	private boolean isEndOfSentence(String str)
	{
		String normalSentenceEndRegex = ".{0,}[. !]";
		String citationSentenceEndRegex = ".{1,}\\.\\[[0-9]{1,}\\]";
		
		return str.matches(normalSentenceEndRegex) || str.matches(citationSentenceEndRegex);
	}
	
	private boolean isStringAYear(String str)
	{
		return str.matches("^\\d{4}");
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
	
	private final String[] sites = {"reddit", "tumblr", "facebook", "instagram", "twitter",
			"snapchat", "ifunny", "funnyjunk", "vine", "youtube", "9gag", "4chan", "flickr",
			"imgur", "/r/", "/b/", "/pol/", "urban dictionary", "deviantart"};
}
