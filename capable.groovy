definition(
    name: "capable",
    namespace: "sottey",
    author: "Sean Ottey",
    description: "Shows all capabilities and (optionally) the capabilities' Attributes and Commands for selected devices in HTML or (optionally) CSV formats",
    category: "Utility",
    iconUrl: "",
    iconX2Url: "")

preferences {
	
	// no 'def' to make these accessible by other methods
	listError = "";
	listOutput = "";
	
	// Page definition
	page(name: "mainPage", title: "<CENTER><B><H1>capable</H1></B></CENTER>", install: true, uninstall: true) 
	{	
		// Device section
		section("<HR><H2>DEVICE CAPABILITIES</H2>")
		{				
			// If we don't have the info, indicate this
 			if (devices == null || devices.size() < 0)
			{
				paragraph 'No Devices Selected. Scroll down to select.';
			}
			else // We have the info
			{
				// Sort by device name
				def sortedDevices = devices.sort(){it.displayName};
								
				if (csvFormat) // CSV format
				{
					// CSV always shows extra information
					showExtra = true;
					
					// CSV Column Header
					listOutput += "NAME (TYPE),CAPABILITY,ATTRIBS,COMMANDS\n"
					
					// For each device
					sortedDevices.each() 
					{ 
						device ->
						
						ShowDeviceCSV(device);
					} // end each device					
				} // End CSV format
				else // HTML format
				{
					// For each device
					sortedDevices.each() 
					{ 
						device ->
						
						ShowDeviceStd(device);
					} // end each device		
				} // End HTML format
				
				// Show compiled results
				if (listOutput != null && listOutput.size() > 0) paragraph listOutput;
				
				// Show any errors that occurred
				if (listError != null &&listError.size() > 0) paragraph "<H3 style='color:red;'>ERRORS</H3>" + listError;
				
			} // end else device count not 0
		} // end device section
		
		// Settings Section
		section("<HR><H2>SETTINGS</H2>") 
		{
			input name:"devices",type:"capability.*",title:"Which devices should be displayed?",multiple:true,required:true
			input name: "showExtra", type: "bool", title: "Show Attributes and Commands for each Device's Capabilities", defaultValue: true
			input name: "csvFormat", type: "bool", title: "Display in CSV format (If true, showing attributes and commands will be done regardless of above option)", defaultValue: false
			href "mainPage", title:"Refresh", description:""
		} // end settings section
		
	}
}

// Display a device using HTML formatting
def ShowDeviceStd(device) {
	try 
	{	
		listOutput += "<HR><H3>" + device.displayName + " (" + device.name + ")</H3><UL>";

		for (cap in device.capabilities)
		{
			listOutput += "<LI><B>Capability:</B> " + cap.toString() + "</LI>";

			if (showExtra)
			{
				listOutput += "<I>Attributes:</I>\n";
				if (cap.attributes != null && cap.attributes.size() > 0)
				{
					listOutput += "<UL>";

					for (attrib in cap.attributes)
					{
						listOutput += "<LI>" + attrib + "</LI>";
					}
					listOutput += "</UL>";
				}
				else
				{
					listOutput += "<UL><LI>None</LI></UL>"
				}

				listOutput += "<I>Commands:</I>\n";
				if (cap.commands != null && cap.commands.size() > 0)
				{							
					listOutput += "<UL>";

					for (command in cap.commands)
					{
						listOutput += "<LI>" + command + "</LI>";
					}

					listOutput += "</UL>";
				}
				else
				{
					listOutput += "<UL><LI>None</LI></UL>"
				}
			}
		}

		listOutput += "</UL>";

	} // end try
	catch (e) 
	{
		def trace = e.getStackTrace();
		def currError = "<B>" + device.displayName  + "</B> : \n" + e + "\n\n";
		listError += currError;
	} //end catch	
}

// Display a device using CSV formatting
def ShowDeviceCSV(device) {
	try 
	{	
		for (cap in device.capabilities)
		{
            // Display device name
            listOutput += device.displayName + " (" + device.name + ")";	

			if (showExtra)
			{
				listOutput += "," + cap;
				
				if (cap.attributes != null && cap.attributes.size() > 0)
				{
					listOutput += ",";
					
                    def i = 0;
					for (attrib in cap.attributes)
					{
                        i++;

						listOutput += "" + attrib;

                        if (i < cap.attributes.size() && cap.attributes.size() > 1)
                        {
                            listOutput += ";";
                        }

					}
				}
				else
				{
					listOutput += ",None";
				}

				if (cap.commands != null && cap.commands.size() > 0)
				{							
					listOutput += ",";
					
                    def i = 0;
					for (command in cap.commands)
					{
                        i++;
                        
						listOutput += "" + command;

                        if (i < cap.commands.size() && cap.commands.size() > 1)
                        {
                            listOutput += ";";
                        }
					}
				}
				else
				{
					listOutput += ",None"
				}
			}
		    
            listOutput += "<br/>";
        }

		

	} // end try
	catch (e) 
	{
		def trace = e.getStackTrace();
		def currError = "<B>" + device.displayName  + "</B> : \n" + e + "\n\n";
		listError += currError;
	} //end catch	

}

def installed() {
	log.trace "capable:installed()"
	updated()
}

def uninstalled() {
    unsubscribe()
	log.trace "capable:uninstalled()"
}

def updated() {
    unsubscribe()
	if (logEnable) log.trace "capable:unsubscribe()"
	
	// Subscribe to all selected devices
	settings.devices.each() 
	{
		subscribe(it, null, null)
	}
	
	if (logEnable) log.trace "capable:subscribe()"

}
