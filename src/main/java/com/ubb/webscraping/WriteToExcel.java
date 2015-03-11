/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubb.webscraping;

import com.ubb.webscraping.settings.ProfileSettings;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author Hemed
 */
public class WriteToExcel {
    
    public static void main(String [] args) throws IOException
    {
         FileOutputStream fileOut = new FileOutputStream("AcademiaUserProfile.xls");
         Workbook wb = new HSSFWorkbook();
         
         //Create user work sheet
         Sheet userSheet = wb.createSheet("User");
         
         Row headerRow = userSheet.createRow(0);
         
         headerRow.createCell(0)
                   .setCellValue(ProfileSettings.USER_ID);
         
         headerRow.createCell(1)
                   .setCellValue(ProfileSettings.USER_FULL_NAME);
         
         headerRow.createCell(2)
                   .setCellValue(ProfileSettings.TOTAL_PROFILE_VIEWS);
         
          headerRow.createCell(3)
                   .setCellValue(ProfileSettings.FOLLOWERS);
         
         headerRow.createCell(4)
                   .setCellValue(ProfileSettings.FOLLOWING);
         
        
         
         Row firstRow = userSheet.createRow(1);
                  
         firstRow.createCell(0)
                   .setCellValue(ProfileSettings.FOLLOWING);
         
  
         
         //Create publication sheet
         Sheet publicationSheet = wb.createSheet("Publication");
         
         wb.write(fileOut);
         fileOut.close();
    }
}
