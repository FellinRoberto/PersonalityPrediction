package qa.qcri.qf.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.io.Files;

public class CommandLine {
	
	private final org.apache.commons.cli.CommandLine cl;
	
	//private final Options options;
	
	CommandLine(org.apache.commons.cli.CommandLine cl, Options options) {
		if (cl == null)
			throw new NullPointerException("cl is null");
		/*
		if (options == null)
			throw new NullPointerException("options is null");
		*/
		this.cl = cl;
		//this.options = options;
	}
	
	public String getOptionValue(String optionName, String errorMsg) throws ParseException { 
		checkNotNull(optionName, "optionName is null");
		checkNotNull(errorMsg, "errorMsg is null");
		
		if (cl.hasOption(optionName)) {
			return cl.getOptionValue(optionName);
		} else {
			throw new ParseException(errorMsg);
		}
	}
	
	public boolean hasOption(String optionName) {
		checkNotNull(optionName, "optionName is null");
		
		return cl.hasOption(optionName);
	}
	
	public String getPathValue(String optionName, String errorMsg) throws ParseException {
		checkNotNull(optionName, "optionName is null");
		checkNotNull(optionName, "errorMsg is null");
		
		//System.out.printf("cl.hasOption(%s): %s\n", optionName, cl.hasOption(optionName));
		if (cl.hasOption(optionName)) { 
			String path = cl.getOptionValue(optionName);
			//System.out.println("path: " + path);
			try {
				Files.createParentDirs(Paths.get(path).toFile());
			} catch (IOException e) { 
				throw new ParseException(errorMsg);
			}
			return path;
		}
		throw new ParseException(errorMsg);
	}
	
	public String getFileValue(String optionName, String errorMsg) 
		throws ParseException {
		checkNotNull(optionName, "optionName is null");
		checkNotNull(optionName, "errorMsg is null");
		
		if (cl.hasOption(optionName)) {
			String filepath = cl.getOptionValue(optionName);
			if (new File(filepath).exists()) { 
				return filepath;
			} 
		}
		throw new ParseException(errorMsg);
	}
	
	private static <T> T checkNotNull(T elem, String errorMsg) { 
		if (elem == null)
			throw new NullPointerException(errorMsg);
		return elem;
	}

}
