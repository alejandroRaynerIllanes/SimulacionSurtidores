package com.simulacion.bolivia.reports;

import com.simulacion.bolivia.engine.MotorSimulacion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xddf.usermodel.chart.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Clase responsable de la generación de reportes profesionales en Excel
 * utilizando la librería Apache POI.
 */
public class GeneradorExcel {

    public static void generarReporte(MotorSimulacion motor, String rutaArchivo) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            
            // --- CONFIGURACIÓN DE FUENTES ---
            Font fontBase = workbook.createFont();
            fontBase.setFontName("Segoe UI");
            fontBase.setFontHeightInPoints((short) 10);
            
            Font fontBold = workbook.createFont();
            fontBold.setFontName("Segoe UI");
            fontBold.setFontHeightInPoints((short) 10);
            fontBold.setBold(true);

            Font fontTitle = workbook.createFont();
            fontTitle.setFontName("Segoe UI");
            fontTitle.setFontHeightInPoints((short) 16);
            fontTitle.setBold(true);
            fontTitle.setColor(IndexedColors.WHITE.getIndex());

            // --- CONFIGURACIÓN DE COLORES ---
            XSSFColor darkBlue = new XSSFColor(new java.awt.Color(31, 78, 120), null);
            XSSFColor lightBlue = new XSSFColor(new java.awt.Color(217, 225, 242), null);
            XSSFColor zebraColor = new XSSFColor(new java.awt.Color(245, 247, 250), null);
            
            BorderStyle thinBorder = BorderStyle.THIN;
            short borderGrey = IndexedColors.GREY_40_PERCENT.getIndex();

            // --- ESTILOS DE CELDA ---
            // Título Principal
            XSSFCellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(fontTitle);
            titleStyle.setFillForegroundColor(darkBlue);
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Encabezado de Tablas
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            Font fontHeader = workbook.createFont();
            fontHeader.setFontName("Segoe UI");
            fontHeader.setFontHeightInPoints((short) 10);
            fontHeader.setBold(true);
            fontHeader.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(fontHeader);
            headerStyle.setFillForegroundColor(darkBlue);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(headerStyle, thinBorder, borderGrey);

            // Celdas de Datos Básicas
            XSSFCellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setFont(fontBase);
            setBorders(dataStyle, thinBorder, borderGrey);

            // Celdas de Datos Zebra (Filas pares)
            XSSFCellStyle zebraStyle = workbook.createCellStyle();
            zebraStyle.setFont(fontBase);
            zebraStyle.setFillForegroundColor(zebraColor);
            zebraStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorders(zebraStyle, thinBorder, borderGrey);

            // Formateadores numéricos
            DataFormat format = workbook.createDataFormat();

            XSSFCellStyle decimalStyle = workbook.createCellStyle();
            decimalStyle.cloneStyleFrom(dataStyle);
            decimalStyle.setDataFormat(format.getFormat("#,##0.00"));
            decimalStyle.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle integerStyle = workbook.createCellStyle();
            integerStyle.cloneStyleFrom(dataStyle);
            integerStyle.setDataFormat(format.getFormat("#,##0"));
            integerStyle.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.cloneStyleFrom(dataStyle);
            currencyStyle.setDataFormat(format.getFormat("Bs #,##0.00"));
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Formateadores Zebra
            XSSFCellStyle zebraDec = workbook.createCellStyle();
            zebraDec.cloneStyleFrom(zebraStyle);
            zebraDec.setDataFormat(format.getFormat("#,##0.00"));
            zebraDec.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle zebraInt = workbook.createCellStyle();
            zebraInt.cloneStyleFrom(zebraStyle);
            zebraInt.setDataFormat(format.getFormat("#,##0"));
            zebraInt.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle zebraCurr = workbook.createCellStyle();
            zebraCurr.cloneStyleFrom(zebraStyle);
            zebraCurr.setDataFormat(format.getFormat("Bs #,##0.00"));
            zebraCurr.setAlignment(HorizontalAlignment.RIGHT);

            // Estilos para Totales
            XSSFCellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setFont(fontBold);
            totalStyle.setFillForegroundColor(lightBlue);
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorders(totalStyle, thinBorder, borderGrey);

