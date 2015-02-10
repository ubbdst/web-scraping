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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Hemed, Øyvind
 * @date 04-02-2015
 * @location : Universitetsbiblioteket i Bergen
 */
public class UserProfileMain {
    
     final static int TIMEOUT_MILLIS = 30000;
     final static String PAGE_URL = "https://uib.academia.edu/";
     final static String ACADEMIA_USERS_TEMP_FILE = "academiaUsersTemp.json" ;
     
     public static void main(String[] args) throws IOException, InterruptedException
     {   
            Gson gson = new Gson();
            AcademiaUserProfile academiaUser = new AcademiaUserProfile();
            //String systemTempDirectoryPath =  FilenameUtils.concat(FileUtils.getTempDirectoryPath(), ACADEMIA_USERS_TEMP_FILE );
            
            //Create temp file to store data for uib.academia.edu users
            File  tempFile = new File(ACADEMIA_USERS_TEMP_FILE , "UTF-8");
            
            PrintWriter fileWriter = new PrintWriter(tempFile);
            
           //Turn off loggings
            Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
            
            //Initialize web client object
            WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setTimeout(TIMEOUT_MILLIS);
            webClient.getOptions().setCssEnabled(false);
         
            HtmlPage academiaFrontPage = webClient.getPage(PAGE_URL);
            
             //Get a list of all the departments in uib.academia.edu 
             List<DomNode> departmentList = (DomNodeList)academiaFrontPage.getElementById("department_list").getElementsByTagName("a");
   
            for(DomNode aNode : departmentList)
            {  
               JsonObject userProperties = new JsonObject();
               //Convert a node into an html element for easier processing
               HtmlAnchor a  = (HtmlAnchor)aNode;
               
               if(a.getAttribute("href").contains(PAGE_URL))
               {
                   HtmlPage academiaUserPage = (HtmlPage)webClient.getPage(a.getAttribute("href")); 
                   webClient.waitForBackgroundJavaScript(TIMEOUT_MILLIS);
                   //TO DO : here we need to check if the page is completely loaded before proceeding ..
                   
                   //Get a list of all the user
                   List<HtmlElement> userNodeList = (ArrayList)academiaUserPage.getByXPath("//div[@id='user_list']/descendant-or-self::h3/a[contains(@href,'https://uib.academia.edu')]");
                   
                  for(HtmlElement aUserElement : userNodeList)
                  {  
                      //Get information about the user in Academia.edu
                      academiaUser.getUserProperties(webClient, aUserElement, userProperties, TIMEOUT_MILLIS);
                      
                     //Print the json representation of the user to the console
                      System.out.println(gson.toJson(userProperties) + "\n");
                      
                      //TO DO: Write to a file make it support UTF-8
                      fileWriter.write(gson.toJson(userProperties));
                      fileWriter.close();
                  }
   
               }
            }
            webClient.closeAllWindows();
      
         }
            
      }
    

