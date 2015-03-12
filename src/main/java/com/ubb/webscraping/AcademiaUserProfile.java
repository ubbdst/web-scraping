
package com.ubb.webscraping;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ubb.webscraping.settings.ProfileSettings;
import java.io.IOException;
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
         * @param waitTimeout
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
            
            JsonArray publications = new JsonArray();
            
            try {
                
                //We are doing this because number of followings, X is returned as "X Following" 
                //and we are not interested with the String part of the result 
                 String [] numberOfFollowings  = numberOfFollowing.asText().split(" ");
                 
                 String userURI = aUserElement.getAttribute("href");
                 String fullName = aUserElement.asText();
                 String firstName = extractFirstName(fullName);
                 String lastName = extractLastName(fullName);
                 int profileViews = Integer.parseInt(totalViews.asText());
                 int followings =  Integer.parseInt(numberOfFollowings[0]);
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

                for (HtmlElement element : publicationCount) 
                {
                    //Create publication JSON object
                    JsonObject publicationProperties = new JsonObject();
                    
                    String dataCountId =  element.getAttribute("data-work_id");
                    String xpathExp = "//a[@data-container='.work_" + dataCountId + "']/preceding-sibling::a[1]";
                    String xpathPublicationViewCount = "//strong[@class='view-count' and ancestor::div[@data-work_id='" + dataCountId + "']]";
                    
                    HtmlElement publicationViewCount = (HtmlElement)page.getFirstByXPath(xpathPublicationViewCount);
                    HtmlElement link = (HtmlElement)page.getFirstByXPath(xpathExp);
                    
                    publicationProperties.addProperty(ProfileSettings.PUBLICATION_ID, link.getAttribute("href"));
                    publicationProperties.addProperty(ProfileSettings.PUBLICATION_NAME, link.asText());
                    publicationProperties.addProperty(ProfileSettings.PUBLICATION_VIEWS, Integer.parseInt(publicationViewCount.asText()));
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
        
    
}
