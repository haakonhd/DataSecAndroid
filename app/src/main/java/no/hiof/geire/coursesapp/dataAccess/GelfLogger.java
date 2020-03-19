package no.hiof.geire.coursesapp.dataAccess;

import org.graylog2.gelfclient.GelfConfiguration;
import org.graylog2.gelfclient.GelfMessage;
import org.graylog2.gelfclient.GelfMessageBuilder;
import org.graylog2.gelfclient.GelfMessageLevel;
import org.graylog2.gelfclient.GelfTransports;
import org.graylog2.gelfclient.transport.GelfTransport;

import java.net.InetSocketAddress;


public class GelfLogger {

    public void gelfLogger(String appMessage){
        final String appliMessage = appMessage;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final GelfConfiguration config = new GelfConfiguration(new InetSocketAddress("158.39.188.222", 1515))
                            .transport(GelfTransports.UDP)
                            .queueSize(512)
                            .connectTimeout(5000)
                            .reconnectDelay(1000)
                            .tcpNoDelay(true)
                            .sendBufferSize(32768);
                    final GelfTransport transport = GelfTransports.create(config);
                    final GelfMessageBuilder builder = new GelfMessageBuilder("", "158.39.188.222")
                            .level(GelfMessageLevel.INFO)
                            .additionalField("_version", "1");

                    final GelfMessage message = builder.message(appliMessage)
                            .build();
                    transport.send(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

}
