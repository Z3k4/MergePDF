module com.mezkay.mergepdf {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;


    opens com.mezkay.mergepdf to javafx.fxml;
    exports com.mezkay.mergepdf;
}