package com.gvccracing.android.tttimer.Utilities;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelUtils {

  private String inputFile;

  public void setInputFile(String inputFile) {
    this.inputFile = inputFile;
  }

  public void read() throws IOException  {
    File inputWorkbook = new File(inputFile);
    Workbook w;
    try {
      w = Workbook.getWorkbook(inputWorkbook);
      // Get the first sheet
      Sheet sheet = w.getSheet(0);
      // Loop over first 10 column and lines

      for (int j = 0; j < sheet.getColumns(); j++) {
        for (int i = 0; i < sheet.getRows(); i++) {
          Cell cell = sheet.getCell(j, i);
          CellType type = cell.getType();
          if (type == CellType.LABEL) {
            Log.i("Read", "I got a label "
                + cell.getContents());
          }

          if (type == CellType.NUMBER) {
            Log.i("Read", "I got a number "
                + cell.getContents());
          }

        }
      }
    } catch (BiffException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    ExcelUtils test = new ExcelUtils();
    File rootDir = Environment.getExternalStorageDirectory();
    test.setInputFile(rootDir.toString() + "/excel/test.xls");
    test.read();
  }

} 
