package graph;

public class Main {
    public static void main(String[] args) {
        PlotGraph plotGraph = new PlotGraph();
        plotGraph.plotCsvMetrics("Categories");
        plotGraph.plotCsvMetrics("Projects");
        plotGraph.plotCsvMetrics("Todos");
    }
}
