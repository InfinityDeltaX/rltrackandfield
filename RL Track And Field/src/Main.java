import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
	static int SHEET_WIDTH = 100;
	static int TEAM_ENUM_ROW = 0;
	static int SCORING_AMOUNT_ROW = 2;
	static int EVENT_COLUMN = 0;

	public static void main(String[] args) {
		setup();
		
		//exportDataToSS(generateTemplateData(meet));
		//System.exit(0);
		try {
			exportDataToSS(processScoring(importDataFromSpreadsheet()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//generateDataFiles(PATH, "Test Meet");
	}
	
	public static void setup(){
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

		 ArrayList<Event> events = new ArrayList<Event>(Arrays.asList(Event.events));
		
		meet = new Meet(teams, events);
	}
	
	private static String[][] processScoring(String[][] data){
		
		int totalteams = meet.getTeams().size();
		int[] totalTeamScores = new int[totalteams];
		int totalfinishers = Meet.getPointScoringAmounts(totalteams, false).length;
		
		for(int i = 0; i < meet.getEvents().size(); i++){
			
			int[] teamScores = new int[totalteams];
			int rowIndex = 2*i+3;
			
			for(int j = 0; j < totalfinishers; j++){ //get the total scores from each team
				int colIndex = j+EVENT_COLUMN+1;
				try {
					int currentTeamIndex = meet.getTeamIndex(meet.getCompetitorOwner(data[rowIndex][colIndex]));
					boolean isRelay = Event.events[i].isRelay;
					teamScores[currentTeamIndex] += Meet.getPointScoringAmounts(totalteams, isRelay)[j];
				} catch (Exception e) {if(!data[rowIndex][colIndex].trim().equals("")) System.err.println("WARNING: " + data[rowIndex][colIndex] + " not attributed to any team!");}
			}
			
			for(int j = 0; j < totalteams; j++){ //put total scores from each team into the array.
				int colIndex = j+EVENT_COLUMN+Meet.getPointScoringAmounts(totalteams, false).length+1;
				data[rowIndex][colIndex] = "" + teamScores[j];
				totalTeamScores[j] += teamScores[j];
			}
		}
		
		for(int j = 0; j < totalteams; j++){ //put ultimate totals into array.
			int colIndex = j+EVENT_COLUMN+Meet.getPointScoringAmounts(totalteams, false).length+1;
			data[2*meet.getEvents().size()+3][colIndex] = "" + totalTeamScores[j];
		}
		//arrayPrint(data);
		return data;
	}
	
	private static void arrayPrint(String[][] in){
		for (int i = 0; i < in.length; i++) {
			for (int j = 0; j < in[i].length; j++) {
				System.out.print(in[i][j] + " ");
			}
			System.out.println();
		}
	}

	private static String[][] generateTemplateData(Meet meet){

		
		String[][] data = new String[SHEET_WIDTH][SHEET_WIDTH];
		
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
			data[SCORING_AMOUNT_ROW][1+currentRow++] = t.getFullName() + " Total";
		}
		
		int currentCol = 0;
		for(Event e : Event.events){
			data[3+currentCol++][EVENT_COLUMN] = e.getName() + "";
			data[3+currentCol++][EVENT_COLUMN] = e.getName() + "";
		}
		
		return data;
	}
	
	private static String[][] importDataFromSpreadsheet() throws IOException{
		String[][] data = new String[SHEET_WIDTH][SHEET_WIDTH];
	    XSSFWorkbook workbook = null;
	    try {workbook = (XSSFWorkbook) WorkbookFactory.create(template);} catch (EncryptedDocumentException | InvalidFormatException e) {e.printStackTrace();}
		XSSFSheet sheet = workbook.getSheetAt(0);
		System.out.println(sheet);
		for(int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++){
			XSSFRow row = sheet.getRow(rowIndex);
			for(int colIndex = 0; colIndex < row.getPhysicalNumberOfCells(); colIndex++){
				Cell cell = row.getCell(colIndex);
				if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC) data[rowIndex][colIndex] = cell.getNumericCellValue() + "";
				else data[rowIndex][colIndex] = cell.getRichStringCellValue().getString();
			}
			//sheet.autoSizeColumn(rowIndex);
		}
		
		workbook.close();
		
		return data;
	}
	
	private static void exportDataToSS(String[][] data){
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Template");
		for(int rowIndex = 0; rowIndex < data.length; rowIndex++){
			XSSFRow row = sheet.createRow(rowIndex);
			String[] currentRow = data[rowIndex];
			for(int colIndex = 0; colIndex < currentRow.length; colIndex++){
				Cell cell = row.createCell(colIndex);
				if(!(currentRow[colIndex]==null || currentRow[colIndex].equals(""))){
					cell.setCellValue(currentRow[colIndex]);
				}
			}
			sheet.autoSizeColumn(rowIndex);
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
