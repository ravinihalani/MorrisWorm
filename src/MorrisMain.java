
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MorrisMain {

	private static Pattern VALID_IPV4_PATTERN = null;
	private static Pattern VALID_IPV6_PATTERN = null;
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private static final String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";

	static {
		try {
			VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
			VALID_IPV6_PATTERN = Pattern.compile(ipv6Pattern, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			// logger.severe("Unable to compile pattern", e);
		}
	}

	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

	public static boolean isIpAddress(String ipAddress) {

		Matcher m1 = MorrisMain.VALID_IPV4_PATTERN.matcher(ipAddress);
		if (m1.matches()) {
			return true;
		}
		if (ipAddress.contains("::"))
			return true;
		Matcher m2 = MorrisMain.VALID_IPV6_PATTERN.matcher(ipAddress);
		return m2.matches();

	}
public static String specialformat(String line)
{
	if(line.indexOf("[") == 0 && line.length() >= 5 && line.indexOf("]") > 1
			&& line.indexOf(":") == (line.indexOf("]") + 1) && MorrisMain.isInteger(line.substring(line.indexOf(":")+1,line.length()-1),10))
		{
	return line.substring(1, line.indexOf("]"));
		}
	else
		return line;
	
}
	public static boolean isInteger(String s, int radix) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), radix) < 0)
				return false;
		}
		return true;
	}

	public static void main (String[] args) throws IOException {
		
		List<String> non_distinct_hosts = new ArrayList<String>();

		/* GETTING ALL USER DIRECTORIES LOGIC */
		

		MorrisMain obj = new MorrisMain();
		String command = "cut -d: -f 6 /etc/passwd";
		String output = obj.executeCommand(command);
		String all_users_directory[] = output.split("\n");
		String line;
		String temparray[];
		String dummyop="";

		/* 1. Scanning /etc/hosts for hosts */
		try {
	//	dummyop = obj.executeCommand("chmod 777 /etc/hosts  > /dev/null 2>&1");
		FileReader etc_hosts = new FileReader("/etc/hosts");
		BufferedReader br1 = new BufferedReader(etc_hosts);
		int num = 0;
		
		//System.out.println(
		//		"1--> The list of all host names known and possibly trusted by the current host in /etc/hosts are :");

		while ((line = br1.readLine()) != null) {
			line = line.replaceAll("\\s+", " ").trim();
			if (line.indexOf("#") >= 0) // the line starting from # till EOL is
										// a comment hence skip it
			{
				line = line.replaceAll(line.substring(line.indexOf("#"), line.length()), "");
			}
			temparray = line.split(" ");
			for (int i = 0; i < temparray.length; i++) {
				if (isIpAddress(temparray[i])) {
					//System.out.print(++num + ". ");

					{
						for (int j = i + 1; j < temparray.length; j++) {
							{
								if(!isIpAddress(temparray[j]) && (Pattern.matches("[a-zA-Z]+", ""+temparray[j].charAt(0))) && (Character.isLetterOrDigit(temparray[j].charAt(temparray[j].length()-1))))
								{
							//System.out.print(temparray[j]);
							non_distinct_hosts.add(temparray[j]);
								}
								}
							if (j < temparray.length - 1)
								{
							//	System.out.print(" OR ");
							}
						}
					}
					//System.out.println();
					break;
				}
			}
		}
		etc_hosts.close();
		}
		catch (FileNotFoundException exception) {
			
		}
		//System.out.println();
		
		/* 2a.Scanning ~/.ssh/config for each user */

		otherfile2a: for (int u = 0; u < all_users_directory.length; u++) {
			try {
				//System.out.println("number of user="+valid_users.length);
			//  dummyop = obj.executeCommand("chmod 777 "+all_users_directory[u]+ "/.ssh/config  > /dev/null 2>&1");
				FileReader config = new FileReader(all_users_directory[u] + "/.ssh/config");
				//System.out.println(all_users_directory[u] + "/.ssh/config");
				BufferedReader br2a = new BufferedReader(config);
				int num2 = 0;
				String temparray2[];
				//System.out.println(
						//"2a-->The list of all host names known and possibly trusted by the current host in "+all_users_directory[u]+"/.ssh/config are :");

				while ((line = br2a.readLine()) != null) {

					line = line.replaceAll("\\s+", " ").trim();
					line =line.toLowerCase().replace("hostname=","hostname");
					line =line.toLowerCase().replace("hostname =","hostname");
					line =line.toLowerCase().replace("host=","host");
					line =line.toLowerCase().replace("host =","host");
					line = line.replace("!", "");
					line = line.replace("*.", "");
					line = line.replace("*", "");
					if (line.indexOf("#") >= 0) // the line starting from # till EOL is a comment hence skip
					{
						line = line.replaceAll(line.substring(line.indexOf("#"), line.length()), "");
					}

					// line2 = line2.toLowerCase();

					if (line.toLowerCase().contains("hostname") ) {
						temparray2 = line.split(" ");
								if ((temparray2[0].toLowerCase().equals("hostname") ) && temparray2[1].length() > 0 && !isIpAddress(temparray2[1])) {
								for (int i = 1; i < temparray2.length; i++) 
								{
									if(temparray2[i].contains("@"))
									{
									//	System.out.println(++num2 + ". " + temparray2[i].substring(temparray2[i].indexOf("@")+1,temparray2[i].length()));
										non_distinct_hosts.add(temparray2[i].substring(temparray2[i].indexOf("@")+1,temparray2[i].length()));
									}
									else
									{
									//	System.out.println(++num2 + ". " + temparray2[i]);
										non_distinct_hosts.add(temparray2[i]);
									}
								
								}
							}
						
					}
					if (line.toLowerCase().contains("host") && !line.toLowerCase().contains("hostname")) {
						temparray2 = line.split(" ");
						if (temparray2.length>1)
						{
								if ((temparray2[0].toLowerCase().equals("host") ) && temparray2[1].length() > 0 && !isIpAddress(temparray2[1])) {
								for (int i = 1; i < temparray2.length; i++) 
								{
									if(temparray2[i].contains("@"))
									{
									//	System.out.println(++num2 + ". " + temparray2[i].substring(temparray2[i].indexOf("@")+1,temparray2[i].length()));
										non_distinct_hosts.add(temparray2[i].substring(temparray2[i].indexOf("@")+1,temparray2[i].length()));
									}
									else
									{
									//	System.out.println(++num2 + ". " + temparray2[i]);
										non_distinct_hosts.add(temparray2[i]);
									}
								}
							}
						}
					}
					
		/*			if (line.toLowerCase().contains("host") && !line.toLowerCase().contains("hostname")) {
						temparray2 = line.split(" ");
						if (temparray2.length>1)
						{
							if (line.indexOf("[") == 0 && line.length() >= 5 && line.indexOf("]") > 1
					&& line.indexOf(":") == (line.indexOf("]") + 1) && MorrisMain.isInteger(line.substring(line.indexOf(":")+1,line.length()-1),10))
				{
				if (isIpAddress(line.substring(1, line.indexOf("]")))) 
				continue;
				
				System.out.println(++num2 + ". " + line.substring(1, line.indexOf("]")));
				non_distinct_hosts.add(line.substring(1, line.indexOf("]")));
			}
					
				}
					} */
				}
				config.close();
				//System.out.println();

			}
			catch (FileNotFoundException exception) {
				continue otherfile2a;
			}
		}
		//System.out.println();

		/* 2b.Scanning /etc/ssh/ssh_config for hosts */
		
		try {
	//	dummyop = obj.executeCommand("chmod 777 /etc/ssh/ssh_config  > /dev/null 2>&1");
		FileReader ssh_config = new FileReader("/etc/ssh/ssh_config");
		BufferedReader br2 = new BufferedReader(ssh_config);
		int num2 = 0;
		String temparray2[];
		//System.out.println(
			//	"2b-->The list of all host names known and possibly trusted by the current host in /etc/ssh/ssh_config are :");

		while ((line = br2.readLine()) != null) {
			line = line.replaceAll("\\s+", " ").trim();
			line =line.toLowerCase().replace("hostname=","hostname");
			line =line.toLowerCase().replace("hostname =","hostname");
			line =line.toLowerCase().replace("host=","host");
			line =line.toLowerCase().replace("host =","host");
			line = line.replace("!", "");
			line = line.replace("*.", "");
			line = line.replace("*", "");
			if (line.indexOf("#") >= 0) // the line starting from # till EOL is a comment hence skip
			{
				line = line.replaceAll(line.substring(line.indexOf("#"), line.length()), "");
			}

			// line2 = line2.toLowerCase();

			if (line.toLowerCase().contains("hostname") ) {
				temparray2 = line.split(" ");
						if ((temparray2[0].toLowerCase().equals("hostname") ) && temparray2[1].length() > 0 && !isIpAddress(temparray2[1])) {
						for (int i = 1; i < temparray2.length; i++) 
						{
							if(temparray2[i].contains("@"))
							{
						//		System.out.println(++num2 + ". " + temparray2[i].substring(temparray2[i].indexOf("@")+1,temparray2[i].length()-1));
								non_distinct_hosts.add(temparray2[i].substring(temparray2[i].indexOf("@")+1,temparray2[i].length()-1));
							}
							else
							{
						//		System.out.println(++num2 + ". " + temparray2[i]);
								non_distinct_hosts.add(temparray2[i]);
							}
						
						}
					}
				
			}
			if (line.toLowerCase().contains("host") && !line.toLowerCase().contains("hostname")) {
				temparray2 = line.split(" ");
				if (temparray2.length>1)
				{
						if ((temparray2[0].toLowerCase().equals("host") ) && temparray2[1].length() > 0 && !isIpAddress(temparray2[1])) {
						for (int i = 1; i < temparray2.length; i++) 
						{
							if(temparray2[i].contains("@"))
							{
						//		System.out.println(++num2 + ". " + temparray2[i].substring(temparray2[i].indexOf("@"),temparray2[i].length()-1));
								non_distinct_hosts.add(temparray2[i].substring(temparray2[i].indexOf("@"),temparray2[i].length()-1));
							}
							else
							{
						//		System.out.println(++num2 + ". " + temparray2[i]);
								non_distinct_hosts.add(temparray2[i]);
							}
						}
					}
				}
						
						
				
			}
			
			/*		if (temparray4[i].indexOf("[") == 0 && temparray4[i].length() >= 5 && temparray4[i].indexOf("]") > 1
			&& temparray4[i].indexOf(":") == (temparray4[i].indexOf("]") + 1) && MorrisMain.isInteger(temparray4[i].substring(temparray4[i].indexOf(":")+1,temparray4[i].length()-1),10))
		{
//		if (isIpAddress(temparray4[i].substring(1, temparray4[i].indexOf("]")))) 
//		continue;
		
//		System.out.println(++num4 + ". " + temparray4[i]);
		non_distinct_hosts.add(temparray4[i]);
	}*/
			
			
		}
		ssh_config.close();
		}
		catch (FileNotFoundException exception)
		{
			
		}
		//System.out.println();

		/* 3.Scanning ~/.ssh/authorized_keys for each user */

		otherfile: for (int u = 0; u < all_users_directory.length; u++) {
			try {
				//System.out.println("number of user="+valid_users.length);
			//	dummyop = obj.executeCommand("chmod 777 "+all_users_directory[u]+ "/.ssh/authorized_keys  > /dev/null 2>&1");
				FileReader authorized_keys = new FileReader(all_users_directory[u] + "/.ssh/authorized_keys");
				//System.out.println(all_users_directory[u] + "/.ssh/authorized_keys");
				BufferedReader br3 = new BufferedReader(authorized_keys);
			//	int num3 = 0;

				String temparray3[];
				String hostarray_ak[];
				//System.out.println(
					//	"3-->The list of all host names known and possibly trusted by the current host in "+ all_users_directory[u] + "/.ssh/authorized_keys is :");

				while ((line = br3.readLine()) != null) {
					line = line.replaceAll("\\s+", " ").trim();
					if (line.indexOf("#") == 0) {
						continue; // Comments allowed at start of line hence the
									// line is
									// to be ignored
					}

					temparray3 = line.split(" ");
					for (int i = 0; i < temparray3.length; i++) {
						if (temparray3[i].contains("@") && !isIpAddress(temparray3[i].substring(temparray3[i].indexOf("@")+1, temparray3[i].length()))) {
						//	System.out.println(++num3 + ". "
							//		+ temparray3[i].substring(temparray3[i].indexOf("@")+1, temparray3[i].length()));
							non_distinct_hosts.add(temparray3[i].substring(temparray3[i].indexOf("@")+1, temparray3[i].length()));
						}

						if (temparray3[i].contains("from=")) {
							hostarray_ak = temparray3[i].substring(6, temparray3[i].length() - 1).split(",");
							for (int k = 0; k < hostarray_ak.length; k++)
								if (!isIpAddress(hostarray_ak[k]))
								{
						//			System.out.println(++num3 + ". " + hostarray_ak[k]);
									non_distinct_hosts.add(hostarray_ak[k]);
								}
						}
						if (temparray3[i].contains("permitopen=")) {
							hostarray_ak = temparray3[i].split(",");
							for (int k = 0; k < hostarray_ak.length; k++)
								if (!isIpAddress(hostarray_ak[k].substring(hostarray_ak[k].indexOf("=")+2, hostarray_ak[k].indexOf(":"))))
								{
								//	System.out.println("ravi test ="+hostarray_ak[k].substring(hostarray_ak[k].indexOf("=")+2, hostarray_ak[k].indexOf(":")-1));
							    //	System.out.println(++num3 + ". " + hostarray_ak[k].substring(hostarray_ak[k].indexOf("=")+2, hostarray_ak[k].indexOf(":")));
									non_distinct_hosts.add(hostarray_ak[k].substring(hostarray_ak[k].indexOf("=")+2, hostarray_ak[k].indexOf(":")));
								}
							
						}

					}
				}
				authorized_keys.close();
	//			System.out.println();
			} catch (FileNotFoundException exception) {
				continue otherfile;
			}
			

		}
		
		/* 4a.Scanning ~/.ssh/known_hosts for each user */
		
	 for (int u = 0; u < all_users_directory.length; u++) {
		
		try {
		//	dummyop = obj.executeCommand("chmod 777 "+all_users_directory[u]+ "/.ssh/known_hosts  > /dev/null 2>&1");
			FileReader known_hosts = new FileReader(all_users_directory[u] + "/.ssh/known_hosts");
//System.out.println(all_users_directory[u] + ".ssh/known_hosts");
			BufferedReader br4 = new BufferedReader(known_hosts);
			int num4 = 0;

			String temparray4[];
			String hostarray[];
			//System.out.println(
				//	"4b-->The list of all host names known and possibly trusted by the current host in /etc/ssh/ssh_known_hosts are :");

			while ((line = br4.readLine()) != null) {
				line = line.replaceAll("\\s+", " ").trim();
				if (line.indexOf("#") == 0) {
					continue; // Comments allowed at start of line hence the line is
								// to be ignored
				}
				if (line.contains("ssh-rsa"))
				{
					line=line.substring(0,line.indexOf("ssh-rsa")-1);
				}
				line=line.replaceAll(", ", ",");
				line=line.replaceAll(" ,", ",");
				temparray4 = line.split(" ");
				if (temparray4.length==1)
				{
					line=line+",";
					temparray4[0]=temparray4[0]+",";
				}
				if ((temparray4[0].equals("@revoked") || temparray4[0].equals("@cert-authority") ) && (temparray4.length>1))
				{
					for (int j = 1; j < temparray4.length; j++)
					{
						temparray4[j]=temparray4[j]+",";	
					}
				}
				for (int i = 0; i < temparray4.length; i++) {
					if (temparray4[i].equals("*"))
					{
						//	System.out.println(++num4 + ". " + temparray4[i]);
						non_distinct_hosts.add(temparray4[i]);	
					}
					if ( temparray4[i].charAt(0)=='|' || temparray4[i].equals("ssh-rsa"))
					{
					continue;	
					} 
				
				
					if (temparray4[i].contains(",")) {
						hostarray = temparray4[i].split(",");
						for (int k = 0; k < hostarray.length; k++)
							if (!isIpAddress(hostarray[k]) && !hostarray[k].contains("ssh-rsa") )
							{
								//System.out.println(++num4 + ". " + hostarray[k]);
								non_distinct_hosts.add(hostarray[k]);
							}
					}
			/*		if (temparray4[i].indexOf("[") == 0 && temparray4[i].length() >= 5 && temparray4[i].indexOf("]") > 1
							&& temparray4[i].indexOf(":") == (temparray4[i].indexOf("]") + 1) && MorrisMain.isInteger(temparray4[i].substring(temparray4[i].indexOf(":")+1,temparray4[i].length()-1),10))
						{
				//		if (isIpAddress(temparray4[i].substring(1, temparray4[i].indexOf("]")))) 
				//		continue;
						
				//		System.out.println(++num4 + ". " + temparray4[i]);
						non_distinct_hosts.add(temparray4[i]);
					}*/
					
					
				}
			}
			known_hosts.close();
			}
			catch(FileNotFoundException exception)
			{
			} 
		}
	//	System.out.println();
		/* 4b.Scanning /etc/ssh/ssh_known_hosts for hosts */
		
		 try {
	//		 dummyop = obj.executeCommand("chmod 777 /etc/ssh/ssh_known_hosts  > /dev/null 2>&1");
		FileReader ssh_known_hosts = new FileReader("/etc/ssh/ssh_known_hosts");
		BufferedReader br4 = new BufferedReader(ssh_known_hosts);
		int num4 = 0;

		String temparray4[];
		String hostarray[];
		//System.out.println(
			//	"4b-->The list of all host names known and possibly trusted by the current host in /etc/ssh/ssh_known_hosts are :");

		while ((line = br4.readLine()) != null) {
			line = line.replaceAll("\\s+", " ").trim();
			if (line.indexOf("#") == 0) {
				continue; // Comments allowed at start of line hence the line is
							// to be ignored
			}
			if (line.contains("ssh-rsa"))
			{
				line=line.substring(0,line.indexOf("ssh-rsa")-1);
			}
			line=line.replaceAll(", ", ",");
			line=line.replaceAll(" ,", ",");
			temparray4 = line.split(" ");
			if (temparray4.length==1)
			{
				line=line+",";
				temparray4[0]=temparray4[0]+",";
			}
			if ((temparray4[0].equals("@revoked") || temparray4[0].equals("@cert-authority") ) && (temparray4.length>1))
			{
				for (int j = 1; j < temparray4.length; j++)
				{
					temparray4[j]=temparray4[j]+",";	
				}
			}
			for (int i = 0; i < temparray4.length; i++) {
				if (temparray4[i].equals("*"))
				{
				//System.out.println(++num4 + ". " + temparray4[i]);
					non_distinct_hosts.add(temparray4[i]);	
				}
				if ( temparray4[i].charAt(0)=='|' || temparray4[i].equals("ssh-rsa"))
				{
				continue;	
				} 
			
			
				if (temparray4[i].contains(",")) {
					hostarray = temparray4[i].split(",");
					for (int k = 0; k < hostarray.length; k++)
						if (!isIpAddress(hostarray[k]) && !hostarray[k].contains("ssh-rsa") )
						{
							//System.out.println(++num4 + ". " + hostarray[k]);
							non_distinct_hosts.add(hostarray[k]);
						}
				}
		/*		if (temparray4[i].indexOf("[") == 0 && temparray4[i].length() >= 5 && temparray4[i].indexOf("]") > 1
						&& temparray4[i].indexOf(":") == (temparray4[i].indexOf("]") + 1) && MorrisMain.isInteger(temparray4[i].substring(temparray4[i].indexOf(":")+1,temparray4[i].length()-1),10))
					{
			//		if (isIpAddress(temparray4[i].substring(1, temparray4[i].indexOf("]")))) 
			//		continue;
					
			//		System.out.println(++num4 + ". " + temparray4[i]);
					non_distinct_hosts.add(temparray4[i]);
				}*/
				
				
			}
		}
		ssh_known_hosts.close();
		}
		catch(FileNotFoundException exception)
		{
		}
	//	System.out.println();
		
		Set<String> set = new HashSet<String>(non_distinct_hosts);
		ArrayList<String> uniqueList = new ArrayList<String>(set);
		int del =0;
		Enumeration<String> e = Collections.enumeration(uniqueList);
		//System.out.println("The list of hosts discovered :");
		//System.out.println("-----------------------------");
		String t="";
		while(e.hasMoreElements())
		{
			t=e.nextElement();
			t= MorrisMain.specialformat(t);
		if(!isIpAddress(t) && !t.equals("=") )
		System.out.println(t);

			
	//	System.out.println(++del +". "+ e.nextElement());
		}  

	}
}
