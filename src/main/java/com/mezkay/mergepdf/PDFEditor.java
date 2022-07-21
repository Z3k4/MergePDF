package com.mezkay.mergepdf;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class PDFEditor {

    private ArrayList<File> listPages;
    private PDDocument loadedDocument;


    private Label statutLabel;
    private Pane foundFilesPane;

    public PDFEditor(Label statutLabel, Pane filesPane) {
        this.listPages = new ArrayList<>();
        this.loadedDocument = new PDDocument();
        this.foundFilesPane = filesPane;

        this.statutLabel = statutLabel;

    }


    private void newPDPage(File file) throws IOException {
        PDImageXObject pdImage = PDImageXObject.createFromFile(file.getAbsolutePath(), this.loadedDocument);

        if(pdImage.getWidth() > 1000) {
            PDRectangle rectSize = new PDRectangle(pdImage.getWidth() / 2, pdImage.getHeight());

            //Create first page and cut
            PDPage newPageLeft = new PDPage(rectSize);
            PDPageContentStream contents = new PDPageContentStream(this.loadedDocument, newPageLeft);


            contents.drawImage(pdImage, -pdImage.getWidth() / 2,0, pdImage.getWidth(), pdImage.getHeight());
            contents.close();

            //Right

            PDPage newPageRight = new PDPage(rectSize);
            PDPageContentStream contentsRight = new PDPageContentStream(this.loadedDocument, newPageRight);
            PDRectangle sizeRectRight = newPageRight.getBBox();


            contentsRight.drawImage(pdImage, 0,0, pdImage.getWidth(), sizeRectRight.getHeight());

            //Second page


            contentsRight.close();
            this.loadedDocument.addPage(newPageLeft);
            this.loadedDocument.addPage(newPageRight);
        } else {

            PDRectangle rectSize = new PDRectangle(pdImage.getWidth(), pdImage.getHeight());
            PDPage newPage = new PDPage(rectSize);

            PDPageContentStream contents = new PDPageContentStream(this.loadedDocument, newPage);
            PDRectangle sizerect = newPage.getBBox();


            contents.drawImage(pdImage, 0,0, sizerect.getWidth(), sizerect.getHeight());

            //Second page

            contents.close();
        }

    };

    public void combineFiles(String path, String outPath, ObservableList<File> allPages) throws IOException {
        if(path.length() > 0) {
            statutLabel.setText("Statut " + "Combining files..");

            File file = new File(path);
            this.loadedDocument = new PDDocument();
            this.loadImages(file);
            this.mergeImages(allPages);
            loadedDocument.save(outPath);
            loadedDocument.close();

            statutLabel.setText("Statut " + "Finished !");
        }
    }

    void loadImages(File file) {

        ListView filesList = new ListView();
        filesList.setPrefHeight(200);
        filesList.setPrefWidth(430);

        foundFilesPane.getChildren().clear();

        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File pathname, String name) {

                if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".JPG")) {
                    listPages.add(new File(pathname + "/" + name));

                    filesList.getItems().add(pathname + "/" + name);

                    return true;
                }
                return false;
            }
        });

        foundFilesPane.getChildren().add(filesList);
    }

    private void mergeImages(ObservableList<File> allPages) {
        //Collections.sort(listPages, Collections.reverseOrder());
        for(File file : allPages) {
            try {
                newPDPage(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
