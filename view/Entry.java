package view;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Meme;
import model.MemeConverter;
import model.MemeURLGrabber;

public class Entry extends BorderPane
{
	public Entry()
	{
		initializeNodes();
		initializeLayout();
		addUserInputHooks();
		addListeners();
	}
	
	private void initializeNodes()
	{
		lblURL = new Label("URL: ");
		tfURL = new TextField("");
		tfURL.setPromptText("https://www.knowyourmeme.com/...");
		
		btCrawl = new Button("Crawl One From URL");
		btCrawlAll = new Button("Crawl All From File");
		
		lblMemeOrigin = new Label("Meme Origin: ");
		tfMemeOrigin = new TextField("");
		tfMemeOrigin.setMaxWidth(100);
		
		lblMemeYear = new Label("Meme Origin Year: ");
		tfMemeYear = new TextField("0");
		tfMemeYear.setMaxWidth(50);
		
		tfDelay = new TextField("2000");
		tfStartIndex = new TextField("0");
		tfEndIndex = new TextField("500");
		tfDelay.setPromptText("# of ms delay");
		tfStartIndex.setPromptText("Start index");
		tfEndIndex.setPromptText("End index");
		tfDelay.setMaxWidth(100);
		tfStartIndex.setMaxWidth(100);
		tfEndIndex.setMaxWidth(100);
		
		btCopyTabText = new Button("Copy Tab Text");
		btExportSelected = new Button("Export Selected");
		btExportAll = new Button("Export All");
		lvMemes = new ListView<>();
		
		taPlainText = new TextArea("");
		spPlainText = new ScrollPane(taPlainText);
		taRDF = new TextArea("");
		spRDF = new ScrollPane(taRDF);
		taCSV = new TextArea("");
		spCSV = new ScrollPane(taCSV);
		
		tpFormats = new TabPane();
		Tab tabPlainText = new Tab("Plain");
		tabPlainText.setContent(taPlainText);
		Tab tabRDF = new Tab("RDF");
		tabRDF.setContent(spRDF);
		Tab tabCSV = new Tab("CSV");
		tabCSV.setContent(spCSV);
		tpFormats.getTabs().addAll(tabPlainText, tabRDF, tabCSV);
		
		tbButtons = new ToolBar(btCopyTabText, new Separator(Orientation.VERTICAL), btExportSelected, btExportAll, new Separator(Orientation.VERTICAL), btCrawl, btCrawlAll);
		btExportSelected.setDisable(true);
	}
	
	private void initializeLayout()
	{
		// TOP
		HBox hboxURL = new HBox(20, lblURL, tfURL);
		hboxURL.setPadding(new Insets(0, 20, 0, 20));
		tfURL.setMinWidth(100);
		tfURL.setPrefWidth(500);
		tfURL.setMaxWidth(500);
		
		HBox hboxOrigin = new HBox(15, lblMemeOrigin, tfMemeOrigin, lblMemeYear, tfMemeYear, tfDelay, tfStartIndex, tfEndIndex);
		hboxOrigin.setPadding(new Insets(0, 20, 0, 20));
		tbButtons.setOrientation(Orientation.HORIZONTAL);
		
		VBox vboxTop = new VBox(20, tbButtons, hboxURL, hboxOrigin);
		vboxTop.setPadding(new Insets(0, 0, 20, 0));
		setTop(vboxTop);
		
		// CENTER
		HBox hboxCenter = new HBox(lvMemes, tpFormats);
		taPlainText.setEditable(false);
		
		// new
		taPlainText.setWrapText(true);
		
		spPlainText.setFitToWidth(true);
		spPlainText.setFitToHeight(true);
		taRDF.setEditable(false);
		spRDF.setFitToWidth(true);
		spRDF.setFitToHeight(true);
		taCSV.setEditable(false);
		spCSV.setFitToWidth(true);
		spCSV.setFitToHeight(true);
		HBox.setHgrow(tpFormats, Priority.ALWAYS);
		setCenter(hboxCenter);
		tpFormats.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		lvMemes.setCellFactory(lv -> new ListCell<Meme>()
		{
			@Override
			public void updateItem(Meme item, boolean empty)
			{
				Platform.runLater(() ->
				{
					super.updateItem(item, empty);
					if(empty)
					{
						setText(null);
					}
					else
					{
						setText("Meme: " + item.getName());
					}
				});
			}
		});
	}
	
