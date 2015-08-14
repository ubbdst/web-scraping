/*
 This is the Class for getting invoices from ReadMine. It is only for testing purpose.
 */

package com.ubb.webscraping;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class HttpGetInvoices {
    final static String URI_TO_FETCH = "https://oppdrag.ub.uib.no/projects/marcus/invoices.json";

    public final static void main(String[] args) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            
            //Trust any of the SSL certificate - not recommended in production environment unless you trust the resources.
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy(){
            @Override
            public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                return true;
             }
           })
          .useTLS()
          .build();
             
           SSLConnectionSocketFactory  connectionFactory = new SSLConnectionSocketFactory(
                                                           sslContext, new AllowAllHostnameVerifier());
           httpclient = HttpClients.custom()
            .setSSLSocketFactory(connectionFactory)
            .build();
            

            //Shall we POST login form?
            RedmineManagerFactory.createWithUserAuth(URI_TO_FETCH,"koka", "colla");
            
            HttpGet httpget = new HttpGet(URI_TO_FETCH);
            System.out.println("Executing request " + httpget.getRequestLine());
            
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException(status + ": " + response.getStatusLine().getReasonPhrase());
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println(responseBody);
        } finally {
            httpclient.close();
        }
        
        
    }
  
}

