package ma.ahmedbenidar.tp14.config;

import ma.ahmedbenidar.tp14.ws.CompteSoapService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfig {

    private final CompteSoapService compteSoapService;
    private final Bus bus;

    public CxfConfig(CompteSoapService compteSoapService, Bus bus) {
        this.compteSoapService = compteSoapService;
        this.bus = bus;
    }

    @Bean
    public EndpointImpl endpointComptes() {
        EndpointImpl endpoint = new EndpointImpl(bus, compteSoapService);
        endpoint.publish("/ws");
        return endpoint;
    }

}
