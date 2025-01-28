package com.dmj.action.teachingInformation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.LengthAdjustmentType;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.Rotation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.general.DefaultPieDataset;

/* loaded from: LineChar.class */
public class LineChar {
    public JFreeChart getLineChar(double[][] data, String[] rowKeys, Integer[] columnKeys, String pattern, Double unit, Integer count, String export_data, String decimals) {
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("隶书", 1, 20));
        standardChartTheme.setRegularFont(new Font("宋书", 0, 15));
        standardChartTheme.setLargeFont(new Font("宋书", 0, 12));
        ChartFactory.setChartTheme(standardChartTheme);
        return makeLineAndShapeChart(data, rowKeys, columnKeys, pattern, unit, count, export_data, decimals);
    }

    public JFreeChart getLineChar2(double[][] data, String[] rowKeys, String[] columnKeys, String pattern, Double unit, Integer count, String export_data, String decimals) {
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("隶书", 1, 20));
        standardChartTheme.setRegularFont(new Font("宋书", 0, 15));
        standardChartTheme.setLargeFont(new Font("宋书", 0, 12));
        ChartFactory.setChartTheme(standardChartTheme);
        return makeLineAndShapeChart2(data, rowKeys, columnKeys, pattern, unit, count, export_data, decimals);
    }

    public JFreeChart makeBarChart(double[][] data, String[] rowKeys, String[] columnKeys) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createBarChart(dataset, "教师", "平均分", "每小题各阅卷老师平均分对比");
    }

    public JFreeChart makeBarChart2(double[][] data, String[] rowKeys, String[] columnKeys) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createBarChart(dataset, "题号", "批阅份数", "教师工作量对比");
    }

    public JFreeChart makeBarChart3(double[][] data, String[] rowKeys, String[] columnKeys) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createBarChart(dataset, "题号", "判错个数", "分数错漏判统计");
    }

    public JFreeChart makeBarChart4(double[][] data, String[] rowKeys, String[] columnKeys) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createBarChart(dataset, "题组号", "完成进度百分比", "进度查询");
    }

    public JFreeChart makeBarChart5(double[][] data, String[] rowKeys, String[] columnKeys) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createBarChart(dataset, "科目", "完成进度百分比", "科目进度");
    }

    public JFreeChart makeBarChart6(double[][] data, String[] rowKeys, String[] columnKeys) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return panfencreateBarChart(dataset, "", "", "");
    }

    public JFreeChart makeT4(double[][] data, String[] rowKeys, String[] columnKeys) {
        return createBarChart_report(data, rowKeys, columnKeys, "", "", "");
    }

    public JFreeChart makeBarCharT7(double[][] data, String[] rowKeys, String[] columnKeys) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createBarChart(dataset, "", "", "");
    }

    public JFreeChart makeBarCharL5(double[][] data, String[] rowKeys, String[] columnKeys, String xName, String yName, String chartTitle) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createBarChart(dataset, xName, yName, chartTitle);
    }

    public JFreeChart createBarChart(CategoryDataset dataset, String xName, String yName, String chartTitle) {
        int size;
        double left = 0.05d;
        if (null != dataset && (size = dataset.getColumnKeys().size()) > 10) {
            left = 0.5d / size;
        }
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("隶书", 1, 20));
        standardChartTheme.setRegularFont(new Font("宋书", 0, 15));
        standardChartTheme.setLargeFont(new Font("宋书", 0, 12));
        ChartFactory.setChartTheme(standardChartTheme);
        JFreeChart chart = ChartFactory.createBarChart(chartTitle, xName, yName, dataset, PlotOrientation.VERTICAL, true, false, false);
        chart.setTitle(new TextTitle(chartTitle, new Font("宋书", 1, 24)));
        Font labelFont = new Font("SansSerif", 0, 12);
        Font labelFont2 = new Font("宋书", 0, 18);
        chart.setTextAntiAlias(false);
        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.gray);
        NumberAxis vn = plot.getRangeAxis();
        DecimalFormat df = new DecimalFormat("#0.0");
        vn.setNumberFormatOverride(df);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(labelFont);
        domainAxis.setTickLabelFont(labelFont);
        domainAxis.setMaximumCategoryLabelWidthRatio(1.0f);
        domainAxis.setLowerMargin(left);
        domainAxis.setUpperMargin(left);
        plot.setDomainAxis(domainAxis);
        plot.setBackgroundPaint(new Color(255, 255, 204));
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(labelFont);
        rangeAxis.setTickLabelFont(labelFont);
        rangeAxis.setUpperMargin(0.05d);
        rangeAxis.setLowerMargin(0.05d);
        plot.setRangeAxis(rangeAxis);
        BarRenderer renderer = new BarRenderer();
        renderer.setMaximumBarWidth(0.2d);
        renderer.setMinimumBarLength(0.2d);
        renderer.setDefaultOutlinePaint(Color.BLACK);
        renderer.setDrawBarOutline(true);
        renderer.setSeriesPaint(0, new Color(125, 125, 250));
        renderer.setSeriesPaint(1, new Color(153, 204, 255));
        renderer.setSeriesPaint(2, new Color(51, 204, 204));
        renderer.setItemMargin(0.0d);
        renderer.setIncludeBaseInRange(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(labelFont2);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.BASELINE_CENTER));
        plot.setRenderer(renderer);
        plot.setForegroundAlpha(1.0f);
        return chart;
    }

    public JFreeChart panfencreateBarChart(CategoryDataset dataset, String xName, String yName, String chartTitle) {
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("隶书", 1, 20));
        standardChartTheme.setRegularFont(new Font("宋书", 0, 12));
        standardChartTheme.setLargeFont(new Font("宋书", 0, 11));
        ChartFactory.setChartTheme(standardChartTheme);
        JFreeChart chart = ChartFactory.createBarChart(chartTitle, xName, yName, dataset, PlotOrientation.VERTICAL, true, false, false);
        chart.setTitle(new TextTitle(chartTitle, new Font("宋书", 1, 24)));
        Font labelFont = new Font("SansSerif", 0, 12);
        chart.setTextAntiAlias(false);
        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.gray);
        NumberAxis vn = plot.getRangeAxis();
        DecimalFormat df = new DecimalFormat("#0.0");
        vn.setNumberFormatOverride(df);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(labelFont);
        domainAxis.setTickLabelFont(labelFont);
        domainAxis.setMaximumCategoryLabelWidthRatio(1.0f);
        domainAxis.setLowerMargin(0.05d);
        domainAxis.setUpperMargin(0.05d);
        plot.setDomainAxis(domainAxis);
        plot.setBackgroundPaint(new Color(255, 255, 204));
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(labelFont);
        rangeAxis.setTickLabelFont(labelFont);
        rangeAxis.setUpperMargin(0.09d);
        rangeAxis.setLowerMargin(0.05d);
        plot.setRangeAxis(rangeAxis);
        BarRenderer renderer = new BarRenderer();
        renderer.setMaximumBarWidth(0.1d);
        renderer.setMinimumBarLength(0.2d);
        renderer.setDefaultOutlinePaint(Color.BLACK);
        renderer.setDrawBarOutline(true);
        renderer.setSeriesPaint(0, new Color(125, 125, 250));
        renderer.setSeriesPaint(1, new Color(153, 204, 255));
        renderer.setSeriesPaint(2, new Color(51, 204, 204));
        renderer.setItemMargin(0.0d);
        renderer.setIncludeBaseInRange(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        plot.setRenderer(renderer);
        plot.setForegroundAlpha(1.0f);
        return chart;
    }

    public JFreeChart createBarChart_report(double[][] data, String[] rowKeys, String[] columnKeys, String xName, String yName, String chartTitle) {
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("隶书", 1, 20));
        standardChartTheme.setRegularFont(new Font("宋书", 0, 15));
        standardChartTheme.setLargeFont(new Font("宋书", 0, 12));
        ChartFactory.setChartTheme(standardChartTheme);
        String[] columnKeyss = {""};
        CategoryDataset dataset = DatasetUtils.createCategoryDataset(rowKeys, columnKeyss, data);
        JFreeChart chart = ChartFactory.createBarChart(chartTitle, xName, yName, dataset, PlotOrientation.VERTICAL, true, false, false);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        BarRenderer renderer = plot.getRenderer();
        ItemLabelPosition itemLabelPositionFallback = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER, TextAnchor.TOP_CENTER, 0.0d);
        renderer.setPositiveItemLabelPositionFallback(itemLabelPositionFallback);
        renderer.setNegativeItemLabelPositionFallback(itemLabelPositionFallback);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelFont(new Font("黑体", 0, 12));
        renderer.setIncludeBaseInRange(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelsVisible(true);
        plot.setRenderer(renderer);
        domainAxis.setLowerMargin(0.1d);
        domainAxis.setUpperMargin(0.2d);
        return chart;
    }

    public JFreeChart makeLineAndShapeChart(double[][] data, String[] rowKeys, Integer[] columnKeys, String pattern, Double unit, Integer count, String export_data, String decimals) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createTimeXYChar("各题教师分数分布对比", "分数", "百分比", dataset, pattern, unit, count, export_data, decimals);
    }

    public JFreeChart makeLineAndShapeChart2(double[][] data, String[] rowKeys, String[] columnKeys, String pattern, Double unit, Integer count, String export_data, String decimals) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createTimeXYChar("各题教师分数分布对比", "分数", "百分比", dataset, pattern, unit, count, export_data, decimals);
    }

    public JFreeChart getLineChar_questions(double[][] data, String[] rowKeys, String[] columnKeys, String xName, String yName, String chartTitle, String pattern, Double unit, String rangeMarker) {
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("隶书", 1, 20));
        standardChartTheme.setRegularFont(new Font("宋书", 0, 15));
        standardChartTheme.setLargeFont(new Font("宋书", 0, 12));
        ChartFactory.setChartTheme(standardChartTheme);
        return makeLineAndShapeChart_questions(data, rowKeys, columnKeys, xName, yName, chartTitle, pattern, unit, rangeMarker);
    }

    public JFreeChart makeLineAndShapeChart_questions(double[][] data, String[] rowKeys, String[] columnKeys, String xName, String yName, String chartTitle, String pattern, Double unit, String rangeMarker) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createTimeXYChar_area_color(chartTitle, xName, yName, dataset, pattern, unit, rangeMarker);
    }

    public JFreeChart getLineChar(double[][] data, String[] rowKeys, String[] columnKeys, String xName, String yName, String chartTitle, String pattern, Double unit, Integer count, String export_data, String decimals) {
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("隶书", 1, 20));
        standardChartTheme.setRegularFont(new Font("宋书", 0, 15));
        standardChartTheme.setLargeFont(new Font("宋书", 0, 12));
        ChartFactory.setChartTheme(standardChartTheme);
        return makeLineAndShapeChart(data, rowKeys, columnKeys, xName, yName, chartTitle, pattern, unit, count, export_data, decimals);
    }

    public JFreeChart makeLineAndShapeChart(double[][] data, String[] rowKeys, String[] columnKeys, String xName, String yName, String chartTitle, String pattern, Double unit, Integer count, String export_data, String decimals) {
        CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
        return createTimeXYChar(chartTitle, xName, yName, dataset, pattern, unit, count, export_data, decimals);
    }

    public CategoryDataset getBarData(double[][] data, String[] rowKeys, Integer[] columnKeys) {
        return DatasetUtils.createCategoryDataset(rowKeys, columnKeys, data);
    }

    public CategoryDataset getBarData(double[][] data, String[] rowKeys, String[] columnKeys) {
        CategoryDataset a = null;
        try {
            a = DatasetUtils.createCategoryDataset(rowKeys, columnKeys, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }

    public JFreeChart createTimeXYChar(String chartTitle, String x, String y, CategoryDataset xyDataset, String pattern, Double unit, Integer count, String export_data, String decimals) {
        JFreeChart chart = ChartFactory.createLineChart(chartTitle, x, y, xyDataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setTextAntiAlias(false);
        chart.setBackgroundPaint(ChartColor.WHITE);
        Font font = new Font("宋书", 1, 24);
        TextTitle title = new TextTitle(chartTitle);
        title.setFont(font);
        title.setPosition(RectangleEdge.TOP);
        LegendTitle legendTitle = chart.getLegend();
        legendTitle.setPosition(RectangleEdge.BOTTOM);
        legendTitle.setVisible(true);
        chart.setTitle(title);
        Font labelFont = new Font("SansSerif", 0, 12);
        CategoryPlot categoryplot = chart.getPlot();
        categoryplot.setDomainGridlinesVisible(false);
        categoryplot.setDomainGridlinePaint(new Color(128, 128, 128));
        categoryplot.setRangeGridlinesVisible(true);
        categoryplot.setRangeGridlinePaint(new Color(128, 128, 128));
        categoryplot.setDomainGridlineStroke(new BasicStroke());
        categoryplot.setRangeGridlineStroke(new BasicStroke());
        categoryplot.setBackgroundPaint(ChartColor.WHITE);
        categoryplot.setRangeGridlinePaint(ChartColor.LIGHT_GRAY);
        CategoryAxis domainAxis = categoryplot.getDomainAxis();
        domainAxis.setLabelFont(labelFont);
        domainAxis.setTickLabelFont(labelFont);
        domainAxis.setLowerMargin(0.0d);
        domainAxis.setUpperMargin(0.0d);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        NumberAxis numberaxis = categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        numberaxis.setAutoRangeIncludesZero(false);
        numberaxis.setAutoRangeStickyZero(false);
        DecimalFormat df = new DecimalFormat(pattern);
        numberaxis.setNumberFormatOverride(df);
        numberaxis.setTickLabelFont(new Font("宋体", 0, 10));
        if (null != unit && unit.doubleValue() > 0.0d) {
            numberaxis.setAutoTickUnitSelection(false);
            NumberTickUnit nt = new NumberTickUnit(unit.doubleValue());
            numberaxis.setTickUnit(nt);
        } else {
            numberaxis.setAutoTickUnitSelection(true);
        }
        LineAndShapeRenderer lineandshaperenderer = categoryplot.getRenderer();
        int[] arrayOfInt1 = {-3, 3, -3};
        int[] arrayOfInt2 = {-3, 0, 3};
        int[] arrayOfInt12 = {-3, 3, 3};
        int[] arrayOfInt22 = {0, -3, 3};
        Shape[] arrayOfShape = {new Polygon(arrayOfInt1, arrayOfInt2, 3), new Rectangle2D.Double(-2.0d, -3.0d, 3.0d, 6.0d), new Polygon(arrayOfInt12, arrayOfInt22, 3)};
        lineandshaperenderer.setDefaultShapesVisible(true);
        lineandshaperenderer.setDefaultLinesVisible(true);
        if (export_data.equals("Y") && count.intValue() <= 4) {
            DecimalFormat decimalformat1 = new DecimalFormat(decimals);
            lineandshaperenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", decimalformat1));
            lineandshaperenderer.setDefaultItemLabelsVisible(false);
            lineandshaperenderer.setDefaultShapesFilled(Boolean.TRUE.booleanValue());
            lineandshaperenderer.setDefaultShapesVisible(true);
            lineandshaperenderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE9, TextAnchor.BASELINE_CENTER));
        }
        return chart;
    }

    public JFreeChart createTimeXYChar_area_color(String chartTitle, String x, String y, CategoryDataset xyDataset, String pattern, Double unit, String rangeMarker) {
        JFreeChart chart = ChartFactory.createLineChart(chartTitle, x, y, xyDataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setTextAntiAlias(false);
        chart.setBackgroundPaint(ChartColor.WHITE);
        Font font = new Font("宋书", 1, 24);
        TextTitle title = new TextTitle(chartTitle);
        title.setFont(font);
        title.setPosition(RectangleEdge.TOP);
        LegendTitle legendTitle = chart.getLegend();
        legendTitle.setPosition(RectangleEdge.RIGHT);
        legendTitle.setVisible(true);
        chart.setTitle(title);
        Font labelFont = new Font("SansSerif", 0, 12);
        CategoryPlot categoryplot = chart.getPlot();
        categoryplot.setDomainGridlinesVisible(false);
        categoryplot.setDomainGridlinePaint(new Color(128, 128, 128));
        categoryplot.setRangeGridlinesVisible(false);
        categoryplot.setRangeGridlinePaint(new Color(128, 128, 128));
        categoryplot.setDomainGridlineStroke(new BasicStroke());
        categoryplot.setRangeGridlineStroke(new BasicStroke());
        categoryplot.setBackgroundPaint(ChartColor.WHITE);
        categoryplot.setRangeGridlinePaint(ChartColor.LIGHT_GRAY);
        if (rangeMarker.equals("Y")) {
            IntervalMarker intermarker = new IntervalMarker(-1.0d, 0.2d);
            intermarker.setPaint(Color.decode("#FF6464"));
            if (xyDataset != null) {
                categoryplot.addRangeMarker(intermarker, Layer.BACKGROUND);
            }
            IntervalMarker inter = new IntervalMarker(0.2d, 0.3d);
            inter.setLabelOffsetType(LengthAdjustmentType.EXPAND);
            inter.setPaint(Color.decode("#FFFF00"));
            if (xyDataset != null) {
                categoryplot.addRangeMarker(inter, Layer.BACKGROUND);
            }
        }
        CategoryAxis domainAxis = categoryplot.getDomainAxis();
        domainAxis.setLabelFont(labelFont);
        domainAxis.setTickLabelFont(labelFont);
        domainAxis.setLowerMargin(0.0d);
        domainAxis.setUpperMargin(0.0d);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        NumberAxis numberaxis = categoryplot.getRangeAxis();
        DecimalFormat df = new DecimalFormat(pattern);
        numberaxis.setNumberFormatOverride(df);
        numberaxis.setTickLabelFont(new Font("宋体", 0, 10));
        if (null != unit && unit.doubleValue() > 0.0d) {
            numberaxis.setAutoTickUnitSelection(false);
            NumberTickUnit nt = new NumberTickUnit(unit.doubleValue());
            numberaxis.setTickUnit(nt);
        } else {
            numberaxis.setAutoTickUnitSelection(true);
        }
        LineAndShapeRenderer lineandshaperenderer = categoryplot.getRenderer();
        int[] arrayOfInt1 = {-3, 3, -3};
        int[] arrayOfInt2 = {-3, 0, 3};
        int[] arrayOfInt12 = {-3, 3, 3};
        int[] arrayOfInt22 = {0, -3, 3};
        Shape[] arrayOfShape = {new Polygon(arrayOfInt1, arrayOfInt2, 3), new Rectangle2D.Double(-2.0d, -3.0d, 3.0d, 6.0d), new Polygon(arrayOfInt12, arrayOfInt22, 3)};
        lineandshaperenderer.setDefaultShapesVisible(true);
        lineandshaperenderer.setDefaultLinesVisible(true);
        lineandshaperenderer.setSeriesPaint(0, Color.blue);
        lineandshaperenderer.setSeriesPaint(1, Color.red);
        DecimalFormat decimalformat1 = new DecimalFormat("##.##");
        lineandshaperenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", decimalformat1));
        lineandshaperenderer.setDefaultItemLabelsVisible(true);
        lineandshaperenderer.setDefaultShapesFilled(Boolean.TRUE.booleanValue());
        lineandshaperenderer.setDefaultShapesVisible(true);
        return chart;
    }

    public static JFreeChart makePieChart3D(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart3D("饼状图", dataset, true, false, false);
        new Font("宋体", 1, 25);
        chart.setTitle(new TextTitle(""));
        chart.setTextAntiAlias(false);
        chart.setBackgroundPaint(new Color(255, 255, 255));
        LegendTitle legend = chart.getLegend(0);
        legend.setItemFont(new Font("宋体", 1, 15));
        PiePlot plot = chart.getPlot();
        plot.setLabelFont(new Font("宋体", 0, 12));
        plot.setForegroundAlpha(0.95f);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1}", NumberFormat.getNumberInstance(), new DecimalFormat("0%")));
        plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({1})"));
        plot.setStartAngle(0.0d);
        plot.setDirection(Rotation.ANTICLOCKWISE);
        return chart;
    }

    public static void main(String[] args) {
        DefaultPieDataset dfp = new DefaultPieDataset();
        dfp.setValue("完全掌握", 35.0d);
        dfp.setValue("基本掌握", 10.0d);
        dfp.setValue("掌握一点", 25.0d);
        dfp.setValue("完全不会", 5.0d);
        makePieChart3D(dfp);
    }
}
