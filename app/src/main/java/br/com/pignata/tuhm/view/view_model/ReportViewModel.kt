package br.com.pignata.tuhm.view.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.data.database.entity.ProblemEntity
import br.com.pignata.tuhm.data.database.entity.ProjectWithProblem
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import java.io.ByteArrayOutputStream
import java.io.File

class ReportViewModel : ViewModel() {
    fun generateReportPdf(
        context: Context?,
        file: File,
        logo: Bitmap?,
        graphic: Bitmap,
        projectWithProblem: ProjectWithProblem
    ) {
        val pdfDocument = PdfDocument(PdfWriter(file))
        pdfDocument.defaultPageSize = PageSize.A4
        val document = Document(pdfDocument).apply { topMargin = 80f }

        val project = projectWithProblem.project
        val problems = projectWithProblem.listProblems

        val stream = ByteArrayOutputStream()
        logo?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()
        logo?.recycle()

        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE) {
            val docEvent = it as? PdfDocumentEvent
            val canvas = PdfCanvas(docEvent?.page)
            val pageSize: Rectangle = docEvent!!.page.pageSize

            canvas.apply {
                addImageAt(ImageDataFactory.create(byteArray), 0f, pageSize.height - 100f, true)
                beginText()
                setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 22f)
                setColor(DeviceRgb(0x2e, 0x7d, 0x32), true)
                moveText(80.0, pageSize.top - 50.0)
                showText(context?.getString(R.string.app_name))
                setColor(DeviceRgb(0, 0, 0), true)
                endText()
                release()
            }
        }

        document.add(generateTitlePdf(project.title))
        document.add(generateLineSeparator())
        document.add(generateDescriptionPdf(project.description).setMarginTop(10f))

        document.add(
            generateTableInformations(
                context,
                problems.size,
                problems.fold(0) { count, problem ->
                    count + (problem.listHeuristics?.size ?: 0)
                })
        )

        document.add(generateGravity(context, problems, graphic))

        document.add(
            generateTitlePdf(context?.getString(R.string.txt_problems_found)).setMarginTop(40f)
        )

        document.add(generateLineSeparator())

        val listGravity = context?.resources?.getStringArray(R.array.list_gravity)
        val listHeuristic = context?.resources?.getStringArray(R.array.list_title_heuristic)
        problems.sortedByDescending { it.gravity }.forEach {
            val color = colorBackgroundGravity(it.gravity ?: -1)

            val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 1f))).apply {
                useAllAvailableWidth()
                setMarginTop(20f)
                setBackgroundColor(color, .2f)

                addCell(Cell().apply {
                    setBorder(Border.NO_BORDER)

                    add(generateSubTitlePdf(listGravity?.get(it.gravity ?: 0)))
                    if (it.description?.isNotEmpty() == true)
                        add(generateCaptionPdf(it.description).setMarginTop(10f))

                    add(
                        generateDescriptionPdf(context?.getString(R.string.txt_violated_heuristics))
                            .setMarginTop(10f)
                    )

                    val list = List()
                    if (it.listHeuristics.isNullOrEmpty()) list.add(context?.getString(R.string.error_report_heuristics_empty))
                    it.listHeuristics?.forEach { itHeuristic ->
                        list.add(listHeuristic?.get(itHeuristic))
                    }
                    add(list)
                })

                addCell(Cell().apply {
                    setBorder(Border.NO_BORDER)
                    it.srcImage?.let { srcImage ->
                        try {
                            val bmp = BitmapFactory.decodeFile(srcImage)
                            val stream = ByteArrayOutputStream()
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val byteArray = stream.toByteArray()
                            bmp.recycle()
                            add(Image(ImageDataFactory.create(byteArray)).apply {
                                setMargins(10f, 20f, 10f, 10f)
                                setWidth(100f)
                                setHeight((100f / bmp.width) * bmp.height)
                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            }

            document.add(table)
        }

        document.close()
    }

    private fun generateTitlePdf(text: String?): Paragraph {
        return Paragraph(text).apply {
            setFontSize(20f)
        }
    }

    private fun generateSubTitlePdf(text: String?): Paragraph {
        return Paragraph(text).apply {
            setFontSize(18f)
        }
    }

    private fun generateDescriptionPdf(text: String?): Paragraph {
        return Paragraph(text).apply {
            setFontSize(14f)
            setFontColor(DeviceRgb(0, 0, 0), .87f)
        }
    }

    private fun generateCaptionPdf(text: String?): Paragraph {
        return Paragraph(text).apply {
            setFontSize(12f)
            setFontColor(DeviceRgb(0, 0, 0), .6f)
        }
    }

    private fun generateTableInformations(
        context: Context?,
        problems: Int,
        heuristics: Int
    ): IBlockElement {
        return Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f))).apply {
            setMarginTop(40f)
            setHorizontalAlignment(HorizontalAlignment.CENTER)
            addHeaderCell(generateHeaderCell(context?.getString(R.string.txt_problems_found)))
            addHeaderCell(generateHeaderCell(context?.getString(R.string.txt_violated_heuristics_report)))
            addCell(generateInformationCell(problems.toString()))
            addCell(generateInformationCell(heuristics.toString()))
        }
    }

    private fun generateGravity(
        context: Context?,
        problems: List<ProblemEntity>,
        bitmap: Bitmap
    ): IBlockElement {
        return Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f))).apply {
            setMarginTop(40f)
            addCell(generateCell(getTextGravity(context, problems, R.string.txt_gravity_absent, 0)))
            addCell(generateGraphicGravity(bitmap))
            addCell(
                generateCell(
                    getTextGravity(
                        context,
                        problems,
                        R.string.txt_gravity_cosmetic,
                        1
                    )
                )
            )
            addCell(generateCell(getTextGravity(context, problems, R.string.txt_gravity_small, 2)))
            addCell(generateCell(getTextGravity(context, problems, R.string.txt_gravity_great, 3)))
            addCell(
                generateCell(
                    getTextGravity(
                        context,
                        problems,
                        R.string.txt_gravity_catastrofic,
                        4
                    )
                )
            )

        }
    }

    private fun generateGraphicGravity(bitmap: Bitmap): Cell {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()
        bitmap.recycle()
        return Cell(5, 1).apply {
            setBorder(Border.NO_BORDER)
            add(Image(ImageDataFactory.create(byteArray)).apply {
                setMarginLeft(40f)
                setWidth(152f)
                setHeight(80f)
            })
        }
    }

    private fun generateHeaderCell(text: String?): Cell {
        return Cell().apply {
            setBackgroundColor(DeviceRgb(0x2e, 0x7d, 0x32), .2f)
            setBorder(Border.NO_BORDER)
            add(Paragraph(text).setTextAlignment(TextAlignment.CENTER))
        }
    }

    private fun generateInformationCell(text: String?): Cell {
        return Cell().apply {
            add(Paragraph(text).apply {
                setTextAlignment(TextAlignment.CENTER)
                setFontSize(22f)
            })
            setBorder(Border.NO_BORDER)
        }
    }

    private fun generateCell(text: String?): Cell {
        return Cell().apply {
            add(Paragraph(text))
            setBorder(Border.NO_BORDER)
        }
    }

    private fun getTextGravity(
        context: Context?,
        problems: List<ProblemEntity>,
        @StringRes idString: Int,
        id: Int
    ): String? = context?.getString(idString, problems.count { it.gravity == id })

    private fun generateLineSeparator(): LineSeparator =
        LineSeparator(SolidLine(1f).apply { color = DeviceRgb(0xec, 0xef, 0xf1) })

    private fun colorBackgroundGravity(gravity: Int): DeviceRgb {
        return when (gravity) {
            0 -> DeviceRgb(0x21, 0x96, 0xf3)
            1 -> DeviceRgb(0x4c, 0xaf, 0x50)
            2 -> DeviceRgb(0xff, 0xeb, 0x3b)
            3 -> DeviceRgb(0xfb, 0x8c, 0x00)
            4 -> DeviceRgb(0xb0, 0x00, 0x20)
            else -> DeviceRgb(0xff, 0xff, 0xff)
        }
    }
}