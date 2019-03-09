package view;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javafx.application.Platform;
import model.Meme;
import model.MemeCrawler;
import model.MemeURLGrabber;

class CrawlUIThread implements Runnable
{
	public CrawlUIThread(Entry entry, long delay, int startIndex, int endIndex, String... urls)
	{
		this.entry = entry;
		this.urls = urls;
		this.urlsCallable = null;
		
		this.delay = delay;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public CrawlUIThread(Entry entry, long delay, int startIndex, int endIndex, Callable<String[]> urls)
	{
		this.entry = entry;
		this.urlsCallable = urls;
		this.urls = null;
		
		this.delay = delay;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	@Override
	public void run()
	{
		// Get URL (if we got a callable)
		if(urlsCallable != null)
		{
			Future<String[]> grabbedURLs = entry.getExecutorService().submit(new MemeURLGrabber());
			try
			{
				urls = grabbedURLs.get();
				urls = Arrays.copyOfRange(urls, startIndex, endIndex);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				Platform.runLater(() -> new ErrorPopup(e.getMessage() + "\nError URL grabbing."));
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
				Platform.runLater(() -> new ErrorPopup(e.getMessage() + "\nError URL grabbing."));
			}
		}
		
		Future<Meme[]> crawler = entry.getExecutorService().submit(new MemeCrawler(delay, urls));
		
		int expectedDuration = (int) ((delay + 0.5) * urls.length);
		Platform.runLater(() -> new WaitPopup(expectedDuration, startIndex, endIndex));
		
		Meme[] memes = null;
		try
		{
			memes = crawler.get(); // This is a blocking call
		}
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
			Platform.runLater(() -> new ErrorPopup(e.getMessage() + "\nError crawling memes (called from CrawlUIThread)."));
		}
		
		final Meme[] memesFinal = memes;
		
		Platform.runLater(() ->
		{
			entry.getLvMemes().getItems().addAll(memesFinal);
			entry.getLvMemes().getSelectionModel().select(entry.getLvMemes().getItems().size() - 1); // select last
		});
	}
	
	private final Entry entry;
	private String[] urls;
	private Callable<String[]> urlsCallable;
	
	private long delay;
	private int startIndex;
	private int endIndex;
}