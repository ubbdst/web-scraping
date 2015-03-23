package com.ubb.webscraping;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ubb.webscraping.settings.ProfileSettings;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Hemed, Øyvind
 * @date 04-02-2015
 * @location : Universitetsbiblioteket i Bergen
 */
public class UserProfileMain {
    
     final static String PAGE_URL = "https://uib.academia.edu/";
     final static String ACADEMIA_USERS_TEMP_FILE = "academiaUsersTemp.txt" ;
     
     public static void main(String[] args) throws IOException, InterruptedException
     {   
            Gson gson = new Gson();
            AcademiaUserProfile academiaUser = new AcademiaUserProfile();
            FileOutputStream fileOut = new FileOutputStream("AcademiaUserProfile.xlsx");
            
            //Create XLS Workbook
            Workbook wb = new XSSFWorkbook();
         
            //Create user work sheet
            Sheet userSheet = wb.createSheet("User");
            AcademiaUserProfile.writeHeaderForUserSheet(userSheet);
            
            //Create publication work sheet
            Sheet publicationSheet = wb.createSheet("Publication");
            AcademiaUserProfile.writeHeaderForPublicationSheet(publicationSheet);

            
            //Create temp file to store data for uib.academia.edu users
            File  tempFile = new File(ACADEMIA_USERS_TEMP_FILE);
            
         try (Writer fileWriter = new BufferedWriter(new OutputStreamWriter(
                         new FileOutputStream(tempFile), "utf-8"))) {
             //Turn off loggings
             Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
             
             //Initialize web client object
             WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
             webClient.getOptions().setThrowExceptionOnScriptError(false);
             webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
             webClient.getOptions().setTimeout(ProfileSettings.TIMEOUT_MILLIS);
             webClient.getOptions().setCssEnabled(false);
             
             HtmlPage academiaFrontPage = webClient.getPage(PAGE_URL);
             
             //Get a list of all the departments in uib.academia.edu 
             List<DomNode> departmentList = (DomNodeList)academiaFrontPage.getElementById("department_list").getElementsByTagName("a");
             
             //Iterate through every department
             for(DomNode aNode : departmentList)
             {
                 JsonObject jsonObject = new JsonObject();
                 //Convert a node into an html element for easier processing
                 HtmlAnchor a  = (HtmlAnchor)aNode;
                 
                 if(a.getAttribute("href").contains(PAGE_URL))
                 {
                     HtmlPage academiaUserPage = (HtmlPage)webClient.getPage(a.getAttribute("href"));
                     webClient.waitForBackgroundJavaScript(ProfileSettings.TIMEOUT_MILLIS);
                     //TO DO : here we need to check if the page is completely loaded before proceeding ..
                     
                     //Get a list of all the user
                     List<HtmlElement> userNodeList = (ArrayList)academiaUserPage.getByXPath("//div[@id='user_list']/descendant-or-self::h3/a[contains(@href,'https://uib.academia.edu')]");
                     
                     //Iterate through each user in the department
                     for(HtmlElement aUserElement : userNodeList)
                     {   
                         //Get information about the user in Academia.edu
                         //academiaUser.getUserProperties(webClient, aUserElement, userProperties, TIMEOUT_MILLIS);
                          academiaUser.getUserProperties(webClient, aUserElement, jsonObject,userSheet, publicationSheet);
                          wb.write(fileOut);
                          ProfileSettings.LOCAL_USER_COUNT++;
                         
                         //Print the json representation of the user to the console
                         //System.out.println(gson.toJson(jsonObject) + "\n");
                         
                         //TO DO: Write to a file with UTF-8 encoding
                         //fileWriter.write(gson.toJson(jsonObject)); 
                     }
                     
                 } 
                 //Test for only one department for now.
                 fileWriter.close();
                 fileOut.close();  
             }         
             webClient.closeAllWindows();
           }
         }  
      }
    

