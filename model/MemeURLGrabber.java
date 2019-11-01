package model;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javafx.application.Platform;
import view.ErrorPopup;

public class MemeURLGrabber implements Callable<String[]>
{
	@Override
	public String[] call()
	{
		try
		{						
			File input = new File("resources/"+fileName);
			htmlPage = Jsoup.parse(input, "UTF-8", "http://example.com/");
			getMemeURLSFromPage();
		}
		catch (Exception e)
		{
			Platform.runLater(() -> new ErrorPopup(e.getMessage() + "\nError grabbing meme URLs from HTML file."));
		}
		
		return memeURLS.toArray(new String[0]);
	}
	
	private void getMemeURLSFromPage()
	{
		Element htmlEntryBodySection = htmlPage.getElementById("entries_list");
		Elements entireMemeListDiv = htmlEntryBodySection.getElementsByAttributeValueStarting("class", "entry_");
		
		memeURLS = new ArrayList<String>();
		
		for (Element e : entireMemeListDiv)
		{
			Elements theMemeURLDiv = e.getElementsByClass("photo");
			String individualMemeURL = theMemeURLDiv.attr("href"); 
			// updated for new
			memeURLS.add(/*URLPrefix + */individualMemeURL);
		}
		
		System.out.println(memeURLS.size());
	}
	
//	private final String fileName = "everyConfirmedMeme.html";
	private final String fileName = "allConfirmedMemesNEW.html";
	private ArrayList<String> memeURLS;
	private Document htmlPage;
	private final String URLPrefix = "https://knowyourmeme.com";
}