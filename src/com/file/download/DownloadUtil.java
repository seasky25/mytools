package com.file.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class DownloadUtil {
	public static void downloadFile(String url, String dir) throws MalformedURLException, IOException{
		String fileName = url.substring(url.lastIndexOf("/"));
		downloadFile(url, dir, fileName);
	}
	
	public static void downloadFile(String url, String dir, String name) throws MalformedURLException, IOException{
		File folder = new File(dir);
		folder.mkdirs();		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(dir+File.separator+name)));
		BufferedInputStream bis = new BufferedInputStream(new URL(url).openStream());
		byte[] buffer = new byte[1024];
		int count = -1;
		while((count=bis.read(buffer))!=-1){
			bos.write(buffer, 0, count);
		}	
		bos.close();
		bis.close();
	}
	
	
	public static void downloadFiles(String[] urls, String dir) throws MalformedURLException, IOException{
		for(String url:urls){
			downloadFile(url, dir);
		}
	}
	
	public static void downloadFiles(File addresses, String dir) throws MalformedURLException, IOException{
		downloadFiles(addresses, dir, "/", "");
	}
	
	public static void downloadFiles(File addresses, String dir, String sep, String postfix) throws MalformedURLException, IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(addresses)));
		String url = null;
		String fileName = null;
		while((url=br.readLine()) != null){
			fileName = url.substring(url.lastIndexOf(sep)+1)+postfix;
			downloadFile(url, dir, fileName);
		}
		br.close();
	}
	
	
	public static void downloadFile(String url, String dir, String fileName, String username, String password) throws ClientProtocolException, IOException{
		File folder = new File(dir);
		folder.mkdirs();		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(dir+File.separator+fileName)));
		
        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(
                new AuthScope(url, 443),
                new UsernamePasswordCredentials(username, password));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                response.getEntity().writeTo(bos);
                System.out.println(response.getEntity().getContentType());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
            bos.close();
        }
	}
	
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		String dir = "/Users/lxie/Documents/workspace/backyard";
//		downloadFiles(new File(dir+File.separator+"addr.txt"), dir, "=", "_package_version_migration.zip");
		downloadFile("https://org62.my.salesforce.com/setup/packagemigrationdownload?id=04ti0000000XpAd", dir, "test.txt", "lxie@salesforce.com", "Woyaomima30!");
	}
}
