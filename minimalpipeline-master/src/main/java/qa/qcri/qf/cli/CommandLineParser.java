package qa.qcri.qf.cli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.io.Files;

public class CommandLineParser {
	
	//private static String HELP_OPT = "help"; // -help option
	
	private static String ARGUMENTS_FILEPATH_OPT = "argumentsFilePath"; // -argumentsFilepath option
	
	private static HelpFormatter helpFormatter = new HelpFormatter();
	
	private static Logger logger = Logger.getLogger(CommandLineParser.class.getName());
		
	public CommandLine parse(Options options, String[] args) throws ParseException {
		checkNotNull(options, "options is null");
		checkNotNull(options, "args is null");

		org.apache.commons.cli.CommandLineParser parser = new BasicParser();
		org.apache.commons.cli.CommandLine cl = parser.parse(options, args);
		
		try {
			if (cl.hasOption(ARGUMENTS_FILEPATH_OPT)) {
				String argumentsFilepath = cl.getOptionValue(ARGUMENTS_FILEPATH_OPT);
				logger.info("Reading arguments from file: '" + argumentsFilepath + "'");
				String[] newArgs = readArgs(argumentsFilepath);
				cl = new BasicParser().parse(options, newArgs);			
			}
		} catch (IOException e) { 
			throw new ParseException(e.getMessage());
		}
		
		return new CommandLine(cl, options);
	}
	
	/*
	public boolean hasOption(String optionName) {
		checkNotNull(optionName, "optionName is null");
		
		return cl.hasOption(optionName);
	}
	*/
	
	public void printHelp(String className, Options options) { 
		checkNotNull(className, "className is null");
		checkNotNull(options, "options is null");
		
		helpFormatter.printHelp(className, options);
	}
	
	public void printHelpAndExit(String className, Options options) {
		printHelp(className, options);
		System.exit(0);
	}
	
	private String[] readArgs(String argumentsFilepath) throws IOException {
		if (argumentsFilepath == null)
			throw new NullPointerException("argumentsFilepath is null");
		
		List<String> lines = Files.readLines(
				new File(argumentsFilepath), Charset.defaultCharset());
		return Joiner.on(" ").join(lines).split(" "); 
	}
	
	private static <T> T checkNotNull(T elem, String errorMsg) { 
		if (elem == null)
			throw new NullPointerException(errorMsg);
		return elem;
	}
	
	


}
