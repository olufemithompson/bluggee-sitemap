package com.bluggee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Content extends DBObject{
	
	String title;
	String description;
	String image;
	String url;
	String originalUrl;
	String uniqueId;
	long sourceId;
	
	public void reset(){
		title="";
		description="";
		image="";
		url="";
		uniqueId="";
		originalUrl="";
	}
	
	

	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getImage() {
		return image;
	}



	public void setImage(String image) {
		this.image = image;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public String getOriginalUrl() {
		return originalUrl;
	}



	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}



	public String getUniqueId() {
		return uniqueId;
	}



	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}



	public long getSourceId() {
		return sourceId;
	}



	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
	
	
	

	public static List<String> getContacts(DbConnection connection) throws SQLException{
	    List<String>  links = new ArrayList<String>();
		Connection dbConnection = null;
		dbConnection = getDBConnection(connection);
		Statement statement = dbConnection.createStatement();

		ResultSet rs = statement.executeQuery("select id, url from content where sitemap_completed is null or sitemap_completed = 0");
		while (rs.next()) {
			links.add( rs.getString("url"));
			complete(connection, rs.getInt("id"));
			
		}
		if (statement != null) {
			statement.close();
		}
		if (dbConnection != null) {
			dbConnection.close();
		}
		return links;
	}


	public static void complete(DbConnection connection, int id) throws SQLException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		dbConnection = getDBConnection(connection);
		preparedStatement = dbConnection.prepareStatement("update content set sitemap_completed = 1 where id = ?", Statement.RETURN_GENERATED_KEYS);
		preparedStatement.setInt(1, id);
		preparedStatement.executeUpdate();
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (dbConnection != null) {
			dbConnection.close();
		}
	}
	
	
	
	
	
	
	
}
