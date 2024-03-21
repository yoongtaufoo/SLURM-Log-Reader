
import javafx.scene.chart.Chart;
import javafx.scene.control.TableView;

public interface MetricManager {
    // Returns the name of the metric
    String getName();

    TableView<?> getTableView();

    Chart getChart();
}