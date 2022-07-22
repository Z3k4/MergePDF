package com.mezkay.mergepdf;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AppController implements Initializable {


    private Stage rootStage;
    private Window windows;
    @FXML
    private Pane root;
    @FXML
    private Button chooseFolderButton;

    @FXML
    private TextField textPath;
    @FXML
    private TextField outputFile;



    @FXML
    private TabPane combineSource;

    @FXML
    private Label statutLabel;
    @FXML
    private Pane foundFilesPane;

    @FXML
    private TextField minChapter;
    @FXML
    private TextField maxChapter;
    @FXML
    private TextField minPage;
    @FXML
    private TextField maxPage;
    @FXML
    private TextField websiteSource;

    private PDFEditor pdfEditor;
    private ArrayList<File> listPages = new ArrayList<>();

    private ListView statutList;

    @FXML
    private RadioButton singlePageRadioBtn;
    @FXML
    private RadioButton doublePageRadioBtn;

    @FXML
    private TextField outputDirectory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.statutList = new ListView();
        this.statutList.setPrefHeight(200);
        this.statutList.setPrefWidth(430);

        ToggleGroup toggleGroup = new ToggleGroup();
        singlePageRadioBtn.setToggleGroup(toggleGroup);
        doublePageRadioBtn.setToggleGroup(toggleGroup);

        singlePageRadioBtn.setSelected(true);

        foundFilesPane.getChildren().add(this.statutList);

        pdfEditor = new PDFEditor(statutLabel, foundFilesPane);


    }

    @FXML
    protected void chooseOutputFile() {
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getenv("USERPROFILE")));
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(windows);

        if (file!=null) {
            outputFile.setText(file.getAbsolutePath());
        }
    }

    @FXML
    protected void chooseOutputFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getenv("USERPROFILE") + "\\Documents"));
        File file = directoryChooser.showDialog(windows);

        if (file!=null) {
            outputDirectory.setText(file.getAbsolutePath());
        }
    }


    @FXML
    protected void chooseFolder() throws IOException {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getenv("USERPROFILE") + "\\Documents"));
        File file = directoryChooser.showDialog(windows);

        if(file != null) {
            textPath.setText(file.getAbsolutePath());
            this.pdfEditor.loadImages(file);

        }

    }

    @FXML
    protected void combineFiles() throws IOException {
        int i = 0;
        int selectedIndex = -1;
        while(selectedIndex < 0 && i < combineSource.getTabs().size()) {
            if(combineSource.getTabs().get(i).isSelected()) {
                selectedIndex = i;

            }
            i++;
        }
        if(selectedIndex == 0) {

            this.pdfEditor.combineFiles(textPath.getText(), outputFile.getText(), null, this.singlePageRadioBtn.isSelected());
        } else if(selectedIndex == 1) {
        }else if (selectedIndex == 2) {

            WebsiteSource websource = new WebsiteSource(this, websiteSource.getText(),"test.pdf", minChapter.getText(), maxChapter.getText(), minPage.getText(), maxPage.getText());

            statutLabel.setText("Downloading files..");
            websource.getStatutProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    statutList.getItems().add(observableValue.getValue());
                }
            });

            websource.run();
            websource.onSucceededProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observableValue, Object o, Object t1) {
                    try {
                        pdfEditor.combineFiles("", outputFile.getText(), websource.getImagesPath(), singlePageRadioBtn.isSelected());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        };

    }

    @FXML
    protected void testImage() throws IOException {
        URL url = new URL("https://scansmangas.ws/scans/friends-games/1/100.jpg");
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        //FileOutputStream test = new FileOutputStream(saveURL);

        //test.write(response);
        //test.close();
    }


    public void setStage(Stage stage) {
        this.rootStage = stage;
        this.windows = this.rootStage.getScene().getWindow();
    }

    public File getOutputDirectory() {
        File directory = new File(this.outputDirectory.getText());

        if(directory.exists()) {
            return directory;
        }

        return null;
    }

    public PDFEditor getPdfEditor() {
        return this.pdfEditor;
    }

}
