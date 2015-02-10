
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
import java.util.Iterator;
import java.util.List;

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
           public void getUserProperties(WebClient webClient, HtmlElement aUserElement , JsonObject userProperties, long waitTimeout ) throws IOException
           {
            HtmlPage page = (HtmlPage)webClient.getPage(aUserElement.getAttribute("href"));
            webClient.waitForBackgroundJavaScript(waitTimeout);
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
                
                //Build user object                
                 userProperties.addProperty(ProfileSettings.USER_ID , aUserElement.getAttribute("href"));
                 userProperties.addProperty(ProfileSettings.USER_FULL_NAME , aUserElement.asText());
                 userProperties.addProperty(ProfileSettings.TOTAL_PROFILE_VIEWS, Integer.parseInt(totalViews.asText()));
                 userProperties.addProperty(ProfileSettings.FOLLOWING , Integer.parseInt(numberOfFollowings[0]));
                 userProperties.addProperty(ProfileSettings.FOLLOWERS , Integer.parseInt(numberOfFolower.asText()));

                for (HtmlElement element : publicationCount) 
                {
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
                }
            }
            catch(Exception ex){ex.printStackTrace();}
            
            finally{
                
             if(!publications.isJsonNull())
                userProperties.add(ProfileSettings.PUBLICATIONS, publications);
            }
        }
    
}
