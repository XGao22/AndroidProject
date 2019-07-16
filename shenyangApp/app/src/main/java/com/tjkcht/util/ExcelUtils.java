package com.tjkcht.util;

import com.tjkcht.pojo.ExcelPojo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by APPLE on 2019/2/12.
 */

public class ExcelUtils {
 public static List getXlsData(String  filePath)throws Exception{
 List list=new ArrayList<ExcelPojo>();
//创建输入流
  InputStream stream= new FileInputStream(filePath);
//获取Excel文件对象
 Workbook rwb=  Workbook.getWorkbook(stream);
  //获取文件的指定工作表，默认第一个
 Sheet sheet= rwb.getSheet(0);
  int sheetRows=sheet.getRows();
  int sheetColumns=sheet.getColumns();
for (int i = 1; i < sheet.getRows(); i++){
 ExcelPojo excelPojo=new ExcelPojo();
 for (int j = 0; j < sheet.getColumns(); j++) {
  String pid =sheet.getCell(1, i).getContents();
  excelPojo.setPid(pid);
  if(pid.toString().trim().equals("")){
   return list;
  }

 }
  list.add(excelPojo);

}
return list;
 }

}
