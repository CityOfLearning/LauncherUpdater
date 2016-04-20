package com.dyn.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class FileUtils {

	public static void copyFile(File from, File to) throws Exception {
		createFileSafely(to);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(from));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(to));
		byte[] block;
		while (bis.available() > 0) {
			block = new byte[8192];
			final int readNow = bis.read(block);
			bos.write(block, 0, readNow);
		}
		bos.flush();
		bos.close();
		bis.close();
	}

	public static void createFileSafely(File file) throws Exception {
		File parentFile = new File(file.getParent());
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				throw new IOException("Unable to create parent file: " + file.getParent());
			}
		}
		if (file.exists()) {
			if (!file.delete()) {
				if (!file.renameTo(new File(file.getParentFile(), "old-" + file.getName()))) {
					throw new IOException(
							"Couldn't delete '".concat(file.getName()).concat("'").concat(" Try Signing in again"));
				}
			}
		}
		if (!file.createNewFile()) {
			throw new IOException("Couldn't create '".concat(file.getAbsolutePath()).concat("'"));
		}
	}

	public static void downloadFileWithProgress(String url, File dest, IProgressMonitor progress) {
		try {
			String md5 = null;
			if (dest.exists()) {
				md5 = getMD5(dest);
			}

			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			if (md5 != null) {
				connection.setRequestProperty("If-None-Match", md5);
			}
			connection.connect();

			// local copy is up-to-date
			if (connection.getResponseCode() == 304) {
				return;
			}
			createFileSafely(dest);

			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));

			final int len = connection.getContentLength();
			if (progress != null) {
				progress.setMax(len);
			}

			int readBytes = 0;
			byte[] block;

			while (readBytes < len) {
				block = new byte[8192];
				int readNow = in.read(block);
				if (readNow > 0) {
					out.write(block, 0, readNow);
				}
				if (progress != null) {
					progress.setProgress(readBytes);
				}
				readBytes += readNow;
			}
			out.flush();
			out.close();
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	// this method is copied from original launcher, as the MD5-ing function
	// needs to be the same
	public static String getMD5(File file) throws Exception {
		DigestInputStream stream = null;
		try {
			stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("MD5"));
			byte[] buffer = new byte[65536];

			int read = stream.read(buffer);
			while (read >= 1) {
				read = stream.read(buffer);
			}
		} catch (Exception ignored) {
			return null;
		} finally {
			stream.close();
		}

		return String.format("%1$032x", new BigInteger(1, stream.getMessageDigest().digest()));
	}

	public static byte[] readFully(InputStream stream) throws IOException {
		byte[] data = new byte[4096];
		ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
		int len;
		do {
			len = stream.read(data);
			if (len > 0) {
				entryBuffer.write(data, 0, len);
			}
		} while (len != -1);

		return entryBuffer.toByteArray();
	}

	public static void writeFile(File dest, String str) throws Exception {
		createFileSafely(dest);
		FileWriter fw = new FileWriter(dest);
		fw.write(str);
		fw.flush();
		fw.close();
	}
}
