package ptwop.networker;

import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Chart extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	public Chart(String chartTitle, String lbx, String lby, XYSeriesCollection dataSet) {
		super(chartTitle);
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, lbx, lby, dataSet, PlotOrientation.VERTICAL, true,
				true, false);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(560, 367));
		setContentPane(chartPanel);

		// Create an NumberAxis
		NumberAxis xAxis = new NumberAxis();
		xAxis.setTickUnit(new NumberTickUnit(Math.round(dataSet.getSeries(0).getMaxX() / 10)));

		// Assign it to the chart
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainAxis(xAxis);

		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
	}
}