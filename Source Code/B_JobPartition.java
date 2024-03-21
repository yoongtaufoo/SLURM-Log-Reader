
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class B_JobPartition extends Application implements MetricManager{


    public static void main(String[] args) {
        launch(args);
    }
    
    public static class Data {

        private String partition;
        private int numOfJobs;
        
        public Data(String partition, int numOfJobs) {
            this.partition = partition;
            this.numOfJobs = numOfJobs;
        }
        public String getPartition() {
            return partition;
        }
        public int getNumOfJobs() {
            return numOfJobs;
        }

    }
    
    // read file , return the number of jobs by partition
    public int getNumberOfJobsByPartition(String keyword1) {
        int count = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("extracted_log"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Allocate") && line.contains(keyword1)){
                    count++;
                }
            }
            reader.close();
            
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException e) {
            System.out.println("Error occurs while reading the file");
        }
        // return number of jobs by partition for creating table and line graph
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
        return "Number of Jobs by Partition";
    }

    @Override
    public Chart getChart() {
        // Line Chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        xAxis.setLabel("Partition");
        yAxis.setLabel("Job count");
        lineChart.setTitle("Number of Jobs by Partition");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Job count");
        
        String[] keywords = { "cpu-epyc", "cpu-opteron", "gpu-k10", "gpu-k40c", "gpu-titan", "gpu-v100s" };

        // loop for each keyword(partition)
        for (String keyword1 : keywords) {
            // get the number of jobs by partition to create line graph
            series.getData().add(new XYChart.Data<>(keyword1, getNumberOfJobsByPartition(keyword1)));
        }
        lineChart.getData().add(series);
        
        return lineChart;
    }
        
    @Override
        public TableView<Data> getTableView() {
        // Creating the table
        TableView<Data> table = new TableView<>();
        table.setEditable(true);
        table.setPrefSize(300, 200);

        // Creating columns for the table
        TableColumn<Data, String> partitionCol = new TableColumn<>("Partition");
        partitionCol.setCellValueFactory(new PropertyValueFactory<>("partition"));
        partitionCol.setPrefWidth(100);

        TableColumn<Data, Double> numOfJobsCol = new TableColumn<>("Number of Jobs by Partition");
        numOfJobsCol.setCellValueFactory(new PropertyValueFactory<>("numOfJobs"));
        numOfJobsCol.setPrefWidth(200);

        // Adding columns to the table
        table.getColumns().addAll(Arrays.asList(partitionCol, numOfJobsCol));

        // Adding data to the table
        ObservableList<Data> dataList = FXCollections.observableArrayList();
        
        String[] keywords = { "cpu-epyc", "cpu-opteron", "gpu-k10", "gpu-k40c", "gpu-titan", "gpu-v100s" };
        for (String keyword1 : keywords) {
            dataList.add(new Data(keyword1, getNumberOfJobsByPartition(keyword1)));
        }
        table.setItems(dataList);
        
        return table;
    }
    
}




    