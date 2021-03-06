/*
  (c) copyright
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */
 
package eu.fluidforms.io;

/**
	Copywrite: Stephen Williams - stephen.williams@fluid-forms.com
*/

import java.io.File;
import java.io.InputStream;
import java.util.Vector;

import eu.fluidforms.geom.FTriangle;

public class STLConverter {

	public static final int SUCCESS = 1;
	public static final int FILE_NOT_READABLE = 2;
	public static final int DIRECTORY_NOT_WRITABLE = 3;
	public static final int INVALID_FILE_FORMAT = 4;

	public STLConverter(){
	}

	public static int convert(String fromFile, String toFile){
		int returnValue = SUCCESS;
		InputStream inputStream = null;
		
		if(!(new File(fromFile)).canRead()){
			return FILE_NOT_READABLE;
		}
		
		if(!(new File(toFile)).getParentFile().canWrite()){
			return DIRECTORY_NOT_WRITABLE;
		}

		try {
			System.out.println("Converting file: " + fromFile + "...");
			System.out.print("Parsing...");
	
			/*
			inputStream = IOUtils.openStream(fromFile);
			STLLexer lexer = new STLLexer(inputStream);
			STLParser parser = new STLParser(lexer);
			parser.parse();
	
			FSolid solid = (FSolid)parser.solids.get(0);
			System.out.print("Writing Triangles: " + solid.getTriangles().size() + "...");

			Vector<FTriangle> triangles = (Vector<FTriangle>)solid.getTriangles();
			*/
			STLWriter writer = new STLWriter(toFile, true);
			
			Vector<FTriangle> triangles = STLReaderFast.parse(fromFile);
			// I should add them all at once
			for(int i=0; i<triangles.size(); i++){
				writer.addTriangle(triangles.get(i));
			}
	
			
			writer.write();
			System.out.println("DONE");
			returnValue = SUCCESS;
//		} catch( RecognitionException e){
//			e.printStackTrace(); // so we can get stack trace
//			returnValue = INVALID_FILE_FORMAT;
//		} catch( TokenStreamException e){
//			e.printStackTrace(); // so we can get stack trace
//			returnValue = INVALID_FILE_FORMAT;
		} catch (Exception e) {
			System.err.println("parser exception: " + e);
			e.printStackTrace(); // so we can get stack trace
			returnValue = INVALID_FILE_FORMAT;
		}
		if(inputStream != null){
			try{
				inputStream.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return returnValue;
	}

	public static void main(String[] args) {

		String fromPath = IOUtils.trailingSlash(args[0]);
		String toPath = fromPath;

		if(args.length > 1){
			toPath = IOUtils.trailingSlash(args[1]);
		}


		while(true){
			File srcDir = new File(fromPath);
			String[] files = srcDir.list();
			if(files == null){
				System.err.println("ERROR :: Can not read input directory: " + fromPath);
			}
			for (int i = 0; i < files.length; i++) {
				String file = files[i];
				if(file.toLowerCase().indexOf(".stl") >= 0){
					File fromFile = new File(fromPath + file);
					File destFile = new File(toPath + file);
					int returnCode = STLConverter.convert(fromFile.getAbsolutePath(), destFile.getAbsolutePath());
					if(returnCode == SUCCESS){
						
						if(!fromFile.delete()){
							System.err.println("ERROR :: Could not remove ascii file: " + fromFile.getAbsolutePath());
						}
					}else if(returnCode == FILE_NOT_READABLE){
						System.err.println("ERROR :: Can not read file: " + fromFile.getAbsolutePath());
					}else if(returnCode == DIRECTORY_NOT_WRITABLE){
						System.err.println("ERROR :: Can not write to directory: " + toPath);
						return;
					} else if(returnCode == INVALID_FILE_FORMAT){
						System.err.println("ERROR :: Could not parse STL file.");
						if(!moveFile(fromFile, new File(fromPath + "defect/" + file))){
							System.err.println("ERROR :: Could not move file to : " + fromPath + "defect/" + file);
						}
					}
				}
			}
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){
			}
		}
		  
	}
	private static boolean moveFile(File fromFile, File destFile){
		if(destFile.exists()){
			return fromFile.delete();
		}else{
			return fromFile.renameTo(destFile);
		}
		
	}
}
