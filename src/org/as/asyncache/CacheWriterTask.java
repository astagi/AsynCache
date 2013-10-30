package org.as.asyncache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.as.asyncache.AsynCache.WriteResponseHandler;

import android.os.AsyncTask;

class CacheWriterTask extends AsyncTask<Void, Void, Exception> {

	private File file;
	private byte[] data;
	private WriteResponseHandler callback;

	public CacheWriterTask(File file, byte[] data, WriteResponseHandler callback) {
		this.file = file;
		this.data = data;
		this.callback = callback;
	}

	@Override
	protected Exception doInBackground(Void... arg0) {
		try {
			String path = file.getAbsolutePath();
			new File(path.substring(0, path.lastIndexOf('/') + 1)).mkdirs();
			setData(file, data);
			return null;
		} catch (IOException e) {
			return e;
		}
	}

	protected void onPostExecute(Exception result) {
		if (result == null)
			callback.onSuccess();
		else
			callback.onFailure(result);
	}

	private void setData(File file, byte[] data) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		try {
			os.write(data);
		} finally {
			os.flush();
			os.close();
		}
	}

}
