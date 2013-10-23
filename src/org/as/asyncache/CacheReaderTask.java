package org.as.asyncache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.as.asyncache.AsynCache.ReadResponseHandler;

import android.os.AsyncTask;

class CacheReaderTask extends AsyncTask<Void, Void, CacheReaderTask.DataException> {

	private File file;
	private ReadResponseHandler callback;

	protected static class DataException {
		byte[] data;
		Exception e;

		DataException(byte[] data, Exception e) {
			this.data = data;
			this.e = e;
		}
	}

	public CacheReaderTask(File file, ReadResponseHandler callback) {
		this.file = file;
		this.callback = callback;
	}

	@Override
	protected DataException doInBackground(Void... arg0) {
		Exception readException = null;
		byte[] data = null;
		try {
			data = getData(file);
		} catch (IOException e) {
			readException = e;
		}
		return new DataException(data, readException);
	}

	protected void onPostExecute(DataException result) {
		if (result.e == null)
			callback.onSuccess(result.data);
		else
			callback.onFailure(result.e);
	}

	private byte[] getData(File file) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			int i;
			while ((i = is.read()) > 0)
				bos.write(i);
			return bos.toByteArray();
		} finally {
			if (is != null)
				is.close();
		}
	}

}
