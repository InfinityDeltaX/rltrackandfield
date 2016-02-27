import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {

	public static String PATH = "M:\\Documents\\RL Track and Field";
	static String tempMeetName = "Test Meet";
	static File dir = new File(PATH+"\\"+tempMeetName);
	static File meetData = new File(dir.getAbsolutePath() + "\\" + "Meet Data.txt");
	static File template = new File(dir.getAbsolutePath() + "\\" + "Template.xlsx");
	static Meet meet;

	public static void main(String[] args) {

		
		System.out.println(Arrays.toString(Meet.getPointScoringAmounts(10, false)));

		ArrayList<Team> teams = new ArrayList<Team>();
		Scanner teamScanner = null;
		try {
			teamScanner = new Scanner(meetData);
		} catch (FileNotFoundException e) {
			System.err.println("Could not find Meet Data.txt. Has it been deleted?");
			e.printStackTrace();
			System.exit(-1);
		}

		while(teamScanner.hasNextLine()){
			teams.add(new Team(teamScanner.nextLine()));
		}

		ArrayList<Event> events = new ArrayList<Event>();
		
		meet = new Meet(teams, events);
		System.out.println(meet.getTeams());
		
		exportTemplateToSS(generateTemplateData(meet));
		//generateDataFiles(PATH, "Test Meet");
	}

	private static String[][] generateTemplateData(Meet meet){
		int TEAM_ENUM_ROW = 0;
		int SCORING_AMOUNT_ROW = 2;
		int EVENT_COLUMN = 0;
		
		String[][] data = new String[100][100];
		
		int currentRow = 0;
		data[TEAM_ENUM_ROW][0] = "Teams";
		for(Team t : meet.getTeams()){
			data[TEAM_ENUM_ROW][1+currentRow++] = t.getFullName() + " [" + t.getAbbreviation() + "]";
		}
		
		
		int[] scoring = Meet.getPointScoringAmounts(meet.getTeams().size(), false);
		data[SCORING_AMOUNT_ROW][0] = "Events";
		currentRow = 0;
		int place = 0;
		for(int i = 0; i < scoring.length; i++){ //which of these are relays?
			data[SCORING_AMOUNT_ROW][1+currentRow++] = "Finisher " + (place+1) + " [" + scoring[place] + "]";
			place++;
		}
		
		for(Team t : meet.getTeams()){
			data[SCORING_AMOUNT_ROW][1+currentRow++] = t.getFullName() + "Total";
		}
		
		int currentCol = 0;
		for(Event e : Event.events){
			data[3+currentCol++][EVENT_COLUMN] = e.getName() + " Finisher";
			data[3+currentCol++][EVENT_COLUMN] = e.getName() + " Time";
		}
		return data;
	}
	
	private static void exportTemplateToSS(String[][] data){
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Template");
		for(int rowIndex = 0; rowIndex < data.length; rowIndex++){
			XSSFRow row = sheet.createRow(rowIndex);
			String[] currentRow = data[rowIndex];
			for(int colIndex = 0; colIndex < currentRow.length; colIndex++){
				Cell cell = row.createCell(colIndex);
				cell.setCellValue(currentRow[colIndex]);
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(template);
			workbook.write(out);
			out.close();
			System.out.println("Saved Template!");
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateDataFiles(String path, String meetName){
		dir = new File(path+"\\"+meetName);
		if(!dir.exists()) dir.mkdir();
		meetData = new File(dir.getAbsolutePath() + "\\" + "Meet Data.txt");
		if(!meetData.exists()){
			try {
				meetData.createNewFile();
			} catch (IOException e) {
				System.err.println("Could not create meet data file!");
				e.printStackTrace();
			}
		}

	}

}
