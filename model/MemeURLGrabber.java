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
			File input = loadFile();
			htmlPage = Jsoup.parse(input, "UTF-8", "http://example.com/");
			getMemeURLSFromPage();
		}
		catch (Exception e)
		{
			Platform.runLater(() -> new ErrorPopup(e.getMessage() + "\nError grabbing meme URLs from HTML file."));
		}
		
		return memeURLS.toArray(new String[0]);
	}
	
	private File loadFile()
	{
		// Brute force load the html file using multiple ways:
		File input = null;
		
		// Try loading it if it was inside model folder
		try
		{
			input = new File(this.getClass().getResource(fileName).getPath());
			System.out.println("Loaded url file cause inside of model directory");
		}
		catch(Exception e)
		{
		}
		
		// Short circuit if we loaded
		if(input != null)
			return input;
		
		// Try loading it if it was inside view folder
		try
		{
			input = new File(this.getClass().getResource("../view/" + fileName).getPath());
			System.out.println("Loaded url file cause inside of view directory");
		}
		catch(Exception e)
		{
		}
		
		// Short circuit if we loaded
		if(input != null)
			return input;
		
		// Try loading it if it was outside model folder (same directory as jar)
		try
		{
			input = new File(this.getClass().getResource("../" + fileName).getPath());
			System.out.println("Loaded url file cause of same directory of jar");
		}
		catch(Exception e)
		{
		}
		
		// Short circuit if we loaded
		if(input != null)
			return input;
		
		// Try loading it if it was outside the same directory of the jar
		try
		{
			input = new File(this.getClass().getResource("../../" + fileName).getPath());
			System.out.println("Loaded url file cause of outside of same directory of jar");
		}
		catch(Exception e)
		{
		}
		
		return input;
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
			memeURLS.add(URLPrefix + individualMemeURL);
		}
	}
	
	private final String fileName = "everyConfirmedMeme.html";
	private ArrayList<String> memeURLS;
	private Document htmlPage;
	private final String URLPrefix = "https://knowyourmeme.com";
}