package de.hawhh.gewiss.get.fx;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Dialog for displaying exceptions during runtime with a meaningful text for the user.
 * 
 * @author Thomas Preisler
 */
public class ExceptionDialog extends Alert {

    private final TextArea exceptionArea;

    public ExceptionDialog() {
        // set the type of this dialog to error
        super(AlertType.ERROR);

        Label label = new Label("The exception stacktrace was:");

        exceptionArea = new TextArea();
        exceptionArea.setEditable(false);
        exceptionArea.setWrapText(true);

        exceptionArea.setMaxWidth(Double.MAX_VALUE);
        exceptionArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(exceptionArea, Priority.ALWAYS);
        GridPane.setHgrow(exceptionArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(exceptionArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        this.getDialogPane().setExpandableContent(expContent);
    }

    /**
     * Set the given exception to be displaying in the dialog.
     * 
     * @param ex
     */
    public void setException(Exception ex) {
        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        
        exceptionArea.setText(exceptionText);
    }
}