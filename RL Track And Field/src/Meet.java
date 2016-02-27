import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Meet {

	private static int[][] individualPointScoringAmounts = {
			{5, 3, 1},
			{5, 3, 2, 1},
			{6, 4, 3, 2, 1},
			{8, 6, 4, 2, 1},
			{10, 8, 6, 4, 2, 1},
			{10, 8, 6, 4, 2, 1}
	};

	private static int[][] relayPointScoringAmounts = {
			{5},
			{5, 3},
			{6, 4, 2},
			{8, 6, 4, 2},
			{10, 8, 6, 4, 2},
			{10, 8, 6, 4, 2, 1}
	};

	private ArrayList<Team> teams;
	private ArrayList<Event> events;
	
	public Meet(List<Team> teams, List<Event> events){
		this.teams = new ArrayList<Team>(teams);
		this.events = new ArrayList<Event>(events);
	}

	public static int[] getPointScoringAmounts(int teams, boolean isRelay){
		int teamIndex;
		if(teams > 7) teamIndex = 5;
		else teamIndex = teams-2;
		System.out.println(teamIndex);

		int[] output = new int[teams];
		if(isRelay){
			output = Arrays.copyOfRange(relayPointScoringAmounts[teamIndex], 0, relayPointScoringAmounts[teamIndex].length);
		} else {
			output = Arrays.copyOfRange(individualPointScoringAmounts[teamIndex], 0, individualPointScoringAmounts[teamIndex].length);
		}

		return output;
	}
	
	public List<Team> getTeams(){
		return new ArrayList<Team>(teams);
	}
	
	public List<Event> getEvents(){
		return new ArrayList<Event>(events);
	}
}
