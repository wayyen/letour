package com.twt.xtreme;

/**
*
* User: Lim, Teck Hooi
* Date: 7/23/11
* Time: 3:58 AM
*
*/
public class PhotoProperties
{
private String title;
private String description;
private double longtitude;
private double latitude;
private String username;
private String location;
private long length;
private String encodingType;

public String getEncodingType()
{
return encodingType;
}

public void setEncodingType(String encodingType)
{
this.encodingType = encodingType;
}

public long getLength()
{
return length;
}

public void setLength(long length)
{
this.length = length;
}

public String getTitle()
{
return title;
}

public void setTitle(String title)
{
this.title = title;
}

public String getDescription()
{
return description;
}

public void setDescription(String description)
{
this.description = description;
}

public double getLongitude()
{
return longtitude;
}

public void setLongtitude(double longtitude)
{
this.longtitude = longtitude;
}

public double getLatitude()
{
return latitude;
}

public void setLatitude(double latitude)
{
this.latitude = latitude;
}

public String getUsername()
{
return username;
}

public void setUsername(String username)
{
this.username = username;
}

public String getLocation()
{
return location;
}

public void setLocation(String location)
{
this.location = location;
}
}
