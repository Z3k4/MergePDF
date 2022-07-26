package com.mezkay.mergepdf;


import com.mezkay.mergepdf.pdfeditor.PDFEditor;
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


        Label test = new Label("From folder");
        test.setPrefSize(100,100);
        test.setMinWidth(100);
        test.setMinHeight(100);


        ListView websiteDownload = new ListView();
        websiteDownload.setId("webDownloadInfos");
        ScrollPane pane = (ScrollPane)root.lookup("#websiteDownloadInfoPane");
        pane.setContent(websiteDownload);

        combineSource.getTabs().get(0).setGraphic(test);
        combineSource.getTabs().get(1).setGraphic(new Label("From archive"));
        combineSource.getTabs().get(2).setGraphic(new Label("From website"));

        combineSource.setTabMinHeight(100);
        combineSource.setTabMaxHeight(100);
        combineSource.setTabMinWidth(100);
        combineSource.setTabMaxWidth(100);

        foundFilesPane.setVisible(false);


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
        AppController appController = this;
        System.out.println("Start ");
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {*/
                try {

                    int i = 0;
                    int selectedIndex = -1;

                    //Get active tab

                    while (selectedIndex < 0 && i < combineSource.getTabs().size()) {
                        //Combine from existing images
                        if (combineSource.getTabs().get(i).isSelected()) {
                            selectedIndex = i;
                        }
                        i++;
                    }

                    if (selectedIndex == 0) {

                        pdfEditor.combineFiles(textPath.getText(), outputFile.getText(), null, singlePageRadioBtn.isSelected());
                    } else if (selectedIndex == 1) {
                        // TODO : Code for combine from archive
                    } else if (selectedIndex == 2) {
                        //Use website to get images
                        WebsiteSource websource = new WebsiteSource(appController, websiteSource.getText(), "test.pdf", minChapter.getText(), maxChapter.getText(), minPage.getText(), maxPage.getText());
                        //websource.downloadImages();
                        websource.downloadAllImages();
                    }
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }

           /* }
        });
        thread.setDaemon(true);
        thread.start();*/


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

    public Pane getRoot() {
        return this.root;
    }

}
