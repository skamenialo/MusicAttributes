package com.skamenialo.musicattributes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
public class Music
{
	static PrintWriter writer;
	static ProgressMonitor monitor;
	static int progress;
	public static void main(String args[])
	{	
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int fileResult = jfc.showOpenDialog(null);
    	if (fileResult == JFileChooser.APPROVE_OPTION) {
    		int i=getFilesCount(jfc.getSelectedFile());
    		monitor=new ProgressMonitor(null, "Progress", "", 0, i);
    		monitor.setProgress(0);
    		progress=0;
    		try {
    			writer = new PrintWriter("text.txt", "UTF-8");
    		} catch (FileNotFoundException | UnsupportedEncodingException e) {
    			JOptionPane.showMessageDialog(null, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
    		directory(jfc.getSelectedFile());
    		JOptionPane.showMessageDialog(null, "Saved in 'text.txt'", "Done", JOptionPane.INFORMATION_MESSAGE);
        	writer.close();
    	}
	}

	public static int getFilesCount(File file) {
		File[] files = file.listFiles();
		int count = 0;
		for (File f : files)
			if (f.isDirectory())
				count += getFilesCount(f);
			else
				count++;

		return count;
	}
	public static void directory(File dir){
	    File[] files = dir.listFiles();
	    int i=1;
	    for(File file:files){
	        printFileString(i,file);
	        i++;
	        progress++;
	        monitor.setProgress(progress);
		    if(monitor.isCanceled()){
	    		JOptionPane.showMessageDialog(null, "Saved in 'text.txt'", "Cancelled", JOptionPane.WARNING_MESSAGE);
		    	writer.close();
		    	System.exit(1);
		    }
	        if(file.listFiles() != null){
	    	    writer.println();
		    	System.out.println(file.getName());
	    	    writer.println(file.getName());
	            directory(file);        
	        }
	    }
	}
	public static void printFileString(int i,File file){
		try {
			Mp3File mp3file = new Mp3File(file);
			int m=(int)Math.floor(mp3file.getLengthInSeconds()/60.0);
			int s=(int)(mp3file.getLengthInSeconds()-m*60);
			String time=m+":"+(s<10?"0"+s:s);
			String name;
	        if (mp3file.hasId3v1Tag()) {
	          ID3v1 id3v1Tag = mp3file.getId3v1Tag();
	          name=id3v1Tag.getTitle();
	        }
	        else{
	            name=file.getName().substring(0, file.getName().length()-4);
	        }
	        name=name.replaceAll("_", " ");
	        int j;
	        j=name.toLowerCase().indexOf("feat");
	        if(j>0)
	        	name=name.substring(0,j);
	        j=name.toLowerCase().indexOf("live");
	        if(j>0)
	        	name=name.substring(0,j);
	        j=name.toLowerCase().indexOf("life");
	        if(j>0)
	        	name=name.substring(0,j);
	        j=name.indexOf("(");
	        if(j>0&&j==name.length()-1)
	        	name=name.substring(0,j);
	        j=name.indexOf("[");
	        if(j>0)
	        	name=name.substring(0,j);
	        j=name.toLowerCase().lastIndexOf(" ");
	        while(j==name.length()-1){
	        	name=name.substring(0,j);
		        j=name.toLowerCase().lastIndexOf(" ");
	        }
	        name=(i<10?"0"+i:i)+". \"" +name+"\" - "+time;
	        writer.println(name);

	    } catch (UnsupportedTagException | InvalidDataException | IOException e) {
	        //e.printStackTrace();
	        //System.out.println("File not found.");
	    } catch (StringIndexOutOfBoundsException e){}
	}
}