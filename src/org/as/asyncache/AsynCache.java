package org.as.asyncache;

import java.io.File;

import android.content.Context;

public class AsynCache {

	private static final long INITIAL_MAX_SIZE = 5242880L;
	private static final String ASYNCACHE_FOLDER = "asyncache";

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

	public void write(Context context, String category, String name, String data, WriteResponseHandler callback) {
		write(context, Utils.pathJoin(category, name), data.getBytes(), callback);
	}

	public void write(Context context, String name, String data, WriteResponseHandler callback) {
		write(context, name, data.getBytes(), callback);
	}

	public void write(Context context, String name, byte[] data, WriteResponseHandler callback) {

		File cacheDir = getDirectory(context);
		long size = getDirSize(cacheDir);
		long newSize = data.length + size;

		if (newSize > getMaxSize()) {
			cleanDir(cacheDir, newSize - getMaxSize());
		}

		File file = new File(Utils.pathJoin(cacheDir.toString(), name));
		new CacheWriterTask(file, data, callback).execute();
	}

	public void read(Context context, String category, String name, ReadResponseHandler callback) {
		read(context, Utils.pathJoin(category, name), callback);
	}

	public void read(Context context, String name, ReadResponseHandler callback) {

		File cacheDir = getDirectory(context);
		File file = new File(Utils.pathJoin(cacheDir.toString(), name));

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

	private File getDirectory(Context context) {
		return new File(Utils.pathJoin(context.getCacheDir().toString(), ASYNCACHE_FOLDER));
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

		if (files == null)
			return 0;

		for (File file : files) {
			if (file.isFile()) {
				size += file.length();
			}
		}

		return size;
	}

	public static class Utils {

		public static String pathJoin(String... paths) {
			String finalPath = "";
			for (String path : paths) {
				finalPath = joinTwoPaths(finalPath, path);
			}
			return finalPath;
		}

		private static String joinTwoPaths(String path1, String path2) {
			File file1 = new File(path1);
			File file2 = new File(file1, path2);
			return file2.getPath();
		}
	}

}