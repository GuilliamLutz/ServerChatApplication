import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.jws.soap.SOAPBinding;

import java.util.ArrayList;

public class UserListView extends Application {

    private Canvas canvas;
    private ObservableList<UserStaturListener> statusListner;

    @Override
    public void start(Stage stage) throws Exception {

        Client client = new Client("localhost", 8818);

//        this.statusListner = client.getUserStaturListeners();

        BorderPane mainPane = new BorderPane();
        VBox userlist = new VBox();
        userlist.getChildren().add(new Text(client.getUsername()));

        ListView<UserStaturListener> fpListView = new ListView();

        fpListView.setStyle("-fx-background-color: #FFFFFF; " +
                "-fx-text-fill: #B76F88; " +
                "-fx-font-size: 15; " +
                "-fx-font-family: Helvetica; ");

//        fpListView.setItems(client.getUserStaturListeners());

        mainPane.setLeft(userlist);
        stage.setScene(new Scene(mainPane, 500, 1000));
        stage.setTitle("Eindopdracht Netwerkprogrammeren");
        stage.show();

    }

}
