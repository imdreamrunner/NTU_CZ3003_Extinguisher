package cms.dataClasses.oldStuff;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")  
@JsonSubTypes({  
	    @Type(value = GPSLocation.class, name = "gps"),  
	    @Type(value = CommunityLocation.class, name = "community"),
	    @Type(value = PolygonalLocation.class, name = "polygonal")})  
public abstract class Location {
}
