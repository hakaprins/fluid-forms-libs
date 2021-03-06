package eu.fluidforms.processing;

import java.io.BufferedOutputStream;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import processing.core.PApplet;

public class DownloadUtils {
	static PApplet p5;
	
	public DownloadUtils(PApplet p5){
		DownloadUtils.p5 = p5;
	}
	
	/**
	 * Downloads a file whilst displaying its progress.
	 * @param urlString
	 * @param name
	 */
	public static void downloadFile(String urlString, String name){
		downloadFile(urlString, name, false);
	}
	/**
	 * Downloads a file whilst displaying its progress.
	 * @param url The URL to download the file from.
	 * @param name The path to save the file at, relative to the data directory.
	 * @param overwrite If we should overwrite an existing file or not.
	 */
	public static void downloadFile(String url, String name, boolean overwrite){
		PApplet.println("Downloading "+url);
		try{
			if(FluidFormsLibs.p5 != null){
				p5 = FluidFormsLibs.p5;
			}
			
			if(p5!=null){
				name = p5.dataPath(name);
			}else{
				if(name.indexOf("/")!=0 && name.indexOf(":")<0){
					name = System.getProperty("user.dir")+File.separator+name;
				}
			}
			if(!overwrite){
				File file = new File(name);
				if(file.exists()){
					PApplet.println("File " + name + " already exists.");
					return;
				}
			}

			URL url1 = new java.net.URL(url);
			URLConnection conn;
			int size;
			conn = url1.openConnection();
			size = conn.getContentLength();
			String sSize = "" + Math.round((float)size/1000);
			if(size<=0){
				sSize = "Lots of";
			}
			PApplet.println("Expecting " + sSize + " Kb");
			
			conn.setConnectTimeout(1000*60*20);
			conn.setReadTimeout(1000*60*20);
			java.io.BufferedInputStream in = new java.io.BufferedInputStream(conn.getInputStream());
			
			// in = new java.io.BufferedInputStream(url1.openStream());
			java.io.FileOutputStream fos = new java.io.FileOutputStream(name);
			java.io.BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
			byte data[] = new byte[32768];
			float bytesDownloaded = 0;
			float percentStep = 0.1f;
			float percentDownloaded = -percentStep;
			int count;
			while( (count = in.read(data,0,1024)) != -1)
			{
				bytesDownloaded += count;
				bout.write(data,0,count);
				if(bytesDownloaded/size > percentDownloaded+percentStep){
					if(percentDownloaded>=0){
						PApplet.println("");
					}
					percentDownloaded += percentStep;
					PApplet.print(Math.round(percentDownloaded*100)+"%");
				}
				PApplet.print(".");
			}
			bout.close();
			fos.close();
			in.close();
			
			PApplet.println("");
			PApplet.println("100%");
			PApplet.println("Saved to "+name);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
