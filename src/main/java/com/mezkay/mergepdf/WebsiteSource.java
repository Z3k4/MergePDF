package com.mezkay.mergepdf;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;


public class WebsiteSource {

    private String source;

    private int minChapter;
    private int maxChapter;

    private int minPage;
    private int maxPage;

    private String outputName;

    private ArrayList<Integer> imagesPath;
    private StringProperty nowStatut;

    private BooleanProperty finishedProperty;
    private String directoryByDateName;
    private AppController appController;

    private Pane root;

    private ProgressBar progressBar;

    private int nowChapter;
    private int nowPage;

    private String nowExtension;

    private ListView downloadListView;


    public WebsiteSource(AppController appController, String sourceURL, String outputName, String minChapter, String maxChapter, String minPage, String maxPage) {
        this.appController = appController;
        this.source = sourceURL;
        this.outputName = outputName;
        this.minChapter = Integer.parseInt(minChapter);
        this.maxChapter = Integer.parseInt(maxChapter);

        this.root = appController.getRoot();

        this.minPage = Integer.parseInt(minPage);
        this.maxPage = Integer.parseInt(maxPage);
        nowStatut = new SimpleStringProperty("");

        this.imagesPath = new ArrayList<>();
        this.finishedProperty = new SimpleBooleanProperty(false);

        this.progressBar = (ProgressBar) appController.getRoot().lookup("#progressDownload");
        nowChapter = 1;
        nowPage = 1;

        nowExtension = null;

        downloadListView = (ListView) appController.getRoot().lookup("#webDownloadInfos");
    }

    public ArrayList<Integer> getImagesPath() {
        return this.imagesPath;
    }


    private Task  downloadImage(int chapter, int page, String format) throws IOException {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                //TODO use getExtension to auto determine extension
                String formatFromURL = ".jpg";
                String add = "";

                //Some site use 01, 02 instead 1, 2
                CheckBox digitCheck = (CheckBox) appController.getRoot().lookup("#digitMode");


                if(digitCheck.isSelected() && page < 10) {
                    add += "0";
                }
                String parentDirectory = appController.getOutputDirectory().getAbsolutePath() + "\\temp\\";

                //I think chapter 0 doesn't exist
                if(source.indexOf("$chapter") <= 0) {
                    minChapter = 1;
                    maxChapter = 1;
                }

                File directory = new File(parentDirectory + "Chapter_" + chapter);
                if(!directory.exists()) {
                    directory.mkdir();
                }


                String urlToGet = source.replace("$chapter",  "" + chapter).replace("$page", add + page + formatFromURL);
                String saveURL = directory.getAbsolutePath() + "\\" +  page + formatFromURL;

                URL url = new URL(urlToGet);


                double imgSize = url.openConnection().getContentLength();
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {

                    out.write(buf, 0, n);
                    updateProgress(out.size(), imgSize);

                }
                out.close();
                in.close();
                byte[] response = out.toByteArray();
                FileOutputStream test = new FileOutputStream(saveURL);

                test.write(response);
                test.close();


                imagesPath.add(page);

                return null;
            }
        };

        return task;
    }

    //Add info and progress bar for each page
    private ProgressBar addProgressItem() {
        HBox test = new HBox();
        Label label = new Label("Chapter " + nowChapter + " page " + nowPage);
        ProgressBar progressBar = new ProgressBar();

        test.getChildren().add(label);
        test.getChildren().add(progressBar);


        downloadListView.getItems().add(test);

        return progressBar;

    }


    public void downloadAllImages() throws IOException {

        if(nowChapter >= 0) {

            Task task = downloadImage(nowChapter, nowPage, nowExtension);
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    try {
                        downloadAllImages();

                        nowPage++;
                    } catch (IOException exception) {
                        if (exception instanceof FileNotFoundException) {
                            if (nowPage == 1) {
                                nowChapter = -1;
                            } else {
                                String pathImages = appController.getOutputDirectory() + "\\temp\\" + "Chapter_" + nowChapter;
                                String outputPath = appController.getOutputDirectory() + "\\chapter_" + nowChapter + ".pdf";


                                appController.getPdfEditor().loadImages(new File(pathImages));
                                try {
                                    appController.getPdfEditor().combineFiles(pathImages + "\\", outputPath, null, true);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                nowChapter++;
                                nowPage = 1;
                            }
                        }
                    }
                }
            });

            addProgressItem().progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        }

    }
}
