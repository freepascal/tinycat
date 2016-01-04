package khang.javautil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class JavaCompiler {
    public static void main(String[] args) {        
        
		String fileName = null;
        try {        
			URL location = JavaCompiler.class.getProtectionDomain().getCodeSource().getLocation();		
			fileName = location.getFile();
		} catch(Exception e) {
			
		}
		
		final String fName = (fileName != null)? fileName: "JavaCompiler.jar";			
                        
        CommandParser cmdParser = new CommandParser(args) {
            @Override public void onError(Exception e) {
				String fmt = String.format(
					"Usage: java -jar %s <options> <filepath>\n",
					fName
				);
								
                //System.out.printf("Usage: JavaCompiler.jar <options> <filepath>\n");
                System.out.print(fmt);
                System.out.printf("\t -sourcedir \t the top-level directory contains source code\n");
                System.out.printf("\t -classdir \t the top-level directory will contain .class files generated\n");
            }
        };       
        
        
        if (cmdParser.sourceDir == null || cmdParser.classDir == null) {
            cmdParser.onError();
            return;
        }
        
        Path sourceDir = new File(cmdParser.sourceDir).toPath();
        Path classDir = new File(cmdParser.classDir).toPath();         
        
        if (!Files.exists(sourceDir)) {
            System.err.printf("-sourcedir not found %s\n", cmdParser.sourceDir);
            return;
        }
        
        if (!Files.exists(classDir)) {
            try {
                Process p = Runtime.getRuntime().exec(
                    String.format(
                        "sudo mkdir -p %s",
                        cmdParser.classDir    
                    )
                );
                p.waitFor();
                p = Runtime.getRuntime().exec(
					String.format(
						"sudo chown -R %s:users %s",
						System.getProperty("user.name"),
						cmdParser.classDir
					)
                );
                p.waitFor();
            } catch(IOException | InterruptedException e) {
                
            }
        }

        StringBuilder javaWilcardDirectories = new StringBuilder();
        
        // javac -d classes -classpath classes package1/*.java package2/*.java
        try {
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                boolean mark;
                @Override public FileVisitResult postVisitDirectory(Path directory, IOException e) throws IOException  {
                    if (e == null) {
                        if (mark) {
                            javaWilcardDirectories.append(directory.toString()).append("/").append("*.java ");
                            mark = false;
                        }
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw e;
                    }                
                }
                @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
                    if (file.toString().endsWith(".java")) {
                        mark = true;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch(IOException e) {
            System.err.printf("IOException occurs\n");
        }
        
        final String command = String.format("javac -d %s -classpath %s %s",
            classDir.toString(),
            classDir.toString(),
            javaWilcardDirectories.toString()
        );        
        
        try {
            Process p = Runtime.getRuntime().exec(new String[] {
				"/bin/sh",
				"-c",
				command
			});
			p.waitFor();      
        } catch(IOException | InterruptedException e) {
            System.err.printf("IOException or InterruptedException when javac executing\n");
        }
    }       
    
    static abstract class CommandParser {
       
        public String sourceDir;
        public String classDir;
        
        public CommandParser(final String[] args) {            
            for(int i = 0; i < args.length; ++i) {
                String s = args[i];
                try {
                    if (s.equalsIgnoreCase("-sourcedir")) {
                        sourceDir = args[i+1];
                    } else if (s.equalsIgnoreCase("-classdir")) {
                        classDir = args[i+1];
                    }
                } catch(ArrayIndexOutOfBoundsException e) {
                    onError(e);
                }
            }
        }                    
        
        public void onError() {
            onError(null);
        }
        
        abstract void onError(Exception e);
    }
}
