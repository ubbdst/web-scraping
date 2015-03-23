
package com.ubb.webscraping;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ubb.webscraping.settings.ProfileSettings;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author Hemed
 */
public class AcademiaUserProfile {
    
         /** 
         * A method to get extra user information from academia.edu
         * @param webClient
         * @param aUserElement
         * @param userProperties
         * @throws java.io.IOException
          */
           public void getUserProperties(WebClient webClient, HtmlElement aUserElement , JsonObject userProperties, Sheet userSheet, Sheet publicationSheet) throws IOException
           {              
            HtmlPage page = (HtmlPage)webClient.getPage(aUserElement.getAttribute("href"));
            webClient.waitForBackgroundJavaScript(ProfileSettings.TIMEOUT_MILLIS);
            //TO DO: We need to make sure the page is totally loaded before proceeding..
            HtmlElement numberOfFollowing = (HtmlElement)page.getFirstByXPath("//div[@id='following']/a/h3");
            HtmlDivision totalViews = (HtmlDivision)page.getFirstByXPath("//div[@class='stat total-views']/a/div[@class='count']");
            HtmlDivision numberOfFolower = (HtmlDivision)page.getFirstByXPath("//div[@class='stat followers']/a/div[@class='count']");
            List<HtmlElement> publicationCount = (ArrayList)page.getByXPath("//div[div[@class='right-icons']/div[@class='views']/span[@class='view-count-widget']/strong[@class='view-count']]");
            List<DomAttr> coauthorNameList = (ArrayList)page.getByXPath("//div[@id='coauthors']/ul[@class='facepile']/a/img/@data-original-title");
            List<DomAttr> coauthorURIList = (ArrayList)page.getByXPath("//div[@id='coauthors']/ul[@class='facepile']/a/@href");

            JsonArray publications = new JsonArray();
            
            try {
                
                //We are doing this because number of followings, X is returned as "X Following" 
                //and we are not interested with the String part of the result 
                 String [] numberOfFollowings  = numberOfFollowing.asText().split(" ");
                 
                 String userURI = URLDecoder.decode(aUserElement.getAttribute("href"), "UTF-8");
                 String fullName = aUserElement.asText();
                 String firstName = extractFirstName(fullName);
                 String lastName = extractLastName(fullName);
                 String coAuthors = getCoAuthorURI(coauthorURIList);
                
                 String profileViews = totalViews.asText();
                 String followings =   numberOfFollowings[0];
                 String followers = numberOfFolower.asText();
                
                //Build user object                
                 userProperties.addProperty(ProfileSettings.USER_ID , userURI);
                 userProperties.addProperty(ProfileSettings.FIRST_NAME , firstName);
                 userProperties.addProperty(ProfileSettings.LAST_NAME , lastName);
                 userProperties.addProperty(ProfileSettings.USER_FULL_NAME , fullName);
                 userProperties.addProperty(ProfileSettings.TOTAL_PROFILE_VIEWS , profileViews);
                 userProperties.addProperty(ProfileSettings.FOLLOWING , followings );
                 userProperties.addProperty(ProfileSettings.FOLLOWERS , followers);
                 
                 
                //For every user, create it's own row.
                 Row userRow = userSheet.createRow(ProfileSettings.LOCAL_USER_COUNT);
                 
                 //Append user URI
                 userRow.createCell(0)
                           .setCellValue(userURI);
                 
                 //Insert user first name
                 userRow.createCell(1)
                           .setCellValue(firstName);
                 
                 //Insert user last name
                 userRow.createCell(2)
                           .setCellValue(lastName);
                 
                 //Insert user full Name
                 userRow.createCell(3)
                           .setCellValue(fullName);
                 
                 //Insert total profile views
                 userRow.createCell(4)
                           .setCellValue(profileViews);
                 
                 //Insert number of followigs
                 userRow.createCell(5)
                           .setCellValue(followings);
                 
                 //Insert number of followers
                 userRow.createCell(6)
                           .setCellValue(followers);
                         
                 //Insert co URI list of coauthors
                 userRow.createCell(7)
                           .setCellValue(coAuthors);

                for (HtmlElement element : publicationCount) 
                {
                    //Create publication JSON object
                    JsonObject publicationProperties = new JsonObject();
                    
                    String dataCountId =  element.getAttribute("data-work_id");
                    String xpathExp = "//a[@data-container='.work_" + dataCountId + "']/preceding-sibling::a[1]";
                    String xpathPublicationViewCount = "//strong[@class='view-count' and ancestor::div[@data-work_id='" + dataCountId + "']]";
                    
                    HtmlElement publicationViewCount = (HtmlElement)page.getFirstByXPath(xpathPublicationViewCount);
                    HtmlElement link = (HtmlElement)page.getFirstByXPath(xpathExp);
                    
                    String publicationURI = URLDecoder.decode(link.getAttribute("href") , "UTF-8");
                    String name = link.asText();
                    String views = publicationViewCount.asText();
                    
                    publicationProperties.addProperty(ProfileSettings.PUBLICATION_ID, publicationURI);
                    publicationProperties.addProperty(ProfileSettings.PUBLICATION_NAME, name);
                    publicationProperties.addProperty(ProfileSettings.PUBLICATION_VIEWS, views);
                    publications.add(publicationProperties); 
                    
                    
                    //Create a new row for every publication
                     Row publicationRow = publicationSheet.createRow(ProfileSettings.LOCAL_PUBLICATION_COUNT);
                     
                     //publication URI
                     publicationRow.createCell(1)
                               .setCellValue(link.getAttribute("href"));
                     
                     //Get user Id (URI) as a foreign key for each publication
                     publicationRow.createCell(0)
                           .setCellValue(userURI);
                     
                     
                     //Publication Title
                     publicationRow.createCell(2)
                               .setCellValue(link.asText());

                     //Publication views
                     publicationRow.createCell(3)
                               .setCellValue(Integer.parseInt(publicationViewCount.asText()));

                     ProfileSettings.LOCAL_PUBLICATION_COUNT++;
                    
                }
            }
             catch(Exception ex){ex.printStackTrace();}
            
             finally{
     
             if(!publications.isJsonNull())
                userProperties.add(ProfileSettings.PUBLICATIONS, publications);
            }
        }
           
