package model;
import java.util.concurrent.Callable;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import view.ErrorPopup;

public class MemeCrawler implements Callable<Meme[]>
{
	public MemeCrawler(long sleepDuration, String... urls)
	{
		this.urls = urls;
		this.sleepDuration = sleepDuration;
	}
	
	@Override
	public Meme[] call() throws Exception
	{
		Meme[] memes = new Meme[urls.length];
		
		for(int i = 0; i < urls.length; i++)
		{
			try
			{
				meme = new Meme();
				memes[i] = meme;
				htmlPage = Jsoup.connect(urls[i]).get();
				noMemeCategories = 0;
				
				Element htmlEntryBodySection = htmlPage.getElementById("entry_body").child(0);
				
				checkIfMemeHasCategoriesElement(htmlEntryBodySection);
				
				getMemeName();
				getContentYear(htmlEntryBodySection);
				getContentOrigin();
				getTags(htmlEntryBodySection);
				getCategories(htmlEntryBodySection);	
				
				new MemeBODYCrawlerUpdated(htmlPage, meme).findCorrectMemeInfo();
			}
			catch (HttpStatusException e)
			{
				e.printStackTrace();
				new ErrorPopup("You got banned. You crawled too many memes too fast. "
						+ "Get a different IP and slow it down.\n" + e.getMessage());
				break;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				new ErrorPopup("Error in crawling! (Called directly in MemeCrawler)\n" + e.getMessage());
				break;
			}
			
			Thread.sleep(sleepDuration);
		}
		
		return memes;
	}
	
	private void getMemeName()
	{
		String name = htmlPage.getElementsByTag("title").text();
		int titleSuffixLength = 17;
		meme.setName(name.substring(0, name.length() - titleSuffixLength));
	}
	
	private void getContentYear(Element htmlEntryBody)
	{
		Element htmlAsideSection = htmlEntryBody.child(0);
		String memeYearText;
		
		try
		{
			if (htmlAsideSection.child(5).text().equals("Badges:"))
			{
				Element memeYear = htmlAsideSection.child(8 - noMemeCategories);
				memeYearText = memeYear.text();
				meme.setOriginYear(Integer.valueOf(memeYearText));
			}
			else
			{
				Element memeYear = htmlAsideSection.child(6 - noMemeCategories);
				memeYearText = memeYear.text();
				meme.setOriginYear(Integer.valueOf(memeYearText));
			}
		}
		catch (NumberFormatException e)
		{
			meme.setOriginYear(-1);
		}
	}
	
	private void getContentOrigin()
	{
		Elements contentOrigin = htmlPage.getElementById("entry_body").getElementsByClass("entry_origin_link");
		String[] contentOriginWords = contentOrigin.text().split(", ");
		meme.setContentOrigin(contentOriginWords);
	}
	
	private void getCategories(Element htmlEntryBody)
	{
		if (noMemeCategories == 2)
		{
			return;
		}
		
		Element categories = htmlEntryBody.child(0).child(4);		
	    String[] categoryWords = categories.text().split(", ");
	    meme.setCategories(categoryWords);
	}
	
	private void getTags(Element htmlEntryBody)
	{
		Element tags = htmlEntryBody.child(1).child(1);
		String[] tagWords = tags.text().split(", ");
		meme.setTags(tagWords);
	}
	
	private void checkIfMemeHasCategoriesElement(Element htmlEntryBody)
	{
		Element htmlAsideSection = htmlEntryBody.child(0);
		if (!htmlAsideSection.child(3).text().equals("Type:"))
		{
			noMemeCategories = 2;
		}
	}
	
	private final long sleepDuration;
	private final String[] urls;
	private Document htmlPage;
	private Meme meme;
	private int noMemeCategories;
}
