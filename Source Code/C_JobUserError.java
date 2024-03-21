
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class C_JobUserError extends Application implements MetricManager {
    
    public static void main(String[] args) {
        launch(args);
    }

    //At here, initisalize variables that will use in Table and LineChart
    private static int totalError = 0;
    private static String[] userArr; //declare at here and initialized at below
    private static int[] countArr; //declare at here and initialized at below

    public C_JobUserError() {
        HashMap<String, Integer> userMap = new HashMap<>();
        userArr = new String[userMap.size()];
        countArr = new int[userMap.size()];
        searchErrors();
    }

    public static class userCount {

        private String snum;
        private String username;
        private int count;

        userCount(String snum, String username, int count) {
            this.snum = snum;
            this.username = username;
            this.count = count;
        }

        public String getSnum() {
            return snum;
        }

        public String getUsername() {
            return this.username;
        }

        public int getCount() {
            return this.count;
        }

    }

    public static void searchErrors() {

        String regex = "error: This association.*user='(?<Username>[A-Za-z0-9._]*)'";

        Pattern pt = Pattern.compile(regex);
        Matcher mt;

        HashMap<String, Integer> userMap = new HashMap<>();
        HashSet<String> set = new HashSet<>(); //use hashset to avoid duplicate users

        //Step 1: read file and put key, value into HashMap
        try {

            String filename = "extracted_log";
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String line;

            while ((line = reader.readLine()) != null) {

                mt = pt.matcher(line);

                if (mt.find()) {

                    String username = mt.group("Username");

                    //put (username , count) to HashMap
                    if (set.add(username)) { //If this set already contains the element, the call leaves the set unchanged and returns false.
                        userMap.put(username, 1);
                        totalError++;
                    } else {
                        userMap.put(username, userMap.get(username) + 1); //get variable by key+1	
                        totalError++;
                    }
                }
            }

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Problem while reading the file");
        }

        //Step 2: put Hashmap to Arrays for sorting
        userArr = new String[userMap.size()];
        countArr = new int[userMap.size()];

        //Iterate through HashMap entrySet using advanced for loop 
        int i = 0;
        for (Map.Entry<String, Integer> entry : userMap.entrySet()) {  //must import Map
            userArr[i] = entry.getKey();
            countArr[i] = entry.getValue();
            i++;
        }
        //entry = key-value
        //Map.Entry is a Type -> stores both the key and value 

        //Step 3: Bubble sort 2 arrays by "Count" in decreasing order
        for (i = 0; i < countArr.length - 1; i++) {
            for (int j = 0; j < countArr.length - i - 1; j++) {
                if (countArr[j] < (countArr[j + 1])) {
                    int temp = countArr[j];
                    countArr[j] = countArr[j + 1];
                    countArr[j + 1] = temp;

                    String temp2 = userArr[j];
                    userArr[j] = userArr[j + 1];
                    userArr[j + 1] = temp2;
                }
            }
        }

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
        return "Number of Jobs causing Errors and Corresponding Users";
    }

    @Override
    public Chart getChart() {

        //Set x-axis and y-axis label
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Users with error");
        yAxis.setLabel("Number of errors");

        //Initialise lineChart
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        //Set title        
        lineChart.setTitle("Number of errors encountered by Users");

        //Set series name                        
        XYChart.Series series = new XYChart.Series();
        series.setName("2022");

        //Adding data to line chart
        for (int i = 0; i < userArr.length; i++) {
            series.getData().add(new XYChart.Data(userArr[i], countArr[i]));
        }

        lineChart.getData().add(series);
        return lineChart;
    }

    @Override
    public TableView<userCount> getTableView() {
        //Creating the table
        TableView<userCount> table = new TableView<>();
        table.setEditable(true);
        table.setPrefSize(300, 300);

        //Creating columns for the table
        TableColumn<userCount, String> col1 = new TableColumn<>("No.");
        col1.setCellValueFactory(new PropertyValueFactory<>("snum"));
        col1.setPrefWidth(40);

        TableColumn<userCount, String> col2 = new TableColumn<>("Users with errors");
        col2.setCellValueFactory(new PropertyValueFactory<>("username"));
        col2.setPrefWidth(200);

        TableColumn<userCount, Integer> col3 = new TableColumn<>("Count");
        col3.setCellValueFactory(new PropertyValueFactory<>("count"));
        col3.setPrefWidth(50);

        //Adding columns to the table
        table.getColumns().addAll(col1, col2, col3);

        //Use observable list for taking multiple values at a time
        ObservableList<userCount> userCountList = FXCollections.observableArrayList();                                                                                //or else will get blank row (null,null,0)

        int count = 1;
        for (int i = 0; i < userArr.length; i++) {
            String scount = Integer.toString(count++); //bcos want to leave the last row blank(""), so need String instead of integer
            userCountList.add(new userCount(scount, userArr[i], countArr[i]));
        }

        userCountList.add(new userCount("", "Number of jobs causing error", totalError));

        table.setItems(userCountList);
        return table;
    }

}
