package view;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProgramLaunch extends Application
{
	@Override
	public void start(Stage stage) throws Exception
	{
		Entry entry = new Entry();
		Scene scene = new Scene(entry, windowWidth, windowHeight);
		scene.getStylesheets().add(ProgramLaunch.class.getResource("style.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Meme Crawler 6000");
		stage.setResizable(true);
		stage.setOnCloseRequest(e -> entry.getExecutorService().shutdownNow());
		stage.show();
		stage.setMinWidth(windowWidth);
		stage.setMinHeight(windowHeight);
	}
	
	public static void main(String[] args)
	{
		launch();
	}
	
	private final int windowWidth = 850;
	private final int windowHeight = 540;
}