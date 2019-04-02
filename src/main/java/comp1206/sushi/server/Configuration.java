package comp1206.sushi.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;

import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.Supplier;

/**
 * Implementation of parser of configuration files
 * @author wkr1u18
 *
 */
public class Configuration {

	private Server server;
	private Logger logger;
	
	
	/**
	 * Constructor for COnfiguration class
	 * @param server reference to {@link ServerInterface} object
	 */
	public Configuration(Server server) {
		this.server = server;
		this.logger = server.getLogger();
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
			throw new ParsingException("Too few tokens in:" + line);
		}
		//Create arrayList of all elements separated by colons
		ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(line.split(":")));
		//Switch over the first token
		switch(arguments.get(0)) {
		case "POSTCODE":
			//Check number of arguments
			if(arguments.size()!=2) {
				throw new ParsingException("Wrong number of arguments in :" + line);
			}
			//Check whether postcode is correct
			if(!isValidPostcode(arguments.get(1))) {
				throw new ParsingException("Incorrect postcode in: " + line);
			}
			//Call server to add given postcode
			server.addPostcode(arguments.get(1));
			logger.info("Successfully added postcode: " + arguments.get(1));
			break;
		case "RESTAURANT":
			//Check number of arguments
			if(arguments.size()!=3) {
				throw new ParsingException("Wrong number of arguments in :" + line);
			}
			Postcode restaurantPostcode = server.getPostcode(arguments.get(2));
			//Check whether postcode has been added already
			if(restaurantPostcode==null) {
				throw new ParsingException("Wrong postcode: " + restaurantPostcode);
			}
			//Update the restaurant object in server
			server.restaurant = new Restaurant(arguments.get(1), restaurantPostcode);
			logger.info("Successfully created restaurant: " + arguments.get(1) + " at: " + arguments.get(2));
			break;
		case "SUPPLIER":
			//Check number of arguments
			if(arguments.size()!=3) {
				throw new ParsingException("Wrong number of arguments in :" + line);
			}
			Postcode supplierPostcode = server.getPostcode(arguments.get(2));
			//Check whether postcode has been added already
			if(supplierPostcode==null) {
				throw new ParsingException("Wrong postcode: " + supplierPostcode);
			}
			//Update the restaurant object in server
			server.addSupplier(arguments.get(1), supplierPostcode);
			logger.info("Successfully created supplier: " + arguments.get(1) + " at: " + arguments.get(2));
			break;
		case "INGREDIENT":
			//Check number of arguments
			if(arguments.size()!=7) {
				throw new ParsingException("Wrong number of arguments in :" + line);
			}
			
			Supplier itemSupplier = server.getSupplier(arguments.get(3));
			//Check whether given supplier exists
			if(itemSupplier==null) {
				throw new ParsingException("Wrong supplier: " + itemSupplier);
			}
			
			//Parse numbers
			Number restockThreshold = parseNumber(arguments.get(4));
			Number restockAmount = parseNumber(arguments.get(5));
			Number weight = parseNumber(arguments.get(6));
			
			//Add to server
			server.addIngredient(arguments.get(1), arguments.get(2), itemSupplier, restockThreshold, restockAmount, weight);
			logger.info("Successfully added ingredient: " + arguments.get(1));
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
	}
	
	/**
	 * Checks whether given String is a valid postcode
	 * @param postcode postcode to be chcked
	 * @return true, when given string is a valid postcode, otherwise false
	 */
	private boolean isValidPostcode(String postcode) {
		//UK valid postcode regex
		Pattern validPostcode = Pattern.compile("^([A-PR-UWYZ](([0-9](([0-9]|[A-HJKSTUW])?)?)|([A-HK-Y][0-9]([0-9]|[ABEHMNPRVWXY])?)) ?[0-9][ABD-HJLNP-UW-Z]{2})$");
		Matcher matcher = validPostcode.matcher(postcode);
		return matcher.matches();
	}
	
	/**
	 * Takes string and returns a {@link Number object}
	 * @param numberString String to be converted
	 * @return pares number
	 * @throws ParsingException when wrong format
	 */
	private Number parseNumber(String numberString) throws ParsingException {
		try {
			Double inputDouble = Double.parseDouble(numberString);
			if(inputDouble<0) {
				throw new ParsingException("Number cannot be negative: " + numberString);
			}
			Number result;
			if(inputDouble%1==0&&!numberString.contains(".")) {
				result = Integer.parseInt(numberString);
			}
			else {
				result = inputDouble;
			}
			return result;
		}
		catch (NumberFormatException nfe) {
			throw new ParsingException("Wrong number format in : " + numberString);
		}
	}
	
	/**
	 * Custom Exception for handling parsing errors
	 * @author wkr1u18
	 *
	 */
	class ParsingException extends Exception {
		private static final long serialVersionUID = 8220479200093993291L;
		String message;
		
		public ParsingException(String message) {
			this.message = message;
		}
		public String toString() {
			return "ParsingException: " + message;
		}
	}
}
