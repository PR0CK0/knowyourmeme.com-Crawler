package cluster;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Test extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Pane circlesPane = new Pane();
		circlesPane.setMinWidth(640);
		circlesPane.setMinHeight(430);
		Pane clustersPane = new Pane();
		
		Button clearButton = new Button("Clear");
		clearButton.setOnAction(e ->
		{
			circlesPane.getChildren().clear();
			clustersPane.getChildren().clear();
		});
		
		Label clusterLabel = new Label("Clusters: ");
		clusterLabel.setTextFill(Color.WHITE);
		
		TextField clusterTextField = new TextField("3");
		clusterTextField.setMaxWidth(80);
		
		Button clusterButton = new Button("Cluster");
		
		HBox hbox = new HBox(20, clearButton, clusterLabel, clusterTextField, clusterButton);
		
		clusterButton.setOnAction(e ->
		{
			int clusterCount = 3;
			try
			{
				clusterCount = Integer.valueOf(clusterTextField.getText());
			}
			catch(Exception ex)
			{
				clusterCount = 3;
				clusterTextField.setText("3");
			}
			
			cluster(clusterCount, circlesPane, clustersPane, hbox);
		});
		
		VBox root = new VBox(clustersPane, circlesPane, hbox);
		root.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
		Scene scene = new Scene(root);
		
		scene.setOnMouseClicked(e ->
		{
			// Spawn circles on LMB
			if(!isClustering && e.getButton() == MouseButton.PRIMARY && e.getY() < 430)
			{
				Circle circle = new Circle(8, Color.WHITE);
				circle.setCenterX(e.getX());
				circle.setCenterY(e.getY());
				circlesPane.getChildren().add(circle);
				
				// Remove circles on RMB
				circle.setOnMouseClicked(e2 ->
				{
					if(e2.getButton() == MouseButton.SECONDARY)
					{
						circlesPane.getChildren().remove(circle);
					}
				});
			}
		});
		
		primaryStage.setScene(scene);
		primaryStage.setWidth(640);
		primaryStage.setHeight(480);
		primaryStage.setResizable(false);
		primaryStage.setTitle("K-Means Test");
		primaryStage.show();
		
		Alert helpAlert = new Alert(AlertType.INFORMATION);
		helpAlert.setHeaderText("Lemme show u da wae");
		helpAlert.setContentText("Left Mouse Button - Add Meme Vector\nRight Mouse Button - Delete Meme Vector\n");
		helpAlert.setTitle("Controls");
		helpAlert.showAndWait();
	}
	
	private void cluster(int clusterCount, Pane circlesPane, Pane clustersPane, HBox hbox)
	{
		clusteringIndex = 0;
		final int maxIterations = 10;
		
		
		// Get points from gui
		Vector[] points = new Vector[circlesPane.getChildren().size()];
		for(int i = 0; i < circlesPane.getChildren().size(); i++)
		{
			Circle circle = (Circle) circlesPane.getChildren().get(i);
			circle.setFill(Color.WHITE);
			points[i] = new Vector(circle.getCenterX(), circle.getCenterY());
		}
		
		KMeans algorithm = new KMeans(clusterCount, 2, points);
		
		isClustering = true;
		hbox.setDisable(true);
		
		clustersPane.getChildren().clear();
		for(int i = 0; i < clusterCount; i++)
		{
			Cluster cluster = algorithm.getClusters()[i];
			Circle clusterCircle = new Circle(32, Color.hsb(360.0 / clusterCount * i, 1, 1, 0.3));
			clusterCircle.setCenterX(cluster.getPosition().getValues()[0]);
			clusterCircle.setCenterY(cluster.getPosition().getValues()[1]);
			clustersPane.getChildren().add(clusterCircle);
		}
		
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.35), e ->
		{
			// Do clustering!
			algorithm.update();
			
			for(int i = 0; i < circlesPane.getChildren().size(); i++)
			{
				Circle circle = (Circle) circlesPane.getChildren().get(i);
				Vector pos = new Vector(circle.getCenterX(), circle.getCenterY());
				Cluster cluster = algorithm.getNearestCluster(pos);
				if(cluster != null)
				{
					int index = 0;
					for(index = 0; index < algorithm.getClusters().length; index++)
					{
						if(algorithm.getClusters()[index] == cluster)
							break;
					}
					
					Circle clusterCircle = (Circle) clustersPane.getChildren().get(index);
					Color color = (Color) clusterCircle.getFill();
					circle.setFill(color.deriveColor(0, 1, 1, 2));
				}
			}
			
			for(int i = 0; i < clusterCount; i++)
			{
				Cluster cluster = algorithm.getClusters()[i];
				
				Circle clusterCircle = (Circle) clustersPane.getChildren().get(i);
				clusterCircle.setCenterX(cluster.getPosition().getValues()[0]);
				clusterCircle.setCenterY(cluster.getPosition().getValues()[1]);
				
				Vector furthestPoint = cluster.getFurthestPoint();
				double size = 32;
				if(furthestPoint != null)
				{
					size = Math.max(32, Vector.getDistance(cluster.getPosition(), furthestPoint));
				}
				clusterCircle.setRadius(size*2);
			}
			
			clusteringIndex++;
			
			if(clusteringIndex == maxIterations)
			{
				isClustering = false;
				hbox.setDisable(false);
				timeline.stop();
			}
		}));
		timeline.play();
	}
	
	private int clusteringIndex = 0;
	private boolean isClustering = false;
}
