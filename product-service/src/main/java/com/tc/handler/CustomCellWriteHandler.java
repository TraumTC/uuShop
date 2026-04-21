package com.tc.handler;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCellWriteHandler extends AbstractColumnWidthStyleStrategy {

    private final Map<Integer, Map<Integer, Integer>> CACHE = new ConcurrentHashMap<>();

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        // 添加空检查，避免空指针异常
        if (writeSheetHolder == null || cell == null) {
            return;
        }
        
        // 替换 CollectionUtils.isEmpty，直接用原生判断
        boolean needSetWidth = isHead || (cellDataList != null && !cellDataList.isEmpty());
        if (!needSetWidth) {
            return;
        }

        // 获取当前 Sheet 的列宽缓存
        Map<Integer, Integer> maxColumnWidthMap = CACHE.computeIfAbsent(writeSheetHolder.getSheetNo(), k -> new HashMap<>());

        // 计算当前单元格内容所需宽度
        Integer columnWidth = this.dataLength(cellDataList, cell, isHead);
        if (columnWidth < 0) {
            return;
        }

        // 限制最大列宽（Excel 最大为 255 字符宽度）
        if (columnWidth > 255) {
            columnWidth = 255;
        }

        // 仅保留该列的最大宽度，避免重复设置
        Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());
        if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
            maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
            // Excel 列宽单位：字符数 × 256
            writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
        }
    }

    /**
     * 计算单元格内容长度（适配 WriteCellData 类型）
     */
    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        try {
            if (isHead) {
                // 表头：直接按字符数计算，解决中文过宽问题
                String headValue = cell.getStringCellValue();
                return getSafeLength(headValue);
            } else {
                WriteCellData<?> cellData = cellDataList.get(0);
                CellDataTypeEnum type = cellData.getType();
                if (type == null) {
                    return 10; // 默认宽度兜底
                }

                return switch (type) {
                    case STRING -> getSafeLength(cellData.getStringValue());
                    case BOOLEAN -> 6; // "true"/"false" 固定宽度
                    case NUMBER -> getSafeLength(cellData.getNumberValue() != null ? cellData.getNumberValue().toString() : "");
                    default -> 10; // 其他类型默认宽度
                };
            }
        } catch (Exception e) {
            // 异常兜底，避免导出失败
            return 10;
        }
    }

    /**
     * 安全计算字符串长度，处理 null/空值 + 适配中英文
     */
    private int getSafeLength(String content) {
        if (content == null || content.isEmpty()) {
            return 2; // 空值默认 2 个字符宽度
        }
        // 直接按字符数计算，避免 getBytes() 导致的中文宽度异常
        return content.length() + 2; // +2 作为内边距，防止文字贴边
    }
}