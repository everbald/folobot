package com.telegram.folobot.service

import com.telegram.folobot.persistence.entity.toDto
import com.telegram.folobot.persistence.repos.FoloIndexRepo
import com.telegram.folobot.prettyPrint
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.StandardChartTheme
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.category.BarRenderer
import org.jfree.chart.renderer.category.StandardBarPainter
import org.jfree.data.category.CategoryDataset
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.ui.RectangleInsets
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Paint
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate

@Component
class WeeklyIndexChartService(
    private val foloIndexRepo: FoloIndexRepo,
    private val messageService: MessageService
) {
    fun weeklyIndex(chatId: Long) {
        val chart = ChartFactory.createBarChart(
            "Динамика Фолоиндекса за прошедшую неделю",
            "", "Процент от среднегодового значения",
            getDataset(chatId), PlotOrientation.VERTICAL,
            false, true, false
        ).setStyle()

        val chartOs = ByteArrayOutputStream()
        ChartUtilities.writeChartAsJPEG(chartOs, chart, 800, 600)

        messageService.sendPhoto(
            InputFile(ByteArrayInputStream(chartOs.toByteArray()), "weekly_chart.png"),
            "#фолоиндекснедели",
            chatId
        )
    }

    private fun getDataset(chatId: Long): CategoryDataset {
        val dataset = DefaultCategoryDataset()
        val weeklyIndex = foloIndexRepo.findByIdChatIdAndIdDateBetweenOrderByIdDate(
            chatId, LocalDate.now().minusDays(6), LocalDate.now()
        ).map { it.toDto() }
        weeklyIndex.forEach {
            dataset.addValue(it.index?: 0, "index", it.id.date.prettyPrint())
        }
        return dataset
    }

    private fun JFreeChart.setStyle(): JFreeChart {
        val fontName = "Lucida Sans"

        this.categoryPlot.renderer = CustomBarRenderer()

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
        theme.barPainter = StandardBarPainter()
        theme.axisLabelPaint = Color.decode("#666666")
        theme.apply(this)

        this.categoryPlot.isOutlineVisible = false
        this.categoryPlot.rangeAxis.isAxisLineVisible = false
        this.categoryPlot.rangeAxis.isTickMarksVisible = false
        this.categoryPlot.rangeGridlineStroke = BasicStroke()
        this.categoryPlot.rangeAxis.tickLabelPaint = Color.decode("#666666")
        this.categoryPlot.domainAxis.tickLabelPaint = Color.decode("#666666")
        this.setTextAntiAlias(true)
        this.antiAlias = true

        val rend = this.categoryPlot.renderer as BarRenderer
        rend.setShadowVisible(true)
        rend.shadowXOffset = 2.0
        rend.shadowYOffset = 0.0
        rend.shadowPaint = Color.decode("#C0C0C0")
        rend.maximumBarWidth = 0.1

        return this
    }

    inner class CustomBarRenderer : BarRenderer() {
        override fun getItemPaint(series: Int, item: Int): Paint {
            val dataset = plot.dataset
            val value = dataset.getValue(series, item)
            return if (value.toDouble() >= 100.0) {
                Color.decode("#589149")
            } else {
                Color.decode("#c94e06")
            }
        }
    }
}