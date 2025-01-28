package com.dmj.util.jfreechar.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.dmj.util.Const;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.DefaultXYZDataset;

/* loaded from: JFreeChartUtils.class */
public class JFreeChartUtils {
    private static String NO_DATA_MSG = "数据为空";
    private static int DEFAULT_BAR_WIDTH = 20;
    private static int DEFAULT_BOTTOM = 40;
    private static int DOCUMENT_HEIGHT = Const.width;
    private static Font FONT = new Font("宋体", 0, 12);
    private static Color defaultColor = new Color(115, 115, 115);
    public static Color[] CHART_COLORS = {new Color(0, 168, 225), new Color(153, 204, 0), new Color(227, 0, 57), new Color(252, 211, 0), new Color(128, 0, 128), new Color(0, 153, 78), new Color(255, 102, 0), new Color(128, 128, 0), new Color(219, 0, 194), new Color(0, 128, 128), new Color(0, 0, 255), new Color(200, 204, 0)};

    static {
        setChartTheme();
    }

    public static void setChartTheme() {
        StandardChartTheme chartTheme = new StandardChartTheme("CN");
        chartTheme.setExtraLargeFont(FONT);
        chartTheme.setRegularFont(FONT);
        chartTheme.setLargeFont(FONT);
        chartTheme.setSmallFont(FONT);
        chartTheme.setTitlePaint(new Color(51, 51, 51));
        chartTheme.setSubtitlePaint(new Color(85, 85, 85));
        chartTheme.setLegendBackgroundPaint(Color.WHITE);
        chartTheme.setLegendItemPaint(Color.BLACK);
        chartTheme.setChartBackgroundPaint(Color.WHITE);
        Paint[] OUTLINE_PAINT_SEQUENCE = {Color.WHITE};
        DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(CHART_COLORS, CHART_COLORS, OUTLINE_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        chartTheme.setDrawingSupplier(drawingSupplier);
        chartTheme.setPlotBackgroundPaint(Color.WHITE);
        chartTheme.setPlotOutlinePaint(Color.WHITE);
        chartTheme.setLabelLinkPaint(new Color(8, 55, 114));
        chartTheme.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);
        chartTheme.setAxisOffset(new RectangleInsets(5.0d, 12.0d, 5.0d, 12.0d));
        chartTheme.setDomainGridlinePaint(new Color(192, 208, 224));
        chartTheme.setRangeGridlinePaint(new Color(192, 192, 192));
        chartTheme.setBaselinePaint(Color.WHITE);
        chartTheme.setCrosshairPaint(Color.BLUE);
        chartTheme.setAxisLabelPaint(new Color(51, 51, 51));
        chartTheme.setTickLabelPaint(new Color(67, 67, 72));
        chartTheme.setBarPainter(new StandardBarPainter());
        chartTheme.setXYBarPainter(new StandardXYBarPainter());
        chartTheme.setItemLabelPaint(Color.black);
        chartTheme.setThermometerPaint(Color.white);
        ChartFactory.setChartTheme(chartTheme);
    }

