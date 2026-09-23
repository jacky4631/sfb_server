package com.mailvor.modules.tk.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.tk.domain.MwReconcileDiff;
import com.mailvor.modules.tk.domain.MwReconcileLog;
import com.mailvor.modules.tk.service.MwReconcileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Api(tags = "商城：订单对账管理")
@RestController
@RequestMapping("/api/reconcile")
public class MwReconcileController {

    private static final Set<String> VALID_PLATFORMS = new HashSet<>(Arrays.asList("TB", "JD", "PDD", "DY"));

    private final MwReconcileService reconcileService;

    @Log("手动触发对账")
    @ApiOperation("手动触发对账")
    @PostMapping(value = "/trigger")
    @PreAuthorize("hasAnyRole('admin','RECONCILE')")
    public ResponseEntity trigger(@RequestBody Map<String, Object> params) {
        String dateStr = (String) params.get("date");
        List<String> platforms = (List<String>) params.get("platforms");
        if (dateStr == null || dateStr.isEmpty()) {
            return new ResponseEntity<>("日期不能为空", HttpStatus.BAD_REQUEST);
        }
        if (platforms == null || platforms.isEmpty()) {
            return new ResponseEntity<>("平台不能为空", HttpStatus.BAD_REQUEST);
        }

        List<String> invalidPlatforms = platforms.stream()
                .filter(p -> !VALID_PLATFORMS.contains(p))
                .collect(Collectors.toList());
        if (!invalidPlatforms.isEmpty()) {
            return new ResponseEntity<>("不支持的平台: " + invalidPlatforms + "，仅支持 TB/JD/PDD/DY",
                    HttpStatus.BAD_REQUEST);
        }

        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            return new ResponseEntity<>("日期格式错误，请使用yyyy-MM-dd", HttpStatus.BAD_REQUEST);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        if (date.after(today)) {
            return new ResponseEntity<>("对账日期不能晚于今天", HttpStatus.BAD_REQUEST);
        }

        if (reconcileService.existsReconcileForDate(date)) {
            return new ResponseEntity<>("该日期已完成对账，请勿重复触发。如需重新对账请先删除已有对账记录。",
                    HttpStatus.BAD_REQUEST);
        }

        MwReconcileLog log = reconcileService.triggerReconcile(date, platforms);
        return new ResponseEntity<>(log, HttpStatus.OK);
    }

    @Log("查询对账报告")
    @ApiOperation("查询对账报告")
    @GetMapping(value = "/report")
    @PreAuthorize("hasAnyRole('admin','RECONCILE')")
    public ResponseEntity getReport(Map<String, Object> criteria, Pageable pageable) {
        return new ResponseEntity<>(reconcileService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("查询差异明细")
    @ApiOperation("查询差异明细")
    @GetMapping(value = "/diff")
    @PreAuthorize("hasAnyRole('admin','RECONCILE')")
    public ResponseEntity getDiff(Map<String, Object> criteria, Pageable pageable) {
        return new ResponseEntity<>(reconcileService.queryDiffAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("处理差异")
    @ApiOperation("处理差异")
    @PutMapping(value = "/diff/{id}")
    @PreAuthorize("hasAnyRole('admin','RECONCILE')")
    public ResponseEntity handleDiff(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String action = body.get("action");
        String remark = body.get("remark");
        if (action == null || action.isEmpty()) {
            return new ResponseEntity<>("操作类型不能为空", HttpStatus.BAD_REQUEST);
        }
        reconcileService.handleDiff(id, action, remark);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("查询对账日志")
    @ApiOperation("查询对账日志")
    @GetMapping(value = "/logs")
    @PreAuthorize("hasAnyRole('admin','RECONCILE')")
    public ResponseEntity getLogs(Map<String, Object> criteria) {
        return new ResponseEntity<>(reconcileService.queryAll(criteria), HttpStatus.OK);
    }

    @Log("导出对账差异报告")
    @ApiOperation("导出对账差异报告Excel")
    @GetMapping(value = "/export/{logId}")
    @PreAuthorize("hasAnyRole('admin','RECONCILE')")
    public void exportDiff(@PathVariable Long logId, HttpServletResponse response) {
        try {
            List<MwReconcileDiff> diffs = reconcileService.exportDiffs(logId);

            String fileName = URLEncoder.encode("对账差异报告_" + logId + ".xlsx", "UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("差异明细");

                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);

                String[] headers = {"序号", "平台", "差异类型", "订单号", "平台金额", "本地金额",
                        "平台佣金", "本地佣金", "处理状态", "处理备注", "处理时间"};
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                int rowNum = 1;
                for (MwReconcileDiff diff : diffs) {
                    Row row = sheet.createRow(rowNum++);
                    createCell(row, 0, String.valueOf(diff.getId()), dataStyle);
                    createCell(row, 1, diff.getPlatform(), dataStyle);
                    createCell(row, 2, getDiffTypeName(diff.getDiffType()), dataStyle);
                    createCell(row, 3, diff.getOrderNo(), dataStyle);
                    createCell(row, 4, formatBD(diff.getPlatformAmount()), dataStyle);
                    createCell(row, 5, formatBD(diff.getLocalAmount()), dataStyle);
                    createCell(row, 6, formatBD(diff.getPlatformCommission()), dataStyle);
                    createCell(row, 7, formatBD(diff.getLocalCommission()), dataStyle);
                    createCell(row, 8, getHandleStatusName(diff.getHandleStatus()), dataStyle);
                    createCell(row, 9, diff.getHandleRemark() != null ? diff.getHandleRemark() : "", dataStyle);
                    createCell(row, 10, diff.getHandleTime() != null ? sdf.format(diff.getHandleTime()) : "", dataStyle);
                }

                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                OutputStream out = response.getOutputStream();
                workbook.write(out);
                out.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String formatBD(BigDecimal val) {
        return val != null ? val.toPlainString() : "";
    }

    private String getDiffTypeName(Integer diffType) {
        if (diffType == null) return "";
        switch (diffType) {
            case 1: return "平台缺失";
            case 2: return "本地多余";
            case 3: return "金额不一致";
            default: return "未知";
        }
    }

    private String getHandleStatusName(Integer status) {
        if (status == null) return "待处理";
        switch (status) {
            case 0: return "待处理";
            case 1: return "已忽略";
            case 2: return "已补录";
            case 3: return "已重试";
            default: return "未知";
        }
    }
}