            XSSFCellStyle totalIntegerStyle = workbook.createCellStyle();
            totalIntegerStyle.cloneStyleFrom(totalStyle);
            totalIntegerStyle.setDataFormat(format.getFormat("#,##0"));
            totalIntegerStyle.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle totalDecimalStyle = workbook.createCellStyle();
            totalDecimalStyle.cloneStyleFrom(totalStyle);
            totalDecimalStyle.setDataFormat(format.getFormat("#,##0.00"));
            totalDecimalStyle.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle totalCurrencyStyle = workbook.createCellStyle();
            totalCurrencyStyle.cloneStyleFrom(totalStyle);
            totalCurrencyStyle.setDataFormat(format.getFormat("Bs #,##0.00"));
            totalCurrencyStyle.setAlignment(HorizontalAlignment.RIGHT);

            // --- HOJA 1: DASHBOARD ---
            XSSFSheet sheetDashboard = workbook.createSheet("Dashboard");
            sheetDashboard.setDisplayGridlines(true);

            // Título
            Row rowTitle = sheetDashboard.createRow(1);
            rowTitle.setHeightInPoints(40);
            Cell cellTitle = rowTitle.createCell(1);
            cellTitle.setCellValue("REPORTE GENERAL DE SIMULACIÓN");
            cellTitle.setCellStyle(titleStyle);
            sheetDashboard.addMergedRegion(new CellRangeAddress(1, 1, 1, 4));

