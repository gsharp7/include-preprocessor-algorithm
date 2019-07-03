import java.util.*;
import java.io.*;

class CImporter{

	ArrayList<String> filesIncluded = new ArrayList<String>();
	public String output = "";
	static final String sysls = System.getProperty("line.separator");

	public static void main(String args[]) throws IOException{
		
		CImporter ci = new CImporter();

		Scanner sc = new Scanner(System.in);
		System.out.print("Specify the name of the input file: ");

		ci.initImports(sc.next());
		ci.writeOutputToFile();
	}

	void initImports(String filename){
		ArrayList<String> currentFileImports = getImports(filename);

		//loops through all of the includes of the current file
		for(int i = 0; i < currentFileImports.size(); i++){

			//get the includes/imports of the included files
			initImports(currentFileImports.get(i));

		}
		if(!filesIncluded.contains(filename)){
			System.out.println("adding " + filename + " ... ");
			toOutput(fileContentToString(filename));
			filesIncluded.add(filename);
		}
	}
	
	/* NOTE: by default, Java's working directory is where the executable (.jar) is generated.
	 * This program assumes that the input files are all located in the classpath.
	 */
	//reads the content of a file (in the class's directory) and convert the contents to string
	String fileContentToString(String filename){
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
		Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
    	return s.hasNext() ? s.next() : "";
	}

	ArrayList<String> getImports(String filename){

		ArrayList<String> imports = new ArrayList<String>();
		String fileContent = fileContentToString(filename);

		//parses the file's content to look for #include tokens
		for(int i = 0; i < fileContent.length(); i++){
			if(i + 8 <= fileContent.length()){
				if(fileContent.substring(i, i + 8).equals("#include")){
					i = i + 8;
					//loops through any excess spaces after the #include token until the string for the filename is detected
					while(fileContent.charAt(i) != '"'){
						i++;
					}
					i++;
					String importFile = "";
					while(fileContent.charAt(i) != '"'){
						importFile = importFile + fileContent.charAt(i);
						i++;
					}
					//adds the included file's name to the list of includes for the current file
					imports.add(importFile);
				}			
			}
			else{
				break;
			}
		}

		return imports;
	}

	void toOutput(String s){
		output = output + s + sysls + sysls;
	}

	//strips the #include tokens in the final output string that will be written to the ouput file.
	String stripIncludes(String s){

		for(int i = 0; i < s.length(); i++){
			if(i + 8 <= s.length()){
				if(s.substring(i, i + 8).equals("#include")){
					int j = i, qcount = 0;
					i = i + 8;
					while(qcount != 2){
						if(s.charAt(i) == '"')
							qcount++;
						i++;
					}
					s = s.substring(0, j) +  s.substring(i, s.length());
					i = 0;
				}
			}
			else{
				break;
			}
		}
		//trims any excess line breaks
		s = s.replace(sysls + sysls + sysls, sysls);
		return s;
	}

	//writes the final output to a file named "outputFile.out" in the classpath
	void writeOutputToFile() throws java.io.IOException{
		try {
			File f = new File("outputFile.out");
			FileWriter fileWriter = new FileWriter(f);
			fileWriter.write(stripIncludes(output));
			fileWriter.flush();
			fileWriter.close();
			System.out.println("\nSuccessfully written output to 'outputFile.out'...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}