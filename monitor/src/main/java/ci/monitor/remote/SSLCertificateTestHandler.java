package ci.monitor.remote;

import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class SSLCertificateTestHandler {

	public void acceptCertificates() {

		try {
			SSLContext sc = SSLContext.getInstance("SSL");

			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new MockX509TrustManager() };

			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLSocketFactory sslSocketFactory = sc.getSocketFactory();

			MockHostnameVerifier mockhostnameVerifier = new MockHostnameVerifier();
			HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
			HttpsURLConnection.setDefaultHostnameVerifier(mockhostnameVerifier);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	class MockHostnameVerifier implements HostnameVerifier {
         public boolean verify(String urlHostName,SSLSession session) {
        	 return true;
         }
	}

	class MockX509TrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public void checkClientTrusted(X509Certificate[] certs,String authType) {
        }
        public void checkServerTrusted(X509Certificate[] certs,String authType) {
        }
    }
}

