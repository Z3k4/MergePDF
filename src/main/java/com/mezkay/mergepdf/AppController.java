package com.mezkay.mergepdf;


import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.Collator;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pdfEditor = new PDFEditor(statutLabel, foundFilesPane);
        System.out.println("ok");

    }

    @FXML
    protected void chooseOutput() {
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\kamel\\Pictures\\Koezio"));
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(windows);

        if (file!=null) {
            outputFile.setText(file.getAbsolutePath());
        }
    }

    @FXML
    protected void chooseFolder() throws IOException {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("C:\\Users\\kamel\\Pictures\\Koezio"));
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
            //this.pdfEditor.combineFiles(textPath.getText(), outputFile.getText());
        } else if(selectedIndex == 1) {
        }else if (selectedIndex == 2) {
            WebsiteSource websource = new WebsiteSource(websiteSource.getText(),"test.pdf", minChapter.getText(), maxChapter.getText(), minPage.getText(), maxPage.getText());

            this.pdfEditor.combineFiles("", outputFile.getText(), websource.getAllImages());
            statutLabel.textProperty().bind(websource.getStatutProperty());

        }
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

}
