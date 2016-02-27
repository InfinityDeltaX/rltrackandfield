
public class Event {

	String name;
	boolean isRelay;
	
	public Event(String name, boolean isRelay){
		this.name = name;
		this.isRelay = isRelay;
	}
	public Event(String name){
		this(name, false);
	}
	
	public String getName(){
		return name;
	}
	
	public static Event[] events = { //which are relays?
			new Event("PV"), 
			new Event("LI"), 
			new Event("TJ"), 
			new Event("HJ"), 
			new Event("JT"), 
			new Event("SP"), 
			new Event("DT"), 
			new Event("400R", true), 
			new Event("1500"), 
			new Event("110HH"), 
			new Event("400"), 
			new Event("100"), 
			new Event("800"), 
			new Event("300IH"), 
			new Event("200"), 
			new Event("3000"), 
			new Event("1600R", true)};
}
