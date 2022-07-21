package com.mezkay.mergepdf;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class WebsiteSource {

    private String source;

    private String minChapter;
    private String maxChapter;

    private String minPage;
    private String maxPage;

    private String outputName;

    private ObservableList<File> imagesPath;
    private StringProperty nowStatut;

    public WebsiteSource(String sourceURL,String outputName, String minChapter, String maxChapter, String minPage, String maxPage) {
        this.source = sourceURL;
        this.outputName = outputName;
        this.minChapter = minChapter;
        this.maxChapter = maxChapter;

        this.minPage = minPage;
        this.maxPage = maxPage;
        nowStatut = new SimpleStringProperty("");

        this.imagesPath = FXCollections.observableArrayList();
    }



    private void downloadImage(int chapter, int page, String format) throws IOException {

        String saveURL = "C:\\Users\\kamel\\Pictures\\Koezio\\test\\" + outputName.substring(0, outputName.indexOf(".") - 1) + "_" + chapter + "_" + page + "." + format;
        URL url = new URL(source.replace("$chapter", chapter + "").replace("$page", page + ""));
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
        FileOutputStream test = new FileOutputStream(saveURL);

        test.write(response);
        test.close();


        imagesPath.add(new File(saveURL));

    }

    public ObservableList<File> getAllImages() throws IOException {
        String format = ".jpg";
        boolean pageNotFound = false;
        boolean chapterNotFound = false;

        int page = Integer.parseInt(minPage);
        int chapter = Integer.parseInt(minChapter);

        int maxPageInt = Integer.parseInt(maxPage);
        int maxChapterInt = Integer.parseInt(maxChapter);


        while(!chapterNotFound && (chapter < maxChapterInt || maxChapterInt < 0)) {
            while(!pageNotFound && (page < maxPageInt || maxPageInt < 0)) {
                    try {
                        downloadImage(chapter, page, format);
                        page++;
                    }
                    catch(IOException exception) {
                        if(exception instanceof FileNotFoundException) {
                            if (page == 1) {
                                chapterNotFound = true;
                            }

                            pageNotFound = true;
                            chapter++;
                            page = 1;

                        }
                    }

            }
            pageNotFound = false;
        }


        /*for(int chapter = Integer.parseInt(minChapter); chapter < Integer.parseInt(maxChapter); chapter++) {
            for(int page = Integer.parseInt(minPage); page < Integer.parseInt(maxPage); page++) {
                downloadImage(chapter, page, format);

                nowStatut.setValue("Chapter " + chapter + " - Page " + page + " downloaded");
            }
        }*/

        return imagesPath;
    }

    public StringProperty getStatutProperty() {
        return this.nowStatut;
    }

    private ObservableList<File> getImagesFiles() {
        return imagesPath;
    }



}
