package comp1206.sushi.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of parser of configuration files
 * @author wkr1u18
 *
 */
public class Configuration {

	private ServerInterface server;
	
	
	/**
	 * Constructor for COnfiguration class
	 * @param server reference to {@link ServerInterface} object
	 */
	public Configuration(ServerInterface server) {
		this.server = server;
	}
	
	/**
	 * Loads configuration from file and sets up appropriate server components
	 * @param filename path to configuration file in format given in specification
	 */
	public void loadConfiguration(String filename) {
		try {
			//Read all lines to List of String objects
			List<String> allLines = Files.readAllLines(Paths.get(filename));
			//For each line which is not empty call parse
			for(String line : allLines) {
				if(!line.equals("")) {
					parse(line);
				}
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ParsingException pe) {
			System.out.println(pe);
		}
	}
	
	/**
	 * Parses given line
	 * @param line line to be parsed
	 * @throws ParsingException when line is not in correct format
	 */
	private void parse(String line) throws ParsingException {
		//If no separators - throw exception
		if (!line.contains(":")) {
			throw new ParsingException(line);
		}
		//Create arrayList of all elements separated by colons
		ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(line.split(":")));
		//Switch over the first token
		switch(arguments.get(0)) {
		case "POSTCODE":
			break;
		case "RESTAURANT":
			break;
		case "SUPPLIER":
			break;
		case "INGREDIENT":
			break;
		case "DISH":
			break;
		case "USER":
			break;
		case "ORDER":
			break;
		case "STOCK":
			break;
		case "STAFF":
			break;
		case "DRONE":
			break;
		default:
			//if token not recognized, throw ParsingException
			throw new ParsingException(line);
		}
		
		System.out.println("");
	}
	
	/**
	 * Custom Exception for handling parsing errors
	 * @author wkr1u18
	 *
	 */
	class ParsingException extends Exception {
		private static final long serialVersionUID = 8220479200093993291L;
		String faultyLine;
		
		public ParsingException(String faultyLine) {
			this.faultyLine = faultyLine;
		}
		public String toString() {
			return "ParsingException: " + faultyLine;
		}
	}

}
