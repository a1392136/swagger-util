package com.xws.docdog.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xws.docdog.dto.MergedRegion;
import com.xws.docdog.dto.OperationDto;
import com.xws.docdog.dto.PathDto;
import com.xws.docdog.dto.SwaggerDto;
import io.swagger.models.Tag;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author xuwangshen
 * @date 2018年10月25日
 * @company ZiYun
 **/
public class SwaggerParseService {

    /**
     * 生成返回码说明文档
     *
     * @param swaggerApiJsonFile swagger生成json文件
     * @param saveFile           返回码存储excel文件
     * @throws IOException
     */
    public void generateApiDoc(File swaggerApiJsonFile, File saveFile) throws IOException {
        if (!swaggerApiJsonFile.exists()) {
            System.out.println("swagger文档不存在");
            return;
        }
        if (!saveFile.exists()) {
            saveFile.createNewFile();
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet catalogSheet = workbook.createSheet("目录");
        createTitleRow(workbook, catalogSheet);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SwaggerDto swagger = objectMapper.readValue(swaggerApiJsonFile, SwaggerDto.class);
        System.out.println("swagger文档读取完毕");
        //所有接口集合
        Map<String, PathDto> pathMap = swagger.getPaths();
        List<Tag> tagList = swagger.getTags();
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        CreationHelper createHelper = workbook.getCreationHelper();

        CellStyle hlinkStyle = workbook.createCellStyle();
        Font hlinkFont = workbook.createFont();
        hlinkFont.setUnderline(Font.U_SINGLE);
        hlinkFont.setColor(IndexedColors.BLUE.getIndex());
        hlinkStyle.setFont(hlinkFont);

        int rowNum = 1;
        for (int i = 0; i < tagList.size(); i++) {
            Tag tag = tagList.get(i);
            String tagName = tag.getName();
            MergedRegion mergedRegion = new MergedRegion();
            boolean startRowFlag = false;
            int startRowIndex = rowNum;
            int endRowIndex = rowNum;

            for (String key : pathMap.keySet()) {
                //当前接口对象
                PathDto path = pathMap.get(key);
                List<OperationDto> operationList = path.getOperations();
                //接口名称
                String name;
                for (OperationDto operation : operationList) {
                    name = operation.getSummary();
                    for (String t : operation.getTags()) {
                        if (tagName.equals(t)) {
                            //当前接口属于当前接口类别，创建一行记录
                            Row row = catalogSheet.createRow(rowNum);
                            if (!startRowFlag) {
                                //设置合并起始行
                                startRowFlag = true;
                                mergedRegion.setStartRowIndex(endRowIndex);
                                Cell typeCell = row.createCell(0);
                                //接口名称与接口类别一致，创建单元格
                                typeCell.setCellValue(tagName);
                                typeCell.setCellStyle(style);
                            }

                            //接口编号
                            String id = "API-" + i + "-" + endRowIndex;
                            //创建接口sheet
                            Sheet sheet = workbook.createSheet(id);
                            Cell cell1 = row.createCell(1);
                            cell1.setCellValue(id);
                            String coordinate = cell1.getAddress().formatAsString();
                            Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
                            hyperlink.setAddress("'" + sheet.getSheetName() + "'!" + coordinate);
                            cell1.setHyperlink(hyperlink);
                            cell1.setCellStyle(hlinkStyle);

                            //接口名称
                            Cell cell2 = row.createCell(2);
                            cell2.setCellValue(name);
                            //接口路径
                            Cell cell3 = row.createCell(3);
                            cell3.setCellValue(key);
                            //接口描述

                            createApiSheet(workbook, sheet, tagName, name, key);
                            endRowIndex++;
                            rowNum++;
                            break;
                        }
                    }
                }

            }

            if (startRowIndex + 1 != endRowIndex) {
                //验证是否需要合并行
                mergedRegion.setEndRowIndex(endRowIndex - 1);
                catalogSheet.addMergedRegion(new CellRangeAddress(mergedRegion.getStartRowIndex(), mergedRegion.getEndRowIndex(), mergedRegion.getStartCellIndex(), mergedRegion.getEndCellIndex()));
            }

        }
        setSizeColumn(catalogSheet);
        workbook.write(saveFile);
        System.out.println("返回码文档生成完成:"+saveFile.getPath());
    }

    /**
     * 编辑接口sheet
     */
    private void createApiSheet(Workbook workbook, Sheet sheet, String apiType, String apiName, String apiPath) {
        String[] title = {"接口类别", "接口名称", "PATH", "返回码说明"};
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 11);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(titleFont);
        style.setBorderBottom(BorderStyle.THIN); //下边框
        style.setBorderLeft(BorderStyle.THIN);//左边框
        style.setBorderTop(BorderStyle.THIN);//上边框
        style.setBorderRight(BorderStyle.THIN);//右边框

        CellStyle hlinkStyle = workbook.createCellStyle();
        Font hlinkFont = workbook.createFont();
        hlinkFont.setUnderline(Font.U_SINGLE);
        hlinkFont.setColor(IndexedColors.BLUE.getIndex());
        hlinkStyle.setFont(hlinkFont);

        MergedRegion mergedRegion = new MergedRegion();
        for (int i = 0; i < title.length; i++) {
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
            Cell cell2 = row.createCell(1);
            mergedRegion.setStartRowIndex(i);
            mergedRegion.setEndRowIndex(i);
            mergedRegion.setStartCellIndex(1);
            mergedRegion.setEndCellIndex(3);
            switch (title[i]) {
                case "接口类别":
                    cell2.setCellValue(apiType);
                    break;
                case "接口名称":
                    cell2.setCellValue(apiName);
                    break;
                case "PATH":
                    cell2.setCellValue(apiPath);
                    break;
            }
            if (i != 3) {
                sheet.addMergedRegion(new CellRangeAddress(mergedRegion.getStartRowIndex(), mergedRegion.getEndRowIndex(), mergedRegion.getStartCellIndex(), mergedRegion.getEndCellIndex()));
            }
        }

        CreationHelper createHelper = workbook.getCreationHelper();
        Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);

        int j = title.length;
        String[] codes = {"Code", "SubCode", "Message"};
        Row row = sheet.getRow(j - 1);
        for (int i = 0; i < codes.length; i++) {
            Cell cell = row.getCell(i + 1);
            if (cell == null) {
                cell = row.createCell(i + 1);
            }
            cell.setCellStyle(style);
            cell.setCellValue(codes[i]);
        }

        Cell cell = row.createCell(5);
        hyperlink.setAddress("'目录'!" + cell.getAddress().formatAsString());
        cell.setCellValue("返回目录");
        cell.setHyperlink(hyperlink);
        cell.setCellStyle(hlinkStyle);
        setSizeColumn(sheet);
    }

    private void createTitleRow(HSSFWorkbook workbook, Sheet catalogSheet) {
        if (workbook == null || catalogSheet == null) {
            return;
        }

        Row titleRow = catalogSheet.createRow(0);
        String[] title = {"接口类别", "接口编号", "接口名称", "PATH", "描述"};
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);

        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.WHITE.index);
        titleFont.setFontHeightInPoints((short) 14);
        style.setFont(titleFont);

        for (int i = 0; i < title.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
    }

    /**
     * 自适应宽度(中文支持)
     *
     * @param sheet
     */
    private static void setSizeColumn(Sheet sheet) {
        for (int columnNum = 0; columnNum <= 8; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                Row currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    Cell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == CellType.STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
    }

}