	private void addUserInputHooks()
	{
		btCrawl.setOnAction(e ->
		{
			if (!tfURL.getText().isEmpty())
			{
				// Do the heavy loading on a separate thread to prevent UI from freezing
				executorService.execute(new CrawlUIThread(this, 0, 0, 0, tfURL.getText()));
				tfURL.clear();				
			}
		});
		
		btCrawlAll.setOnAction(e ->
		{
			if (crawlAllInfoValid())
			{				
				// Do the heavy loading on a separate thread to prevent UI from freezing
				executorService.execute(new CrawlUIThread(this, Long.valueOf(tfDelay.getText()), 
						Integer.valueOf(tfStartIndex.getText()), Integer.valueOf(tfEndIndex.getText()), new MemeURLGrabber()));
			}
		});
		
		btExportSelected.setOnAction(e ->
		{
			exportSelected();
		});
		
		btExportAll.setOnAction(e ->
		{
			exportAll();
		});
		
		btCopyTabText.setOnAction(e ->
		{
			copyTabText();
		});
		
		addEventHandler(KeyEvent.KEY_PRESSED, e ->
		{
			// Delete meme by pressing DELETE
			if(e.getCode() == KeyCode.DELETE && getSelectedMeme() != null)
			{
				lvMemes.getItems().remove(getSelectedMeme());
			}
		});
	}

	private boolean crawlAllInfoValid()
	{
		boolean isDelayOkay = !tfDelay.getText().isEmpty();
		boolean isStartIndexOkay = !tfStartIndex.getText().isEmpty();
		boolean isEndIndexOkay = !tfEndIndex.getText().isEmpty();
		
		boolean startVsEndOkay = Integer.parseInt(tfEndIndex.getText()) - Integer.parseInt(tfStartIndex.getText()) > 0;
		
		return isDelayOkay && isStartIndexOkay && isEndIndexOkay && startVsEndOkay;
	}
	
	private void exportSelected()
	{
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save File");
		
		int tabIndex = tpFormats.getSelectionModel().getSelectedIndex();
		FileChooser.ExtensionFilter filters;
		if(tabIndex == 0) // plain text
		{
			filters = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		}
		else if(tabIndex == 1) // RDF
		{
			filters = new FileChooser.ExtensionFilter("OWL files (*.owl)", "*.owl");
		}
		else // CSV
		{
			filters = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		}
		chooser.getExtensionFilters().add(filters);
		
		File file = chooser.showSaveDialog(getScene().getWindow());
		if(file != null)
		{
			try(PrintWriter writer = new PrintWriter(file))
			{
				// Export selected meme
				Meme meme = getSelectedMeme();
				String exportString = "";
				if(tabIndex == 0) // plain text
				{
					exportString = meme.toString();
				}
				else if(tabIndex == 1) // RDF
				{
					exportString = mc.memesToRDF(meme);
				}
				else if(tabIndex == 2) // CSV
				{
					exportString = mc.memesToCSV(meme);
				}
				
				writer.println(exportString);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				new ErrorPopup(e.getMessage() + "\nError exporting: file not found");
			}
		}
	}
	
	private void exportAll()
	{
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save File");
		
		int tabIndex = tpFormats.getSelectionModel().getSelectedIndex();
		FileChooser.ExtensionFilter filters;
		if(tabIndex == 0) // plain text
		{
			filters = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		}
		else if(tabIndex == 1) // RDF
		{
			filters = new FileChooser.ExtensionFilter("OWL files (*.owl)", "*.owl");
		}
		else // CSV
		{
			filters = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		}
		chooser.getExtensionFilters().add(filters);
		
		File file = chooser.showSaveDialog(getScene().getWindow());
		if(file != null)
		{
			try(PrintWriter writer = new PrintWriter(file))
			{
				// Export all memes
				String exportString = "";
				if(tabIndex == 1) // RDF
				{
					exportString = mc.memesToRDF(lvMemes.getItems().toArray(new Meme[0]));
				}
				else if(tabIndex == 2 || tabIndex == 0) // CSV
				{
					exportString = mc.memesToCSV(lvMemes.getItems().toArray(new Meme[0]));
				}
				
				writer.println(exportString);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				new ErrorPopup(e.getMessage() + "\nError exporting: file not found");
			}
		}
	}
	
	private void copyTabText()
	{
		// Copy selected meme
		Meme meme = getSelectedMeme();
		String exportString = "";
		int tabIndex = tpFormats.getSelectionModel().getSelectedIndex(); 
		if(tabIndex == 0) // plain text
		{
			exportString = meme.toString();
		}
		else if(tabIndex == 1) // RDF
		{
			exportString = mc.memeToRDF(meme);
		}
		else if(tabIndex == 2) // CSV
		{
			exportString = mc.memeToCSV(meme);
		}
		
		Toolkit.getDefaultToolkit()
				.getSystemClipboard()
				.setContents(
					new StringSelection(exportString),
					null
				);
	}
	