            // Encabezados KPI
            Row rowHeader = sheetDashboard.createRow(3);
            rowHeader.setHeightInPoints(25);
            String[] headersKpi = {"Métrica / Indicador", "Subvencionado", "Internacional", "Total Consolidado"};
            for (int i = 0; i < headersKpi.length; i++) {
                Cell cell = rowHeader.createCell(i + 1);
                cell.setCellValue(headersKpi[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos Fila 5: Vehículos Atendidos
            Row rowVeh = sheetDashboard.createRow(4);
            rowVeh.setHeightInPoints(20);
            createCell(rowVeh, 1, "Vehículos Atendidos", dataStyle);
            createCell(rowVeh, 2, motor.getVehiculosAtendidosSubv(), integerStyle);
            createCell(rowVeh, 3, motor.getVehiculosAtendidosInt(), integerStyle);
            createFormulaCell(rowVeh, 4, "SUM(C5:D5)", totalIntegerStyle);

            // Datos Fila 6: Volumen Vendido (Litros)
            Row rowVol = sheetDashboard.createRow(5);
            rowVol.setHeightInPoints(20);
            createCell(rowVol, 1, "Volumen Vendido (Litros)", dataStyle);
            createCell(rowVol, 2, motor.getLitrosVendidosSubv(), decimalStyle);
            createCell(rowVol, 3, motor.getLitrosVendidosInt(), decimalStyle);
            createFormulaCell(rowVol, 4, "SUM(C6:D6)", totalDecimalStyle);

            // Datos Fila 7: Ingresos Brutos (Bs.)
            Row rowIng = sheetDashboard.createRow(6);
            rowIng.setHeightInPoints(20);
            createCell(rowIng, 1, "Ingresos Brutos (Bs.)", dataStyle);
            createCell(rowIng, 2, motor.getIngresosAcumuladosSubv(), currencyStyle);
            createFormulaCell(rowIng, 3, "D6*" + motor.getPrecioInternacional(), currencyStyle);
            createFormulaCell(rowIng, 4, "SUM(C7:D7)", totalCurrencyStyle);

            // Separador o espaciador
            sheetDashboard.createRow(7);

            // Fila 9: Tiempo Promedio de Espera (Global)
            Row rowEspera = sheetDashboard.createRow(8);
            rowEspera.setHeightInPoints(20);
            createCell(rowEspera, 1, "Tiempo Promedio de Espera (Global)", dataStyle);
            double totalVeh = motor.getVehiculosAtendidosSubv() + motor.getVehiculosAtendidosInt();
            double esperaMedia = totalVeh > 0 ? (motor.getTiempoEsperaTotal() / totalVeh) : 0.0;
            createCell(rowEspera, 2, esperaMedia, decimalStyle);
            sheetDashboard.addMergedRegion(new CellRangeAddress(8, 8, 2, 4));
            applyStyleToRange(sheetDashboard, 8, 8, 2, 4, decimalStyle);

            // Fila 10: Eventos de Desabastecimiento
            Row rowDes = sheetDashboard.createRow(9);
            rowDes.setHeightInPoints(20);
            createCell(rowDes, 1, "Eventos de Desabastecimiento", dataStyle);
            createCell(rowDes, 2, motor.getVecesDesabastecido(), integerStyle);
            sheetDashboard.addMergedRegion(new CellRangeAddress(9, 9, 2, 4));
            applyStyleToRange(sheetDashboard, 9, 9, 2, 4, integerStyle);

            // Fila 11: Tiempo de Simulación Transcurrido
            Row rowTime = sheetDashboard.createRow(10);
            rowTime.setHeightInPoints(20);
            createCell(rowTime, 1, "Tiempo de Simulación (Minutos)", dataStyle);
            createCell(rowTime, 2, motor.getReloj(), decimalStyle);
            sheetDashboard.addMergedRegion(new CellRangeAddress(10, 10, 2, 4));
            applyStyleToRange(sheetDashboard, 10, 10, 2, 4, decimalStyle);

            // Auto-ajustar columnas del Dashboard
            for (int col = 1; col <= 4; col++) {
                sheetDashboard.autoSizeColumn(col);
            }

            // --- HOJA 2: DETALLE DE OPERACIONES ---
            XSSFSheet sheetDetalle = workbook.createSheet("Detalle de Operaciones");
            sheetDetalle.setDisplayGridlines(true);

            // Encabezados Detalle
            Row rowDetHeader = sheetDetalle.createRow(0);
            rowDetHeader.setHeightInPoints(25);
            String[] headersDet = {
                "Tiempo Salida (min)", "Tipo Vehículo", "Estación de Servicio", 
                "Surtidor", "Espera (min)", "Litros Cargados (L)", "Importe Pagado (Bs.)"
            };
            for (int i = 0; i < headersDet.length; i++) {
                Cell cell = rowDetHeader.createCell(i);
                cell.setCellValue(headersDet[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar Datos Detalle
            List<RegistroOperacion> historial = motor.getHistorialOperaciones();
            int rIndex = 1;
            for (RegistroOperacion reg : historial) {
                Row row = sheetDetalle.createRow(rIndex);
                row.setHeightInPoints(18);
                
                boolean isPair = (rIndex % 2 == 0);
                XSSFCellStyle currentStyle = isPair ? zebraStyle : dataStyle;
                XSSFCellStyle currentDec = isPair ? zebraDec : decimalStyle;
                XSSFCellStyle currentCurr = isPair ? zebraCurr : currencyStyle;

                createCell(row, 0, reg.getTiempoSalida(), currentDec);
                createCell(row, 1, reg.getTipoVehiculo(), currentStyle);
                createCell(row, 2, reg.getNombreEstacion(), currentStyle);
                createCell(row, 3, reg.getIdSurtidor(), currentStyle);
                createCell(row, 4, reg.getTiempoEsperaMin(), currentDec);
                createCell(row, 5, reg.getLitrosCargados(), currentDec);
                createCell(row, 6, reg.getMontoPagadoBs(), currentCurr);
                
                rIndex++;
            }

            // Auto-ajustar columnas del Detalle
            for (int col = 0; col < headersDet.length; col++) {
                sheetDetalle.autoSizeColumn(col);
            }

            // --- HOJA 3: ANÁLISIS GRÁFICO ---
            XSSFSheet sheetGraficos = workbook.createSheet("Análisis Gráfico");
            sheetGraficos.setDisplayGridlines(true);
            XSSFDrawing drawing = sheetGraficos.createDrawingPatriarch();

            // 1. Gráfico de Pastel (Distribución de Ingresos)
            XSSFClientAnchor anchorPie = drawing.createAnchor(0, 0, 0, 0, 1, 1, 7, 15); // B2 a G15 (Col B=1, Row 2=1, Col H=7, Row 16=15)
            XSSFChart chartPie = drawing.createChart(anchorPie);
            chartPie.setTitleText("Distribución de Ingresos (Bs)");
            chartPie.setTitleOverlay(false);

            XDDFChartLegend legendPie = chartPie.getOrAddLegend();
            legendPie.setPosition(LegendPosition.RIGHT);

            XDDFDataSource<String> catPie = XDDFDataSourcesFactory.fromArray(new String[]{"Subvencionado", "Internacional"});
            double ingresosInt = motor.getLitrosVendidosInt() * motor.getPrecioInternacional();
            XDDFNumericalDataSource<Double> valPie = XDDFDataSourcesFactory.fromArray(new Double[]{
                motor.getIngresosAcumuladosSubv(),
                ingresosInt
            });

            XDDFPieChartData pieData = (XDDFPieChartData) chartPie.createData(ChartTypes.PIE, null, null);
            pieData.addSeries(catPie, valPie);
            chartPie.plot(pieData);

            // 2. Gráfico de Barras (Vehículos Atendidos)
            XSSFClientAnchor anchorBar = drawing.createAnchor(0, 0, 0, 0, 8, 1, 14, 15); // I2 a N15 (Col I=8, Row 2=1, Col O=14, Row 16=15)
            XSSFChart chartBar = drawing.createChart(anchorBar);
            chartBar.setTitleText("Volumen de Vehículos Atendidos");
            chartBar.setTitleOverlay(false);

            XDDFChartLegend legendBar = chartBar.getOrAddLegend();
            legendBar.setPosition(LegendPosition.RIGHT);

            XDDFCategoryAxis xAxis = chartBar.createCategoryAxis(AxisPosition.BOTTOM);
            xAxis.setTitle("Tipo de Surtidor");
            XDDFValueAxis yAxis = chartBar.createValueAxis(AxisPosition.LEFT);
            yAxis.setTitle("Cantidad");
            yAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            XDDFDataSource<String> catBar = XDDFDataSourcesFactory.fromArray(new String[]{"Subvencionado", "Internacional"});
            XDDFNumericalDataSource<Double> valBar = XDDFDataSourcesFactory.fromArray(new Double[]{
                (double) motor.getVehiculosAtendidosSubv(),
                (double) motor.getVehiculosAtendidosInt()
            });

            XDDFBarChartData barData = (XDDFBarChartData) chartBar.createData(ChartTypes.BAR, xAxis, yAxis);
            barData.setBarDirection(BarDirection.COL); // Vertical bars
            barData.addSeries(catBar, valBar);
            chartBar.plot(barData);

            // --- ESCRIBIR ARCHIVO con fallback si está bloqueado/abierto en otro proceso ---
            java.io.File file = new java.io.File(rutaArchivo);
            String parent = file.getParent();
            String name = file.getName();
            String baseName = name;
            String extension = "";
            
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                baseName = name.substring(0, dotIndex);
                extension = name.substring(dotIndex);
            }
            
            java.io.FileOutputStream fos = null;
            String finalPath = rutaArchivo;
            int counter = 1;
            
            while (fos == null && counter <= 100) {
                try {
                    fos = new java.io.FileOutputStream(finalPath);
                } catch (java.io.FileNotFoundException e) {
                    java.io.File targetFile = new java.io.File(finalPath);
                    if (targetFile.exists()) {
                        String newName = baseName + " (" + counter + ")" + extension;
                        finalPath = (parent != null) ? new java.io.File(parent, newName).getPath() : newName;
                        counter++;
                    } else {
                        throw e;
                    }
                }
            }
            
            if (fos != null) {
                try (java.io.FileOutputStream finalFos = fos) {
                    workbook.write(finalFos);
                }
                System.out.println("Reporte Excel generado con éxito en: " + finalPath);
                
                // Mostrar diálogo informativo si no es modo headless
                if (!java.awt.GraphicsEnvironment.isHeadless()) {
                    final String msgPath = finalPath;
                    final int tries = counter;
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        String message = "El reporte de simulación se ha exportado con éxito a:\n" + msgPath;
                        if (tries > 1) {
                            message += "\n\nNota: El archivo original estaba abierto/bloqueado, por lo que se guardó una copia numerada.";
                        }
                        javax.swing.JOptionPane.showMessageDialog(
                            null, 
                            message, 
                            "Reporte Excel Generado", 
                            javax.swing.JOptionPane.INFORMATION_MESSAGE
                        );
                    });
                }
            } else {
                throw new java.io.IOException("No se pudo escribir el reporte Excel tras múltiples intentos.");
            }
            
        } catch (IOException e) {
            System.err.println("Error al generar el reporte Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- MÉTODOS AUXILIARES ---
    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void createCell(Row row, int column, double value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void createFormulaCell(Row row, int column, String formula, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellFormula(formula);
        cell.setCellStyle(style);
    }

    private static void setBorders(CellStyle style, BorderStyle border, short color) {
        style.setBorderTop(border);
        style.setTopBorderColor(color);
        style.setBorderBottom(border);
        style.setBottomBorderColor(color);
        style.setBorderLeft(border);
        style.setLeftBorderColor(color);
        style.setBorderRight(border);
        style.setRightBorderColor(color);
    }

    private static void applyStyleToRange(XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol, CellStyle style) {
        for (int r = startRow; r <= endRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) row = sheet.createRow(r);
            for (int c = startCol; c <= endCol; c++) {
                Cell cell = row.getCell(c);
                if (cell == null) cell = row.createCell(c);
                cell.setCellStyle(style);
            }
        }
    }
}
