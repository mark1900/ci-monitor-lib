package ci.monitor.jws;

import ci.monitor.display.BuildDisplay;
import ci.monitor.remote.HttpContentGrabber;
import groovy.lang.GroovyClassLoader;

public class Main {

    public static void main(String[] args) {

        try {
            // Required as groovy will throw security exceptions otherwise
            System.setSecurityManager(null);

            ClassLoader parent = Main.class.getClassLoader();
            GroovyClassLoader loader = new GroovyClassLoader(parent);
            
            String groovyClassUrl = System.getProperty("groovyClassUrl");
            String buildHostUpdateInterval = System.getProperty("build.host.update.interval");

            String groovyClassContents = HttpContentGrabber.getUrlContents(groovyClassUrl).toString();
            
            Class groovyClass = loader.parseClass(groovyClassContents);
            BuildDisplay groovyBuildDisplayInstance = (BuildDisplay) groovyClass.newInstance();
            groovyBuildDisplayInstance.getInstanceProperties().setProperty("build.host.update.interval", buildHostUpdateInterval);
            groovyBuildDisplayInstance.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
