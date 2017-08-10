package util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

public class ClassPathPrint {

	public static void main(String[] args) {
		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> paramList = new ArrayList<String>();
		paramList.add(RuntimemxBean.getClassPath());
		
		System.out.println("export CLASSPATH=target/iyas.jar");

		for (String p : paramList) {
			for(String entry : p.split(":")) {
				String filteredEntry = entry.replace("/home/noname/workspace/Iyas/", "");
				
				if(filteredEntry.contains(".m2")) {
					filteredEntry = "${MAVEN_DEPS}" + filteredEntry.substring(filteredEntry.indexOf(".m2") + 3);
				}
				
				System.out.println("export CLASSPATH=${CLASSPATH}:" + filteredEntry);
			}
		}
	}
	
	public static void printRuntime() {
		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> paramList = new ArrayList<String>();
		paramList.add(RuntimemxBean.getClassPath());
		
		for (String p : paramList) {
			for(String entry : p.split(":")) {
				System.out.println(entry);
			}
		}
	}
}
