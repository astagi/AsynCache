package org.as.asyncache;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;

public class AsynCache {

	private static final long INITIAL_MAX_SIZE = 5242880L;
	private static AsynCache instance;

	private long maxSize = INITIAL_MAX_SIZE;

	public static interface WriteResponseHandler {
		public void onSuccess();

		public void onFailure(Throwable t);
	}

	public static interface ReadResponseHandler {
		public void onSuccess(byte[] data);

		public void onFailure(Throwable t);
	}

	private AsynCache() {
	}

	public synchronized static AsynCache getInstance() {
		if (instance == null)
			instance = new AsynCache();
		return instance;
	}

	public void write(Context context, String name, String data, WriteResponseHandler callback) {
		write(context, name, data.getBytes(), callback);
	}

	public void write(Context context, String name, byte[] data, WriteResponseHandler callback) {

		name = md5(name);

		File cacheDir = context.getCacheDir();
		long size = getDirSize(cacheDir);
		long newSize = data.length + size;

		if (newSize > getMaxSize()) {
			cleanDir(cacheDir, newSize - getMaxSize());
		}

		File file = new File(cacheDir, name);
		new CacheWriterTask(file, data, callback).execute();
	}

	public void read(Context context, String name, ReadResponseHandler callback) {

		name = md5(name);

		File cacheDir = context.getCacheDir();
		File file = new File(cacheDir, name);

		if (!file.exists()) {
			callback.onFailure(new Exception("File not found"));
			return;
		}

		new CacheReaderTask(file, callback).execute();
	}

	public long getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

	private void cleanDir(File dir, long bytes) {

		long bytesDeleted = 0;
		File[] files = dir.listFiles();

		for (File file : files) {
			bytesDeleted += file.length();
			file.delete();

			if (bytesDeleted >= bytes) {
				break;
			}
		}
	}

	private long getDirSize(File dir) {

		long size = 0;
		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				size += file.length();
			}
		}

		return size;
	}

	private static String md5(final String s) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}