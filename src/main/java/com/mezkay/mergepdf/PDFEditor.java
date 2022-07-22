package com.mezkay.mergepdf;

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
import java.util.*;

public class PDFEditor {

    private ArrayList<Integer> listPages;
    private PDDocument loadedDocument;


    private Label statutLabel;
    private Pane foundFilesPane;
    private String pathToFile;

    public PDFEditor(Label statutLabel, Pane filesPane) {
        this.listPages = new ArrayList<>();
        this.loadedDocument = new PDDocument();
        this.foundFilesPane = filesPane;

        this.statutLabel = statutLabel;

    }



    private void newPDPage(File file, Boolean singlePageMode) throws IOException {
        PDImageXObject pdImage = PDImageXObject.createFromFile(file.getAbsolutePath(), this.loadedDocument);
        System.out.println("Adding " + file.getName());

        if(!singlePageMode) {
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
            this.loadedDocument.addPage(newPage);

            System.out.println("Page " + file.getName() + " merged!");
        }

    };

    public void combineFiles(String path, String outPath, ArrayList<Integer> allPages, Boolean singlePageMode) throws IOException {
        this.pathToFile = path;
        ArrayList<Integer> pageneedToBeSort = allPages;
        if(allPages == null) {
            pageneedToBeSort = this.listPages;
        }

        for(int test : pageneedToBeSort) {
            System.out.println(test);
        }



        if(path.length() > 0) {
            statutLabel.setText("Statut " + "Combining files..");

            File file = new File(path);
            this.loadedDocument = new PDDocument();
            this.loadImages(file);
            if (allPages != null) {
                this.mergeImages(allPages,singlePageMode);
            } else {
                this.mergeImages(this.listPages, singlePageMode);
            }
            loadedDocument.save(outPath);
            loadedDocument.close();

            statutLabel.setText("Statut " + "Finished !");
        }
    }

    void loadImages(File file) {

        listPages.clear();
        ListView listView = (ListView) foundFilesPane.getChildren().get(0);
        listView.getItems().clear();


        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File pathname, String name) {

                if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".JPG")) {
                    int pageNumber = Integer.parseInt(name.substring(0, name.indexOf(".")));

                    listPages.add(pageNumber);

                    //listView.getItems().("Registering " + pathname + "\\" + name);

                    return true;
                }
                return false;
            }
        });

    }

    private void mergeImages(ArrayList<Integer> allPages, Boolean singlePageMode) {
        Collections.sort(allPages);

        //Collections.sort(listPages, Collections.reverseOrder());
        for(int path : allPages) {
            try {
                //System.out.println(file.getAbsolutePath());
                newPDPage(new File(pathToFile + "\\" +path + ".jpg"), singlePageMode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
