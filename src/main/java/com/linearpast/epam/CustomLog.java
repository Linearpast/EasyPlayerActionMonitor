package com.linearpast.epam;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.*;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

@OnlyIn(Dist.DEDICATED_SERVER)
public class CustomLog {
	public static Logger LOGGER = Logger.getLogger(CustomLog.class.getName());
	public static void init() {
		DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
		try {
			Path logDir = Paths.get("logs/epam");
			if (!Files.exists(logDir))
				Files.createDirectories(logDir);
			String date = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());

			int fileNumber = 0;
			try (Stream<Path> paths = Files.list(logDir)){
				List<Path> pathList = paths.toList();
				List<Path> pathStream = pathList.stream().filter(path ->
						path.getFileName().toString().startsWith("info-" + date)
								&& (path.getFileName().toString().endsWith(".log")
								|| path.getFileName().toString().endsWith(".log.gz"))
				).toList();
				fileNumber = pathStream.size();
				for (Path path : pathList) {
					Path gzPath = Path.of(path + ".gz");
					if(path.getFileName().toString().endsWith(".log")){
						try(InputStream in = Files.newInputStream(path); OutputStream out = Files.newOutputStream(gzPath); GZIPOutputStream gzipOut = new GZIPOutputStream(out)){
							byte[] buffer = new byte[8192];
							int bytesRead;

							while ((bytesRead = in.read(buffer)) != -1) {
								gzipOut.write(buffer, 0, bytesRead);
							}
							gzipOut.finish();
						}
					}
					if (Files.exists(gzPath)) {
						Files.deleteIfExists(path);
					}
					if(path.getFileName().toString().endsWith(".lck")){
						Files.deleteIfExists(path);
					}
				}
			}catch (Exception ignored){}

			String logFileName = "info-" + date + "-"+ fileNumber + ".log";
			Path logFileDir = Path.of(logDir.toString(), logFileName);
			while (Files.exists(logFileDir) || Files.exists(Path.of(logFileDir + ".gz"))) {
				fileNumber++;
				logFileName = "info-" + date + "-"+ fileNumber + ".log";
				logFileDir = Path.of(logDir.toString(), logFileName);
			}
			String logFilePath = logFileDir.toString();
			FileHandler fileHandler = new FileHandler(logFilePath, true);
			fileHandler.setFormatter(new Formatter() {
				public String format(LogRecord record) {
					return "[" + TIMESTAMP_FORMATTER.format(LocalDateTime.now()) + "] " + record
							.getMessage() + System.lineSeparator();
				}
			});
			LOGGER.setUseParentHandlers(false);
			LOGGER.addHandler(fileHandler);
			LOGGER.setLevel(Level.ALL);
		} catch (IOException ignored) {
		}
	}
}
