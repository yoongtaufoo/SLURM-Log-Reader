
import java.io.*;
import java.util.Arrays;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class E_CompletedCleanUp extends Application implements MetricManager {

    public static void main(String[] args) {
        launch(args);
    }

    public static class Data {

        private String month;
        private double avg;

        // initializing the values for month and average
        public Data(String month, double avg) {
            this.month = month;
            this.avg = avg;
        }

        public String getMonth() {
            return month;
        }

        public double getAvg() {
            return avg;
        }
    }

    public static double search(String month) {
        String search = "cleanup_completing";
        int count = 0;
        int totalTime = 0;
        double avg;
        String date = "";

        switch (month) {
            case "June" ->
                date = "2022-06";
            case "July" ->
                date = "2022-07";
            case "August" ->
                date = "2022-08";
            case "September" ->
                date = "2022-09";
            case "October" ->
                date = "2022-10";
            case "November" ->
                date = "2022-11";
            case "December" ->
                date = "2022-12";
            default -> {
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("extracted_log"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(search) && line.contains(date)) {
                    String[] newLine = line.split(" ");
                    String time = newLine[6];
                    totalTime += Integer.parseInt(time);
                    count++;
                }
            }
            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File was not found");
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }
        if (count == 0) {
            avg = 0;
        } else {
            avg = Math.round((totalTime / count / 60.0) * 100.0) / 100.0;
        }

        return avg;

    }

    @Override
    public void start(Stage stage) {
        stage.setTitle(getName());

        // Adding the bar chart and table to the border
        BorderPane layout = new BorderPane();
        layout.setLeft(getTableView());
        layout.setRight(getChart());

        // Creating the scene and showing the stage
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public String getName() {
        return "Average Time Taken to Complete a Cleanup Process";
    }

    @Override
    public Chart getChart() {

        // Creating the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Average time (min)");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Average time taken to complete a cleanup process per day");

        // Adding data to the bar chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("2022");
        for (String month : new String[]{"June", "July", "August", "September", "October", "November", "December"}) {
            series.getData().add(new XYChart.Data<>(month, search(month)));
        }
        barChart.getData().add(series);

        return barChart;
    }

    @Override
    public TableView<Data> getTableView() {
        // Creating the table
        TableView<Data> table = new TableView<>();
        table.setPrefSize(300, 200);

        // Creating columns for the table
        TableColumn<Data, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        monthCol.setPrefWidth(100);

        TableColumn<Data, Double> avgCol = new TableColumn<>("Average time (min)");
        avgCol.setCellValueFactory(new PropertyValueFactory<>("avg"));
        avgCol.setPrefWidth(200);

        // Adding columns to the table
        table.getColumns().addAll(Arrays.asList(monthCol, avgCol));

        // Adding data to the table
        ObservableList<Data> dataList = FXCollections.observableArrayList();
        for (String month : new String[]{"June", "July", "August", "September", "October", "November", "December"}) {
            dataList.add(new Data(month, search(month)));
        }
        table.setItems(dataList);

        return table;
    }
}
