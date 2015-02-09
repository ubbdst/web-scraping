/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubb.webscraping;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * @author Hemed, Øyvind
 * @date 04-02-2015
 * @location : Universitetsbiblioteket i Bergen
 */
public class UserProfile {
    
     final static int TIMEOUT_MILLIS = 20000;
     final static String PAGE_URL = "https://uib.academia.edu/";
    
     public static void main(String[] args) throws IOException, InterruptedException
     {   
           int count = 0;
           URL url = new URL(PAGE_URL);
         
           Document document = Jsoup.parse(url , TIMEOUT_MILLIS);
           Element departments = document.getElementById("department_list");
           Elements links = document.select("a[href]");
          
            //Turn off loggings
            Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
        
            System.out.println("Printing UiB Departments in Academia.edu");
        
            //get div which has a 'name' attribute of 'John'
    
            WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setTimeout(TIMEOUT_MILLIS);
            webClient.getOptions().setCssEnabled(false);
         
            HtmlPage academiaFrontPage = webClient.getPage(PAGE_URL);
            
            //Get a list of all the departments
            List<DomNode> departmentNodeList = (DomNodeList)academiaFrontPage.getElementById("department_list").getElementsByTagName("a");
   
            for(DomNode aNode : departmentNodeList)
            {  
               //Convert a node into an html element for easier processing
               HtmlAnchor a  = (HtmlAnchor)aNode;
               
               if(a.getAttribute("href").contains(PAGE_URL))
               {

                  System.out.println(a.asText() + "-->" + a.getAttribute("href"));
                  System.out.println();
                  
                 
                  HtmlPage academiaUserPage = (HtmlPage)webClient.getPage(a.getAttribute("href")); 
                  webClient.waitForBackgroundJavaScript(TIMEOUT_MILLIS);

                  
                  
                  //Get a list of all the users
                  //List<HtmlAnchor> userNodeList = (ArrayList)academiaUserPage.getHtmlElementById("user_list"); //.getByXPath("descendant-or-self::a");
                   List<HtmlElement> userNodeList = (ArrayList)academiaUserPage.getByXPath("//div[@id='user_list']/descendant-or-self::h3/a[contains(@href,'https://uib.academia.edu')]");
                   
                     //System.out.println(userNodeList.toString());
                  
                  for(HtmlElement aUserElement : userNodeList)
                  {  
                      String userURI = aUserElement.getAttribute("href");
                      System.out.println(aUserElement.asText() + "-->" + aUserElement.getAttribute("href"));
                      System.out.println();
                     
                      getUserInfo(webClient , userURI, TIMEOUT_MILLIS);
                     
                  }
   
               }
               
               count++;
               
               if(count > 1) break;
               
               webClient.closeAllWindows();
            }
       }
            
 
            
