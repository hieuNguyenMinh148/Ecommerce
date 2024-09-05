package com.ecommerce.admin.controller;

import com.aspose.cells.Cells;
import com.aspose.cells.ChartType;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Worksheet;
import com.ecommerce.library.model.Dashboard;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.DashboardService;
import com.ecommerce.library.service.ProductService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class AdminController {
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ProductService productService;

    @GetMapping("/export-excel")
    public void exportExcel(HttpServletResponse response) {
        // Lấy danh sách Dashboard từ service
        List<Dashboard> dashboards = dashboardService.findDashboardForExport();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // Tạo workbook mới
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dashboard Data");

        // Create title row
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Dashboard Data");
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 25);
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        // Tạo header row
        Row headerRow = sheet.createRow(1);
        headerRow.createCell(0).setCellValue("#Order");
        headerRow.createCell(1).setCellValue("Buyer");
        headerRow.createCell(2).setCellValue("User");
        headerRow.createCell(3).setCellValue("Payment Method");
        headerRow.createCell(4).setCellValue("Date Paid");
        headerRow.createCell(5).setCellValue("Payment Price");

        // Thêm dữ liệu từ danh sách Dashboard vào các dòng tiếp theo
        int rowNum = 2;
        double totalPayment = 0.0;
        for (Dashboard dashboard : dashboards) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dashboard.getOrderId());
            row.createCell(1).setCellValue(dashboard.getBuyer());
            row.createCell(2).setCellValue(dashboard.getUser());
            row.createCell(3).setCellValue(dashboard.getPaymentMethod());
            row.createCell(4).setCellValue(dateFormatter.format(dashboard.getDatePaid())
                    .toString()); // Convert Date thành String
            row.createCell(5).setCellValue(dashboard.getPaymentPrice() + "$");

            totalPayment += dashboard.getPaymentPrice();
        }

        // Tạo khung viền cho bảng
        for (int i = 1; i <= rowNum; i++) {
            Row currentRow = sheet.getRow(i);
            if (currentRow != null) {
                for (int j = 0; j < 6; j++) {
                    if (currentRow.getCell(j) == null) {
                        currentRow.createCell(j);
                    }
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    currentRow.getCell(j).setCellStyle(cellStyle); // Áp dụng CellStyle cho các ô
                }
            }
        }

        // Tạo font in đậm
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);

        // Tạo CellStyle và áp dụng font in đậm cho header row
        CellStyle boldCellStyle = workbook.createCellStyle();
        boldCellStyle.setFont(boldFont);
        boldCellStyle.setBorderTop(BorderStyle.THIN);
        boldCellStyle.setBorderBottom(BorderStyle.THIN);
        boldCellStyle.setBorderLeft(BorderStyle.THIN);
        boldCellStyle.setBorderRight(BorderStyle.THIN);

        // Đặt chữ in đậm cho các ô trong header row
        for (int i = 0; i < 6; i++) {
            headerRow.getCell(i).setCellStyle(boldCellStyle);
        }

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 4));
        // Tạo hàng tổng ở cuối bảng
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(0).setCellValue("Total");

        // Tạo CellStyle cho hàng tổng
        CellStyle totalRowStyle = workbook.createCellStyle();
        Font totalRowFont = workbook.createFont();
        totalRowFont.setBold(true);
        totalRowFont.setFontHeight((short) (13 * 20));// Thiết lập font in đậm
        totalRowStyle.setFont(totalRowFont);
        totalRow.getCell(0).setCellStyle(totalRowStyle); // Áp dụng CellStyle cho ô "Total"

        // Ghi giá trị tổng vào ô "Payment Price" và áp dụng CellStyle
        totalRow.createCell(5).setCellValue(totalPayment + "$");
        totalRow.getCell(5).setCellStyle(totalRowStyle); // Áp dụng CellStyle cho ô "Payment Price"

        for (int i = 0; i < 6; i++) {
            if (i > 0) {
                sheet.setColumnWidth(i, 20 * 256);
            }
        }

        // Thiết lập response header
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=dashboard_data.xlsx");

        // Ghi workbook vào OutputStream của response
        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/export-inventory")
    public void exportInventory(HttpServletResponse response) {
        List<Product> products = productService.getListForExport();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // Tạo workbook mới
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventory Report");

        // Create title row
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Inventory Report");
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 25);
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        // Tạo header row
        Row headerRow = sheet.createRow(1);
        headerRow.createCell(0).setCellValue("#Id");
        headerRow.createCell(1).setCellValue("Product Name");
        headerRow.createCell(2).setCellValue("Quantity");
        headerRow.createCell(3).setCellValue("Price");
        headerRow.createCell(4).setCellValue("Sale");
        headerRow.createCell(5).setCellValue("Discounted Price");

        CellStyle borderCellStyle = workbook.createCellStyle();
        borderCellStyle.setBorderTop(BorderStyle.THIN);
        borderCellStyle.setBorderBottom(BorderStyle.THIN);
        borderCellStyle.setBorderLeft(BorderStyle.THIN);
        borderCellStyle.setBorderRight(BorderStyle.THIN);
        // Thêm dữ liệu từ danh sách Dashboard vào các dòng tiếp theo
        int rowNum = 2;
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            Cell quantityCell = row.createCell(2);
            quantityCell.setCellValue(product.getCurrentQuantity());
            row.createCell(3).setCellValue(product.getCostPrice() + "$");
            row.createCell(4).setCellValue(product.getSalePrice() + "%");
            row.createCell(5).setCellValue(product.getDiscountedPrice() + "$");

            for (int i = 0; i < 6; i++) {
                Cell currentCell = row.getCell(i);
                if (currentCell == null) {
                    currentCell = row.createCell(i);
                }
                currentCell.setCellStyle(borderCellStyle);
            }

            if (product.getCurrentQuantity() <= 25) {
                CellStyle quantityCellStyle = workbook.createCellStyle();
                quantityCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                quantityCellStyle.setBorderTop(BorderStyle.THIN);
                quantityCellStyle.setBorderBottom(BorderStyle.THIN);
                quantityCellStyle.setBorderLeft(BorderStyle.THIN);
                quantityCellStyle.setBorderRight(BorderStyle.THIN);
                quantityCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                quantityCell.setCellStyle(quantityCellStyle);
            } else if (product.getCurrentQuantity() <= 50) {
                CellStyle quantityCellStyle = workbook.createCellStyle();
                quantityCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                quantityCellStyle.setBorderTop(BorderStyle.THIN);
                quantityCellStyle.setBorderBottom(BorderStyle.THIN);
                quantityCellStyle.setBorderLeft(BorderStyle.THIN);
                quantityCellStyle.setBorderRight(BorderStyle.THIN);
                quantityCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                quantityCell.setCellStyle(quantityCellStyle);
            }
        }

        // Tạo font in đậm
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);

        // Tạo CellStyle và áp dụng font in đậm cho header row
        CellStyle boldCellStyle = workbook.createCellStyle();
        boldCellStyle.setFont(boldFont);
        boldCellStyle.setBorderTop(BorderStyle.THIN);
        boldCellStyle.setBorderBottom(BorderStyle.THIN);
        boldCellStyle.setBorderLeft(BorderStyle.THIN);
        boldCellStyle.setBorderRight(BorderStyle.THIN);

        // Đặt chữ in đậm cho các ô trong header row
        for (int i = 0; i < 6; i++) {
            headerRow.getCell(i).setCellStyle(boldCellStyle);
        }

        for (int i = 0; i < 6; i++) {
            if (i == 1) {
                sheet.setColumnWidth(i, 40 * 256);
            } else if (i > 1) {
                sheet.setColumnWidth(i, 15 * 256);
            }
        }

        // Thiết lập response header
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=inventory_report.xlsx");

        // Ghi workbook vào OutputStream của response
        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/export-chart")
    public void exportChart(HttpServletResponse response, @RequestParam String type) throws Exception {
        // Lấy danh sách Dashboard từ service
        List<Dashboard> dashboards = dashboardService.findDashboardForExport();

        // Khởi tạo danh sách thống kê
        List<String> categories = new ArrayList<>();
        List<Double> revenueList = new ArrayList<>();

        // Thống kê dữ liệu tùy theo type
        if ("week".equalsIgnoreCase(type)) {
            // Thống kê theo tuần
            SimpleDateFormat weekFormat = new SimpleDateFormat("w-yyyy");
            for (Dashboard dashboard : dashboards) {
                Date datePaid = dashboard.getDatePaid();
                String week = weekFormat.format(datePaid);
                int index = categories.indexOf(week);
                if (index == -1) {
                    categories.add(week);
                    revenueList.add(dashboard.getPaymentPrice());
                } else {
                    revenueList.set(index, revenueList.get(index) + dashboard.getPaymentPrice());
                }
            }
        } else if ("month".equalsIgnoreCase(type)) {
            // Thống kê theo tháng
            for (Dashboard dashboard : dashboards) {
                Date datePaid = dashboard.getDatePaid();
                String month = new SimpleDateFormat("MMM yyyy").format(datePaid);
                int index = categories.indexOf(month);
                if (index == -1) {
                    categories.add(month);
                    revenueList.add(dashboard.getPaymentPrice());
                } else {
                    revenueList.set(index, revenueList.get(index) + dashboard.getPaymentPrice());
                }
            }
        } else if ("year".equalsIgnoreCase(type)) {
            // Thống kê theo năm
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            for (Dashboard dashboard : dashboards) {
                Date datePaid = dashboard.getDatePaid();
                String year = yearFormat.format(datePaid);
                int index = categories.indexOf(year);
                if (index == -1) {
                    categories.add(year);
                    revenueList.add(dashboard.getPaymentPrice());
                } else {
                    revenueList.set(index, revenueList.get(index) + dashboard.getPaymentPrice());
                }
            }
        } else {
            // Nếu type không hợp lệ, trả về lỗi
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid type parameter.");
            return;
        }

        // Tạo workbook mới
        com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook();
        Worksheet sheet = workbook.getWorksheets().get(0);

        // Đưa dữ liệu vào sheet
        Cells cells = sheet.getCells();
        cells.get("A1").setValue(type.toUpperCase());
        cells.get("B1").setValue("Revenue");
        for (int i = 0; i < categories.size(); i++) {
            cells.get("A" + (i + 2)).setValue(categories.get(i));
            cells.get("B" + (i + 2)).setValue(revenueList.get(i));
        }

        // Tạo biểu đồ cột
        int chartIndex = sheet.getCharts().add(ChartType.COLUMN, 3, 3, 25, 15);
        com.aspose.cells.Chart chart = sheet.getCharts().get(chartIndex);
        chart.getNSeries().add("B2:B" + (categories.size() + 1), true);
        chart.getNSeries().setCategoryData("A2:A" + (categories.size() + 1));
        chart.getTitle().setText("Revenue by " + type.substring(0, 1).toUpperCase() + type.substring(1));

        // Thiết lập response header
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=revenue_data.xlsx");

        // Ghi workbook vào OutputStream của response
        workbook.save(response.getOutputStream(), SaveFormat.XLSX);
        workbook.dispose();
    }

}
