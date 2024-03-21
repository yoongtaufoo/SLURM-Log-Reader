
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private BorderPane layout = new BorderPane();

    @Override
    public void start(Stage stage){
        
        MenuBar menuBar = getMenuBar();

        TableView<Integer> table = new TableView<>();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

        layout.setLeft(table);
        layout.setCenter(chart);
        VBox vBox = new VBox(menuBar, layout);

        Scene scene = new Scene(vBox, 800, 600);
        stage.setTitle("Monitoring System");
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar getMenuBar() {
        
        //create MenuItem
        MenuItem createEndItem = getMenuItem(new A_JobEndCreate());
        MenuItem createEndItem2 = getMenuItem(new B_JobPartition());
        MenuItem createEndItem3 = getMenuItem(new C_JobUserError());
        MenuItem createEndItem4 = getMenuItem(new D_AveTime());
        MenuItem createEndItem5 = getMenuItem(new E_CompletedCleanUp());
        MenuItem createEndItem6 = getMenuItem(new E_RequestedKillJob());
        
        //create Menu(metrics)
        Menu menu = new Menu("Metrics");
        
        menu.getItems().addAll(createEndItem,createEndItem2,createEndItem3,createEndItem4,createEndItem5,createEndItem6);
        
        //create MenuBar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        return menuBar;
    }

    private MenuItem getMenuItem(MetricManager manager) {
        MenuItem menuItem = new MenuItem(manager.getName());
        menuItem.setOnAction(new EventHandler<ActionEvent>() {  
            @Override
            public void handle(ActionEvent e) {
                layout.setLeft(manager.getTableView());
                layout.setCenter(manager.getChart());
            }
        });
        return menuItem;
    }
}