	private void addListeners()
	{
		tfMemeOrigin.textProperty().addListener((args, oldValue, newValue) ->
		{
			Meme meme = getSelectedMeme();
			meme.setMemeOrigin(newValue);
			updateTextualDisplay();
		});
		
		tfMemeYear.textProperty().addListener((args, oldValue, newValue) ->
		{
			Meme meme = getSelectedMeme();
			
			// Ensure only positive integers are entered.
			try
			{
				int value = Integer.parseUnsignedInt(newValue);
				
				// Only update if we have a 4-digit year value
				if(value >= 1000)
				{
					meme.setMemeYear(value);
					updateTextualDisplay();
				}
			}
			catch(NumberFormatException e)
			{
				// Revert to old value
				tfMemeYear.setText(oldValue);
			}
		});
		
		validateNumberTextField(tfDelay);
		validateNumberTextField(tfStartIndex);
		validateNumberTextField(tfEndIndex);
		
		tfDelay.textProperty().addListener((args, oldValue, newValue) -> 
		{
			try
			{
				int a = Integer.valueOf(newValue);
			}
			catch(NumberFormatException e)
			{
				tfDelay.setText(oldValue);
			}
		});
		
		lvMemes.getSelectionModel().selectedItemProperty().addListener((args, oldValue, newValue) ->
		{
			updateTextualDisplay();
			tfMemeOrigin.clear();
			tfMemeYear.setText("0");
			if(newValue != null)
			{
				btExportSelected.setDisable(false);
			}
			else
			{
				btExportSelected.setDisable(true);
			}
		});
		
		tfMemeOrigin.disableProperty().bind(lvMemes.selectionModelProperty().get().selectedItemProperty().isNull());
		tfMemeYear.disableProperty().bind(lvMemes.selectionModelProperty().get().selectedItemProperty().isNull());
		tpFormats.disableProperty().bind(lvMemes.selectionModelProperty().get().selectedItemProperty().isNull());
		btCopyTabText.disableProperty().bind(lvMemes.selectionModelProperty().get().selectedItemProperty().isNull());
	}
	
	private void validateNumberTextField(TextField tf)
	{
		tf.textProperty().addListener((args, oldValue, newValue) -> 
		{
			try
			{
				int a = Integer.valueOf(newValue);
			}
			catch(NumberFormatException e)
			{
				tf.setText(oldValue);
			}
		});
	}
	
	private Meme getSelectedMeme()
	{
		return lvMemes.getSelectionModel().getSelectedItem();
	}
	
	private void updateTextualDisplay()
	{
		Meme meme = getSelectedMeme();
		taPlainText.setText(meme.toString());
		taRDF.setText(mc.memeToRDF(meme));
		taCSV.setText(mc.memeToCSV(meme));
	}
	
	public Label getLblURL()
	{
		return lblURL;
	}

	public TextField getTfURL()
	{
		return tfURL;
	}

	public Button getBtCrawl()
	{
		return btCrawl;
	}

	public Button getBtCrawlAll()
	{
		return btCrawlAll;
	}

	public Label getLblMemeOrigin()
	{
		return lblMemeOrigin;
	}

	public TextField getTfMemeOrigin()
	{
		return tfMemeOrigin;
	}

	public Label getLblMemeYear()
	{
		return lblMemeYear;
	}

	public TextField getTfMemeYear()
	{
		return tfMemeYear;
	}

	public Button getBtCopy()
	{
		return btCopyTabText;
	}

	public Button getBtExport()
	{
		return btExportSelected;
	}

	public ListView<Meme> getLvMemes()
	{
		return lvMemes;
	}

	public TextArea getTaPlainText()
	{
		return taPlainText;
	}

	public ScrollPane getSpPlainText()
	{
		return spPlainText;
	}

	public TextArea getTaRDF()
	{
		return taRDF;
	}

	public ScrollPane getSpRDF()
	{
		return spRDF;
	}

	public TextArea getTaCSV()
	{
		return taCSV;
	}

	public ScrollPane getSpCSV()
	{
		return spCSV;
	}

	public TabPane getTpFormats()
	{
		return tpFormats;
	}

	public ToolBar getTbButtons()
	{
		return tbButtons;
	}
	
	public ExecutorService getExecutorService()
	{
		return executorService;
	}

	private MemeConverter mc = new MemeConverter();
	
	private Label lblURL;
	private TextField tfURL;
	private Button btCrawl;
	private Button btCrawlAll;
	private Label lblMemeOrigin;
	private TextField tfMemeOrigin;
	private Label lblMemeYear;
	private TextField tfMemeYear;
	
	private TextField tfDelay;
	private TextField tfStartIndex;
	private TextField tfEndIndex;
	
	private Button btCopyTabText;
	private Button btExportSelected;
	private Button btExportAll;
	private ListView<Meme> lvMemes;
	private TextArea taPlainText;
	private ScrollPane spPlainText;
	private TextArea taRDF;
	private ScrollPane spRDF;
	private TextArea taCSV;
	private ScrollPane spCSV;
	private TabPane tpFormats;
	private ToolBar tbButtons;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
}