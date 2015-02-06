/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubb.webscraping;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Hemed, Øyvind
 * @date 04-02-2015
 * @location : Universitetsbiblioteket i Bergen
 */
public class UserProfile {
    
    final static int TIMEOUT_MILLIS = 5000;
    final static String PAGE_URL = "https://uib.academia.edu/";
    
    public static void main(String[] args) throws IOException, InterruptedException
    {   
         int count = 0;
         URL url = new URL(PAGE_URL);
         Document document = Jsoup.parse(url, TIMEOUT_MILLIS);
         Element departments = document.getElementById("department_list");
        //Elements links = document.select("a[href]");
        
            System.out.println("Printing UiB Departments in Academia.edu");
        
            //get div which has a 'name' attribute of 'John'
          
            WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
            
            
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setTimeout(TIMEOUT_MILLIS);
            webClient.getOptions().setCssEnabled(false);
         
            
            HtmlPage page = webClient.getPage(new URL("https://uib.academia.edu/NilsAnfinset"));
            //Thread.sleep(20000);
            
            webClient.waitForBackgroundJavaScript(TIMEOUT_MILLIS);
           
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
            List<HtmlElement> publications = (ArrayList)page.getByXPath("//div[@class='header']/div[@class='title']/a[contains(@href,'academia.edu')]");
            
            //Get publication counts
      //                  List<HtmlElement> publicationCount = (ArrayList)page.getByXPath("//div[@class='views']/span[@class='view-count-widget']/strong[@class='view-count']");

            List<HtmlElement> publicationCount = (ArrayList)page.getByXPath("//div[div[@class='right-icons']/div[@class='views']/span[@class='view-count-widget']/strong[@class='view-count']]");
            

            
            System.out.println("================= Person Information Academia.edu ==========================");
            
            System.out.println("Total Views: " + totalViews.asText());
            System.out.println("Followings : " + numberOfFollowing.asText());
            System.out.println("Followers: " + numberOfFolower.asText());
            
            
            //for(HtmlElement element : publications)
            //{
              
                   
                //HtmlElement e = (HtmlElement)element.getByXPath("//a").get(1);
               
           //    System.out.println("Paper: " + element.getFirstByXPath("//a/@href") + " : " + element.asText());
                 //System.out.println("Paper: " + element.getAttribute("href") + " : " + element.asText());
 
               
            //}
            
                      
            for(HtmlElement element : publicationCount)
            {
               String dataCounId =  element.getAttribute("data-work_id");
               String xpathExp = "//a[@data-container='.work_"+dataCounId+"']/preceding-sibling::a[1]";
               String xpathExpCount = "//strong[@class='view-count' and ancestor::div[@data-work_id='"+dataCounId+"']]";
               HtmlElement countPub = (HtmlElement)element.getFirstByXPath("//div[@class='right-icons']/div[@class='views']/span[@class='view-count-widget']/strong[@class='view-count']");
               HtmlElement strongCount = (HtmlElement)page.getFirstByXPath(xpathExpCount);

               HtmlElement link = (HtmlElement)page.getFirstByXPath(xpathExp);
               
               System.out.println("LINK: " + link.getAttribute("href") + "NAME: " + link.asText() + "COUNT " + strongCount.asText());
   
               
               //HtmlElement el = (HtmlElement)element.getByXPath("//div[@class='views']/span[@class='view-count-widget']/strong[@class='view-count']").get(0);
               //System.out.println("Count Id : " + element.getAttribute("data-work_id"));
            }
            webClient.closeAllWindows();
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
    
}
