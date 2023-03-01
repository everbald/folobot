package com.telegram.folobot.service

import com.telegram.folobot.persistence.entity.toDto
import com.telegram.folobot.persistence.repos.FoloIndexRepo
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.StandardChartTheme
import org.jfree.chart.axis.DateAxis
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.ValueAxis
import org.jfree.chart.plot.Plot
import org.jfree.chart.plot.ValueMarker
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYItemRenderer
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.time.Day
import org.jfree.data.time.TimeSeries
import org.jfree.data.time.TimeSeriesCollection
import org.jfree.data.xy.IntervalXYDataset
import org.jfree.ui.RectangleInsets
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Paint
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate


@Component
class FoloIndexChartService(
    private val foloIndexRepo: FoloIndexRepo
) {
    fun buildChart(chatId: Long, startDate: LocalDate, endDate: LocalDate): InputFile {
        val plot = getPlot(chatId, startDate, endDate)
        val chart = JFreeChart(plot).applyStyle()

        val chartOs = ByteArrayOutputStream()
        ChartUtilities.writeChartAsPNG(chartOs, chart, 800, 600)
        return InputFile(ByteArrayInputStream(chartOs.toByteArray()), "weekly_chart.png")
    }

    private fun getPlot(chatId: Long, startDate: LocalDate, endDate: LocalDate): Plot {
        val plot = XYPlot(getDataset(chatId, startDate, endDate), getDomainAxis(), getRangeAxis(), getRenderer())

        val marker = ValueMarker(100.0)
        marker.paint = Color.black
        marker.stroke = BasicStroke(1.0f)

        plot.addRangeMarker(marker)

        return plot
    }

    private fun getDataset(chatId: Long, startDate: LocalDate, endDate: LocalDate): IntervalXYDataset {
        val series = TimeSeries("index")
        val dataset = TimeSeriesCollection()
        val weeklyIndex =
            foloIndexRepo.findByIdChatIdAndIdDateBetweenOrderByIdDate(chatId, startDate, endDate)
                .map { it.toDto() }
        weeklyIndex.forEach {
            series.add(Day(it.id.date.dayOfMonth, it.id.date.monthValue, it.id.date.year ), it.index?: 0)
        }
        dataset.addSeries(series)
        return dataset
    }

    private fun getDomainAxis(): ValueAxis {
        val domain = DateAxis()
        domain.dateFormatOverride = SimpleDateFormat("dd.MM")
        return domain
    }

    private fun getRangeAxis(): ValueAxis {
        return NumberAxis("Индекс фолоактивности в процентах от среднегодового")
    }

    private fun getRenderer(): XYItemRenderer {
        val renderer = CustomRenderer(15)
        renderer.setSeriesShapesVisible(0, false)
        renderer.setSeriesStroke(0, BasicStroke(5.0f))
        renderer.autoPopulateSeriesStroke = false
        return renderer
    }

    private fun JFreeChart.applyStyle(): JFreeChart {
        val fontName = "Lucida Sans"

        val theme = StandardChartTheme.createJFreeTheme() as StandardChartTheme
        theme.titlePaint = Color.decode("#666666")
        theme.extraLargeFont = Font(fontName, Font.BOLD, 16) //title
        theme.largeFont = Font(fontName, Font.BOLD, 15) //axis-title
        theme.regularFont = Font(fontName, Font.PLAIN, 11)
        theme.rangeGridlinePaint = Color.decode("#C0C0C0")
        theme.plotBackgroundPaint = Color.white
        theme.chartBackgroundPaint = Color.white
        theme.gridBandPaint = Color.red
        theme.axisOffset = RectangleInsets(0.0, 0.0, 0.0, 0.0)
        theme.axisLabelPaint = Color.decode("#666666")
        theme.apply(this)

        this.removeLegend()

        return this
    }

    private inner class CustomRenderer(precision: Int) : XYLineAndShapeRenderer() {
        override fun getItemPaint(row: Int, col: Int): Paint? {
            var cpaint: Paint? = getItemColor(row, col)
            if (cpaint == null) {
                cpaint = super.getItemPaint(row, col)
            }
            return cpaint
        }

        fun getItemColor(row: Int, col: Int): Color? {
            val dataset = plot.dataset
            return if (col == 0) Color.decode("#589149")
            else if (dataset.getYValue(row, col) >= dataset.getYValue(row, col - 1)) Color.decode("#589149")
            else Color.decode("#c94e06")
        }
    }
}