           private static void getUserInfo(WebClient webClient, String userLink , long waitTimeout) throws IOException
           {
            
            HtmlPage page = (HtmlPage)webClient.getPage(userLink);
            webClient.waitForBackgroundJavaScript(waitTimeout);
           
           //get list of all divs
           //final List<?> divs = page.getByXPath("/html/body/div[5]/div[1]/div/div[4]/div[1]/div/div[3]/a/div[1]");
    

            //get div which has a 'name' attribute of 'John'
            //HtmlDivision div = (HtmlDivision)page.getByXPath("//div[@class='count']").get(1);
            //HtmlDivision div = (HtmlDivision)page.getByXPath("//div[@class='count' and parent::a/@href='/HemedAli/Followers']").get(0);
                        
              //HtmlDivision div = (HtmlDivision)page.getByXPath("//div[@class='stat total-views']/a/div[@class='count']").get(0);
            //HtmlDivision div = (HtmlDivision)page.getByXPath("//div[@id='following']/a/h3").get(0);
            
            HtmlElement numberOfFollowing = (HtmlElement)page.getFirstByXPath("//div[@id='following']/a/h3");
            HtmlDivision totalViews = (HtmlDivision)page.getFirstByXPath("//div[@class='stat total-views']/a/div[@class='count']");
            HtmlDivision numberOfFolower = (HtmlDivision)page.getFirstByXPath("//div[@class='stat followers']/a/div[@class='count']");
            
            
            //Get person publication
            //List<HtmlElement> publications = (ArrayList)page.getByXPath("//div[@class='header']/div[@class='title' and a/@class='title_link']");
            //List<HtmlElement> publications = (ArrayList)page.getByXPath("//div[@class='header']/div[@class='title']/a[contains(@href,'academia.edu')]");
            
            //Get publication counts
      //                  List<HtmlElement> publicationCount = (ArrayList)page.getByXPath("//div[@class='views']/span[@class='view-count-widget']/strong[@class='view-count']");

            List<HtmlElement> publicationCount = (ArrayList)page.getByXPath("//div[div[@class='right-icons']/div[@class='views']/span[@class='view-count-widget']/strong[@class='view-count']]");
            

            
            System.out.println("================= Person Information Academia.edu ==========================");
            
            System.out.println("Total Views: " + totalViews.asText());
            System.out.println("Followings : " + numberOfFollowing.asText());
            System.out.println("Followers: " + numberOfFolower.asText());
            
            
                      
            for(HtmlElement element : publicationCount)
            {
               String dataCounId =  element.getAttribute("data-work_id");
               String xpathExp = "//a[@data-container='.work_" + dataCounId + "']/preceding-sibling::a[1]";
               String xpathPublicationViewCount = "//strong[@class='view-count' and ancestor::div[@data-work_id='" + dataCounId + "']]";
               HtmlElement countPub = (HtmlElement)element.getFirstByXPath("//div[@class='right-icons']/div[@class='views']/span[@class='view-count-widget']/strong[@class='view-count']");
               HtmlElement publicationViewCount = (HtmlElement)page.getFirstByXPath(xpathPublicationViewCount);

               HtmlElement link = (HtmlElement)page.getFirstByXPath(xpathExp);
               
               System.out.println("PUBLICATION URI: " + link.getAttribute("href") +
                                 "\nPUBLICATION NAME: " + link.asText() + 
                                 "\nPUBLICATION VIEWS " + publicationViewCount.asText());
               System.out.println();
   
               //HtmlElement el = (HtmlElement)element.getByXPath("//div[@class='views']/span[@class='view-count-widget']/strong[@class='view-count']").get(0);
               //System.out.println("Count Id : " + element.getAttribute("data-work_id"));
            }
                
        }
      }
        
    
        /**Elements l = departments.getElementsByTag("a");
        //for (Element link : departmentLinks) {
            
              String linkHref = l.get(1).attr("href");
              String linkText = l.get(1).text();
              
              System.out.println("Department: " + linkText + " ----> " + linkHref);
              
              
              //Document userDoc = Jsoup.connect(linkHref).get();
              
              //TO DO - We need to keep trying to connect if no response.
              Document userListDoc = Jsoup.parse(new URL(linkHref) , 20000);
              
              Element userList = userListDoc.getElementById("user_list");
              Elements users = userList.select("h3 > a");
              
              
              for(Element user: users)
              {
                  String userUrl = user.attr("href");
                  String userDisplayName = user.text();
                  
                  System.out.println(userDisplayName + " : " + userUrl);
                  
                  
                  
                  //TO DO - We need to keep trying to connect if no response.
                  Document researcherDoc = Jsoup.parse(new URL(userUrl) , 20000);
                  
                  //Thread.sleep(20000);
                  
                  
                  //TO DO - No of following is an ajax call, we get retrieve with Jsoup.
                  // Maybe other alternative is required?
                  //Element followingTag = researcherDoc.getElementById("following");
                  //Elements numberOfFollowing = followingTag.select("a.r > h3");
                  
                  
                  
                  Elements totalView = researcherDoc.select("div.right-column div.content-block div.view-counts div.stat.followers a.pjax div.count");
                  for(Element element : totalView)
                  {
                      System.out.println(element.text());
                  }
                  
                  System.out.println();
                  
                count ++;
                
                if(count > 5) break;
                
              }
              
       }


    //}**/
    

