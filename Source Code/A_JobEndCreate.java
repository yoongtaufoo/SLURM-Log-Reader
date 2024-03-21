
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

public class A_JobEndCreate extends Application implements MetricManager {

    public static void main(String[] args) {
        launch(args);
    }

    public static class Data {

        private String month;
        private int jobsCreated;
        private int jobsEnded;

        // initializing the values for month and jobs
        public Data(String month, int jobs, int end) {
            this.month = month;
            this.jobsCreated = jobs;
            this.jobsEnded = end;
        }

        public String getMonth() {
            return month;
        }

        public int getJobs() {
            return jobsCreated;
        }

        public int getEnd() {
            return jobsEnded;
        }
    }

    // code to search for created jobs in a specific month
    public static int create(String month) {
        int countCreate = 0;
        String date = "";
        String search = "sched: Allocate JobId=";

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

        // search for created job and date in file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\User\\Desktop\\FOP asmt\\extracted_log"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(search) && line.contains(date)) {
                    countCreate++;
                }
            }
            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File was not found");
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }

        return countCreate;
    }

    public static int end(String month) {
        int countEnd = 0;
        String date = "";
        String detect = " _job_complete: JobId=";
        String find = "done";

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

        // search for ended jobs and date in file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("extracted_log"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(detect) && line.contains(date) && line.contains(find)) {
                    countEnd++;
                }
            }
            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File was not found");
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }

        return countEnd;
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
        return "Number of Jobs Ended and Created";
    }

    @Override
    public Chart getChart() {
        // Creating the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(
                new String[] { "June", "July", "August", "September", "October", "November", "December" }));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Jobs Created");
        NumberAxis yAxis2 = new NumberAxis();
        yAxis2.setLabel("Jobs Ended");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Jobs created and ended by month");

        // Adding data to the bar chart
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Created");
        for (String month : new String[] { "June", "July", "August", "September", "October", "November", "December" }) {
            int countCreate = create(month);
            series1.getData().add(new XYChart.Data<>(month, countCreate));
        }

        barChart.getData().add(series1);

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Ended");
        for (String month : new String[] { "June", "July", "August", "September", "October", "November", "December" }) {
            int countEnd = end(month);
            series2.getData().add(new XYChart.Data<>(month, countEnd));
        }
        barChart.getData().add(series2);
        return barChart;
    }

    @Override
    public TableView<Data> getTableView() {
        // Creating the table
        TableView<Data> table = new TableView<>();
        table.setEditable(true);
        table.setPrefSize(300, 300);

        // Creating columns for the table
        TableColumn<Data, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        monthCol.setPrefWidth(100);

        TableColumn<Data, Integer> jobsCol = new TableColumn<>("Jobs Created");
        jobsCol.setCellValueFactory(new PropertyValueFactory<>("jobs"));
        jobsCol.setPrefWidth(100);

        TableColumn<Data, Integer> jobsCol2 = new TableColumn<>("Jobs Ended");
        jobsCol2.setCellValueFactory(new PropertyValueFactory<>("end"));
        jobsCol2.setPrefWidth(100);

        // Adding columns to the table
        table.getColumns().addAll(Arrays.asList(monthCol, jobsCol, jobsCol2));

        // Adding data to the table
        ObservableList<Data> dataList = FXCollections.observableArrayList();
        for (String month : new String[] { "June", "July", "August", "September", "October", "November", "December" }) {
            int countCreate = create(month);
            int countEnd = end(month);
            dataList.add(new Data(month, countCreate, countEnd));
        }
        table.setItems(dataList);
        return table;
    }

}

