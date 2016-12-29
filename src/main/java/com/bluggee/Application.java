package com.bluggee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;



import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.W3CDateFormat;
import com.redfin.sitemapgenerator.W3CDateFormat.Pattern;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;





public class Application {


	public String page;

	DbConnection dbConnection;
	Properties properties;
	Boolean isDebug = true;
	String baseUrl;
	
	String fbId;
	String fbSecret;
	String sitemapDirectory;
	public  WebSitemapGenerator wsg;
	
	
    long sourceId = 1;
	public boolean isDebug() {
		return isDebug;
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			properties.load(input);
		} catch (IOException e) {
			properties = null;
			e.printStackTrace();
		}
		if (properties != null) {
			Application a = null;
			a = new Application(properties);
			try{
				a.run();
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		} else {
			System.out.println("could not load properies file");
		}
	}

	
	
	

	public Application(Properties properties) {
		this.properties = properties;
		baseUrl = properties.get("baseUrl").toString();
		sitemapDirectory=properties.get("sitemapDirectory").toString();
		dbConnection = getDbConnection();
		W3CDateFormat dateFormat = new W3CDateFormat(Pattern.DAY); 
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		try {
			wsg = WebSitemapGenerator.builder(baseUrl, new File(sitemapDirectory))
					.autoValidate(true).dateFormat(dateFormat).build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
	

	/**
	 * Creates a {@link DbConnection} object from properties file using the
	 * specified prefix
	 * 
	 * @param prefix
	 * @return
	 */
	public DbConnection getDbConnection() {
		String host = properties.get("host").toString();
		String port = properties.get("port").toString();
		String user = properties.get("user").toString();
		String pass = properties.get("password").toString();
		String db = properties.get("db").toString();
		DbConnection connection = new DbConnection(host, port, user, pass, db);
		return connection;
	}

	

	public void run(){
		try {
			List<String> links = Content.getContacts(dbConnection);
			for(int i = 0; i < 1000; i++){
				writeUrl(links,1.0, ChangeFreq.YEARLY);
			}
		
			wsg.write();
			if(links.size() * 1000 > 50000){
				wsg.writeSitemapsWithIndex();
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public  void writeUrl(List<String> urls, Double priority, ChangeFreq freq) throws MalformedURLException{
		for(String url : urls){
			WebSitemapUrl wurl = new WebSitemapUrl.Options(url).lastMod(new Date()).priority(priority).changeFreq(freq).build();
			wsg.addUrl(wurl);
		}
	}

}
