package ptwop.networker;

import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Chart extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	public Chart(String chartTitle, String lbx, String lby, CategoryDataset dataSet) {
		super(chartTitle);
		JFreeChart lineChart = ChartFactory.createLineChart(chartTitle, lbx, lby, dataSet, PlotOrientation.VERTICAL,
				true, true, false);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new Dimension(560, 367));
		setContentPane(chartPanel);
		
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
	}
}