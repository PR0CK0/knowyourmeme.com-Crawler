package model;
import java.util.concurrent.Callable;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javafx.application.Platform;
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
				htmlPage = Jsoup.connect(urls[i]).get();				
				noMemeCategories = 0;
				
				Element htmlEntryBodySection = htmlPage.getElementById("entry_body").child(0);
				
				checkIfMemeHasCategoriesElement(htmlEntryBodySection);
				
				meme.setMemeURL(htmlPage.baseUri());
				
				getMemeName();
				// new
				getMemeText();
				getLinksInMemeText();
				getImagesInTextBody();
				// meh
				getExternalReferences();
				
				getContentYear(htmlEntryBodySection);
				getContentOrigin();
				getTags(htmlEntryBodySection);
				getCategories(htmlEntryBodySection);	
				
				new MemeBODYCrawlerUpdated(htmlPage, meme).findCorrectMemeInfo();
				
				memes[i] = meme;	
			}
			catch (HttpStatusException e)
			{
				try
				{					
					// Additional try/catch because of: https://knowyourmeme.com/memes/people/memesaysstuff,
					// a deleted meme that threw a 404- the same error as an IP ban
					
					String arbitraryGuaranteedMemeURL = "https://knowyourmeme.com/memes/is-this-a-pigeon";
					Document banTest = Jsoup.connect(arbitraryGuaranteedMemeURL).get();
				}
				catch (HttpStatusException e2)
				{
					Platform.runLater(() -> 
					{
						new ErrorPopup("Your IP is banned from the site. You crawled too many memes too fast. "
								+ "Get a different IP and slow it down.\n" + e2.getMessage());
					});
							
					break;
				}
				
				final String badURL = urls[i];
				Platform.runLater(() -> 
				{
					new ErrorPopup("Bad URL found: " + badURL
							+ "\nMost likely the meme was deleted from the site. Not your problem."
							+ "\nJust note that there will be an empty meme in the list now. The crawl "
							+ "will still continue. Do not worry.");
				});

				Meme emptyMeme = new Meme();
				memes[i] = emptyMeme;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Platform.runLater(() -> new ErrorPopup("Error in crawling! (Called directly in MemeCrawler)\n" + e.getMessage()));
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
	
	// new
	private void getMemeText()
	{
		Elements textBody = htmlPage.getElementsByClass("bodycopy");
		String text = textBody.text();
		
		meme.setMemeText(textBody.text().substring(6, textBody.text().length()));
	}
	
	// new
	private void getLinksInMemeText()
	{
		Elements links = htmlPage.getElementsByClass("bodycopy").select("a[href~=/memes/.*]");
		
		// lazy
		int i = 0;
		String[] linksArr = new String[links.size()];
		for (Element ele : links)
		{
			String link = ele.attr("abs:href");
			
//			System.out.println(link);
			
			// & symbol is death to an RDF file
			if (!link.contains("&"))
			{
				linksArr[i] = link;
			}
			i++;				
		}
		
		meme.setLinksInMemeText(linksArr);
	}
	
	// new
	private void getExternalReferences()
	{
		Elements links = htmlPage.getElementsByClass("footnote-text").select("a");
		
		// lazy
		int i = 0;
		String[] linksArr = new String[links.size()];
		for (Element ele : links)
		{
			String link = ele.attr("href");
			linksArr[i] = link;
			i++;
		}
		
		meme.setExternalReferenceLinks(linksArr);
	}
	
	// new
	private void getImagesInTextBody()
	{
		// =\"[0-9]+(px)?\"
		Elements images = htmlPage.select("img[style~=(height.+)|(max-width.+);]");
//		Elements images = htmlPage.select("img[class=kym-image]");
		
		// lazy af
		int i = 0;
		
		String[] imageLinks = new String[images.size()];
		
		for (Element ele : images)
		{
			String imageLink = ele.attr("data-src");
			imageLinks[i] = imageLink;
			
			// this saves images to disk; works fine
//			try
//			{
//				URL url = new URL(imageLink);
//				BufferedImage img = ImageIO.read(url);
//				File file = new File("D:\\test\\" + meme.getName() + "" + i + ".jpg");
//				ImageIO.write(img, "jpg", file);
//			}
//			catch (Exception e1)
//			{
//				System.out.println("Oof.");
//			}
//			
			i++;
		}
		
		meme.setImageLinks(imageLinks);
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