        public String extractLastName(String fullName)
        {
            String[] s = fullName.split(" ");
            
            return s[s.length - 1];
            
        }
        
        
         public String extractFirstName(String fullName)
        {
            String[] s = fullName.split(" ");
            String firstName = "";
            int count = 0;
            
               for (String item : s )  { 
                   if(count < s.length -1)
                       firstName = firstName + item + " ";
                   count++;
               }
            return firstName.trim();
        }
        
       /** Concatenate the list of co-authors URIs
        *  Some of the characters in the URI were encoded, 
        *  hence URIs have to be decoded back to UTF-8 for readability.
        */
        
       private String getCoAuthorURI(List<DomAttr> list) throws UnsupportedEncodingException
       {
           String s = "";
           String seperator = ", ";
           int count = 0;
           for (DomAttr item : list){
               if(count == list.size() -1) seperator = " ";
               s  +=  URLDecoder.decode(item.getValue(), "UTF-8") + seperator;
               count++;

              }
           
           return s;
       }
       
       
       public static void writeHeaderForUserSheet(Sheet userSheet){
                 //Create a first row.
                 Row userRow = userSheet.createRow(0);
                 
                 //Append user URI
                 userRow.createCell(0)
                           .setCellValue(ProfileSettings.USER_ID);
                 
                 //Insert user first name
                 userRow.createCell(1)
                           .setCellValue(ProfileSettings.FIRST_NAME);
                 
                 //Insert user last name
                 userRow.createCell(2)
                           .setCellValue(ProfileSettings.LAST_NAME);
                 
                 //Insert user full Name
                 userRow.createCell(3)
                           .setCellValue(ProfileSettings.USER_FULL_NAME);
                 
                 //Insert total profile views
                 userRow.createCell(4)
                           .setCellValue(ProfileSettings.TOTAL_PROFILE_VIEWS);
                 
                 //Insert number of followigs
                 userRow.createCell(5)
                           .setCellValue(ProfileSettings.FOLLOWING);
                 
                 //Insert number of followers
                 userRow.createCell(6)
                           .setCellValue(ProfileSettings.FOLLOWERS);
                         
                 //Insert co URI list of coauthors
                 userRow.createCell(7)
                           .setCellValue(ProfileSettings.CO_AUTHORS);
           
       }
       
       public static void writeHeaderForPublicationSheet(Sheet publicationSheet){
             //Create a first row
             Row publicationRow = publicationSheet.createRow(0);

             //publication URI
             publicationRow.createCell(1)
                       .setCellValue(ProfileSettings.PUBLICATION_ID);

             //User Id (URI) 
             publicationRow.createCell(0)
                   .setCellValue(ProfileSettings.USER_ID);


             //Publication Title
             publicationRow.createCell(2)
                       .setCellValue(ProfileSettings.PUBLICATION_NAME);

             //Publication views
             publicationRow.createCell(3)
                       .setCellValue(ProfileSettings.PUBLICATION_VIEWS);
       }
    
}
