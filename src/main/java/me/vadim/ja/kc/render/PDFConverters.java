package me.vadim.ja.kc.render;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.vadim.ja.kc.render.electron.ElectronPDFConverter;
import me.vadim.ja.kc.render.puppeteer.JvppetteerPDFConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author vadim
 */
public final class PDFConverters {

	private PDFConverters() {}

	public static final int PORT = 8001;

	private static final ExecutorService worker = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("PDF conversion worker %d").build());

	public static ConversionService electron(String convertURL){
		return new ElectronPDFConverter(PORT, convertURL, worker);
	}

	public static ConversionService jvppetteer(){
		return new JvppetteerPDFConverter(PORT, worker);
	}

}
