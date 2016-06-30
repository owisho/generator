package per.owisho.learn.generator.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MyFileWriter extends OutputStreamWriter{

	public MyFileWriter(String fileName) throws IOException{
		super(new FileOutputStream(fileName),"UTF-8");
	}

	public MyFileWriter(String fileName,boolean append) throws IOException{
		super(new FileOutputStream(fileName,append));
	}
	
	public MyFileWriter(File file) throws FileNotFoundException {
		super(new FileOutputStream(file));
	}
	
	public MyFileWriter(File file,boolean append) throws IOException{
		super(new FileOutputStream(file, append));
	}
	
	public MyFileWriter(FileDescriptor fd){
		super(new FileOutputStream(fd));
	}
}
