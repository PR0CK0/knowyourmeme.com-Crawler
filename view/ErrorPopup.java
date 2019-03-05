package view;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorPopup extends Stage
{	
	public ErrorPopup(String error)
	{
		layoutContents(error);

		show();
		
		btOK.setOnAction(e -> close());
	}
	
	private final VBox root = new VBox(20);
	private final Button btOK = new Button("OK!");
	
	private final int SCENE_WIDTH = 500;
	private final int SCENE_HEIGHT = 200;
	private final String SCENE_TITLE = "Oops!";
	
	private Scene scene;
	private Label lblText;
	
	private void layoutContents(String error)
	{
		scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		setScene(scene);
		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);
		setTitle(SCENE_TITLE);
		
		root.setAlignment(Pos.CENTER);
		
		lblText = new Label("Error: " + error + "\n\nTP note: You suck. :)");
		lblText.setStyle("-fx-font-size: 1.2em; -fx-font-style: italic"); 
		
		btOK.setStyle("-fx-background-radius: 0, 0, 0, 0; -fx-font-size: 1.10em");
		
		lblText.setTextAlignment(TextAlignment.CENTER);
		lblText.setPadding(new Insets(0, 10, 0, 10));
		lblText.setWrapText(true);
		
		root.getChildren().addAll(lblText, btOK);
	}
}