    public static void setAntiAlias(JFreeChart chart) {
        chart.setTextAntiAlias(false);
        chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    public static void setLegendEmptyBorder(JFreeChart chart) {
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
    }

    public static DefaultCategoryDataset createDefaultCategoryDataset(Vector<Serie> series, String[] categories) {
        setChartTheme();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Iterator<Serie> it = series.iterator();
        while (it.hasNext()) {
            Serie serie = it.next();
            String name = serie.getName();
            Vector<Object> data = serie.getData();
            if (data != null && categories != null && data.size() == categories.length) {
                for (int index = 0; index < data.size(); index++) {
                    String value = data.get(index) == null ? "" : data.get(index).toString();
                    if (isPercent(value)) {
                        value = value.substring(0, value.length() - 1);
                    }
                    if (isNumber(value)) {
                        dataset.setValue(Double.parseDouble(value), name, categories[index]);
                    }
                }
            }
        }
        return dataset;
    }

    /* JADX WARN: Type inference failed for: r0v24, types: [double[], double[][]] */
    public static DefaultXYZDataset createDefaultXYZDataset(List<Map<String, Object>> list) {
        setChartTheme();
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        double autoSize = getBubbleSize(list);
        for (Map<String, Object> map : list) {
            double[] x_Data = {Convert.toDouble(map.get("x"), Double.valueOf(0.0d)).doubleValue()};
            double[] y_Data = {Convert.toDouble(map.get("y"), Double.valueOf(0.0d)).doubleValue()};
            double s = Convert.toDouble(map.get("size"), Double.valueOf(0.0d)).doubleValue();
            if (s == 0.0d) {
                s = autoSize;
            }
            double[] size = {s};
            dataset.addSeries(map.get("label").toString(), (double[][]) new double[]{x_Data, y_Data, size});
        }
        return dataset;
    }

    public static double getBubbleSize(List<Map<String, Object>> data) {
        double xMin = 1.0E7d;
        double xMax = 0.0d;
        double yMin = 1.0E7d;
        double yMax = 0.0d;
        for (Map<String, Object> map : data) {
            xMin = Math.min(xMin, Convert.toDouble(map.get("x")).doubleValue());
            xMax = Math.max(xMax, Convert.toDouble(map.get("x")).doubleValue());
            yMin = Math.min(yMin, Convert.toDouble(map.get("y")).doubleValue());
            yMax = Math.max(yMax, Convert.toDouble(map.get("y")).doubleValue());
        }
        return ((((xMax - xMin) + yMax) - yMin) / 2.0d) / 8.0d;
    }

    public static int getAutoHeight(int sourceCount, int colsCount) {
        int height = (((sourceCount * 2) - 1) * colsCount * DEFAULT_BAR_WIDTH) + DEFAULT_BOTTOM;
        if (height > DOCUMENT_HEIGHT) {
            return DOCUMENT_HEIGHT;
        }
        if (height < 200) {
            return 200;
        }
        return height;
    }

    public static DefaultPieDataset createDefaultPieDataset(String[] categories, Object[] datas) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < categories.length && categories != null; i++) {
            String value = datas[i].toString();
            if (isPercent(value)) {
                value = value.substring(0, value.length() - 1);
            }
            if (isNumber(value)) {
                dataset.setValue(categories[i], Double.valueOf(value));
            }
        }
        return dataset;
    }

    public static TimeSeries createTimeseries(String category, Vector<Object[]> dateValues) {
        TimeSeries timeseries = new TimeSeries(category);
        if (dateValues != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Iterator<Object[]> it = dateValues.iterator();
            while (it.hasNext()) {
                Object[] objects = it.next();
                Date date = null;
                try {
                    date = dateFormat.parse(objects[0].toString());
                } catch (ParseException e) {
                }
                String sValue = objects[1].toString();
                if (date != null && isNumber(sValue)) {
                    double dValue = Double.parseDouble(sValue);
                    timeseries.add(new Day(date), dValue);
                }
            }
        }
        return timeseries;
    }

    public static void setLineRender(CategoryPlot plot, boolean isShowDataLabels) {
        setLineRender(plot, isShowDataLabels, false);
    }

    public static void setLineRender(CategoryPlot plot, boolean isShowDataLabels, boolean isShapesVisible) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10.0d, 10.0d, 0.0d, 10.0d), false);
        LineAndShapeRenderer renderer = plot.getRenderer();
        renderer.setDefaultStroke(new BasicStroke(1.5f));
        if (isShowDataLabels) {
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()));
            renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER));
        }
        renderer.setDefaultShapesVisible(isShapesVisible);
        setXAixs(plot);
        setYAixs(plot);
    }

    public static void setTimeSeriesRender(Plot plot, boolean isShowData, boolean isShapesVisible) {
        XYPlot xyplot = (XYPlot) plot;
        xyplot.setNoDataMessage(NO_DATA_MSG);
        xyplot.setInsets(new RectangleInsets(10.0d, 10.0d, 5.0d, 10.0d));
        XYLineAndShapeRenderer xyRenderer = xyplot.getRenderer();
        xyRenderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
        xyRenderer.setDefaultShapesVisible(false);
        if (isShowData) {
            xyRenderer.setDefaultItemLabelsVisible(true);
            xyRenderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
            xyRenderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER));
        }
        xyRenderer.setDefaultShapesVisible(isShapesVisible);
        DateAxis domainAxis = xyplot.getDomainAxis();
        domainAxis.setAutoTickUnitSelection(false);
        DateTickUnit dateTickUnit = new DateTickUnit(DateTickUnitType.YEAR, 1, new SimpleDateFormat("yyyy-MM"));
        domainAxis.setTickUnit(dateTickUnit);
        StandardXYToolTipGenerator xyTooltipGenerator = new StandardXYToolTipGenerator("{1}:{2}", new SimpleDateFormat("yyyy-MM-dd"), new DecimalFormat("0"));
        xyRenderer.setDefaultToolTipGenerator(xyTooltipGenerator);
        setXY_XAixs(0.0d, 0, xyplot);
        setXY_YAixs(0.0d, 0, xyplot);
    }

    public static void setTimeSeriesRender(Plot plot, boolean isShowData) {
        setTimeSeriesRender(plot, isShowData, false);
    }

    public static void setTimeSeriesBarRender(Plot plot, boolean isShowDataLabels) {
        XYPlot xyplot = (XYPlot) plot;
        xyplot.setNoDataMessage(NO_DATA_MSG);
        XYBarRenderer xyRenderer = new XYBarRenderer(0.1d);
        xyRenderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
        if (isShowDataLabels) {
            xyRenderer.setDefaultItemLabelsVisible(true);
            xyRenderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
        }
        StandardXYToolTipGenerator xyTooltipGenerator = new StandardXYToolTipGenerator("{1}:{2}", new SimpleDateFormat("yyyy-MM-dd"), new DecimalFormat("0"));
        xyRenderer.setDefaultToolTipGenerator(xyTooltipGenerator);
        setXY_XAixs(0.0d, 0, xyplot);
        setXY_YAixs(0.0d, 0, xyplot);
    }

    public static void setBarRenderer(CategoryPlot plot, boolean isShowDataLabels, DefaultChartItem defaultChartItem, boolean isPercent) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10.0d, 10.0d, 5.0d, 10.0d));
        BarRenderer renderer = plot.getRenderer();
        MyStandardCategoryItemLabelGenerator generator = new MyStandardCategoryItemLabelGenerator(defaultChartItem, isShowDataLabels);
        if (isPercent) {
            NumberFormat format = NumberFormat.getPercentInstance();
            NumberAxis na = plot.getRangeAxis();
            na.setNumberFormatOverride(format);
            generator.setFormat(format);
        }
        renderer.setDefaultItemLabelGenerator(generator);
        renderer.setMaximumBarWidth(0.05d);
        renderer.setItemMargin(0.0d);
        renderer.setDefaultSeriesVisibleInLegend(true);
        if (isShowDataLabels) {
            renderer.setDefaultItemLabelsVisible(true);
        }
        setXAixs(plot);
        setYAixs(plot);
    }

    public static void setBarRenderer(CategoryPlot plot, boolean isShowDataLabels) {
        setBarRenderer(plot, isShowDataLabels, null, false);
    }

    public static void setStackBarRender(CategoryPlot plot) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10.0d, 10.0d, 5.0d, 10.0d));
        StackedBarRenderer renderer = plot.getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        plot.setRenderer(renderer);
        setXAixs(plot);
        setYAixs(plot);
    }

    public static void setAixsMargin(CategoryPlot plot, double lowerDomainAxisMargin, double upperDomainAxisMargin, double lowerValueAxisMargin, double upperValueAxisMargin) {
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(lowerDomainAxisMargin);
        domainAxis.setUpperMargin(upperDomainAxisMargin);
        ValueAxis valueAxis = plot.getRangeAxis();
        valueAxis.setLowerMargin(lowerValueAxisMargin);
        valueAxis.setUpperMargin(upperValueAxisMargin);
    }

    public static double formatMin(double min) {
        double min2;
        String newMinStr = "0.";
        if (min > 1.0d) {
            min2 = (int) min;
        } else {
            String minStr = String.valueOf(min).substring(0, Math.min(5, String.valueOf(min).length()));
            minStr.toCharArray();
            int index = minStr.indexOf(46) + 1;
            String sub = minStr.substring(index);
            for (int i = 0; i < sub.length(); i++) {
                char cc = sub.charAt(i);
                if (i != sub.length() - 1 && cc == '0') {
                    newMinStr = newMinStr + "0";
                } else {
                    newMinStr = newMinStr + cc;
                    break;
                }
            }
            min2 = Convert.toDouble(newMinStr, Double.valueOf(0.0d)).doubleValue();
        }
        return min2;
    }

    public static void setXYPlotAixsMargin(XYPlot plot, double lowerDomainAxisMargin, double upperDomainAxisMargin, double lowerValueAxisMargin, double upperValueAxisMargin) {
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(lowerDomainAxisMargin);
        domainAxis.setUpperMargin(upperDomainAxisMargin);
        ValueAxis valueAxis = plot.getRangeAxis();
        valueAxis.setLowerMargin(lowerValueAxisMargin);
        valueAxis.setUpperMargin(upperValueAxisMargin);
    }

    public static List<Double> computeTicks(CategoryPlot plot, ValueMarker valueMarker, double minValue, double maxValue, int split) {
        double min;
        double step;
        ValueAxis valueAxis = plot.getRangeAxis();
        double max = 0.0d;
        if (minValue > 0.0d) {
            if (valueMarker != null && valueMarker.getValue() < minValue) {
                min = valueMarker.getValue();
            } else {
                min = minValue;
            }
        } else {
            min = valueAxis.getLowerBound();
        }
        if (maxValue <= 0.0d || maxValue < minValue) {
            if (maxValue == 0.0d) {
                max = valueAxis.getUpperBound();
            }
        } else if (valueMarker != null && valueMarker.getValue() > maxValue) {
            max = valueMarker.getValue();
        } else {
            max = maxValue;
        }
        double min2 = formatMin(min);
        double space = max - min2;
        double step2 = space / split;
        String stepStr = String.valueOf(step2).substring(0, Math.min(5, String.valueOf(step2).length()));
        stepStr.toCharArray();
        String newStepStr = "0.";
        if (max == min2) {
            min2 = 0.0d;
            step = max;
        } else if (step2 >= 1.0d) {
            step = Math.ceil(step2);
        } else {
            int index = stepStr.indexOf(46) + 1;
            String sub = stepStr.substring(index);
            for (int i = 0; i < sub.length(); i++) {
                char cc = sub.charAt(i);
                if (i != sub.length() - 1 && cc == '0') {
                    newStepStr = newStepStr + "0";
                } else {
                    newStepStr = newStepStr + cc;
                    break;
                }
            }
            step = Convert.toDouble(newStepStr, Double.valueOf(1.0d)).doubleValue();
        }
        BigDecimal mm = new BigDecimal(String.valueOf(min2));
        BigDecimal bigStep = new BigDecimal(String.valueOf(step));
        BigDecimal bigStep2 = bigStep.compareTo(BigDecimal.valueOf(0L)) <= 0 ? BigDecimal.valueOf(1L) : bigStep;
        List<Double> ticks = new ArrayList<>();
        do {
            ticks.add(Double.valueOf(mm.doubleValue()));
            mm = mm.add(bigStep2);
        } while (mm.doubleValue() <= max);
        return ticks;
    }

    public static void setMinMax(CategoryPlot plot, List<Double> ticks) {
        ValueAxis valueAxis = plot.getRangeAxis();
        valueAxis.setLowerBound(ticks.get(0).doubleValue());
        valueAxis.setUpperBound(ticks.get(ticks.size() - 1).doubleValue());
    }

    public static void setXYPlotMinMaxValue(XYPlot plot, Double xMinValue, Double xMaxValue, Double yMinValue, Double yMaxValue, List<Double> xTicks, List<Double> yTicks) {
        NumberAxis domainAxis = plot.getDomainAxis();
        Double xMin = null;
        if (xMinValue != null) {
            xMin = xMinValue;
        }
        if (CollUtil.isNotEmpty(xTicks)) {
            if (xMin != null) {
                xMin = Double.valueOf(Math.min(((Double) CollUtil.min(xTicks)).doubleValue(), xMin.doubleValue()));
            } else {
                xMin = (Double) CollUtil.min(xTicks);
            }
        }
        if (xMin != null) {
            domainAxis.setLowerBound(xMin.doubleValue());
        }
        Double xMax = null;
        if (xMaxValue != null) {
            xMax = xMaxValue;
        }
        if (CollUtil.isNotEmpty(xTicks)) {
            if (xMax != null) {
                xMax = Double.valueOf(Math.max(((Double) CollUtil.max(xTicks)).doubleValue(), xMax.doubleValue()));
            } else {
                xMax = (Double) CollUtil.max(xTicks);
            }
        }
        if (xMax != null) {
            domainAxis.setUpperBound(xMax.doubleValue());
        }
        NumberAxis rangeAxis = plot.getRangeAxis();
        Double yMin = null;
        if (yMinValue != null) {
            yMin = yMinValue;
        }
        if (CollUtil.isNotEmpty(yTicks)) {
            if (yMin != null) {
                yMin = Double.valueOf(Math.min(((Double) CollUtil.min(yTicks)).doubleValue(), yMin.doubleValue()));
            } else {
                yMin = (Double) CollUtil.min(yTicks);
            }
        }
        if (yMin != null) {
            rangeAxis.setLowerBound(yMin.doubleValue());
        }
        Double yMax = null;
        if (yMaxValue != null) {
            yMax = yMaxValue;
        }
        if (CollUtil.isNotEmpty(yTicks)) {
            if (yMax != null) {
                yMax = Double.valueOf(Math.max(((Double) CollUtil.max(yTicks)).doubleValue(), yMax.doubleValue()));
            } else {
                yMax = (Double) CollUtil.max(yTicks);
            }
        }
        if (yMax != null) {
            rangeAxis.setUpperBound(yMax.doubleValue());
        }
    }

    public static void setXAixs(CategoryPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        plot.getDomainAxis().setAxisLinePaint(lineColor);
        plot.getDomainAxis().setTickMarkPaint(lineColor);
    }

    public static void setYAixs(CategoryPlot plot) {
        Color lineColor = new Color(255, 0, 0);
        ValueAxis axis = plot.getRangeAxis();
        axis.setAxisLinePaint(lineColor);
        axis.setTickMarkPaint(lineColor);
        axis.setAxisLineVisible(false);
        axis.setTickMarksVisible(false);
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        plot.setRangeGridlineStroke(new BasicStroke(1.0f));
    }

    public static void setCustomTicks(CategoryPlot plot, List<Double> ticks) {
        if (ticks != null && ticks.size() > 0) {
            plot.setRangeAxis(new 1("", ticks));
        }
    }

    public static void setCustomTicks(XYPlot plot, List<Double> xTicks, List<Double> yTicks) {
        if (CollUtil.isNotEmpty(xTicks)) {
            plot.setDomainAxis(new 2(plot.getDomainAxis().getLabel(), xTicks));
        }
        if (CollUtil.isNotEmpty(yTicks)) {
            plot.setRangeAxis(new 3(plot.getRangeAxis().getLabel(), yTicks));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String convertDoubleToString(double num) {
        BigDecimal bd = new BigDecimal(String.valueOf(num));
        return bd.stripTrailingZeros().toPlainString();
    }

    public static void setStep(CategoryPlot plot, double step) {
        if (step <= 0.0d) {
            return;
        }
        NumberAxis axis = plot.getRangeAxis();
        axis.setTickUnit(new NumberTickUnit(step));
    }

    public static void setXY_XAixs(double step, int tickCount, XYPlot plot) {
        Color lineColor = defaultColor;
        NumberAxis domainAxis = plot.getDomainAxis();
        domainAxis.setAxisLinePaint(lineColor);
        domainAxis.setTickMarkPaint(lineColor);
        domainAxis.setAxisLineVisible(true);
        domainAxis.setTickMarksVisible(true);
        if (step <= 0.0d) {
            step = NumberUtil.round((domainAxis.getRange().getUpperBound() - domainAxis.getRange().getLowerBound()) / (tickCount > 0 ? tickCount : 10), 2).doubleValue();
        }
        domainAxis.setTickUnit(new NumberTickUnit(step));
        plot.setRangeGridlinePaint(new Color(255, 255, 255));
        plot.setRangeGridlineStroke(new BasicStroke(1.0f));
        plot.setDomainGridlinesVisible(false);
    }

    public static void setXY_YAixs(double step, int tickCount, XYPlot plot) {
        Color lineColor = new Color(115, 115, 115);
        NumberAxis axis = plot.getRangeAxis();
        axis.setAxisLinePaint(lineColor);
        axis.setTickMarkPaint(lineColor);
        axis.setAxisLineVisible(true);
        axis.setTickMarksVisible(true);
        if (step <= 0.0d) {
            step = NumberUtil.round((axis.getRange().getUpperBound() - axis.getRange().getLowerBound()) / (tickCount > 0 ? tickCount : 10), 2).doubleValue();
        }
        axis.setTickUnit(new NumberTickUnit(step));
        plot.setRangeGridlinePaint(new Color(255, 255, 255));
        plot.setRangeGridlineStroke(new BasicStroke(1.0f));
        plot.setDomainGridlinesVisible(false);
        axis.setTickLabelInsets(new RectangleInsets(0.0d, 10.0d, 0.0d, 0.0d));
    }

    public static void setPieRender(Plot plot) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10.0d, 10.0d, 5.0d, 10.0d));
        PiePlot piePlot = (PiePlot) plot;
        piePlot.setInsets(new RectangleInsets(0.0d, 0.0d, 0.0d, 0.0d));
        piePlot.setCircular(true);
        piePlot.setLabelGap(0.01d);
        piePlot.setInteriorGap(0.05d);
        piePlot.setLegendItemShape(new Rectangle(10, 10));
        piePlot.setIgnoreNullValues(true);
        piePlot.setLabelBackgroundPaint((Paint) null);
        piePlot.setLabelShadowPaint((Paint) null);
        piePlot.setLabelOutlinePaint((Paint) null);
        piePlot.setShadowPaint((Paint) null);
        piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{2}"));
    }

    public static String ToBase64(JFreeChart jFreeChart, int width, int height) {
        ByteArrayOutputStream out = null;
        try {
            try {
                out = new ByteArrayOutputStream();
                ChartUtils.writeChartAsPNG(out, jFreeChart, width, height);
                String base64 = Base64.encode(out.toByteArray());
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return base64;
            } catch (IOException e2) {
                e2.printStackTrace();
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static boolean isPercent(String str) {
        return str != null && str.endsWith("%") && isNumber(str.substring(0, str.length() - 1));
    }

    public static boolean isNumber(String str) {
        if (str != null) {
            return str.matches("^[-+]?(([0-9]+)((([.]{0})([0-9]*))|(([.]{1})([0-9]+))))$");
        }
        return false;
    }
}
