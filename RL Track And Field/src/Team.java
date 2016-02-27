
public class Team {
	private String fullName;
	
	public Team(String fullname) {
		fullName = fullname;
	}

	public String getAbbreviation(){
		return getAbbreviation(this.fullName);
	}
	
	public static String getAbbreviation(String fullName){
		String output = "";
		for(int i = 0; i < fullName.length(); i++){
			char current = fullName.charAt(i);
			if(current >= 'A' && current <= 'Z') output = output + "" + current;
		}
		return output;
	}
	
	public String getFullName(){
		return fullName;
	}

	@Override
	public String toString() {
		return "[Team] " + fullName + "; " + getAbbreviation();
	}
	
	
}
