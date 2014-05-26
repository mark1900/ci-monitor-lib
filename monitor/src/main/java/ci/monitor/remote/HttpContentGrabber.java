package ci.monitor.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

public class HttpContentGrabber {

    private static final Logger logger = Logger.getLogger(HttpContentGrabber.class);
    private static HttpClient httpClient = null;

    static {

        // Accepts all ssl certificates, valid and invalid.
        SSLCertificateTestHandler sslCertificateTestHandler = new SSLCertificateTestHandler();
        sslCertificateTestHandler.acceptCertificates();

        // HttpClient is much better than using the URL.getConnection service
        // HttpClient needs to be setup as below due to bug/issue
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setMaxTotalConnections(50);
        params.setDefaultMaxConnectionsPerHost(50);
        params.setSoTimeout(30000);
        params.setLinger(0);
        params.setTcpNoDelay(true);

        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

        connectionManager.setParams(params);

        httpClient = new HttpClient(connectionManager);

    }

    public static StringBuffer getUrlContents(String url) {

        StringBuffer contents = new StringBuffer();
        
        GetMethod request = null;
        int statusCode;

        try {
            request = new GetMethod((new URL(url)).toURI().toString());

            statusCode = httpClient.executeMethod(request);

            if (statusCode == HttpStatus.SC_OK) {

                contents = new StringBuffer(request.getResponseBodyAsString());
                logger.debug(contents);

            } else {

                logger.error(
                        "Failed to read url:  " + url + "\r\n"
                        + "Request to url failed with HttpStatus code:  " + statusCode);

            }

        } catch (Exception e) {

            logger.error(
                    "Request to url failed with Exception.", e);

        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }

        return contents;

    }

    public static byte[] getUrlContentsAsBytes(String url){

        byte[] contentBytes = new byte[0];

        GetMethod request = null;
        int statusCode;

        try {
            request = new GetMethod((new URL(url)).toURI().toString());

            statusCode = httpClient.executeMethod(request);

            if (statusCode == HttpStatus.SC_OK) {

                // Move bytes around, as string to bytes might cause issues.
                InputStream currentInputStream = request.getResponseBodyAsStream();
                ByteArrayOutputStream currentOutputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int bytesRead = -1;
                while((bytesRead = currentInputStream.read(buffer)) > -1) {
                    currentOutputStream.write(buffer, 0, bytesRead);
                }
                currentInputStream.close();
                contentBytes = currentOutputStream.toByteArray();
                currentOutputStream.close();

            } else {

                logger.error(
                        "Failed to read url:  " + url + "\r\n"
                        + "Request to url failed with HttpStatus code:  " + statusCode);

            }

        } catch (Exception e) {

            logger.error(
                    "Request to url failed with Exception.", e);

        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }

        return contentBytes;

    }

    public static InputStream getUrlContentsAsStream(String url) {

        InputStream contentStream = null;

        byte[] contentBytes = getUrlContentsAsBytes(url);

        if (null == contentBytes){
            logger.error("Failed to read url:  " + url + "\r\n");
        }else{
            contentStream = new ByteArrayInputStream(contentBytes);
        }
        
        return contentStream;

    }
}
