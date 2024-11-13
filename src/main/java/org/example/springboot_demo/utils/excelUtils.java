package org.example.springboot_demo.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.springboot_demo.dtos.AttendanceStatisticsDto;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class excelUtils {
    private static Workbook createWorkbook(List<AttendanceStatisticsDto> statistics, String[] headers) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance Overview");
        Row headerRow = sheet.createRow(0);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }
        int rowIdx = 1;
        for (AttendanceStatisticsDto statistic : statistics) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(statistic.getName());
            row.createCell(1).setCellValue(statistic.getWorkingDays());
            row.createCell(2).setCellValue(statistic.getPaidLeaveDays());
            row.createCell(3).setCellValue(statistic.getUnpaidLeaveDays());
            row.createCell(4).setCellValue(statistic.getLateDays());
            row.createCell(5).setCellValue(statistic.getLeaveEarlyDays());
            row.createCell(6).setCellValue(statistic.getSumLateArrivalTime());
            row.createCell(7).setCellValue(statistic.getSumEarlyLeavingTime());
        }
        return workbook;
    }

    public static void exportToExcel(HttpServletResponse response, List<AttendanceStatisticsDto> statistics) throws IOException {
        String[] headers = {
                "Name", "Working Days", "Paid Leave Days", "Unpaid Leave Days",
                "Late Days", "Leave Early Days", "Total Minutes Late",
                "Total Minutes Left Early"
        };
        Workbook workbook = createWorkbook(statistics, headers);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=employee_attendance.xlsx");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    public static ByteArrayResource exportToExcelAndAttachToEmail(List<AttendanceStatisticsDto> statistics) throws IOException {
        String[] headers = {
                "Name", "Working Days", "Paid Leave Days", "Unpaid Leave Days",
                "Late Days", "Leave Early Days", "Total Minutes Late",
                "Total Minutes Left Early"
        };
        Workbook workbook = createWorkbook(statistics, headers);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }
}
