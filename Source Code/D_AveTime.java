
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class D_AveTime extends Application implements MetricManager {

    public static void main(String[] args) {
        launch(args);
    }

    public static class Data {

        private String timeRange;
        private int jobs;

        // initializing the values for timeRange and jobs
        public Data(String timeRange, int jobs) {
            this.timeRange = timeRange;
            this.jobs = jobs;
        }

        public String getTimeRange() {
            return timeRange;
        }

        public int getJobs() {
            return jobs;
        }
    }

    //Declare at here because will use in Chart and TableView
    public static String withinAverage;
    public static String moreAverage;
    public static int countWithinAverage;
    public static int countAfterAverage;
    
    // code to search for average execution time
    public static int findAverage(String timeRange) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        long diffInMilli;
        long totalTime = 0;
        double averageTime;
        int hours;
        int minutes;
        int seconds;
        int milliSeconds;
        int count5min = 0;
        int count30min = 0;
        int count2h = 0;
        int count8h = 0;
        int count16h = 0;
        int countElse = 0;
        countWithinAverage = 0;
        countAfterAverage = 0;
        int count = 0;

        HashMap<String, String> allocateMap = new HashMap<>();
        HashMap<String, String> completeMap = new HashMap<>();

        try {
            Scanner inputStream = new Scanner(new FileInputStream("extracted_log"));
            while (inputStream.hasNextLine()) {
                String text = inputStream.nextLine();
                String patternString = "Allocate JobId=";
                String patternString2 = "complete: JobId=\\d{5} done";
                Pattern pattern = Pattern.compile(patternString);
                Pattern pattern2 = Pattern.compile(patternString2);
                Matcher matcher = pattern.matcher(text);
                Matcher matcher2 = pattern2.matcher(text);

                while (matcher.find()) {
                    String jobId = text.substring(matcher.end(), matcher.end() + 5);
                    String time = text.substring(1, 11) + " " + text.substring(12, 24);
                    allocateMap.put(jobId, time);
                }

                while (matcher2.find()) {
                    String jobId = text.substring(matcher2.end() - 10, matcher2.end() - 5);
                    String time = text.substring(1, 11) + " " + text.substring(12, 24);
                    completeMap.put(jobId, time);
                }
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("File was not found");
        }

        for (String jobId : completeMap.keySet()) {
            if (!allocateMap.containsKey(jobId)) {
                continue;
            }

            String startTime = allocateMap.get(jobId); //get value by key
            String endTime = completeMap.get(jobId);

            LocalDateTime dateTime1 = LocalDateTime.parse(startTime, formatter);
            LocalDateTime dateTime2 = LocalDateTime.parse(endTime, formatter);
            diffInMilli = java.time.Duration.between(dateTime1, dateTime2).toMillis();
            totalTime += diffInMilli;
            count++;
            if (diffInMilli <= 300000)
                count5min++;
            else if (diffInMilli <= 1800000)
                count30min++;
            else if (diffInMilli <= 7200000)
                count2h++;
            else if (diffInMilli <= 28800000)
                count8h++;
            else if (diffInMilli <= 57600000)
                count16h++;
            else
                countElse++;

            if (diffInMilli <= totalTime / count)
                countWithinAverage++;
            else
                countAfterAverage++;
        }

        // Avoid division by zero when calculating average
        if (count == 0) {
            return count;
        }
        
        averageTime = totalTime / count;
        hours = (int) (averageTime / 1000 / 60 / 60);
        minutes = (int) ((averageTime / 1000 / 60) % 60);
        seconds = (int) ((averageTime / 1000) % 60);
        milliSeconds = (int) (averageTime % 1000);
        
        withinAverage = String.format("within average time\n(Average time: %dhrs,%dm,%ds,%dms)",hours, minutes, seconds, milliSeconds);
        moreAverage = String.format("more than average time\n(Average time: %dhrs,%dm,%ds,%dms)",hours, minutes, seconds, milliSeconds);
        

        switch (timeRange) {
            case "within 5 minutes":
                count = count5min;
                break;
            case "between 5 and 30 minutes":
                count = count30min;
                break;
            case "between 30 minutes and 2 hours":
                count = count2h;
                break;
            case "between 2 hours and 8 hours":
                count = count8h;
                break;
            case "between 8 hours and 16 hours":
                count = count16h;
                break;
            case "more than 16 hours":
                count = countElse;
                break;      
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
        return "Average Execution Time & Number of Jobs within a Time Range";
    }

    @Override
    public Chart getChart() {
        // Creating the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time Range");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of jobs");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Number of jobs completed between a given time range");

        // Adding data to the bar chart and table
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("2022");
        for (String timeRange : new String[] { "within 5 minutes", "between 5 and 30 minutes",
                "between 30 minutes and 2 hours", "between 2 hours and 8 hours", "between 8 hours and 16 hours",
                "more than 16 hours"}) {
            int count = findAverage(timeRange);
            series.getData().add(new XYChart.Data<>(timeRange, count));
        }
        series.getData().add(new XYChart.Data<>("within average time",countWithinAverage));
        series.getData().add(new XYChart.Data<>("more than average time",countAfterAverage));
        barChart.getData().add(series);
        
    
        return barChart;
    }

    @Override
    public TableView<Data> getTableView() {
        // Creating the table
        TableView<Data> table = new TableView<>();
        table.setEditable(true);
        table.setPrefSize(320, 200);

        // Creating columns for the table
        TableColumn<Data, String> timeRangeCol = new TableColumn<>("Time Range");
        timeRangeCol.setCellValueFactory(new PropertyValueFactory<>("timeRange"));
        timeRangeCol.setPrefWidth(200);

        TableColumn<Data, Integer> jobsCol = new TableColumn<>("Number of jobs");
        jobsCol.setCellValueFactory(new PropertyValueFactory<>("jobs"));
        jobsCol.setPrefWidth(120);

        // Adding columns to the table
        table.getColumns().addAll(Arrays.asList(timeRangeCol, jobsCol));

        // Adding data to the bar chart and table
        ObservableList<Data> dataList = FXCollections.observableArrayList();
        for (String timeRange : new String[] { "within 5 minutes", "between 5 and 30 minutes",
                "between 30 minutes and 2 hours", "between 2 hours and 8 hours", "between 8 hours and 16 hours",
                "more than 16 hours"}) {
            int count = findAverage(timeRange);
            dataList.add(new Data(timeRange, count));
        }
        
        dataList.add(new Data(withinAverage,countWithinAverage));
        dataList.add(new Data(moreAverage,countAfterAverage));

        
        table.setItems(dataList);
        return table;
    }

}

