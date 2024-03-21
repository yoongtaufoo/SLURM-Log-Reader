
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

public class E_RequestedKillJob extends Application implements MetricManager {

    public static void main(String[] args) {
        launch(args);
    }

    public static class Data {

        private String month;
        private int jobs;

        // initializing the values for month and jobs
        public Data(String month, int jobs) {
            this.month = month;
            this.jobs = jobs;
        }

        public String getMonth() {
            return month;
        }

        public int getJobs() {
            return jobs;
        }
    }

    // code to search for requested kill jobs in a specific month
    public static int searchKillJobs(String month) {
        int count = 0;
        String date = "";
        String search = "_slurm_rpc_kill_job: REQUEST_KILL_JOB";

        // finding the data based on month given
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

        // search for "REQUESTED_KILL_JOB" and date in file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("extracted_log"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(search) && line.contains(date)) {
                    count++;
                }
            }
            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File was not found");
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }

        return count;
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
        return "Number of Requested Kill Jobs";
    }

    @Override
    public Chart getChart() {

        // Creating the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Requested kill jobs");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Requested kill jobs by month");

        // Adding data to the bar chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("2022");
        for (String month : new String[]{"June", "July", "August", "September", "October", "November", "December"}) {
            int count = searchKillJobs(month);
            series.getData().add(new XYChart.Data<>(month, count));
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

        TableColumn<Data, Integer> jobsCol = new TableColumn<>("Requested kill jobs");
        jobsCol.setCellValueFactory(new PropertyValueFactory<>("jobs"));
        jobsCol.setPrefWidth(200);

        // Adding columns to the table
        table.getColumns().addAll(Arrays.asList(monthCol, jobsCol));

        // Adding data to the table
        ObservableList<Data> dataList = FXCollections.observableArrayList();
        for (String month : new String[]{"June", "July", "August", "September", "October", "November", "December"}) {
            int count = searchKillJobs(month);
            dataList.add(new Data(month, count));
        }
        table.setItems(dataList);

        return table;

    }

}
