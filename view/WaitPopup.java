package view;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WaitPopup extends Stage
{	
	public WaitPopup(int waitDuration, int start, int end)
	{		
		this.waitDuration = waitDuration;
		this.start = start;
		this.end = end;
		layoutContents();

		show();
		
		btOK.setOnAction(e -> close());
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), e ->
		{
			this.waitDuration--;
			lblText.setText("Approximate time left: " + this.waitDuration + " seconds."
					+ "\nStart index: " + start + "\nEnd index: " + end);
			if(this.waitDuration <= 0)
			{
				close();
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
	
	private final VBox root = new VBox(20);
	private final Button btOK = new Button("OK!");
	
	private final int SCENE_WIDTH = 500;
	private final int SCENE_HEIGHT = 200;
	private final String SCENE_TITLE = "ZzzzZzzz!";
	
	private Scene scene;
	private Label lblText;
	private int waitDuration;
	private int start;
	private int end;
	private Timeline timeline;
	
	private void layoutContents()
	{
		scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		setScene(scene);
		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);
		setTitle(SCENE_TITLE);
		
		root.setAlignment(Pos.CENTER);
		
		lblText = new Label("Approximate time left: " + this.waitDuration + " seconds for: "
				+ "\nStart index: " + start + "\nEnd index: " + end);
		lblText.setStyle("-fx-font-size: 1.2em; -fx-font-style: italic"); 
		
		btOK.setStyle("-fx-background-radius: 0, 0, 0, 0; -fx-font-size: 1.10em");
		
		lblText.setTextAlignment(TextAlignment.CENTER);
		lblText.setPadding(new Insets(0, 10, 0, 10));
		lblText.setWrapText(true);
		
		root.getChildren().addAll(lblText, btOK);
	}
}
