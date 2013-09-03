package br.gov.lexml.madoc.catalog.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.catalog.store.policy.CachePolicy;
import br.gov.lexml.madoc.catalog.store.policy.PolicyDecision;

public final class LocalCacheDocumentStore implements DocumentStore {

	private static final Logger log = LoggerFactory
			.getLogger(LocalCacheDocumentStore.class);
	private static final int fileNameLength = 200;

	private final DocumentStore store;
	private final File cacheDir;

	private final CachePolicy policy;

	public LocalCacheDocumentStore(DocumentStore store, File cacheDir,
			CachePolicy policy) throws IOException {
		super();
		this.store = store;
		this.cacheDir = cacheDir;
		this.policy = policy;
		if (!cacheDir.isDirectory() && !cacheDir.mkdirs()) {
			throw new IOException("Cannot create cache directory: " + cacheDir);
		}
	}

	private static String encodeUri(String docUri) {
		return Base64.encodeBase64String(docUri.getBytes());
	}

	private static List<String> encodeUriAsList(String docUri) {
		String encoded = encodeUri(docUri);
		List<String> result = new ArrayList<String>();
		int p = 0;
		while (p < encoded.length()) {
			int l = Math.min(encoded.length() - p, fileNameLength);
			result.add(encoded.substring(p, l));
			p += fileNameLength;
		}
		return result;
	}

	private File getFileForUri(String docUri) {
		File f = cacheDir;
		for (String comp : encodeUriAsList(docUri)) {
			f = new File(f, comp);
		}
		return new File(f, "cached");
	}

	private void storeFile(File f, InputStream is) throws IOException {
		File temp = File.createTempFile("temp", ".cached");
		FileUtils.copyInputStreamToFile(is, temp);
		if (f.exists()){
			f.delete();
		}
		FileUtils.moveFile(temp, f);
	}

	private InputStream getFromCacheFirst(String docUri) throws IOException {
		File f = getFileForUri(docUri);
		try {
			return FileUtils.openInputStream(f);
		} catch (IOException ex) {
			InputStream is2 = store.getDocument(docUri);
			if (is2 == null) {
				return null;
			}
			storeFile(f, is2);
			return FileUtils.openInputStream(f);
		}
	}

	private InputStream getFromStoreFirst(String docUri) throws IOException {
		File f = getFileForUri(docUri);

		InputStream is;
		try {
			is = store.getDocument(docUri);
		} catch (IOException ex) {
			is = null;
		}
		if (is == null) {
			is = FileUtils.openInputStream(f);
		}
		if (!f.getParentFile().isDirectory() && !f.getParentFile().mkdirs()) {
			log.error("getFromStoreFirst: Could not create cache file: " + f);
			return is;
		}
		storeFile(f, is);
		return FileUtils.openInputStream(f);
	}

	@Override
	public InputStream getDocument(String docUri) throws IOException {
		File f = getFileForUri(docUri);
		Long age;
		if (f.exists()) {
			age = Math.max(0L, System.currentTimeMillis() - f.lastModified());
		} else {
			age = null;
		}
		PolicyDecision d = policy.choosePolicy(docUri, age);
		switch (d) {
		case CACHE_FIRST:
			return getFromCacheFirst(docUri);
		case STORE_FIRST:
			return getFromStoreFirst(docUri);
		default:
			return store.getDocument(docUri);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("cacheDir", cacheDir)
				.append("store", store).append("policy", policy).toString();
	}
}
