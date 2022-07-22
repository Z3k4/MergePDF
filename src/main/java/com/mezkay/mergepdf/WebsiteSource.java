package com.mezkay.mergepdf;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class WebsiteSource extends Task {

    private String source;

    private String minChapter;
    private String maxChapter;

    private String minPage;
    private String maxPage;

    private String outputName;

    private ArrayList<Integer> imagesPath;
    private StringProperty nowStatut;

    private BooleanProperty finishedProperty;
    private String directoryByDateName;
    private AppController appController;


    public WebsiteSource(AppController appController, String sourceURL, String outputName, String minChapter, String maxChapter, String minPage, String maxPage) {
        this.appController = appController;
        this.source = sourceURL;
        this.outputName = outputName;
        this.minChapter = minChapter;
        this.maxChapter = maxChapter;

        this.minPage = minPage;
        this.maxPage = maxPage;
        nowStatut = new SimpleStringProperty("");

        this.imagesPath = new ArrayList<>();
        this.finishedProperty = new SimpleBooleanProperty(false);
    }

    public ArrayList<Integer> getImagesPath() {
        return this.imagesPath;
    }


    private void downloadImage(int chapter, int page, String format) throws IOException {
        String parentDirectory = appController.getOutputDirectory().getAbsolutePath() + "\\temp\\";
        if(source.indexOf("$chapter") < 0) {
            minChapter = "1";
            maxChapter = "1";
        }

        File directory = new File(parentDirectory + "Chapter_" + chapter);
        if(!directory.exists()) {
            directory.mkdir();
        }
        String urlToGet = source.replace("$chapter", chapter + "").replace("$page", page + "");
        System.out.println(urlToGet);
        String saveURL = directory.getAbsolutePath() + "\\" +  page + format;
        System.out.println(saveURL);
        URL url = new URL(urlToGet);
        //this.nowStatut.setValue("Downloading "+ urlToGet);

        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf))) {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        FileOutputStream test = new FileOutputStream(saveURL);

        test.write(response);
        test.close();


        imagesPath.add(page);

    }


    public StringProperty getStatutProperty() {
        return this.nowStatut;
    }

    private ArrayList<Integer> getImagesFiles() {
        return imagesPath;
    }

    public BooleanProperty getFinishedProperty() {
        return this.finishedProperty;
    }


    @Override
    protected Object call() throws Exception {
        String format = ".jpg";
        boolean pageNotFound = false;
        boolean chapterNotFound = false;

        int page = Integer.parseInt(minPage);
        int chapter = Integer.parseInt(minChapter);

        int maxPageInt = Integer.parseInt(maxPage);
        int maxChapterInt = Integer.parseInt(maxChapter);


        while (!chapterNotFound && (chapter < maxChapterInt || maxChapterInt < 0)) {
            while (!pageNotFound && (page <= maxPageInt || maxPageInt < 0)) {
                try {
                    downloadImage(chapter, page, format);
                    page++;
                    if(page > maxPageInt && maxPageInt > 0) {


                        String pathImages = this.appController.getOutputDirectory() + "\\temp\\" + "Chapter_" + chapter;
                        String outputPath = this.appController.getOutputDirectory() + "\\chapter_" + chapter + ".pdf";
                        System.out.println(pathImages);
                        this.appController.getPdfEditor().loadImages(new File(pathImages));
                        this.appController.getPdfEditor().combineFiles(pathImages + "\\", outputPath, null,true);

                        chapter++;
                        page = 1;
                    }
                } catch (IOException exception) {
                    if (exception instanceof FileNotFoundException) {
                        if (page == 1) {
                            chapterNotFound = true;

                        }

                        String pathImages = this.appController.getOutputDirectory() + "\\temp\\" + "Chapter_" + chapter;
                        String outputPath = this.appController.getOutputDirectory() + "\\chapter_" + chapter + ".pdf";
                        System.out.println(pathImages);
                        this.appController.getPdfEditor().loadImages(new File(pathImages));
                        this.appController.getPdfEditor().combineFiles(pathImages + "\\", outputPath, null,true);

                        pageNotFound = true;
                        chapter++;
                        page = 1;
                        nowStatut.setValue("Page not found, step to next chapter..");

                    }
                }

            }

            pageNotFound = false;
        }

        this.finishedProperty.setValue(true);
        return null;
    }
}
